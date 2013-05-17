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
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyRow;
import org.cytoscape.model.CyTable;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskMonitor;
import org.ncibi.cytoscape.mimi.enums.NodeType;


/**
 * 
 *
 * @author Jing Gao
 */
public class CollapseNodeTask extends AbstractTask{		
	private CyNode node;
	private CyNetwork network;
	
	public CollapseNodeTask(CyNode node, CyNetwork network) {
		this.node = node;
		this.network = network;
	}

	@Override
	public void run(TaskMonitor taskMonitor) throws Exception {
		CyTable hiddenNodeTable = network.getTable(CyNode.class, CyNetwork.HIDDEN_ATTRS);
		
    	List<CyNode> nodeList = network.getNodeList();
    	List<CyEdge> edgeList = network.getEdgeList();
    	
    	List<Integer> nodeIDList=hiddenNodeTable.getRow(node.getSUID()).getList("NodeIDList", Integer.class);
    	List<Integer> edgeIDList=hiddenNodeTable.getRow(node.getSUID()).getList("EdgeIDList", Integer.class);
    	
		//remove expanded edges from network		
		Iterator<CyEdge> edgeIt=edgeList.iterator();
		while (edgeIt.hasNext()){
			CyEdge theedge=(CyEdge)edgeIt.next();
			Iterator<Integer> edgeIDIt=edgeIDList.iterator();
			while (edgeIDIt.hasNext()){
				Integer edgeID = (Integer)edgeIDIt.next();
				CyRow row = network.getRow(theedge);
				if (row.get("ID", Integer.class).equals(edgeID)){
					network.removeEdges(Collections.singleton(theedge));
    				network.getDefaultEdgeTable().deleteRows(Collections.singleton(theedge.getSUID()));
					break;
				}
			}
		}
		
		//remove expanded nodes from network
    	Iterator<CyNode> nodeIt=nodeList.iterator();     	
    	while (nodeIt.hasNext()){
    		CyNode thenode=(CyNode)nodeIt.next();
    		Iterator<Integer> nodeIDIt =nodeIDList.iterator();
    		while (nodeIDIt.hasNext()){
    			Integer nodeID =(Integer)nodeIDIt.next();
    			//System.out.println("node id "+nodeID);
    			if (network.getRow(thenode).get("ID", Integer.class).equals(nodeID)){
    				//System.out.println("remove the node");
    				network.removeNodes(Collections.singleton(thenode));//only remove node from current network
    				network.getDefaultNodeTable().deleteRows(Collections.singleton(thenode.getSUID()));
    				break;
    				//System.out.println("add node to network "+node.getIdentifier());
    			}
    		}
    	}
		//change clciked node color to be origninal
		//System.out.println("collapse node "+node.getIdentifier()+" distance  is "+nodeAttributes.getAttribute(node.getIdentifier(), "Network Distance"));
		if (network.getRow(node).get("Network Distance", String.class).equals("0"))
			network.getRow(node).set("Node Color", NodeType.SEEDNODE.ordinal());
		else if (network.getRow(node).get("Network Distance", String.class).equals("-1"))
			network.getRow(node).set("Node Color", NodeType.EXPANDNEIGHBOR.ordinal());
		else network.getRow(node).set("Node Color", NodeType.SEEDNEIGHBOR.ordinal());
	}

}
