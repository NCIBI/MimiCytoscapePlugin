package org.ncibi.cytoscape.mimi.task;

import java.util.Collection;

import org.cytoscape.event.CyEventHelper;
import org.cytoscape.io.util.StreamUtil;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyRow;
import org.cytoscape.model.CyTable;
import org.cytoscape.model.CyTableFactory;
import org.cytoscape.model.CyTableManager;
import org.cytoscape.model.subnetwork.CyRootNetwork;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.CyNetworkViewFactory;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.cytoscape.work.TaskMonitor;
import org.ncibi.cytoscape.mimi.enums.NodeType;
import org.ncibi.cytoscape.mimi.enums.QueryType;

public class BuildNetworkTask extends AbstractMiMIQueryTask {
	
	private QueryType queryType;
	private String inputStr;
	private CyTableFactory cyTableFactory;
	private CyTableManager cyTableManager;
	private CyNetworkFactory cyNetworkFactory;
	private CyNetworkManager cyNetworkManager;
	private CyNetworkViewFactory cyNetworkViewFactory;
	private CyNetworkViewManager cyNetworkViewManager;
	private CyEventHelper cyEventHelper;
	private ApplyVisualStyleAndLayoutTaskFactory vslTaskFactory;
	private StreamUtil streamUtil;
	
	public BuildNetworkTask(QueryType queryType, String inputStr, CyTableFactory cyTableFactory, CyTableManager cyTableManager,
			CyNetworkFactory cyNetworkFactory, CyNetworkManager cyNetworkManager, CyNetworkViewFactory cyNetworkViewFactory,
			CyNetworkViewManager cyNetworkViewManager, CyEventHelper cyEventHelper, ApplyVisualStyleAndLayoutTaskFactory vslTaskFactory, StreamUtil streamUtil) {
		this.queryType = queryType;
		this.inputStr = inputStr;
		this.cyTableFactory = cyTableFactory;
		this.cyTableManager = cyTableManager;
		this.cyNetworkFactory = cyNetworkFactory;
		this.cyNetworkManager = cyNetworkManager;
		this.cyNetworkViewFactory = cyNetworkViewFactory;
		this.cyNetworkViewManager = cyNetworkViewManager;
		this.cyEventHelper = cyEventHelper;
		this.vslTaskFactory = vslTaskFactory;
		this.streamUtil = streamUtil;
	}
	
