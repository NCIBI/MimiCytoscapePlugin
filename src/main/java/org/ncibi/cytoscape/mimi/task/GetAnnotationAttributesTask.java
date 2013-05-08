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

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.cytoscape.io.util.StreamUtil;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyRow;
import org.cytoscape.model.CyTable;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskMonitor;
import org.ncibi.cytoscape.mimi.MiMIURL;

/** 
 * @author jinggao/UserAnnoAttr
 * @date Jun 24, 2008
 */
public class GetAnnotationAttributesTask extends AbstractTask {
	private BufferedReader rd; 
	
	private Collection<CyNode> nodes;
	private Collection<CyEdge> edges;
	private CyNetwork network;
	private StreamUtil streamUtil;
	private CyTable nodeTable;
	private CyTable edgeTable;
	
	public GetAnnotationAttributesTask(Collection<CyNode> nodes, Collection<CyEdge> edges, CyNetwork network, StreamUtil streamUtil) {
		this.nodes = nodes;
		this.edges = edges;
		this.network = network;
		this.streamUtil = streamUtil;
		
		this.nodeTable = network.getDefaultNodeTable();
		this.edgeTable = network.getDefaultEdgeTable();
	}
	

	@Override
	public void run(TaskMonitor taskMonitor) throws Exception {
		String line;
		
		String geneIDs = "";
		Map<String,CyRow> nodeRowMap = new HashMap<String,CyRow>();
		for(CyNode node: nodes) {
			String id = network.getRow(node).get(CyNetwork.NAME, String.class);
			geneIDs += id + " ";
			nodeRowMap.put(id, network.getRow(node));
		}
		geneIDs = geneIDs.trim();
		if(nodeTable.getColumn("Gene.userAnnot") == null)
			nodeTable.createColumn("Gene.userAnnot",Boolean.class, true, false);
		if (!geneIDs.equals("")){
			//get node userannotationattribute
			doQuery(1,geneIDs);
			line="";
			
			while ((line = rd.readLine()) != null){
				CyRow row = nodeRowMap.get(line);
				row.set("Gene.userAnnot", true);
				nodeRowMap.remove(line);
			}		
			rd.close();
			for(CyRow row: nodeRowMap.values()) {
				row.set("Gene.userAnnot", false);
			}
		}
		
		String edgeIDs = "";
		Map<String,CyRow> edgeRowMap = new HashMap<String,CyRow>();
		for(CyEdge edge: edges) {
			String id = network.getRow(edge).get(CyNetwork.NAME, String.class);
			edgeIDs += "," + id;
			edgeRowMap.put(id, network.getRow(edge));
		}
		edgeIDs = edgeIDs.substring(1).trim();
		if(edgeTable.getColumn("Interaction.userAnnot") == null)
			edgeTable.createColumn("Interaction.userAnnot",Boolean.class, true, false);
		if (!edgeIDs.equals("")){
			//get edge user annotation attribute
			doQuery(0,edgeIDs);
			line="";
			while ((line = rd.readLine()) != null){
				CyRow row = edgeRowMap.get(line);
				row.set("Interaction.userAnnot", true);
				edgeRowMap.remove(line);
			}			
			rd.close();
			for(CyRow row: edgeRowMap.values()) {
				row.set("Interaction.userAnnot", false);
			}
		}
	}
	
	private void doQuery (int attrType, String IDs) throws Exception{
		String query;
		query="TYPE="+attrType+"&IDS="+IDs;
		//System.out.println("Searching user annotation attribute ");
		//System.out.println("url is "+QueryMiMI.urlStr1);
		//System.out.println("query is "+query);

		URLConnection conn = streamUtil.getURLConnection(new URL(MiMIURL.CHECKUSERANNOATTR));
		conn.setDoOutput(true);	
		OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
		wr.write(query);
		wr.flush();			
		// Get the response	        
		rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));	 			
		wr.close();	
	}
	
}
