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
 
package org.ncibi.cytoscape.mimi.action;
import giny.view.NodeView;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.JOptionPane;

import org.ncibi.cytoscape.mimi.plugin.MiMIPlugin;
import org.ncibi.cytoscape.mimi.plugin.QueryMiMI;
import org.ncibi.cytoscape.mimi.plugin.QueryMiMIWrapper;
import org.ncibi.cytoscape.mimi.visual.LayoutUtility;

import cytoscape.CyEdge;
import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;
import cytoscape.util.ProxyHandler;
import cytoscape.view.CyNetworkView;
import ding.view.DGraphView;


/**
 * 
 *
 * @author Jing Gao
 */
public class ListenMouse implements MouseListener{		
	public static String NodeID; 
	//private ArrayList<CyNode> nodeList;
	public ListenMouse(){
		//System.out.println("enter mouse listener");
		CyNetworkView view=Cytoscape.getCurrentNetworkView();		
        ((DGraphView) view).getCanvas().addMouseListener(this);          
	}	
	
	public void mouseClicked(MouseEvent e) {		
		long curClick=e.getWhen();			
		if ((e.getClickCount()==2) && (org.ncibi.cytoscape.mimi.plugin.MiMIPlugin.lastClick !=curClick)){
			 org.ncibi.cytoscape.mimi.plugin.MiMIPlugin.lastClick=curClick;	
			 //get network attribute for MaxDist
			 CyNetworkView view=Cytoscape.getCurrentNetworkView();	
			 String ntwkID = ((CyNetwork)view.getNetwork()).getIdentifier();
			 CyAttributes ntwkattributes=Cytoscape.getNetworkAttributes();
			 //String ntwkMaxDist= ntwkattributes.getStringAttribute(ntwkID,"MaxDist");
			 //get node attribute and node id 
			 NodeView nv =((DGraphView) view).getPickedNodeView(e.getPoint ()); 
			 CyAttributes nodeattributes=Cytoscape.getNodeAttributes();  
			 			 
			 //expand network
			 if (nv!=null){				
				 CyNode node =(CyNode) nv.getNode();
				 String nodeID = node.getIdentifier();
				 NodeID=nodeID;
//				 System.out.println(nodeattributes.getStringAttribute(nodeID, "Gene.name")+"is double clicked");
				 //pre compute number of expand neighbor nodes and decide if do expand 
				 String taxid = nodeattributes.getStringAttribute(NodeID, "Gene.taxid");
				 String molType = "All Molecule Types";
				 if (ntwkattributes.hasAttribute(ntwkID, "Molecule Type"))
					 molType =ntwkattributes.getStringAttribute(ntwkID, "Molecule Type");
				 String dataSource = "All Data Sources";
				 if (ntwkattributes.hasAttribute(ntwkID, "Data Source"))
					 dataSource =ntwkattributes.getStringAttribute(ntwkID, "Data Source");
				 String dplymode="1";
				 if (ntwkattributes.hasAttribute(ntwkID, "Displays for results"))
					 dplymode=ntwkattributes.getStringAttribute(ntwkID, "Displays for results").substring(0, 1);
//				 System.out.println("display mode is ["+dplymode+"]");
				 try {	
					 	String urlstr =MiMIPlugin.PRECOMPUTEEXPAND+"?ID="+NodeID+"&ORGANISMID="+taxid+"&MOLTYPE="+URLEncoder.encode(molType,"UTF-8")+"&DATASOURCE="+URLEncoder.encode(dataSource,"UTF-8");
					 	URL url = new URL(urlstr);
						Proxy CytoProxyHandler = ProxyHandler.getProxyServer();
						URLConnection conn ;
						if (CytoProxyHandler == null) 
							conn = url.openConnection();
						else conn = url.openConnection(CytoProxyHandler);
						//URLConnection conn = url.openConnection();
						conn.setUseCaches(false);
						// Get result        
						BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
						String line=rd.readLine();
						//System.out.println("Node number is "+line);
						rd.close();							
						//if click on a SEED NEIGHBOR node or expansion neighbor or seed node in network on 2,4 mode, expand the node 
						if (nodeattributes.getIntegerAttribute(nodeID, "Node Color")==QueryMiMI.SEEDNEIGHBOR || nodeattributes.getIntegerAttribute(nodeID, "Node Color")==QueryMiMI.EXPANDNEIGHBOR
																|| ((dplymode.equals("2") || dplymode.equals("4")) && nodeattributes.getIntegerAttribute(nodeID, "Node Color") != QueryMiMI.EXPANDNODE )){
							if(line != null ){
								int nodeNo =Integer.parseInt(line);
								if (nodeNo<=30){
									String inputStr = nodeattributes.getStringAttribute(nodeID, "Gene.name")+"/////"+nodeattributes.getStringAttribute(nodeID, "Gene.organism")+"/////"+molType+"/////"+dataSource+"/////1. Query genes + nearest neighbors";
									new QueryMiMIWrapper(QueryMiMI.QUERY_BY_EXPAND,inputStr);
								}
								if (nodeNo>30){
									int decision=JOptionPane.showConfirmDialog(Cytoscape.getDesktop(), "You will expand network with "+nodeNo +" nodes, continue?");
									if (decision==0){
										String inputStr = nodeattributes.getStringAttribute(nodeID, "Gene.name")+"/////"+nodeattributes.getStringAttribute(nodeID, "Gene.organism")+"/////"+molType+"/////"+dataSource+"/////1. Query genes + nearest neighbors";
										new QueryMiMIWrapper(QueryMiMI.QUERY_BY_EXPAND,inputStr);	
									}
								}
							}
						}
						
						//if double click on an EXPANDED node, delete the expanded network
						if (nodeattributes.getIntegerAttribute(nodeID, "Node Color")==QueryMiMI.EXPANDNODE)
							//delete network
							collapseExpandedNetwork(node);
						
							
					}
					catch (Exception ev){
//						System.out.println(ev); //need to return to mimiquery wrapper
					}
				 
				 
			 }
		}
			 
			 
	}
	
