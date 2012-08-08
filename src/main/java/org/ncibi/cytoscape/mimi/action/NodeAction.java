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



import giny.view.EdgeView;
import giny.view.NodeView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;

import org.ncibi.cytoscape.mimi.plugin.QueryMiMI;
import org.ncibi.cytoscape.mimi.visual.LayoutUtility;

import cytoscape.CyEdge;
import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;
import cytoscape.data.Semantics;
import cytoscape.view.CyNetworkView;
import cytoscape.visual.VisualMappingManager;

/**
 * 
 *
 * @author Jing Gao
 */

public class NodeAction {

	public NodeAction(){	
		
	}
	
    public static Object querySelectedNodeDetails(CyNode cynode){
    	CyNetwork currentNetwork=Cytoscape.getCurrentNetwork();
		CyNetworkView currentNetworkview=Cytoscape.getNetworkView(currentNetwork.getIdentifier()); 
		VisualMappingManager manageView=new VisualMappingManager(currentNetworkview);
		String cynodeIdentifier= cynode.getIdentifier();			
		CyAttributes nodeAttributes = Cytoscape.getNodeAttributes();
    	if (cynodeIdentifier!=null && !nodeAttributes.hasAttribute(cynodeIdentifier,"Visited" )){
    		try {
    			URL url = new URL(QueryMiMI.urlStr);
    			URLConnection conn = url.openConnection();
    			conn.setDoOutput(true);
    			String query="ID="+cynodeIdentifier+"&TYPE=1&ORGANISMID=&MOLECULETYPE=&DATASOURCE=";
    			OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
    			wr.write(query);
    			wr.flush();	 		
    			// Get the response	        
    			BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
    			String line;
    			while ((line = rd.readLine()) != null) {
    					// Process line..
   				 	//System.out.println(line);
    				String [] res=line.split("/////");	   				 				
   				 	//source node
    				CyNode sourceNode=Cytoscape.getCyNode(res[1], true);   
				 	currentNetwork.addNode(sourceNode);  			    
				 	nodeAttributes.setAttribute(sourceNode.getIdentifier(),"Gene.name", res[0]);
    				if (res[1].equals(cynodeIdentifier)){   				 			   				 		 	                          
   				 		//add attribute expended to set node color   				
   				 		nodeAttributes.setAttribute(sourceNode.getIdentifier(),"Gene.expanded", "yes");  
   				 		//mark visited to source node
                        nodeAttributes.setAttribute(sourceNode.getIdentifier(),"Gene.visited", "yes"); 
    				}
    				else {
    					//add attribute expended to set node color   				
   				 		nodeAttributes.setAttribute(sourceNode.getIdentifier(),"Gene.expanded", "no");  
   				 		//mark visited to source node
                        nodeAttributes.setAttribute(sourceNode.getIdentifier(),"Gene.visited", "no"); 
    				}
   				 	//target node
   				 	CyNode targetNode=Cytoscape.getCyNode(res[4], true);
   				 	currentNetwork.addNode(targetNode);
   				 	nodeAttributes.setAttribute(targetNode.getIdentifier(),"Gene.name", res[3]);
   				 	nodeAttributes.setAttribute(targetNode.getIdentifier(),"Gene.expanded", "no");
   				 	//edge
   				 	CyEdge edge=Cytoscape.getCyEdge(sourceNode, targetNode, Semantics.INTERACTION, " ", true);              	    
   				 	currentNetwork.addEdge(edge);
   				 	NodeView targerNodeView=Cytoscape.getCurrentNetworkView().addNodeView(targetNode.getRootGraphIndex()); 			    		  
   				 	EdgeView edgeView=Cytoscape.getCurrentNetworkView().addEdgeView(edge.getRootGraphIndex());
   				 	manageView.vizmapNode(targerNodeView, currentNetworkview);
   				 	manageView.vizmapEdge(edgeView, currentNetworkview);  
   			
    			}            
    			wr.close();	
    			rd.close();
    		
    			//refresh attribute browser
    			Cytoscape.firePropertyChange(Cytoscape.ATTRIBUTES_CHANGED, null, null);                       
    			//specify layout
    			LayoutUtility.doLayout();                     
    			Cytoscape.getCurrentNetworkView().fitContent();  
    		}
    		catch (Exception e){
	        	return e;
    		} 
    	}//end of first if 
    	
    	//get all molecule and ineraction attributes
        //parser.Attributs.GetAttribute(keyword,organism,moleType,dataSource,steps,condition);
    	return new Boolean(true);
    }
    
}


