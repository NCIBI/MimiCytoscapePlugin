package org.ncibi.cytoscape.mimi.task;

import java.util.Properties;

import org.cytoscape.application.swing.CytoPanelComponent;
import org.cytoscape.event.CyEventHelper;
import org.cytoscape.io.util.StreamUtil;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.view.model.CyNetworkViewFactory;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.cytoscape.work.TaskIterator;
import org.ncibi.cytoscape.mimi.enums.QueryType;
import org.ncibi.cytoscape.mimi.ui.MiMILegend;

public class BuildNetworkTaskFactory {
	
	private boolean isLegendVisible = false;
	private CyNetworkFactory cyNetworkFactory;
	private CyNetworkManager cyNetworkManager;
	private CyNetworkViewFactory cyNetworkViewFactory;
	private CyNetworkViewManager cyNetworkViewManager;
	private CyEventHelper cyEventHelper;
	private ApplyVisualStyleAndLayoutTaskFactory vslTaskFactory;
	private CyServiceRegistrar cyServiceRegistrar;
	private StreamUtil streamUtil;

	public BuildNetworkTaskFactory(CyNetworkFactory cyNetworkFactory, 
			CyNetworkManager cyNetworkManager, 
			CyNetworkViewFactory cyNetworkViewFactory, CyNetworkViewManager cyNetworkViewManager, 
			CyEventHelper cyEventHelper, ApplyVisualStyleAndLayoutTaskFactory vslTaskFactory,
			CyServiceRegistrar cyServiceRegistrar, StreamUtil streamUtil) {
		this.cyNetworkFactory = cyNetworkFactory;
		this.cyNetworkManager = cyNetworkManager;
		this.cyNetworkViewFactory = cyNetworkViewFactory;
		this.cyNetworkViewManager = cyNetworkViewManager;
		this.cyEventHelper = cyEventHelper;
		this.vslTaskFactory = vslTaskFactory;
		this.cyServiceRegistrar = cyServiceRegistrar;
		this.streamUtil = streamUtil;
	}
	public TaskIterator createTaskIterator(QueryType queryType, String inputStr) {
		if(!isLegendVisible) {
			cyServiceRegistrar.registerService(new MiMILegend(), CytoPanelComponent.class, new Properties());
			isLegendVisible = true;
		}
		return new TaskIterator(new BuildNetworkTask(queryType, inputStr, 
				cyNetworkFactory, cyNetworkManager, 
				cyNetworkViewFactory, cyNetworkViewManager, cyEventHelper, vslTaskFactory, streamUtil));
	}
	
	public boolean isReady(QueryType queryType, String inputStr) {
		return (queryType != null && inputStr != null);
	}
	
}
