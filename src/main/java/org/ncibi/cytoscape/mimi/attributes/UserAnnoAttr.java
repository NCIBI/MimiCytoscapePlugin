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
 
package org.ncibi.cytoscape.mimi.attributes;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;

import org.ncibi.cytoscape.mimi.task.AbstractMiMIQueryTask;

import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;
import cytoscape.util.ProxyHandler;

/** 
 * @author jinggao/UserAnnoAttr
 * @date Jun 24, 2008
 */
public class UserAnnoAttr {
	private  BufferedReader rd; 
	private  URL url;
	private  String geneIDs;
	private  String edgeIDs;
	private  String rtrngeneIDs="";
	private  String rtrnedgeIDs="";
	
	public void getAttribute (String geneIDS, String edgeIDS ){
		geneIDs= geneIDS;
		edgeIDs = edgeIDS;
		CyAttributes nodeAttr=Cytoscape.getNodeAttributes();
		CyAttributes edgeAttr=Cytoscape.getEdgeAttributes();
		//System.out.println("geneids:["+geneIDs+"]");
		//System.out.println("edgeids:["+edgeIDs+"]");
		try{
			url=new URL(AbstractMiMIQueryTask.CHECKUSERANNOATTR);	
			String line;
			if (!geneIDs.equals("")){
				//get node userannotationattribute
				doQuery(1,geneIDs);
				line="";
				
				while ((line = rd.readLine()) != null){
					//System.out.println(" gene is "+line);
					rtrngeneIDs += line+",";
					nodeAttr.setAttribute(line, "Gene.userAnnot", true); 							
			    }		
				rd.close();	
			}
			
			if (!edgeIDs.equals("")){
				//get edge user annotation attribute
				doQuery(0,edgeIDs);
				line="";
				while ((line = rd.readLine()) != null){
					//System.out.println("edge is "+line);
					rtrnedgeIDs += line +",";
					edgeAttr.setAttribute(line, "Interaction.userAnnot", true);           	
				}			
				rd.close();
			}
		}
		catch (Exception e){
			String name = e.getClass().getName();
			String msg = e.getMessage();
			StackTraceElement[] ste = e.getStackTrace();
			String stack = "";
			for (int i = 0; i < ste.length; i++) {
				stack += ste[i].toString() + "\n";
			}
			String txt = "";
			if (name != null) txt += ": " + name;	
			txt += "\n";
			if (msg != null && !msg.equals("")) txt += "\n" + msg + "\n";
			if (!stack.equals("")) txt += "\n" + stack;			
			System.out.println(txt);
		}
    }
	
   
	private  void doQuery (int attrType, String IDs ){
		String query;
		
		
			query="TYPE="+attrType+"&IDS="+IDs;
		    //System.out.println("Searching user annotation attribute ");
		    //System.out.println("url is "+QueryMiMI.urlStr1);
		    //System.out.println("query is "+query);
		
		try {			
			Proxy CytoProxyHandler = ProxyHandler.getProxyServer();
			URLConnection conn ;
			if (CytoProxyHandler == null) 
				conn = url.openConnection();
			else conn = url.openConnection(CytoProxyHandler);
			conn.setDoOutput(true);	
			OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
			wr.write(query);
			wr.flush();			
			// Get the response	        
			rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));	 			
			wr.close();	
		}
		catch (Exception e){
			String name = e.getClass().getName();
			String msg = e.getMessage();
			StackTraceElement[] ste = e.getStackTrace();
			String stack = "";
			for (int i = 0; i < ste.length; i++) {
				stack += ste[i].toString() + "\n";
			}
			String txt = "";
			if (name != null) txt += ": " + name;	
			txt += "\n";
			if (msg != null && !msg.equals("")) txt += "\n" + msg + "\n";
			if (!stack.equals("")) txt += "\n" + stack;			
			//System.out.println(txt);
		}
	}
	
	public String getGenewithPublAnnot(){
		return rtrngeneIDs;
	}
	public String getEdgewithPublAnnot(){
		return rtrnedgeIDs;
	}
	
}
