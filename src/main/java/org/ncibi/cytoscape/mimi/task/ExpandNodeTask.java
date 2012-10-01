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
 
package org.ncibi.cytoscape.mimi.task;
import java.util.ArrayList;
import java.util.Collection;

import org.cytoscape.io.util.StreamUtil;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyIdentifiable;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyRow;
import org.cytoscape.model.CyTable;
import org.cytoscape.work.TaskMonitor;
import org.ncibi.cytoscape.mimi.attributes.AttributesByIDs;
import org.ncibi.cytoscape.mimi.attributes.UserAnnoAttr;
import org.ncibi.cytoscape.mimi.enums.NodeType;
import org.ncibi.cytoscape.mimi.enums.QueryType;


/**
 * 
 *
 * @author Jing Gao
 */
public class ExpandNodeTask extends AbstractMiMIQueryTask{
	
	private CyNode node;
	private CyNetwork network;
	private StreamUtil streamUtil;
	
	public ExpandNodeTask(CyNode node, CyNetwork network, StreamUtil streamUtil) {
		this.node = node;
		this.network = network;
		this.streamUtil = streamUtil;
	}

	@Override
	public void run(TaskMonitor taskMonitor) throws Exception {
		//System.out.println("inputtypeis "+inputtype+ "inputstring is "+inputStr);
		//query mimi rdb and create network	
		String term = network.getRow(node).get("Gene.name", String.class);
		CyTable hiddenNodeTable = network.getTable(CyNode.class, CyNetwork.HIDDEN_ATTRS);
		//System.out.println("start query mimi geneidlist["+geneIDList+"]");				   
		doQuery(QueryType.QUERY_BY_EXPAND,term, streamUtil, taskMonitor);
		nodeIDList =new ArrayList<String>();
		edgeIDList =new ArrayList<String>();
		//System.out.println("query by expand"+QUERY_BY_EXPAND);
		String line;
		//System.out.println("clicked node id "+clickNodeId);
		while ((line = rd.readLine()) != null) {
			//Process line..
			//System.out.println("get line is ["+line+"]");
			String [] res=line.split("/////");
			CyNode sourceNode;
			Collection<CyRow> sourceNodeRows = network.getDefaultNodeTable().getMatchingRows(CyNetwork.NAME, res[1]);
			if(sourceNodeRows.isEmpty()) {
				sourceNode = network.addNode();
				network.getRow(sourceNode).set(CyNetwork.NAME, res[1]);
				if (!geneIDList.contains(res[1]+" ")){
					nodeList.add(sourceNode);
					geneIDList += res[1]+" ";	
					nodeIDList.add(res[1]);
					network.getRow(sourceNode).set("Gene.name", res[0]);
					//add step attribute 	
					//if (!nodeAttributes.hasAttribute(sourceNode.getIdentifier(),"Network Distance"))
					network.getRow(sourceNode).set("Network Distance", "-1");
					network.getRow(sourceNode).set("Gene.userAnnot", false);
					hiddenNodeTable.getRow(sourceNode.getSUID()).set("Node Color", NodeType.EXPANDNEIGHBOR);
				}
			}
			else {
				CyRow sourceNodeRow = sourceNodeRows.iterator().next();
				sourceNode=network.getNode(sourceNodeRow.get(CyIdentifiable.SUID, Long.class));
			}
			//		            	    CyNode sourceNode=Cytoscape.getCyNode(res[1], true); 
			//		            	    CyNode targetNode=Cytoscape.getCyNode(res[4], true); 

			CyNode targetNode;
			Collection<CyRow> targetNodeRows = network.getDefaultNodeTable().getMatchingRows(CyNetwork.NAME, res[4]);
			if(targetNodeRows.isEmpty()) {
				targetNode = network.addNode();
				network.getRow(targetNode).set(CyNetwork.NAME, res[4]);
				if (!geneIDList.contains(res[4]+" ")){
					nodeList.add(targetNode);
					geneIDList += res[4]+" ";
					nodeIDList.add(res[4]);
					network.getRow(targetNode).set("Gene.name", res[3]);
					//if (!nodeAttributes.hasAttribute(targetNode.getIdentifier(),"Network Distance"))	
					network.getRow(targetNode).set("Network Distance", "-1");
					network.getRow(targetNode).set("Gene.userAnnot", false);
					hiddenNodeTable.getRow(targetNode.getSUID()).set("Node Color", NodeType.EXPANDNEIGHBOR);
				}
			}
			else {
				CyRow targetNodeRow = targetNodeRows.iterator().next();
				targetNode=network.getNode(targetNodeRow.get(CyIdentifiable.SUID, Long.class));
			}


			if (!network.containsEdge(sourceNode,targetNode) && !network.containsEdge(targetNode, sourceNode) && !interactionIDList.contains(res[6]+" ")){
				CyEdge edge = network.addEdge(sourceNode, targetNode, true);
				String name = network.getRow(sourceNode).get(CyNetwork.NAME, String.class) + 
						" ( ) " + network.getRow(targetNode).get(CyNetwork.NAME, String.class);
				network.getRow(edge).set(CyEdge.INTERACTION, " ");
				network.getRow(edge).set(CyNetwork.NAME, name);
				edgeList.add(edge);
				interactionIDList += res[6]+" ";
				edgeIDList.add(name);
				network.getRow(edge).set("Interaction.geneName", "("+res[0]+" , "+res[3]+")");
				network.getRow(edge).set("Interaction.userAnnot", false);
				network.getRow(edge).set("Interaction.id", res[6]);
				if (!edgeIDStrList.contains(","+name+","))	 //modified on June 24  				 
					edgeIDStrList += name+",";
				intID2geneID.put(res[6],name);
			}

		}
		rd.close();
		if (!nodeList.isEmpty() && !edgeList.isEmpty()){
			//System.out.println("node list and edge list is not empty");
			//get all molecule and interaction attributes using returned gene IDs and interaction IDs	
			AttributesByIDs.GetAttribute(geneIDList,interactionIDList);
			new UserAnnoAttr().getAttribute(geneIDList.trim(),edgeIDStrList.trim());
			//set node color attributes for expand node. set nodelist and edge list as expand node attributes for collapsing expanded network using 
			hiddenNodeTable.getRow(node.getSUID()).set("Node Color", NodeType.EXPANDNODE);
			hiddenNodeTable.getRow(node.getSUID()).set("NodeIDList", nodeIDList);
			hiddenNodeTable.getRow(node.getSUID()).set("EdgeIDList", edgeIDList);
			
		}
		else {
			throw new Exception("No Expanded network found for this node");	            	 
		}
	}
}
