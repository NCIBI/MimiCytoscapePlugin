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
 
package org.ncibi.cytoscape.mimi;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.cytoscape.io.util.StreamUtil;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyRow;
import org.ncibi.cytoscape.mimi.ui.DetailsPanel;

/**
 * @author Jing Gao
 */
public class QueryBioNlp {
	public static DetailsPanel dp;
	
	public QueryBioNlp(CyEdge edge, CyNetwork network, JFrame frame, StreamUtil streamUtil){
		CyRow sourceRow = network.getRow(edge.getSource());
		CyRow targetRow = network.getRow(edge.getTarget());
		
		String molecule1=sourceRow.get("Gene.name", String.class);
		String molecule2=targetRow.get("Gene.name", String.class);
		String moleculeID1=sourceRow.get(CyNetwork.NAME, String.class);
		String moleculeID2=targetRow.get(CyNetwork.NAME, String.class);
		String taxID1=sourceRow.get("Gene.taxid", String.class);
		String taxID2=targetRow.get("Gene.taxid", String.class);
		if(!molecule1.equalsIgnoreCase("N/A") && !molecule2.equalsIgnoreCase("N/A")){
			
			try{
				
				molecule1=URLEncoder.encode(molecule1,"UTF-8");
				molecule2=URLEncoder.encode(molecule2,"UTF-8");		
				URL url=new URL("http://mimiplugin.ncibi.org/dbaccess/3.2/queryPubmed.php");
				String query="MOLECULE1="+molecule1+"&MOLECULE2="+molecule2+"&TAXID1="+taxID1+"&TAXID2="+taxID2;
				URLConnection conn = streamUtil.getURLConnection(url);
				conn.setUseCaches(false);
				conn.setDoOutput(true);
				OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
				wr.write(query);
				wr.flush();					
				BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
				String inputLine1;
				String ret="";
				while ((inputLine1 = rd.readLine()) != null)			
					ret +=inputLine1+"\n";			
				rd.close();	
				wr.close();
				if(!ret.equals("")){
					dp=new org.ncibi.cytoscape.mimi.ui.DetailsPanel(ret,molecule1,molecule2,moleculeID1,moleculeID2,frame);	
				}
				else {
					JOptionPane.showMessageDialog(frame,"Sorry, no information found from BioNlp for this edge");
				}	
				
			}
			catch (Exception e){
       			//System.out.println(e);
            }		
		}
		
	}

}
