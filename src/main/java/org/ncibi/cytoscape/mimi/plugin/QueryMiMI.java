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

import giny.view.EdgeView;
import giny.view.NodeView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

import org.ncibi.cytoscape.mimi.action.ListenMouse;
import org.ncibi.cytoscape.mimi.parser.AttributesByIDs;
import org.ncibi.cytoscape.mimi.parser.UserAnnoAttr;
import org.ncibi.cytoscape.mimi.util.URLConnect;
import org.ncibi.cytoscape.mimi.visual.LayoutUtility;
import org.ncibi.cytoscape.mimi.visual.MiMIVS;

import cytoscape.CyEdge;
import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;
import cytoscape.data.Semantics;
import cytoscape.util.ProxyHandler;
import cytoscape.view.CyNetworkView;
import cytoscape.visual.VisualMappingManager;
import cytoscape.visual.calculators.Calculator;



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
	private static CyNetwork cynetwork;
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
	public Calculator mimiColorCal;
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
   
	
	public  QueryMiMI() {
	}

	public  QueryMiMI(String inputStr){			
		    query(inputStr);
	}
	
	/**
	 * Parse xml, generate Molecules interaction network 
	 * @author Jing Gao
	 *
	 * @author Alex Ade
	 * Added exception handling
	 *
	 */
	public static Object query(String inputStr) {		
		return query(QueryMiMI.QUERY_BY_NAME, inputStr);
	}
	
	public static Object query(int inputtype, String inputStr) {
		//System.out.println("inputtypeis "+inputtype+ "inputstring is "+inputStr);
        //query mimi rdb and create network	
		interactionIDList=" ";
		edgeIDStrList=",";
		geneIDList=" ";
		intID2geneID = new HashMap<String, String>();
        nodeList = new ArrayList<CyNode>();
 	    edgeList = new ArrayList<CyEdge>();
		//String  MaxDistance="0";
		CyAttributes nodeAttributes = Cytoscape.getNodeAttributes();	
        CyAttributes edgeAttributes = Cytoscape.getEdgeAttributes();	// local varialble not used
        //set mimi visual style
        MiMIVS.SetMiMIVS();
		try{
				   //System.out.println("start query mimi geneidlist["+geneIDList+"]");				   
			       doQuery(inputtype,inputStr);
	               if (inputtype !=QUERY_BY_EXPAND ){//create network and node/edge attributes
	            	   //create networks
	            	   String line;              	   
	            	   //System.out.println();
	            	  int deli=0;
	            	   while ((line = rd.readLine()) != null) {
	            		   deli++;
	            		   // Process line..
	            		  // System.out.println("line is ["+line+"]");
	            		   String [] res=line.split("/////");	
	            		   if (res.length!=7)continue;
	            		   //result from SPs for mimiR2: symbol1,geneid1,step1,symbol2,geneid2,step2,intID
	            		   //source node
	            		   CyNode sourceNode=Cytoscape.getCyNode(res[1], true); 	
	            		   if (!geneIDList.contains(" "+res[1]+" "))
	            			   geneIDList += res[1]+" ";
	            		   if (!geneIDList.contains(" "+res[4]+" "))
	            			   geneIDList += res[4]+" "; 
	   				
	            		   if (!interactionIDList.contains(" "+res[6]+" "))	   				 
	            			   interactionIDList += res[6]+" ";
	   				 
	            		   //MaxDistance = (MaxDistance.compareTo(res[2])<0)? res[2]:MaxDistance ; 
	            		   //MaxDistance = (MaxDistance.compareTo(res[5])<0)? res[5]:MaxDistance ;
	            		   nodeList.add(sourceNode);
	            		   nodeAttributes.setAttribute(sourceNode.getIdentifier(),"Gene.name", res[0]);
	            		   //set default (false) user annotation attribute to node 
	            		   nodeAttributes.setAttribute(sourceNode.getIdentifier(),"Gene.userAnnot", false);
	            		   //add step attribute if it does not exist
	            		   if ( !(nodeAttributes.hasAttribute(sourceNode.getIdentifier(),"Network Distance")
	            				   && nodeAttributes.getAttribute(sourceNode.getIdentifier(),"Network Distance").equals("0")))
	            			   nodeAttributes.setAttribute(sourceNode.getIdentifier(),"Network Distance",res[2] );	            		   
	            		   //add attribute network distance and node color
	            		   if (nodeAttributes.hasAttribute(sourceNode.getIdentifier(), "Network Distance")  &&  nodeAttributes.getStringAttribute(sourceNode.getIdentifier(), "Network Distance").equalsIgnoreCase("0"))
	            			   nodeAttributes.setAttribute(sourceNode.getIdentifier(),"Node Color", SEEDNODE);
	            		 
	            		   if (nodeAttributes.hasAttribute(sourceNode.getIdentifier(), "Network Distance")  &&  !nodeAttributes.getStringAttribute(sourceNode.getIdentifier(), "Network Distance").equalsIgnoreCase("0"))
	            			   nodeAttributes.setAttribute(sourceNode.getIdentifier(),"Node Color", SEEDNEIGHBOR);
	            		
	            		   //target node   				
	            		   CyNode targetNode=Cytoscape.getCyNode(res[4], true);
	            		   nodeList.add(targetNode);
	            		   nodeAttributes.setAttribute(targetNode.getIdentifier(),"Gene.name", res[3]);
	            		   nodeAttributes.setAttribute(targetNode.getIdentifier(),"Gene.userAnnot", false);
	            		   if ( !(nodeAttributes.hasAttribute(targetNode.getIdentifier(),"Network Distance")
	            				   && nodeAttributes.getAttribute(targetNode.getIdentifier(),"Network Distance").equals("0")))
	            			   nodeAttributes.setAttribute(targetNode.getIdentifier(),"Network Distance", res[5]);
	            		  
	            		   if (nodeAttributes.hasAttribute(targetNode.getIdentifier(), "Network Distance")  &&  nodeAttributes.getStringAttribute(targetNode.getIdentifier(), "Network Distance").equalsIgnoreCase("0"))
	            			  nodeAttributes.setAttribute(targetNode.getIdentifier(),"Node Color", SEEDNODE);
	            		   
	            		   if (nodeAttributes.hasAttribute(targetNode.getIdentifier(), "Network Distance")  &&  !nodeAttributes.getStringAttribute(targetNode.getIdentifier(), "Network Distance").equalsIgnoreCase("0"))
	            			   nodeAttributes.setAttribute(targetNode.getIdentifier(),"Node Color", SEEDNEIGHBOR);
	            		  
	            		   //edge
	            		   CyEdge edge=Cytoscape.getCyEdge(sourceNode, targetNode, Semantics.INTERACTION, " ", true);  
	            		   edgeAttributes.setAttribute(edge.getIdentifier(), "Interaction.geneName", "("+res[0]+" , "+res[3]+")");
	            		   edgeAttributes.setAttribute(edge.getIdentifier(), "Interaction.userAnnot", false);
	            		   edgeAttributes.setAttribute(edge.getIdentifier(), "Interaction.id", res[6]);
	            		   if (!edgeIDStrList.contains(","+edge.getIdentifier()+","))	   				 
	            			   edgeIDStrList += edge.getIdentifier()+",";
	            		   intID2geneID.put(res[6],edge.getIdentifier());
	            		   //System.out.println ("edge identifier["+edge.getIdentifier());
	            		   edgeList.add(edge);
	   			
	            	   }
	            	   //System.out.println("delete total return no is:"+deli);
	            	   rd.close();
	            	   nodeAttributes.setUserVisible("Node Color", false);
	            	   nodeAttributes.setUserVisible("NodeIDList", false);
	            	   nodeAttributes.setUserVisible("EdgeIDList", false);
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
	            		   CyAttributes networkAttributes = Cytoscape.getNetworkAttributes();
	            		   if (inputtype==QUERY_BY_ID){
	            			   cynetwork=Cytoscape.createNetwork(nodeList, edgeList, "geneID:"+inputStr);	            			   
	            		   }
	            		   else{	
	            			   //System.out.println ("node list size "+nodeList.size());
	            			   //System.out.println("after drawing network geneidlist is "+geneIDList);
	            			   String [] inputParameters = inputStr.split("/////");	
	            			   String genelist= (inputParameters[0].length()<=15) ? inputParameters[0]:inputParameters[0].substring(0, 15)+"...";
	            			   String networkname =genelist+"|"+ inputParameters[1] + "|" +inputParameters[2] + "|" +inputParameters[3] + "|" +inputParameters[4] + "|" ;
	            			   cynetwork=Cytoscape.createNetwork(nodeList, edgeList, networkname);	            			   
	            			   //networkAttributes.setAttribute(cynetwork.getIdentifier(), "MaxDist",MaxDistance);
	            			   networkAttributes.setAttribute(cynetwork.getIdentifier(), "Input Genes",inputParameters[0].replaceAll(" ", ";"));
	            			   networkAttributes.setAttribute(cynetwork.getIdentifier(), "Organism",inputParameters[1]);
	            			   networkAttributes.setAttribute(cynetwork.getIdentifier(), "Molecule Type",inputParameters[2]);
	            			   networkAttributes.setAttribute(cynetwork.getIdentifier(), "Data Source",inputParameters[3]);
	            			   networkAttributes.setAttribute(cynetwork.getIdentifier(), "Displays for results",inputParameters[4]);	            		            	 
	            		   }
                
	            		   draw();            	 
	            	   } else {
	            		   QueryMiMIWrapper.popupTimer.stop();
	            		   QueryMiMIWrapper.dialog.setVisible(false);
	            		   QueryMiMIWrapper.dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
	            		   QueryMiMIWrapper.dialog.pack();
	            		   JOptionPane.showMessageDialog(Cytoscape.getDesktop(),"No result returned for this query.\n Please check if you entered up to date gene symbols, OR\nyou may need to modify paramters and try again");	            	 
	            	   }
				}
	               else {//query by expand
	            	   
						nodeIDList =new ArrayList<String>();
						edgeIDList =new ArrayList<String>();
						//System.out.println("query by expand"+QUERY_BY_EXPAND);
						CyNetwork curntwk = Cytoscape.getCurrentNetwork();
						CyNetworkView curntwkview=Cytoscape.getCurrentNetworkView();
						String line;
						String clickNodeId=ListenMouse.NodeID;
						//System.out.println("clicked node id "+clickNodeId);
						while ((line = rd.readLine()) != null) {
							//Process line..
		            		//System.out.println("get line is ["+line+"]");
		            	    String [] res=line.split("/////");
		            	    
		            	    CyNode sourceNode=Cytoscape.getCyNode(res[1], true); 
		            	    CyNode targetNode=Cytoscape.getCyNode(res[4], true); 
		            	    if (!curntwk.containsNode(sourceNode) && !geneIDList.contains(res[1]+" ")){
		            	    	nodeList.add(sourceNode);
		            	    	geneIDList += res[1]+" ";	
		            	    	nodeIDList.add(res[1]);
		            	    	nodeAttributes.setAttribute(sourceNode.getIdentifier(),"Gene.name", res[0]);
			            		//add step attribute 	
		            	    	//if (!nodeAttributes.hasAttribute(sourceNode.getIdentifier(),"Network Distance"))
		            	    		nodeAttributes.setAttribute(sourceNode.getIdentifier(),"Network Distance","-1" );
		            	    	nodeAttributes.setAttribute(sourceNode.getIdentifier(),"Gene.userAnnot", false);
			            		
		            	    }
		            	    if (!curntwk.containsNode(targetNode)&& !geneIDList.contains(res[4]+" ")){
		            	    	nodeList.add(targetNode);
		            	    	geneIDList += res[4]+" ";
		            	    	nodeIDList.add(res[4]);
		            	    	nodeAttributes.setAttribute(targetNode.getIdentifier(),"Gene.name", res[3]);
		            	    	//if (!nodeAttributes.hasAttribute(targetNode.getIdentifier(),"Network Distance"))	
		            	    		nodeAttributes.setAttribute(targetNode.getIdentifier(),"Network Distance", "-1");
			            		nodeAttributes.setAttribute(targetNode.getIdentifier(),"Gene.userAnnot", false);
		            	    }
		            	     
		            	    CyEdge edge1=Cytoscape.getCyEdge(sourceNode, targetNode, Semantics.INTERACTION, " ", true);  
		            	    CyEdge edge2=Cytoscape.getCyEdge(targetNode,sourceNode, Semantics.INTERACTION, " ", true);  
		            		if (!curntwk.containsEdge(edge1) && !curntwk.containsEdge(edge2) && !interactionIDList.contains(res[6]+" ")){
		            			edgeList.add(edge1);
		            			interactionIDList += res[6]+" ";
		            			edgeIDList.add(edge1.getIdentifier());
		            			edgeAttributes.setAttribute(edge1.getIdentifier(), "Interaction.geneName", "("+res[0]+" , "+res[3]+")");
		            			edgeAttributes.setAttribute(edge1.getIdentifier(), "Interaction.userAnnot", false);
			            		edgeAttributes.setAttribute(edge1.getIdentifier(), "Interaction.id", res[6]);
		            			if (!edgeIDStrList.contains(","+edge1.getIdentifier()+","))	 //modified on June 24  				 
			            			   edgeIDStrList += edge1.getIdentifier()+",";
			            		intID2geneID.put(res[6],edge1.getIdentifier());
		            		}
		            		
						}
						rd.close();
						if (!nodeList.isEmpty() && !edgeList.isEmpty()){
							//System.out.println("node list and edge list is not empty");
							//get all molecule and interaction attributes using returned gene IDs and interaction IDs	
		            		AttributesByIDs.GetAttribute(geneIDList,interactionIDList);
		            		new UserAnnoAttr().getAttribute(geneIDList.trim(),edgeIDStrList.trim());
		            		//add extend network
		            		Iterator nodeIt=nodeList.iterator();
		            		while (nodeIt.hasNext()){
		            			CyNode node=(CyNode)nodeIt.next();
		            			nodeAttributes.setAttribute(node.getIdentifier(),"Node Color", EXPANDNEIGHBOR);
		            			curntwk.addNode(node); 
		            			//System.out.println("add node to network "+node.getIdentifier());
		            		}
		            		Iterator edgeIt=edgeList.iterator();
		            		while (edgeIt.hasNext()){
		            			CyEdge edge=(CyEdge)edgeIt.next();
		            			curntwk.addEdge(edge);	            			
		            		}
		            		//set node color attributes for expand node. set nodelist and edge list as expand node attributes for collapsing expanded network using 
		            		nodeAttributes.setAttribute(clickNodeId,"Node Color", EXPANDNODE);
		            		nodeAttributes.setListAttribute(clickNodeId,"NodeIDList", nodeIDList);
		            		nodeAttributes.setListAttribute(clickNodeId,"EdgeIDList", edgeIDList);
		            		nodeAttributes.setUserVisible("Node Color", false);
		            		nodeAttributes.setUserVisible("NodeIDList", false);
		            		nodeAttributes.setUserVisible("EdgeIDList", false);
		            		draw();
		            		
						}
						else {
							   QueryMiMIWrapper.popupTimer.stop();
		            		   QueryMiMIWrapper.dialog.setVisible(false);
		            		   QueryMiMIWrapper.dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		            		   QueryMiMIWrapper.dialog.pack();
		            		   JOptionPane.showMessageDialog(Cytoscape.getDesktop(),"No Expanded network found for this node");	            	 
		            	}
					}
		}
	    catch (Exception e){
	    	return e;
        } 
	    
	    return new Boolean(true);
	}
	    
               
	           
	private static void doQuery(int inputtype, String inputStr){
		int continueQuery=1;
		String inputgene="";
		keyword=" ";
		//System.out.println(inputStr);
		if (inputtype==QUERY_BY_NAME || inputtype==QUERY_BY_FILE || inputtype==QUERY_BY_EXPAND || inputtype==QUERY_BY_REMOTEFILE){		
		//System.out.println(inputStr);
		String [] inputArray=inputStr.split("/////");			
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
				
				
				URL url = new URL(urlStr);
				Proxy CytoProxyHandler = ProxyHandler.getProxyServer();
				URLConnection conn ;
				if (CytoProxyHandler == null) 
					conn = url.openConnection();
				else conn = url.openConnection(CytoProxyHandler);
				//URLConnection conn = url.openConnection();
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
					query="ID="+inputStr.trim()+"&TYPE="+QUERY_BY_ID+"&ORGANISMID=null&MOLECULETYPE=null&DATASOURCE=null&STEPS=null&CONDITION=null&FILTER=null";
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
	
	private static void setNodEdgeList(){
		String lineCmpd;
		CyAttributes nodeAttributes = Cytoscape.getNodeAttributes();
		
		try{
			while ((lineCmpd=urlconn.getBrd().readLine())!=null){				
				String [] elems=lineCmpd.split("/////");
				//System.out.println("lineCmpd is"+elems[0]+" "+elems[8]);
				CyNode sourNode=addTargetNode(elems[0]);
				CyNode trgtNode=addTargetNode(elems[8]);
				CyEdge edge=addEdge(sourNode,trgtNode);
				setAttribute(nodeAttributes,trgtNode);
				addNode2Ntwk(sourNode);
				addNode2Ntwk(trgtNode);
				addEdge2Ntwk(edge);
				draw();
    			
				
				//test
				//CyGroup mycygroup=CyGroupManager.createGroup("mygroup", "mygroupview");
				//test end
			}
		}catch (Exception e){
			//System.out.println(e);
		}
			
		
	}
	
	private static CyNode addTargetNode(String nodID){
		return Cytoscape.getCyNode(nodID, true);
	}
	
	private static CyEdge addEdge(CyNode sourNode, CyNode tarNode){
		return Cytoscape.getCyEdge(sourNode, tarNode, Semantics.INTERACTION, "R", true);
	}
	
	private static void addNode2Ntwk(CyNode node){
		VisualMappingManager manageView=new VisualMappingManager(Cytoscape.getCurrentNetworkView());
		Cytoscape.getCurrentNetwork().addNode(node);
		NodeView nodeview=Cytoscape.getCurrentNetworkView().addNodeView(node.getRootGraphIndex()); 			    		  
		manageView.vizmapNode(nodeview, Cytoscape.getCurrentNetworkView()); 
	}
	private static void addEdge2Ntwk(CyEdge edge){
		VisualMappingManager manageView=new VisualMappingManager(Cytoscape.getCurrentNetworkView());
		Cytoscape.getCurrentNetwork().addEdge(edge);
		EdgeView edgeview=Cytoscape.getCurrentNetworkView().addEdgeView(edge.getRootGraphIndex()); 			    		  
		manageView.vizmapEdge(edgeview, Cytoscape.getCurrentNetworkView()); 
	}
	
	private static void draw(){
		//specify layout
		LayoutUtility.doLayout();
	
		//set visual style 
		MiMIVS.SetMiMIVS();
		cynetwork = Cytoscape.getCurrentNetwork();
		CyNetworkView netView = Cytoscape.getNetworkView(cynetwork.getIdentifier()); 
		netView.setVisualStyle("MiMI");
		Cytoscape.getVisualMappingManager().setNetworkView(netView); 
		Cytoscape.getVisualMappingManager().setVisualStyle("MiMI"); 
		netView.redrawGraph(false, true);	            	 
		//refresh attribute browser
		Cytoscape.firePropertyChange(Cytoscape.ATTRIBUTES_CHANGED, null, null);
	}
	
	private static void setAttribute(CyAttributes nodeAttributes,CyNode sourNode){
		nodeAttributes.setAttribute(sourNode.getIdentifier(), "Gene.name", sourNode.getIdentifier());
	}
  }
