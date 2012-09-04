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
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.cytoscape.io.util.StreamUtil;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskMonitor;
import org.ncibi.cytoscape.mimi.plugin.MiMIPlugin;
import org.ncibi.cytoscape.mimi.plugin.QueryMiMIWrapper;


/**
 * 
 *
 * @author Jing Gao
 */
public class ExpandNodeTask extends AbstractTask{
	
	private CyNode node;
	private CyNetwork network;
	private JFrame frame;
	private StreamUtil streamUtil;
	
	public ExpandNodeTask(CyNode node, CyNetwork network, JFrame frame, StreamUtil streamUtil) {
		this.node = node;
		this.network = network;
		this.frame = frame;
		this.streamUtil = streamUtil;
	}
	

	@Override
	public void run(TaskMonitor taskMonitor) throws Exception {
		String name = network.getRow(node).get(CyNetwork.NAME, String.class);
		String taxid = network.getRow(node).get("Gene.taxid", String.class);
		String molType = network.getRow(network).get("Molecule Type", String.class, "All Molecule Types");
		String dataSource = network.getRow(network).get("Data Source", String.class, "All Data Sources");
		String urlstr =MiMIPlugin.PRECOMPUTEEXPAND+"?ID="+name+"&ORGANISMID="+taxid+"&MOLTYPE="+URLEncoder.encode(molType,"UTF-8")+"&DATASOURCE="+URLEncoder.encode(dataSource,"UTF-8");
		URL url = new URL(urlstr);
		URLConnection conn = streamUtil.getURLConnection(url) ;
		conn.setUseCaches(false);
		// Get result        
		BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		String line=rd.readLine();
		//System.out.println("Node number is "+line);
		rd.close();							

		if(line != null ){
			int nodeNo =Integer.parseInt(line);
			String inputStr = network.getRow(node).get("Gene.name", String.class)+"/////"+network.getRow(node).get("Gene.organism", String.class)+"/////"+molType+"/////"+dataSource+"/////1. Query genes + nearest neighbors";
			if (nodeNo<=30){
				new QueryMiMIWrapper(inputStr, node, network, frame, streamUtil);
			}
			if (nodeNo>30){
				int decision=JOptionPane.showConfirmDialog(frame, "You will expand network with "+nodeNo +" nodes, continue?");
				if (decision==0){
					new QueryMiMIWrapper(inputStr, node, network, frame, streamUtil);	
				}
			}
		}
	}
}
