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
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.cytoscape.io.util.StreamUtil;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNode;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskMonitor;
import org.ncibi.cytoscape.mimi.enums.QueryType;
import org.ncibi.cytoscape.mimi.plugin.MiMIURL;
import org.ncibi.cytoscape.mimi.util.URLConnect;



/**
 * 
 * @author Jing Gao
 * @author Alex Ade
 */

public abstract class AbstractMiMIQueryTask extends AbstractTask{
	
	protected static Map<String, String> orgnismHM;
	protected static Map<QueryType, String> rsc;
	protected BufferedReader rd;		
	
	protected String keyword;
	protected String organism;
	protected String moleType;
	protected String dataSource;	
	protected String steps;
	protected String filter;
	protected int condition;	
	
	protected ArrayList<CyNode> nodeList = new ArrayList<CyNode>();
	protected ArrayList<CyEdge> edgeList = new ArrayList<CyEdge>(); 
	protected ArrayList<String> nodeIDList;
	protected ArrayList<String> edgeIDList;
	protected String geneIDList = " ";
	protected String edgeIDStrList = ",";
	protected String interactionIDList = " ";
	protected HashMap<String, String> intID2geneID = new HashMap<String, String>();	
	
	public abstract void run(TaskMonitor arg0) throws Exception;
	
	static {
		orgnismHM = new HashMap<String, String>();
		orgnismHM.put("All Organisms","-1000");
		orgnismHM.put("Caenorhabditis elegans","6239");
		orgnismHM.put("Drosophila melanogaster","7227");
		orgnismHM.put("Escherichia coli","562");	
		orgnismHM.put("Homo sapiens","9606");
		orgnismHM.put("Mus musculus","10090");
		orgnismHM.put("Rattus norvegicus","10116");
		orgnismHM.put("Saccharomyces cerevisiae","4932");	
		orgnismHM.put("Arabidopsis thaliana","3702");
		
		
		rsc = new HashMap<QueryType, String>();
		rsc.put(QueryType.QUERY_BY_NAME, "Gene Symbol");
		rsc.put(QueryType.QUERY_BY_ID, "Gene ID");
		rsc.put(QueryType.QUERY_BY_INTERACTION, "Interaction ID");
		rsc.put(QueryType.QUERY_BY_FILE,"File");
		rsc.put(QueryType.QUERY_BY_EXPAND, "Expand Node ID");
		rsc.put(QueryType.QUERY_BY_REMOTEFILE, "Remote File");
	}
	           
	protected void doQuery(QueryType queryType, String term, StreamUtil streamUtil) throws Exception{
		int continueQuery=1;
		String inputgene="";
		keyword=" ";
		//System.out.println(inputStr);
		if (queryType==QueryType.QUERY_BY_NAME || queryType==QueryType.QUERY_BY_FILE || queryType==QueryType.QUERY_BY_EXPAND || queryType==QueryType.QUERY_BY_REMOTEFILE){		
			//System.out.println(inputStr);
			String [] inputArray=term.split("/////");			
			inputgene=inputArray[0];
			String organismKey=inputArray[1].trim();		
			organism=orgnismHM.get(inputArray[1]);		
			moleType=inputArray[2].trim();		
			dataSource =inputArray[3].trim();	
			String intLvl=inputArray[4].substring(0,1);
			if (intLvl.equalsIgnoreCase("1")){steps="1"; filter="no";}
			if (intLvl.equalsIgnoreCase("2")){steps="1"; filter="yes";}
			if (intLvl.equalsIgnoreCase("3")){steps="2"; filter="no";}
			if (intLvl.equalsIgnoreCase("4")){steps="2"; filter="yes";}


			if (!organismKey.equals("All Organisms") && !moleType.equals("All Molecule Types") && !dataSource.equals("All Data Sources"))
				condition=1;
			if (!organismKey.equals("All Organisms") && !moleType.equals("All Molecule Types") && dataSource.equals("All Data Sources"))
				condition=2;
			if (!organismKey.equals("All Organisms") && moleType.equals("All Molecule Types") && !dataSource.equals("All Data Sources"))
				condition=3;
			if (!organismKey.equals("All Organisms") && moleType.equals("All Molecule Types") && dataSource.equals("All Data Sources"))
				condition=4;
			if (organismKey.equals("All Organisms") && !moleType.equals("All Molecule Types") && !dataSource.equals("All Data Sources"))
				condition=5;
			if (organismKey.equals("All Organisms") && !moleType.equals("All Molecule Types") && dataSource.equals("All Data Sources"))
				condition=6;
			if (organismKey.equals("All Organisms") && moleType.equals("All Molecule Types") && !dataSource.equals("All Data Sources"))
				condition=7;
			if (organismKey.equals("All Organisms") && moleType.equals("All Molecule Types") && dataSource.equals("All Data Sources"))
				condition=8;
		}

		if (continueQuery==1){
			//query mimi rdb 
			String query="" ;
			//get keyword
			//for remote file
			if (queryType==QueryType.QUERY_BY_REMOTEFILE && inputgene.startsWith("http")){					
				URLConnect uc= new URLConnect();
				uc.doURLConnect(inputgene);
				String line="";
				while ((line = uc.getBrd().readLine()) != null){
					keyword += line +" ";
					//System.out.println("keyword is " +keyword);
				}
				uc.closebrd();

			}
			else keyword=inputgene;


			URLConnection conn = streamUtil.getURLConnection(new URL(MiMIURL.GETINTERACTION));
			conn.setUseCaches(false);
			conn.setDoOutput(true);
			switch (queryType){
			case QUERY_BY_NAME: case QUERY_BY_FILE: case QUERY_BY_EXPAND: case QUERY_BY_REMOTEFILE:
				keyword=keyword.replace(',', ' ');
				keyword=keyword.replace(';',' ');
				//System.out.println(keyword);
				keyword=URLEncoder.encode(keyword,"UTF-8");								
				query="ID="+keyword+"&TYPE="+QueryType.QUERY_BY_NAME.ordinal()+"&ORGANISMID="+organism+"&MOLECULETYPE="+moleType+"&DATASOURCE="+dataSource+"&STEPS="+steps+"&CONDITION="+condition+"&FILTER="+filter;
				break;
			case QUERY_BY_ID:
				//System.out.println("input string is "+inputStr);					
				query="ID="+term.trim()+"&TYPE="+QueryType.QUERY_BY_ID.ordinal()+"&ORGANISMID=null&MOLECULETYPE=null&DATASOURCE=null&STEPS=null&CONDITION=null&FILTER=null";
				break;
			default:
				break;
			}				
			//System.out.println("query is "+query) ;
			//System.out.println("url is "+urlStr);				

			OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
			wr.write(query);
			wr.flush();	   

			// Get the response	        
			rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));	 			
			wr.close();	

		}
	}
}
