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
import org.cytoscape.event.CyEventHelper;
import org.cytoscape.io.util.StreamUtil;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.property.CyProperty;
import org.cytoscape.service.util.AbstractCyActivator;
import org.cytoscape.task.NodeViewTaskFactory;
import org.cytoscape.view.layout.CyLayoutAlgorithm;
import org.cytoscape.view.layout.CyLayoutAlgorithmManager;
import org.cytoscape.view.model.CyNetworkViewFactory;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.cytoscape.view.vizmap.VisualMappingFunctionFactory;
import org.cytoscape.view.vizmap.VisualMappingManager;
import org.cytoscape.view.vizmap.VisualStyleFactory;
import org.cytoscape.work.swing.DialogTaskManager;
import org.ncibi.cytoscape.mimi.action.HelpAction;
import org.ncibi.cytoscape.mimi.action.QueryAction;
import org.ncibi.cytoscape.mimi.enums.QueryType;
import org.ncibi.cytoscape.mimi.popupMenu.PopupEdgeContextMenuFactory;
import org.ncibi.cytoscape.mimi.popupMenu.PopupNodeContextMenuFactory;
import org.ncibi.cytoscape.mimi.task.ApplyVisualStyleAndLayoutTaskFactory;
import org.ncibi.cytoscape.mimi.task.BuildNetworkTaskFactory;
import org.ncibi.cytoscape.mimi.task.MiMINodeViewTaskFactory;
import org.ncibi.cytoscape.mimi.task.SearchTaskFactory;
import org.ncibi.cytoscape.mimi.task.UploadFileTaskFactory;
import org.ncibi.cytoscape.mimi.visual.MiMIVisualStyleBuilder;
import org.osgi.framework.BundleContext;

/**
 * MiMI PlugIn.
 *
 * @author Jing Gao
 * @author Alex Ade
 */
public class CyActivator extends AbstractCyActivator {
	/**
	 * @author Jing Gao 
	 * 
	 * Constructor.
	 * This method is called by the main Cytoscape Application upon startup.
	 * redesign input box on Oct 10, 2007
	 */
	
	public CyActivator() {
		super();
	}
	
	public void start(BundleContext bc) throws Exception {
		@SuppressWarnings("unchecked")
		CyProperty<Properties> cytoscapePropertiesServiceRef = getService(bc, CyProperty.class,
				"(cyPropertyName=cytoscape3.props)");
		HashMap<String, QueryType> type = new HashMap<String, QueryType>();
		type.put("queryMiMIByName", QueryType.QUERY_BY_NAME);
		type.put("queryMiMIById", QueryType.QUERY_BY_ID);
		type.put("queryMiMIByRemoteFile", QueryType.QUERY_BY_REMOTEFILE);
		type.put("queryMiMIByGenelist",null);
		
		CySwingApplication cySwingApplication = getService(bc, CySwingApplication.class);
		CyNetworkFactory cyNetworkFactory = getService(bc, CyNetworkFactory.class);
		CyNetworkManager cyNetworkManager = getService(bc, CyNetworkManager.class);
		CyNetworkViewFactory cyNetworkViewFactory = getService(bc, CyNetworkViewFactory.class);
		CyNetworkViewManager cyNetworkViewManager = getService(bc, CyNetworkViewManager.class);
		DialogTaskManager dialogTaskManager = getService(bc, DialogTaskManager.class);
		
		VisualStyleFactory vsFactoryServiceRef = getService(bc, VisualStyleFactory.class);
		VisualMappingFunctionFactory passthroughMappingFactoryRef = getService(bc, VisualMappingFunctionFactory.class,
				"(mapping.type=passthrough)");
		VisualMappingFunctionFactory discreteMappingFactoryRef = getService(bc, VisualMappingFunctionFactory.class,
				"(mapping.type=discrete)");
		VisualMappingManager vmm = getService(bc, VisualMappingManager.class);
		MiMIVisualStyleBuilder vsBuilder = new MiMIVisualStyleBuilder(vsFactoryServiceRef,
				discreteMappingFactoryRef, passthroughMappingFactoryRef);
		
		CyLayoutAlgorithmManager layouts = getService(bc, CyLayoutAlgorithmManager.class);
		CyLayoutAlgorithm layout = layouts.getLayout("force-directed");
		CyEventHelper cyEventHelper = getService(bc, CyEventHelper.class);
		ApplyVisualStyleAndLayoutTaskFactory vslTaskFactory = new ApplyVisualStyleAndLayoutTaskFactory(vsBuilder, vmm, layout);
		StreamUtil streamUtil = getService(bc, StreamUtil.class);
		BuildNetworkTaskFactory buildNetworkTaskFactory = new BuildNetworkTaskFactory(cyNetworkFactory, cyNetworkManager, 
				cyNetworkViewFactory, cyNetworkViewManager, cyEventHelper, vslTaskFactory, streamUtil);
		SearchTaskFactory searchTaskFactory = new SearchTaskFactory(buildNetworkTaskFactory, streamUtil);
		UploadFileTaskFactory uploadFileTaskFactory = new UploadFileTaskFactory(searchTaskFactory, dialogTaskManager, cySwingApplication.getJFrame());
		
		JFrame frame = cySwingApplication.getJFrame();
		frame.setTransferHandler(new URLDropHandler(type, buildNetworkTaskFactory, dialogTaskManager));

		Enumeration<?> keys = cytoscapePropertiesServiceRef.getProperties().propertyNames();
		while (keys.hasMoreElements()) {
			String s = (String)keys.nextElement();			
			if (type.containsKey(s)) {
				String prop = cytoscapePropertiesServiceRef.getProperties().getProperty(s);

				if (prop != null && !prop.equals("")) {	
					//System.out.println("arg key value pair is ["+s+"] ["+prop+"]");
					buildNetworkTaskFactory.createTaskIterator(type.get(s), prop);
					break;
				}
			}
		}
		// Add double click menu to the network view
		Properties mimiNodeViewTaskFactoryProps = new Properties();           
		mimiNodeViewTaskFactoryProps.setProperty("preferredAction","OPEN");
		mimiNodeViewTaskFactoryProps.setProperty("preferredMenu","MiMI Plugin");
		mimiNodeViewTaskFactoryProps.setProperty("title","Expand/Collapse");
		registerService(bc,new QueryAction(searchTaskFactory, uploadFileTaskFactory, dialogTaskManager, frame, streamUtil),CyAction.class, new Properties());
		registerService(bc,new HelpAction(),CyAction.class, new Properties());
		registerService(bc,new PopupNodeContextMenuFactory(dialogTaskManager, vslTaskFactory, frame, streamUtil), CyNodeViewContextMenuFactory.class, new Properties());
		registerService(bc,new PopupEdgeContextMenuFactory(dialogTaskManager, vslTaskFactory, frame, streamUtil), CyEdgeViewContextMenuFactory.class, new Properties());
		registerService(bc,new MiMINodeViewTaskFactory(vslTaskFactory, frame, streamUtil),NodeViewTaskFactory.class, mimiNodeViewTaskFactoryProps);
	}

}
