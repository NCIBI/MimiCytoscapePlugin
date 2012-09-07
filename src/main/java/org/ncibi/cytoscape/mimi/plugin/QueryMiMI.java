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
 
package org.ncibi.cytoscape.mimi.plugin;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.cytoscape.io.util.StreamUtil;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyIdentifiable;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyRow;
import org.cytoscape.model.CyTable;
import org.cytoscape.view.model.CyNetworkView;
import org.ncibi.cytoscape.mimi.parser.AttributesByIDs;
import org.ncibi.cytoscape.mimi.parser.UserAnnoAttr;
import org.ncibi.cytoscape.mimi.util.URLConnect;
import org.ncibi.cytoscape.mimi.visual.LayoutUtility;
import org.ncibi.cytoscape.mimi.visual.MiMIVS;



/**
 * 
 * @author Jing Gao
 * @author Alex Ade
 */

public class QueryMiMI {
	final public static int QUERY_BY_NAME 				= 0;
	final public static int QUERY_BY_ID   				= 1;
	final public static int QUERY_BY_INTERACTION   		= 2;
	final public static int QUERY_BY_FILE 				= 3;
	final public static int QUERY_BY_EXPAND             = 4;
	final public static int QUERY_BY_REMOTEFILE			= 5;
	//MEAD=5
	final public static int SEEDNODE					=0;
	final public static int SEEDNEIGHBOR				=1;
	final public static int EXPANDNODE					=2;
	final public static int EXPANDNEIGHBOR				=3;	

