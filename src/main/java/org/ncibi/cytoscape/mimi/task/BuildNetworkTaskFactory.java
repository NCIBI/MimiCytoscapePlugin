package org.ncibi.cytoscape.mimi.task;

import org.cytoscape.io.util.StreamUtil;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.view.model.CyNetworkViewFactory;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.cytoscape.work.TaskIterator;
import org.ncibi.cytoscape.mimi.enums.QueryType;

public class BuildNetworkTaskFactory {
	
	private CyNetworkFactory cyNetworkFactory;
	private CyNetworkManager cyNetworkManager;
	private CyNetworkViewFactory cyNetworkViewFactory;
	private CyNetworkViewManager cyNetworkViewManager;
	private ApplyVisualStyleAndLayoutTaskFactory vslTaskFactory;
	private StreamUtil streamUtil;

	public BuildNetworkTaskFactory(CyNetworkFactory cyNetworkFactory, 
			CyNetworkManager cyNetworkManager, 
			CyNetworkViewFactory cyNetworkViewFactory, CyNetworkViewManager cyNetworkViewManager, 
			ApplyVisualStyleAndLayoutTaskFactory vslTaskFactory, StreamUtil streamUtil) {
		this.cyNetworkFactory = cyNetworkFactory;
		this.cyNetworkManager = cyNetworkManager;
		this.cyNetworkViewFactory = cyNetworkViewFactory;
		this.cyNetworkViewManager = cyNetworkViewManager;
		this.vslTaskFactory = vslTaskFactory;
		this.streamUtil = streamUtil;
	}
	public TaskIterator createTaskIterator(QueryType queryType, String inputStr) {
		return new TaskIterator(new BuildNetworkTask(queryType, inputStr, 
				cyNetworkFactory, cyNetworkManager, 
				cyNetworkViewFactory, cyNetworkViewManager, vslTaskFactory, streamUtil));
	}
	
	public boolean isReady(QueryType queryType, String inputStr) {
		return (queryType != null && inputStr != null);
	}
	
}
