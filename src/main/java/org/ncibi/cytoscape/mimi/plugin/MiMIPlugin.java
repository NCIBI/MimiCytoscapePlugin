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

import javax.swing.JDesktopPane;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

import org.ncibi.cytoscape.mimi.action.ImportFromMiMI;
import org.ncibi.cytoscape.mimi.popupMenu.AddPopupMenu;
import org.ncibi.cytoscape.mimi.ui.MiMIHelp;

import cytoscape.Cytoscape;
import cytoscape.CytoscapeInit;
import cytoscape.plugin.CytoscapePlugin;
import cytoscape.view.CyMenus;
import cytoscape.view.CytoscapeDesktop;

/**
 * MiMI PlugIn.
 *
 * @author Jing Gao
 * @author Alex Ade
 */
public class MiMIPlugin extends CytoscapePlugin {
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
		initialize();
		//create listener for adding popup menu
		new AddPopupMenu();			
		addMiMIPluginItem();					
	}

	/**
	 * Init method to grab queryMiMIBy[Name|ID] property and start query if
	 * present. Grabs setMiMIHost property and sets host variable appropriately.
	 * Also, sets up drag and drop for URLs onto Cytoscape.
	 *
	 * @author Alex Ade
	 * @date   Thu Nov 16 14:05:11 EST 2006
	 */
	public void initialize() {
		String host = CytoscapeInit.getProperties().getProperty("setMiMIHost", HOST);

		HashMap<String, Integer> type = new HashMap<String, Integer>();
		type.put("queryMiMIByName", QueryMiMI.QUERY_BY_NAME);
		type.put("queryMiMIById", QueryMiMI.QUERY_BY_ID);
		type.put("queryMiMIByRemoteFile", QueryMiMI.QUERY_BY_REMOTEFILE);
		type.put("queryMiMIByGenelist",10);
		JDesktopPane dp = Cytoscape.getDesktop().getNetworkViewManager().getDesktopPane();
		dp.setTransferHandler(new URLDropHandler(type));

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

		Enumeration keys = CytoscapeInit.getProperties().propertyNames();
		while (keys.hasMoreElements()) {
			String s = (String)keys.nextElement();			
			if (type.containsKey(s)) {
				String prop = CytoscapeInit.getProperties().getProperty(s);

				if (prop != null && !prop.equals("")) {	
					//System.out.println("arg key value pair is ["+s+"] ["+prop+"]");
					new QueryMiMIWrapper(type.get(s), prop);
					break;
				}
			}
		}
	}
	
	private void addMiMIPluginItem(){
		CytoscapeDesktop desktop = Cytoscape.getDesktop();
		CyMenus cyMenus = desktop.getCyMenus();
		JMenu plugInMenu = cyMenus.getOperationsMenu();	
		JMenu mimiMenu=new JMenu("MiMI Plugin");
		JMenuItem menuItem1 = new JMenuItem("Query"); 			
		menuItem1.addActionListener(new ImportFromMiMI());  
		mimiMenu.add(menuItem1);		
		JMenuItem menuItem2 = new JMenuItem("Help");
		menuItem2.addActionListener(new MiMIHelp());
		mimiMenu.add(menuItem2);	
		plugInMenu.add(mimiMenu); 	
	}
}