	//public static String ORGANISM ="9606";	
	public static String urlStr="http://mimiplugin.ncibi.org/dbaccess/3.2/getInteraction.php";
	public static String urlStr1="http://mimiplugin.ncibi.org/dbaccess/3.2/getAttributes.php";
	public static String URL4USERANNOATTR="http://mimiplugin.ncibi.org/dbaccess/3.2/checkUserAnnotAttribute.php";
	public static BufferedReader rd;	
	public static HashMap<String, String> dataSources;
	public static HashMap<String, String> orgnismHM ;
	public static HashMap<String, String> verorgnismHM ;
	public static HashMap<String, String> sourceData;
	public static HashMap<String, String> moleculeType;	
	public static HashMap<String, String>intID2geneID;	
	public static String geneIDList;
	private static String keyword;
	private static String organism;
	private static String moleType;
	private static String dataSource;	
	private static String steps;
	private static String filter;
	private static int condition;	
	public  static String interactionIDList;
	public  static String edgeIDStrList;
	public  static ArrayList<CyNode> nodeList;
	public  static ArrayList<CyEdge> edgeList; 
	public  static ArrayList<String> nodeIDList;
	public  static ArrayList<String> edgeIDList;
	protected static URLConnect urlconn;
	
	
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
		
		
		verorgnismHM = new HashMap<String, String>();		
		verorgnismHM.put("6239","Caenorhabditis elegans");
		verorgnismHM.put("7227","Drosophila melanogaster");
		verorgnismHM.put("562","Escherichia coli");	
		verorgnismHM.put("9606","Homo sapiens");
		verorgnismHM.put("10090","Mus musculus");
		verorgnismHM.put("10116","Rattus norvegicus");
		verorgnismHM.put("4932","Saccharomyces cerevisiae");	
		verorgnismHM.put("3702","Arabidopsis thaliana");
		
		
		moleculeType=new HashMap<String, String>();				
		moleculeType.put("All Molecule Types".toUpperCase(),"0");
		moleculeType.put("protein".toUpperCase(),"1");
		moleculeType.put("mRNA".toUpperCase(),"2");
		moleculeType.put("gene".toUpperCase(),"3");
		moleculeType.put("pseudo".toUpperCase(),"4");
		moleculeType.put("chemical".toUpperCase(),"5");
		moleculeType.put("DNA".toUpperCase(),"6");
	}
	
	public static void init() {
		interactionIDList=" ";
		edgeIDStrList=",";
		geneIDList=" ";
		intID2geneID = new HashMap<String, String>();
        nodeList = new ArrayList<CyNode>();
 	    edgeList = new ArrayList<CyEdge>();
	}

	public static Object query(int inputtype, String inputStr, CyNetworkFactory cyNetworkFactory, CyNetworkManager cyNetworkManager, JFrame frame, StreamUtil streamUtil) {
		//System.out.println("inputtypeis "+inputtype+ "inputstring is "+inputStr);
        //query mimi rdb and create network	
		
		//String  MaxDistance="0";
        //set mimi visual style
       // MiMIVS.SetMiMIVS();
        try{
        	init();
        	CyNetwork network = cyNetworkFactory.createNetwork();
     		String [] inputParameters = inputStr.split("/////");	
     		String geneList= (inputParameters[0].length()<=15) ? inputParameters[0]:inputParameters[0].substring(0, 15)+"...";
        	//System.out.println("start query mimi geneidlist["+geneIDList+"]");				   
        	doQuery(inputtype,inputStr, streamUtil);
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
        		if (!geneIDList.contains(" "+res[1]+" "))
        			geneIDList += res[1]+" ";

        		//MaxDistance = (MaxDistance.compareTo(res[2])<0)? res[2]:MaxDistance ; 
        		//MaxDistance = (MaxDistance.compareTo(res[5])<0)? res[5]:MaxDistance ;
        		if(!nodeList.contains(sourceNode)) {
	        		nodeList.add(sourceNode);
	        		network.getRow(sourceNode).set("Gene.name", res[0]);
	        		//set default (false) user annotation attribute to node
	        		network.getRow(sourceNode).set("Gene.userAnnot", false);
	        		//add step attribute if it does not exist
	        		if(!network.getRow(sourceNode).get("Network Distance", String.class).equals("0")) {
	        			network.getRow(sourceNode).set("Network Distance", res[2]);
	        			hiddenNodeTable.getRow(sourceNode.getSUID()).set("Node Color", SEEDNEIGHBOR);
	        		}
	        		else
	        			hiddenNodeTable.getRow(sourceNode.getSUID()).set("Node Color", SEEDNODE);
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
        		
        		if (!geneIDList.contains(" "+res[4]+" "))
        			geneIDList += res[4]+" "; 
        		
        		if(!nodeList.contains(targetNode)) {
	        		nodeList.add(targetNode);
	        		//add step attribute if it does not exist
	        		if(!network.getRow(targetNode).get("Network Distance", String.class).equals("0")) {
	        			network.getRow(targetNode).set("Network Distance", res[5]);
	        			hiddenNodeTable.getRow(targetNode.getSUID()).set("Node Color", SEEDNEIGHBOR);
	        		}
	        		else
	        			hiddenNodeTable.getRow(targetNode.getSUID()).set("Node Color", SEEDNODE);
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
        		
        		if (!edgeIDStrList.contains(","+network.getRow(edge).get(CyNetwork.NAME, String.class)+","))	   				 
        			edgeIDStrList += network.getRow(edge).get(CyNetwork.NAME, String.class)+",";
        		if (!interactionIDList.contains(" "+res[6]+" "))	   				 
        			interactionIDList += res[6]+" ";
        		intID2geneID.put(res[6],network.getRow(edge).get(CyNetwork.NAME, String.class));
        		//System.out.println ("edge identifier["+edge.getIdentifier());
        		if(!edgeList.contains(edge))
        			edgeList.add(edge);

        	}
        	//System.out.println("delete total return no is:"+deli);
        	rd.close();
        	//System.out.println("node list size is:"+nodeList.size());
        	//System.out.println("edge list size is:"+edgeList.size());
        	if (!nodeList.isEmpty()){
        		//get all molecule and ineraction attributes using returned gene IDs and interaction IDs	from MiMI database
        		AttributesByIDs.GetAttribute(geneIDList.trim(),interactionIDList.trim());
        		//query MiMIAnnotation database to set UserAnnotation attriubte to node/edge
        		//System.out.println("gene list is ["+geneIDList+"]\n\n\n\n");
        		//System.out.println("edge list is ["+edgeIDStrList+"]\n\n\n\n");
        		new UserAnnoAttr().getAttribute(geneIDList.trim(),edgeIDStrList.trim());
        		//create network    
        		if (inputtype==QUERY_BY_ID){
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
        	} else {
        		QueryMiMIWrapper.popupTimer.stop();
        		QueryMiMIWrapper.dialog.setVisible(false);
        		QueryMiMIWrapper.dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        		QueryMiMIWrapper.dialog.pack();
        		JOptionPane.showMessageDialog(frame,"No result returned for this query.\n Please check if you entered up to date gene symbols, OR\nyou may need to modify paramters and try again");	            	 
        	}
        }
        catch (Exception e){
        	return e;
        } 
	    
	    return new Boolean(true);
	}
	
	public static Object query(String term, CyNode node, CyNetwork network, JFrame frame, StreamUtil streamUtil) {
		//System.out.println("inputtypeis "+inputtype+ "inputstring is "+inputStr);
        //query mimi rdb and create network	

        try{
        	init();
        	CyTable hiddenNodeTable = network.getTable(CyNode.class, CyNetwork.HIDDEN_ATTRS);
        	//System.out.println("start query mimi geneidlist["+geneIDList+"]");				   
        	doQuery(QueryMiMI.QUERY_BY_EXPAND,term, streamUtil);
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
        				hiddenNodeTable.getRow(sourceNode.getSUID()).set("Node Color", EXPANDNEIGHBOR);
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
        				hiddenNodeTable.getRow(targetNode.getSUID()).set("Node Color", EXPANDNEIGHBOR);
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
        		hiddenNodeTable.getRow(node.getSUID()).set("Node Color", EXPANDNODE);
        		hiddenNodeTable.getRow(node.getSUID()).set("NodeIDList", nodeIDList);
        		hiddenNodeTable.getRow(node.getSUID()).set("EdgeIDList", edgeIDList);

        	}
        	else {
        		QueryMiMIWrapper.popupTimer.stop();
        		QueryMiMIWrapper.dialog.setVisible(false);
        		QueryMiMIWrapper.dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        		QueryMiMIWrapper.dialog.pack();
        		JOptionPane.showMessageDialog(frame,"No Expanded network found for this node");	            	 
        	}
        }
	    catch (Exception e){
	    	return e;
        } 
	    
	    return new Boolean(true);
	}
	    
               
	           
	private static void doQuery(int inputtype, String term, StreamUtil streamUtil){
		int continueQuery=1;
		String inputgene="";
		keyword=" ";
		//System.out.println(inputStr);
		if (inputtype==QUERY_BY_NAME || inputtype==QUERY_BY_FILE || inputtype==QUERY_BY_EXPAND || inputtype==QUERY_BY_REMOTEFILE){		
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
			
			try {
				
				//get keyword
				//for remote file
				if (inputtype==QUERY_BY_REMOTEFILE && inputgene.startsWith("http")){					
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
				
				
				URLConnection conn = streamUtil.getURLConnection(new URL(urlStr));
				conn.setUseCaches(false);
				conn.setDoOutput(true);
				switch (inputtype){
				case QUERY_BY_NAME: case QUERY_BY_FILE: case QUERY_BY_EXPAND:  case QUERY_BY_REMOTEFILE:
					keyword=keyword.replace(',', ' ');
					keyword=keyword.replace(';',' ');
					//System.out.println(keyword);
					keyword=URLEncoder.encode(keyword,"UTF-8");								
					query="ID="+keyword+"&TYPE="+QUERY_BY_NAME+"&ORGANISMID="+organism+"&MOLECULETYPE="+moleType+"&DATASOURCE="+dataSource+"&STEPS="+steps+"&CONDITION="+condition+"&FILTER="+filter;
					break;
				case QUERY_BY_ID:
					//System.out.println("input string is "+inputStr);					
					query="ID="+term.trim()+"&TYPE="+QUERY_BY_ID+"&ORGANISMID=null&MOLECULETYPE=null&DATASOURCE=null&STEPS=null&CONDITION=null&FILTER=null";
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
			catch (Exception e){
				String name = e.getClass().getName();
				String msg = e.getMessage();
				StackTraceElement[] ste = e.getStackTrace();
				String stack = "";
				for (int i = 0; i < ste.length; i++) {
					stack += ste[i].toString() + "<br>";
				}
				//System.out.println("name is "+name); //need to return to mimiquery wrapper
				//System.out.println("msg is "+msg);
				//System.out.println("stack is "+stack);
			}
		}
	
	}	

  }