	public void mousePressed(MouseEvent e) {		
	}
	public void mouseEntered(MouseEvent e) {		
	}
	public void mouseExited(MouseEvent e) {		
	}
	public void mouseReleased(MouseEvent e) {			
	}
	
	//collapse expanded network
    private void collapseExpandedNetwork(CyNode node){
    	CyNetwork curnetwk=Cytoscape.getCurrentNetwork();
    	CyAttributes nodeAttributes = Cytoscape.getNodeAttributes();
    	ArrayList<CyNode> nodeList=(ArrayList<CyNode>)Cytoscape.getCyNodesList();
    	ArrayList edgeList=(ArrayList)Cytoscape.getCyEdgesList();
    	ArrayList nodeIDList=(ArrayList)nodeAttributes.getListAttribute(node.getIdentifier(), "NodeIDList");
    	ArrayList edgeIDList=(ArrayList)nodeAttributes.getListAttribute(node.getIdentifier(), "EdgeIDList");
    	
    	//remove expanded nodes from network
    	Iterator nodeIt=nodeList.iterator();     	
    	while (nodeIt.hasNext()){
    		CyNode thenode=(CyNode)nodeIt.next();
    		Iterator nodeIDIt =nodeIDList.iterator();
    		while (nodeIDIt.hasNext()){
    			String nodeID =(String)nodeIDIt.next();
    			//System.out.println("node id "+nodeID);
    			if (thenode.getIdentifier().equals(nodeID)){
    				//System.out.println("remove the node");
    				curnetwk.removeNode(thenode.getRootGraphIndex(), false);//only remove node from current network
    				break;
    				//System.out.println("add node to network "+node.getIdentifier());
    			}
    		}
    	}
		//remove expanded edges from network		
		Iterator edgeIt=edgeList.iterator();
		while (edgeIt.hasNext()){
			CyEdge theedge=(CyEdge)edgeIt.next();
			Iterator edgeIDIt=edgeIDList.iterator();
			while (edgeIDIt.hasNext()){
				String edgeID = (String)edgeIDIt.next();	
				if (theedge.getIdentifier().equals(edgeID)){
					curnetwk.removeEdge(theedge.getRootGraphIndex(), false);
					break;
				}
			}
		}
		//change clciked node color to be origninal
		//System.out.println("collapse node "+node.getIdentifier()+" distance  is "+nodeAttributes.getAttribute(node.getIdentifier(), "Network Distance"));
		if (nodeAttributes.getAttribute(node.getIdentifier(), "Network Distance").equals("0"))
			nodeAttributes.setAttribute(node.getIdentifier(), "Node Color",QueryMiMI.SEEDNODE );
		else if (nodeAttributes.getAttribute(node.getIdentifier(), "Network Distance").equals("-1"))
			nodeAttributes.setAttribute(node.getIdentifier(), "Node Color",QueryMiMI.EXPANDNEIGHBOR );
		else nodeAttributes.setAttribute(node.getIdentifier(), "Node Color",QueryMiMI.SEEDNEIGHBOR );
		
		//specify layout
		LayoutUtility.doLayout();
	
		//set visual style 
		CyNetworkView netView = Cytoscape.getCurrentNetworkView(); 
		netView.setVisualStyle("MiMI");
		Cytoscape.getVisualMappingManager().setNetworkView(netView); 
		Cytoscape.getVisualMappingManager().setVisualStyle("MiMI"); 
		netView.redrawGraph(false, true);	            	 
		//refresh attribute browser
		Cytoscape.firePropertyChange(Cytoscape.ATTRIBUTES_CHANGED, null, null);
    }

}
