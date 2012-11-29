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
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.CyNetworkViewFactory;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.cytoscape.work.TaskMonitor;
import org.ncibi.cytoscape.mimi.enums.NodeType;
import org.ncibi.cytoscape.mimi.enums.QueryType;

public class BuildNetworkTask extends AbstractMiMIQueryTask {
	
	private QueryType queryType;
	private String inputStr;
	private CyNetworkFactory cyNetworkFactory;
	private CyNetworkManager cyNetworkManager;
	private CyNetworkViewFactory cyNetworkViewFactory;
	private CyNetworkViewManager cyNetworkViewManager;
	private CyEventHelper cyEventHelper;
	private ApplyVisualStyleAndLayoutTaskFactory vslTaskFactory;
	private StreamUtil streamUtil;
	
	public BuildNetworkTask(QueryType queryType, String inputStr, CyNetworkFactory cyNetworkFactory, 
			CyNetworkManager cyNetworkManager, CyNetworkViewFactory cyNetworkViewFactory,
			CyNetworkViewManager cyNetworkViewManager, CyEventHelper cyEventHelper, ApplyVisualStyleAndLayoutTaskFactory vslTaskFactory, StreamUtil streamUtil) {
		this.queryType = queryType;
		this.inputStr = inputStr;
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
		CyNetwork network = cyNetworkFactory.createNetwork();
		String [] inputParameters = inputStr.split("/////");	
		String geneList= (inputParameters[0].length()<=15) ? inputParameters[0]:inputParameters[0].substring(0, 15)+"...";
		//System.out.println("start query mimi geneidlist["+geneIDList+"]");				   
		doQuery(queryType,inputStr, streamUtil, taskMonitor);
		//create networks
		CyTable defaultNodeTable = network.getDefaultNodeTable();
		defaultNodeTable.createColumn("Gene.name", String.class, true);
		defaultNodeTable.createColumn("Gene.userAnnot", Boolean.class, true);
		defaultNodeTable.createColumn("Network Distance", String.class, true);
		CyTable hiddenNodeTable = network.getTable(CyNode.class, CyNetwork.HIDDEN_ATTRS);
		hiddenNodeTable.createColumn("Node Color", Integer.class, true);
		hiddenNodeTable.createListColumn("NodeIDList", String.class, true);
		hiddenNodeTable.createListColumn("EdgeIDList", String.class, true);
		CyTable defaultEdgeTable = network.getDefaultEdgeTable();
		defaultEdgeTable.createColumn("Interaction.geneName", String.class, true);
		defaultEdgeTable.createColumn("Interaction.userAnnot", Boolean.class, true);
		defaultEdgeTable.createColumn("Interaction.id", String.class, true);
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
			//result from SPs for mimiR2: symbol1,geneid1,step1,symbol2,geneid2,step2,intID
			//source node
			CyNode sourceNode;
			Collection<CyRow> sourceNodeRows = defaultNodeTable.getMatchingRows(CyNetwork.NAME, res[1]);
			if(sourceNodeRows.isEmpty()) {
				sourceNode = network.addNode();
				network.getRow(sourceNode).set(CyNetwork.NAME, res[1]);
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
				network.getRow(sourceNode).set("Gene.name", res[0]);
				//set default (false) user annotation attribute to node
				network.getRow(sourceNode).set("Gene.userAnnot", false);
				//add step attribute if it does not exist
				if(!network.getRow(sourceNode).get("Network Distance", String.class, "-1").equals("0")) {
					network.getRow(sourceNode).set("Network Distance", res[2]);
					hiddenNodeTable.getRow(sourceNode.getSUID()).set("Node Color", NodeType.SEEDNEIGHBOR.ordinal());
				}
				else
					hiddenNodeTable.getRow(sourceNode.getSUID()).set("Node Color", NodeType.SEEDNODE.ordinal());
			}

			//target node
			CyNode targetNode;
			Collection<CyRow> targetNodeRows = defaultNodeTable.getMatchingRows(CyNetwork.NAME, res[4]);
			if(targetNodeRows.isEmpty()) {
				targetNode = network.addNode();
				network.getRow(targetNode).set(CyNetwork.NAME, res[4]);
				network.getRow(targetNode).set("Gene.name", res[3]);
				//set default (false) user annotation attribute to node
				network.getRow(targetNode).set("Gene.userAnnot", false);
			}
			else {
				CyRow targetNodeRow = targetNodeRows.iterator().next();
				Long targetNodeSuid = targetNodeRow.get(CyNetwork.SUID, Long.class);
				targetNode = network.getNode(targetNodeSuid);
			}

			if(!nodeList.contains(targetNode)) {
				nodeList.add(targetNode);
				//add step attribute if it does not exist
				if(!network.getRow(targetNode).get("Network Distance", String.class, "-1").equals("0")) {
					network.getRow(targetNode).set("Network Distance", res[5]);
					hiddenNodeTable.getRow(targetNode.getSUID()).set("Node Color", NodeType.SEEDNEIGHBOR.ordinal());
				}
				else
					hiddenNodeTable.getRow(targetNode.getSUID()).set("Node Color", NodeType.SEEDNODE.ordinal());
			}

			//edge
			CyEdge edge;
			String name = network.getRow(sourceNode).get(CyNetwork.NAME, String.class) + 
					" ( ) " + network.getRow(targetNode).get(CyNetwork.NAME, String.class);
			Collection<CyRow> edgeRows = defaultEdgeTable.getMatchingRows(CyNetwork.NAME,name);
			if(edgeRows.isEmpty()) {
				edge = network.addEdge(sourceNode, targetNode, true);
				network.getRow(edge).set(CyNetwork.NAME, name );
				network.getRow(edge).set(CyEdge.INTERACTION, " ");
				network.getRow(edge).set("Interaction.geneName","("+res[0]+" , "+res[3]+")");
				network.getRow(edge).set("Interaction.userAnnot",false);
				network.getRow(edge).set("Interaction.id",res[6]);
			}
			else {
				CyRow edgeRow = edgeRows.iterator().next();
				Long edgeSuid = edgeRow.get(CyNetwork.SUID, Long.class);
				edge = network.getEdge(edgeSuid);
			}

			//System.out.println ("edge identifier["+edge.getIdentifier());
			if(!edgeList.contains(edge))
				edgeList.add(edge);

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
			//insertTasksAfterCurrentTask(new GetMiMIAttributesTask(nodeList, edgeList, network, streamUtil));
			//insertTasksAfterCurrentTask(new GetAnnotationAttributesTask(nodeList, edgeList, network, streamUtil));
			insertTasksAfterCurrentTask(vslTaskFactory.createTaskIterator(view));
		} else {
			throw new Exception("No result returned for this query.\n Please check if you entered up to date gene symbols, OR\nyou may need to modify paramters and try again");	            	 
		}
	}
}