	@Override
	public void run(TaskMonitor taskMonitor) throws Exception {
		CyTable geneTable = null;
		CyTable interactionTable = null;
		for(CyTable table: cyTableManager.getGlobalTables()) {
			if(table.getTitle().equals("MiMI Gene Table")) {
				geneTable = table;
			}
			else if(table.getTitle().equals("MiMI Interaction Table")) {
				interactionTable = table;
			}
			if (geneTable != null && interactionTable != null)
				break;
		}
		if(geneTable == null) {
			geneTable = cyTableFactory.createTable("MiMI Gene Table", "ID", Integer.class, false, true);
			cyTableManager.addTable(geneTable);
			geneTable.createColumn("UserAnnot", Boolean.class, true);
		}
		if(interactionTable == null) {
			interactionTable = cyTableFactory.createTable("MiMI Interaction Table", "ID", Integer.class, false, true);
			cyTableManager.addTable(interactionTable);
			interactionTable.createColumn("UserAnnot", Boolean.class, true);
		}
		CyNetwork network = cyNetworkFactory.createNetwork();
		String [] inputParameters = inputStr.split("/////");	
		String geneList= (inputParameters[0].length()<=15) ? inputParameters[0]:inputParameters[0].substring(0, 15)+"...";
		//System.out.println("start query mimi geneidlist["+geneIDList+"]");				   
		doQuery(queryType,inputStr, streamUtil, taskMonitor);
		//create networks
		CyTable defaultNodeTable = network.getDefaultNodeTable();
		defaultNodeTable.createColumn("ID", Integer.class, true);
		defaultNodeTable.addVirtualColumns(geneTable, "ID", true);
		CyTable localNodeTable = network.getTable(CyNode.class, CyNetwork.LOCAL_ATTRS);
		localNodeTable.createColumn("Network Distance", String.class, true);
		localNodeTable.createColumn("Node Color", Integer.class, true);
		CyTable defaultEdgeTable = network.getDefaultEdgeTable();
		defaultEdgeTable.createColumn("ID", Integer.class, true);
		defaultEdgeTable.addVirtualColumns(interactionTable, "ID", true);
		CyTable defaultNetworkTable = network.getDefaultNetworkTable();
		defaultNetworkTable.createColumn("Input Genes", String.class, true);
		defaultNetworkTable.createColumn("Organism", String.class, true);
		defaultNetworkTable.createColumn("Molecule Type", String.class, true);
		defaultNetworkTable.createColumn("Data Source", String.class, true);
		defaultNetworkTable.createColumn("Displays for results", String.class, true);

		String line;              	   
		while ((line = rd.readLine()) != null) {
			// Process line..
			// System.out.println("line is ["+line+"]");
			String [] res=line.split("/////");	
			if (res.length!=7)continue;
			Integer sourceId;
			Integer targetId;
			Integer interactionId;
			try {
				sourceId = Integer.valueOf(res[1]);
				targetId = Integer.valueOf(res[4]);
				interactionId = Integer.valueOf(res[6]);
			}
			catch(NumberFormatException e) {
				continue;
			}
			//result from SPs for mimiR2: symbol1,geneid1,step1,symbol2,geneid2,step2,intID
			//source node
			CyNode sourceNode;
			Collection<CyRow> sourceNodeRows = defaultNodeTable.getMatchingRows("ID", sourceId);
			if(sourceNodeRows.isEmpty()) {
				sourceNode = network.addNode();
				network.getRow(sourceNode).set(CyNetwork.NAME, res[0]);
				network.getRow(sourceNode).set(CyRootNetwork.SHARED_NAME, res[0]);
				network.getRow(sourceNode).set("ID", sourceId);
			}
			else {
				CyRow sourceNodeRow = sourceNodeRows.iterator().next();
				Long sourceNodeSuid = sourceNodeRow.get(CyNetwork.SUID, Long.class);
				sourceNode = network.getNode(sourceNodeSuid);
			}

			//MaxDistance = (MaxDistance.compareTo(res[2])<0)? res[2]:MaxDistance ; 
			//MaxDistance = (MaxDistance.compareTo(res[5])<0)? res[5]:MaxDistance ;
			if(!nodeList.contains(sourceNode)) {
				nodeList.add(sourceNode);
				//set default (false) user annotation attribute to node
				network.getRow(sourceNode).set("UserAnnot", false);
				//add step attribute if it does not exist
				if(network.getRow(sourceNode).get("Network Distance", String.class) == null) {
					network.getRow(sourceNode).set("Network Distance", res[2]);
				}
				if(network.getRow(sourceNode).get("Network Distance", String.class).equals("0")) {
					network.getRow(sourceNode).set("Node Color", NodeType.SEEDNODE.ordinal());
				}
				else
					network.getRow(sourceNode).set("Node Color", NodeType.SEEDNEIGHBOR.ordinal());
			}

			//target node
			CyNode targetNode;
			Collection<CyRow> targetNodeRows = defaultNodeTable.getMatchingRows("ID", targetId);
			if(targetNodeRows.isEmpty()) {
				targetNode = network.addNode();
				network.getRow(targetNode).set(CyNetwork.NAME, res[3]);
				network.getRow(targetNode).set(CyRootNetwork.SHARED_NAME, res[3]);
				network.getRow(targetNode).set("ID", targetId);
			}
			else {
				CyRow targetNodeRow = targetNodeRows.iterator().next();
				Long targetNodeSuid = targetNodeRow.get(CyNetwork.SUID, Long.class);
				targetNode = network.getNode(targetNodeSuid);
			}

			if(!nodeList.contains(targetNode)) {
				nodeList.add(targetNode);
				//set default (false) user annotation attribute to node
				network.getRow(targetNode).set("UserAnnot", false);
				//add step attribute if it does not exist
				if(network.getRow(targetNode).get("Network Distance", String.class) == null) {
					network.getRow(targetNode).set("Network Distance", res[5]);
				}
				if(network.getRow(targetNode).get("Network Distance", String.class).equals("0")) {
					network.getRow(targetNode).set("Node Color", NodeType.SEEDNODE.ordinal());
				}
				else
					network.getRow(targetNode).set("Node Color", NodeType.SEEDNEIGHBOR.ordinal());
			}

			//edge
			CyEdge edge;
			Collection<CyRow> edgeRows = defaultEdgeTable.getMatchingRows("ID",interactionId);
			if(edgeRows.isEmpty()) {
				edge = network.addEdge(sourceNode, targetNode, true);
				String name = "("+res[0]+" , "+res[3]+")";
				network.getRow(edge).set(CyNetwork.NAME, name);
				network.getRow(edge).set(CyRootNetwork.SHARED_NAME, name);
				network.getRow(edge).set("ID",interactionId);
			}
			else {
				CyRow edgeRow = edgeRows.iterator().next();
				Long edgeSuid = edgeRow.get(CyNetwork.SUID, Long.class);
				edge = network.getEdge(edgeSuid);
			}

			//System.out.println ("edge identifier["+edge.getIdentifier());
			if(!edgeList.contains(edge)) {
				edgeList.add(edge);
				network.getRow(edge).set("UserAnnot",false);
			}

		}
		//System.out.println("delete total return no is:"+deli);
		rd.close();
		//System.out.println("node list size is:"+nodeList.size());
		//System.out.println("edge list size is:"+edgeList.size());
		if (!nodeList.isEmpty()){
			//create network    
			if (queryType==QueryType.QUERY_BY_ID){
				network.getRow(network).set(CyNetwork.NAME, "geneID:"+inputStr);
			}
			else{
				//System.out.println ("node list size "+nodeList.size());
				//System.out.println("after drawing network geneidlist is "+geneIDList);
				//networkAttributes.setAttribute(cynetwork.getIdentifier(), "MaxDist",MaxDistance);
				String networkName =geneList+"|"+ inputParameters[1] + "|" +inputParameters[2] + "|" +inputParameters[3] + "|" +inputParameters[4] + "|" ;
				network.getRow(network).set(CyNetwork.NAME, networkName);
				network.getRow(network).set("Input Genes", inputParameters[0].replaceAll(" ", ";"));
				network.getRow(network).set("Organism", inputParameters[1]); 
				network.getRow(network).set("Molecule Type", inputParameters[2]);
				network.getRow(network).set("Data Source", inputParameters[3]);
				network.getRow(network).set("Displays for results", inputParameters[4]);
			}
			cyNetworkManager.addNetwork(network);
			CyNetworkView view = cyNetworkViewFactory.createNetworkView(network);
			
			cyNetworkViewManager.addNetworkView(view);
			cyEventHelper.flushPayloadEvents();
			insertTasksAfterCurrentTask(vslTaskFactory.createTaskIterator(view));
			insertTasksAfterCurrentTask(new GetMiMIAttributesTask(nodeList, edgeList, network, streamUtil));
			insertTasksAfterCurrentTask(new GetAnnotationAttributesTask(nodeList, edgeList, network, streamUtil));
			insertTasksAfterCurrentTask(vslTaskFactory.createTaskIterator(view));
		} else {
			throw new Exception("No result returned for this query.\n Please check if you entered up to date gene symbols, OR\nyou may need to modify paramters and try again");	            	 
		}
	}
}
