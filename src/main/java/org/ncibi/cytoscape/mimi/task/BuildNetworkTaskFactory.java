package org.ncibi.cytoscape.mimi.task;

import org.cytoscape.event.CyEventHelper;
import org.cytoscape.io.util.StreamUtil;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.model.subnetwork.CyRootNetworkManager;
import org.cytoscape.view.model.CyNetworkViewFactory;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.cytoscape.work.TaskIterator;
import org.ncibi.cytoscape.mimi.enums.QueryType;

public class BuildNetworkTaskFactory {
	
	private CyRootNetworkManager cyRootNetworkManager;
	private CyNetworkFactory cyNetworkFactory;
	private CyNetworkManager cyNetworkManager;
	private CyNetworkViewFactory cyNetworkViewFactory;
	private CyNetworkViewManager cyNetworkViewManager;
	private CyEventHelper cyEventHelper;
	private ApplyVisualStyleAndLayoutTaskFactory vslTaskFactory;
	private StreamUtil streamUtil;

	public BuildNetworkTaskFactory(CyRootNetworkManager cyRootNetworkManager, 
			CyNetworkFactory cyNetworkFactory, CyNetworkManager cyNetworkManager, 
			CyNetworkViewFactory cyNetworkViewFactory, CyNetworkViewManager cyNetworkViewManager, 
			CyEventHelper cyEventHelper, ApplyVisualStyleAndLayoutTaskFactory vslTaskFactory, StreamUtil streamUtil) {
		this.cyRootNetworkManager = cyRootNetworkManager;
		this.cyNetworkFactory = cyNetworkFactory;
		this.cyNetworkManager = cyNetworkManager;
		this.cyNetworkViewFactory = cyNetworkViewFactory;
		this.cyNetworkViewManager = cyNetworkViewManager;
		this.cyEventHelper = cyEventHelper;
		this.vslTaskFactory = vslTaskFactory;
		this.streamUtil = streamUtil;
	}
	public TaskIterator createTaskIterator(QueryType queryType, String inputStr) {
		return new TaskIterator(new BuildNetworkTask(queryType, inputStr,
				cyRootNetworkManager, cyNetworkFactory, cyNetworkManager, 
				cyNetworkViewFactory, cyNetworkViewManager, cyEventHelper, vslTaskFactory, streamUtil));
	}
	
	public boolean isReady(QueryType queryType, String inputStr) {
		return (queryType != null && inputStr != null);
	}
	
}
