/*************************************************************************
 * Copyright 2012 Regents of the University of Michigan 
 * 
 * NCIBI - The National Center for Integrative Biomedical Informatics (NCIBI)
 *         http://www.ncib.org.
 * 
 * This product includes software developed by other, see specific notes in the code. 
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms
 * of the GNU General Public License as published by the Free Software Foundation, 
 * either version 3 of the License, or (at your option) any later version, along with the following terms:
 * 1.	You may convey a work based on this program in accordance with section 5, 
 *      provided that you retain the above notices.
 * 2.	You may convey verbatim copies of this program code as you receive it, 
 *      in any medium, provided that you retain the above notices.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * 
 * See the GNU General Public License for more details, http://www.gnu.org/licenses/.
 * 
 * This work was supported in part by National Institutes of Health Grant #U54DA021519
 *
 ******************************************************************/
 
package org.ncibi.cytoscape.mimi.plugin;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Properties;

import javax.swing.JFrame;

import org.cytoscape.application.swing.CyAction;
import org.cytoscape.application.swing.CyEdgeViewContextMenuFactory;
import org.cytoscape.application.swing.CyNodeViewContextMenuFactory;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.io.util.StreamUtil;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.property.CyProperty;
import org.cytoscape.service.util.AbstractCyActivator;
import org.cytoscape.task.NodeViewTaskFactory;
import org.cytoscape.view.vizmap.VisualMappingFunctionFactory;
import org.cytoscape.view.vizmap.VisualStyleFactory;
import org.ncibi.cytoscape.mimi.action.HelpAction;
import org.ncibi.cytoscape.mimi.action.QueryAction;
import org.ncibi.cytoscape.mimi.popupMenu.PopupEdgeContextMenuFactory;
import org.ncibi.cytoscape.mimi.popupMenu.PopupNodeContextMenuFactory;
import org.ncibi.cytoscape.mimi.task.ExpandCollapseNodeTaskFactory;
import org.ncibi.cytoscape.mimi.visual.MiMIVisualStyleBuilder;
import org.osgi.framework.BundleContext;

/**
 * MiMI PlugIn.
 *
 * @author Jing Gao
 * @author Alex Ade
 */
public class MiMIPlugin extends AbstractCyActivator {
	/**
	 * @author Jing Gao 
	 * 
	 * Constructor.
	 * This method is called by the main Cytoscape Application upon startup.
	 * redesign input box on Oct 10, 2007
	 */
	public static String MiMILOGO="http://www.bioinformatics.med.umich.edu/app/nlp/logo/";	
	public static long lastClick=0;
    public static long lastNodeHashcode=0;
    public static long lastEdgeHashcode=0;  
    public static String currentUserID="0";
	private static String PROTOCOL = "http";
	//private static String HOST ="mimitest.ncibi.org"; 
	private static String HOST="mimi.ncibi.org";
	public static String BIONLPURL="https://portal.ncibi.org/portal/site/!gateway/page/9b2c0673-4252-409d-003d-25996426b215";
	public static String GENEFILETEMPLATE="http://mimiplugin.ncibi.org/template.html";
	public static String MIMIPLUGINHOME="http://mimiplugin.ncibi.org";
	public static String GENE2MESH="http://gene2mesh.ncibi.org/";
	public static String MIMINODELINK="http://mimitest.ncibi.org/mimi/gene"; //?geneid=1436
	public static String FREETEXTSEARCH="http://mimitest.ncibi.org/mimi/symbols";//?query=Prostate+Cancer
	public static String MESHSEARCH="http://gene2mesh.ncibi.org/viewlist.php";//?term=prostatic+neoplasms&qtype=mesh";
	public static String ANNOTEDITORLOGIN="http://mimiplugin.ncibi.org/dbaccess/3.2/queryLogin.php";
	public static String NEWUSERURL="http://mimiplugin.ncibi.org/dbaccess/3.2/addNewUser.php";
	public static String VALIDATEEMAIL="http://mimiplugin.ncibi.org/dbaccess/3.2/validateEmail.php";
	public static String GETSHAREDANNOT="http://mimiplugin.ncibi.org/dbaccess/3.2/getSharedAnnot.php";
	public static String ANNOTSETNAME="http://mimiplugin.ncibi.org/dbaccess/3.2/queryMiMIAnnot_setList.php";
	public static String SENDPSWD="http://mimiplugin.ncibi.org/dbaccess/3.2/sendpswd.php";
	public static String CHECKPLUGINVERSION="http://mimiplugin.ncibi.org/CurrentPluginVersion";
	public static String CURRENTPLUGINVERSION ="3.2";
	public static String PRECOMPUTEEXPAND="http://mimiplugin.ncibi.org/dbaccess/3.2/precomputeexpand.php";
	public static String GENESAMPLE="http://mimiplugin.ncibi.org/mygene.txt";
	public static String QUERYGDS="http://mimiplugin.ncibi.org/dbaccess/3.2/queryGDS_subsetExprSig.php";
	public static String BROWSEGDS="http://mimiplugin.ncibi.org/dbaccess/3.2/browseGds.php";
	public static int 	 RTRNGDSEXPSIGCOLUMNNO=5;	
	public static int    FREETEXT=1;
	public static int    MESHTERM=2;
	
	public MiMIPlugin() {
		super();
	}
	
	public void start(BundleContext bc) throws Exception {
		@SuppressWarnings("unchecked")
		CyProperty<Properties> cytoscapePropertiesServiceRef = getService(bc, CyProperty.class,
				"(cyPropertyName=cytoscape3.props)");
		String host = cytoscapePropertiesServiceRef.getProperties().getProperty("setMiMIHost", HOST);
		HashMap<String, Integer> type = new HashMap<String, Integer>();
		type.put("queryMiMIByName", QueryMiMI.QUERY_BY_NAME);
		type.put("queryMiMIById", QueryMiMI.QUERY_BY_ID);
		type.put("queryMiMIByRemoteFile", QueryMiMI.QUERY_BY_REMOTEFILE);
		type.put("queryMiMIByGenelist",10);
		
		CySwingApplication cySwingApplication = getService(bc, CySwingApplication.class);
		CyNetworkFactory cyNetworkFactory = getService(bc, CyNetworkFactory.class);
		CyNetworkManager cyNetworkManager = getService(bc, CyNetworkManager.class);
		StreamUtil streamUtil = getService(bc, StreamUtil.class);
		VisualStyleFactory vsFactoryServiceRef = getService(bc, VisualStyleFactory.class);
		VisualMappingFunctionFactory passthroughMappingFactoryRef = getService(bc, VisualMappingFunctionFactory.class,
				"(mapping.type=passthrough)");
		VisualMappingFunctionFactory discreteMappingFactoryRef = getService(bc, VisualMappingFunctionFactory.class,
				"(mapping.type=discrete)");
		MiMIVisualStyleBuilder vsBuilder = new MiMIVisualStyleBuilder(vsFactoryServiceRef,
				discreteMappingFactoryRef, passthroughMappingFactoryRef);
		
		JFrame frame = cySwingApplication.getJFrame();
		frame.setTransferHandler(new URLDropHandler(type)); //NetworkViewManager?

		//ContainerListener l = new ContainerListener() {
		//	public void componentAdded(ContainerEvent e) {
		//		if (e.getChild() instanceof JInternalFrame) {
		//			JInternalFrame iframe = (JInternalFrame)e.getChild();
		//			iframe.setTransferHandler(null);
		//			iframe.getRootPane().setTransferHandler(null);
		//			iframe.getLayeredPane().setTransferHandler(null);
		//		}
		//	}

		//	public void componentRemoved(ContainerEvent e) { }
		//};
		//dp.addContainerListener(l);

		Enumeration keys = cytoscapePropertiesServiceRef.getProperties().propertyNames();
		while (keys.hasMoreElements()) {
			String s = (String)keys.nextElement();			
			if (type.containsKey(s)) {
				String prop = cytoscapePropertiesServiceRef.getProperties().getProperty(s);

				if (prop != null && !prop.equals("")) {	
					//System.out.println("arg key value pair is ["+s+"] ["+prop+"]");
					new QueryMiMIWrapper(type.get(s), prop, cyNetworkFactory, cyNetworkManager, frame, streamUtil);
					break;
				}
			}
		}
		// Add double click menu to the network view
		Properties expandCollapseNodeTaskFactoryProps = new Properties();           
		expandCollapseNodeTaskFactoryProps.setProperty("preferredAction","OPEN");
		registerService(bc,new QueryAction(cyNetworkFactory, cyNetworkManager, cySwingApplication, streamUtil),CyAction.class, new Properties());
		registerService(bc,new HelpAction(),CyAction.class, new Properties());
		registerService(bc,new PopupNodeContextMenuFactory(), CyNodeViewContextMenuFactory.class, new Properties());
		registerService(bc,new PopupEdgeContextMenuFactory(), CyEdgeViewContextMenuFactory.class, new Properties());
		registerService(bc,new ExpandCollapseNodeTaskFactory(frame, streamUtil),NodeViewTaskFactory.class, expandCollapseNodeTaskFactoryProps);
	}

}
