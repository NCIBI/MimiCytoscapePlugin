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
 package org.ncibi.cytoscape.mimi.parser;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;

import org.w3c.dom.NodeList;

/**
 * 
 *
 * @author Jing Gao
 * @author Alex Ade
 * Fixed NPE at organismID if no organismID in XML. Improved error handling.
 */
public class ParseXML {
	private  String moleculeName=" ";
	private  String moleculeID=" ";
	private  String moleculeType=" ";
	private  String nameAlias=" ";
	private  String description=" ";
	private  String ids=" ";
	private  String cellularComponent=" ";
	private  String molecularFunction=" ";
	private  String biologicalProcess=" ";
	private  String domain=" ";
	private  String ptm=" ";
	private  String orthologue=" ";
	private  String organismID=" ";
	private  String commonName=" ";
	private  ArrayList<String> targetNodeName =new ArrayList<String>();
	private  ArrayList<String> targetNodeInteractionRef =new ArrayList<String>(); 
	private  ArrayList<String> targetNodeMoleculeRef =new ArrayList<String>();
	
	public ParseXML(org.w3c.dom.Node nodeMolecule,XPath xpath){
		try{
			//get moleculeid
			String subexpression = "moleculeID";
			org.w3c.dom.Node nodeMolecueID = (org.w3c.dom.Node) xpath.evaluate(subexpression, nodeMolecule, XPathConstants.NODE);
			if(nodeMolecueID !=null)
				moleculeID = nodeMolecueID.getTextContent().trim();
			//System.out.println("molecule id "+moleculeID);
			
			//get molecule type
		    subexpression ="moleculeType";
		    org.w3c.dom.Node nodeMolecueType = (org.w3c.dom.Node) xpath.evaluate(subexpression, nodeMolecule, XPathConstants.NODE);
		    if (nodeMolecueType !=null){
		    	moleculeType = nodeMolecueType.getFirstChild().getTextContent().trim();        			      
		    	//System.out.println("molecule type is["+moleculeType+"]"); 
		    }
		    
		    //get alais name 
            subexpression ="name";
		    NodeList nameList = (NodeList) xpath.evaluate(subexpression, nodeMolecule, XPathConstants.NODESET);
		   
		    if (nameList != null) {
		    	for(int j=0; j<nameList.getLength(); j++) {
		    		  org.w3c.dom.Node nodeMoleculeName=nameList.item(j);
		    		  if (nodeMoleculeName !=null){
		    			  String name=nodeMoleculeName.getFirstChild().getTextContent().trim();
		    			  nameAlias +=name+"; ";    
		    		  }
		    	}
		    	if (nameList.item(0) !=null)
		    		moleculeName=nameList.item(0).getFirstChild().getTextContent().trim();
		    } 
		    //System.out.println("nameAlias"+nameAlias+"molecule name"+moleculeName);
		    
		    //get molecule description            			      
		    NodeList NodeDescritionList = (NodeList) xpath.evaluate("description/distribution//value", nodeMolecule, XPathConstants.NODESET);
		  
		    if (NodeDescritionList != null) {
		    	if (NodeDescritionList.getLength()>0)
		    		for(int j=0; j<NodeDescritionList.getLength(); j++) {
		    			String value=NodeDescritionList.item(j).getFirstChild().getTextContent().trim();
		    	        description +=value+" ";          			    	      
		    	    }
		    	if(NodeDescritionList.getLength()==0){  
		    	   org.w3c.dom.Node nodeDesc= (org.w3c.dom.Node)xpath.evaluate("description", nodeMolecule, XPathConstants.NODE);
		    	   if (nodeDesc !=null)
		    		   description =nodeDesc.getFirstChild().getTextContent();		    	       
		    	}
		    } 
		    //System.out.println("description is "+description);
		    
		    //get external reference
		    NodeList NodeIDList = (NodeList) xpath.evaluate("id", nodeMolecule, XPathConstants.NODESET);
		 
		    if (NodeIDList != null) 
		    	for(int j=0; j<NodeIDList.getLength(); j++) {
		    		org.w3c.dom.Node nodeID=NodeIDList.item(j);
		    		//org.w3c.dom.Node nodeIDType=(org.w3c.dom.Node) xpath.evaluate(subexpression, nodeID, XPathConstants.NODE);
		    		String idType =((org.w3c.dom.Node) xpath.evaluate("idType", nodeID, XPathConstants.NODE)).getTextContent().trim();
		    		String idValue=((org.w3c.dom.Node) xpath.evaluate("idValue", nodeID, XPathConstants.NODE)).getTextContent().trim();
		    		ids += idType +"  "+idValue+";     ";
		    	} 
		    //System.out.println("id is "+ids);
		    
		    //get cellular components
		    NodeList cellularComponentList = (NodeList) xpath.evaluate("cellularComponent", nodeMolecule, XPathConstants.NODESET);
		  
		    if (cellularComponentList != null) 
		    	for(int j=0; j<cellularComponentList.getLength(); j++) {
		    		String cellularComponentValue=cellularComponentList.item(j).getFirstChild().getTextContent();
		    		String cellularComponentAttribute=(String) xpath.evaluate("@goid", cellularComponentList.item(j));
		    		cellularComponent += cellularComponentValue +" ("+cellularComponentAttribute+");   " ;  			    		  
		        } 
		    //System.out.println("cellular component is "+cellularComponent);
		    
		    //get molecularFunction
		    NodeList molecularFunctionList = (NodeList) xpath.evaluate("molecularFunction", nodeMolecule, XPathConstants.NODESET);
		 
		    if (molecularFunctionList != null) 
		    	for(int j=0; j<molecularFunctionList.getLength(); j++) {
		    		String molecularFunctionValue=molecularFunctionList.item(j).getFirstChild().getTextContent();
		    		String molecularFunctionAttribute=(String) xpath.evaluate("@goid", molecularFunctionList.item(j));
		    		molecularFunction += molecularFunctionValue +" ("+molecularFunctionAttribute+");   " ;  			    		  
		        }
		    //System.out.println("molecularFunction is "+molecularFunction);
		    
		    //get biologicalProcess
		    NodeList biologicalProcessList = (NodeList) xpath.evaluate("biologicalProcess", nodeMolecule, XPathConstants.NODESET);
		
		    if (biologicalProcessList != null) 
		    	for(int j=0; j<biologicalProcessList.getLength(); j++) {
		    		String biologicalProcessValue=biologicalProcessList.item(j).getFirstChild().getTextContent();
		    		String biologicalProcessAttribute=(String) xpath.evaluate("@goid", biologicalProcessList.item(j));
		    		biologicalProcess += biologicalProcessValue +" ("+biologicalProcessAttribute+");   " ;  			    		  
		        }
		    //System.out.println("biologicalProcess is "+biologicalProcess);
		    
		    //get domain
		    NodeList domainList = (NodeList) xpath.evaluate("structure/BioStruc/BioSite", nodeMolecule, XPathConstants.NODESET);
		
		    if (domainList != null) 
		    	for(int j=0; j<domainList.getLength(); j++) {
		    		String domainValue=domainList.item(j).getFirstChild().getTextContent().trim();
		    		String domainBtypeAttribute=(String) xpath.evaluate("@btype", domainList.item(j));
		    		String domainStartAttribute=(String) xpath.evaluate("@start", domainList.item(j));
		    		String domainEndAttribute=(String) xpath.evaluate("@end", domainList.item(j));
		    		domain += domainValue +" ["+domainBtypeAttribute+", "+domainStartAttribute+"-"+ domainEndAttribute +"];  ";  			    		  
		        }
		    //System.out.println("domain is "+domain);
		    
		    //get Post-Translational Modifications(ptm)
		    NodeList ptmList = (NodeList) xpath.evaluate("ptm", nodeMolecule, XPathConstants.NODESET);
	
		    if (ptmList != null) 
		    	for(int j=0; j<ptmList.getLength(); j++) {
		    		String ptmAttribute=(String) xpath.evaluate("@ptype", ptmList.item(j));
		    		String bioSiteStartAttribute=(String) xpath.evaluate("BioSite/@start", ptmList.item(j));
		    		ptm += ptmAttribute +"["+bioSiteStartAttribute+"];   ";
		        }
		   // System.out.println("ptm is "+ptm);
		    
		    //get orthologue
		    NodeList orthologueList = (NodeList) xpath.evaluate("orthologue", nodeMolecule, XPathConstants.NODESET);
		  
		    if (orthologueList != null) 
		    	for(int j=0; j<orthologueList.getLength(); j++) {
		    		org.w3c.dom.Node nodeorthologue=orthologueList.item(j);          			    		  
		    		String orthologuemethodID =((org.w3c.dom.Node) xpath.evaluate("method/methodID", nodeorthologue, XPathConstants.NODE)).getTextContent().trim();
		    		String orthologuemethodName=((org.w3c.dom.Node) xpath.evaluate("method/methodName", nodeorthologue, XPathConstants.NODE)).getTextContent().trim();
		    		orthologue += orthologuemethodName +":"+orthologuemethodID+"; ";
		    	} 
		    //System.out.println("orthologue is "+orthologue);
		    
		    //get orgnisamID and then get commonname
		    subexpression ="organismID";
		    org.w3c.dom.Node nodeOrganismID = (org.w3c.dom.Node) xpath.evaluate(subexpression, nodeMolecule, XPathConstants.NODE);
		    if (nodeOrganismID !=null){
		    	organismID = nodeOrganismID.getFirstChild().getTextContent().trim();
		    	//System.out.println("nodeOrganism ID is "+organismID);
		    }
		    
		    if (organismID !=null){		    	                        	  	
    			  URL theurl=new URL("http://mimiplugin.ncibi.org/dbaccess/3.2/query_NCIBI_DBX_gene_Taxonomy.php?TAXID="+organismID.trim());
    			  InputStream or=theurl.openStream();
    			  InputStreamReader inr=new InputStreamReader(or); 		
    			  BufferedReader bfr=new BufferedReader(inr);				
    			  String inputLine1;				
    			  while((inputLine1 = bfr.readLine()) != null)
    				  commonName =inputLine1;			    	
		    }
		   
            
            //extract interaction inforamtion
            subexpression ="interaction";
            NodeList interactionList = (NodeList) xpath.evaluate(subexpression, nodeMolecule, XPathConstants.NODESET);          			      
		    if (interactionList != null) {
		    	for(int j=0; j<interactionList.getLength(); j++) {
		    		org.w3c.dom.Node nodeInteraction=interactionList.item(j);
		    		if (nodeInteraction!=null){
		    			subexpression ="interactionRef";
		    			org.w3c.dom.Node nodeInteractionRef=(org.w3c.dom.Node) xpath.evaluate(subexpression, nodeInteraction, XPathConstants.NODE);
		    		    if (nodeInteractionRef !=null){
		    		    	String interactionRef =nodeInteractionRef.getTextContent().trim();
			    			//System.out.println("interaction ref"+interactionRef);
		    		    	targetNodeInteractionRef.add(interactionRef); 		    		   
		    		    	subexpression ="moleculeName";
		    		    	org.w3c.dom.Node nodeMoleculeName=(org.w3c.dom.Node) xpath.evaluate(subexpression, nodeInteraction, XPathConstants.NODE);
		    		    	if (nodeMoleculeName !=null){
		    		    		String moleculeName =nodeMoleculeName.getTextContent().trim();
		    		    		//System.out.println("interaction molecule name"+moleculeName);
		    		    		targetNodeName.add(moleculeName);
		    		    		subexpression ="moleculeRef";
		    		    		org.w3c.dom.Node nodeMoleculeRef=(org.w3c.dom.Node) xpath.evaluate(subexpression, nodeInteraction, XPathConstants.NODE);
		    		    		if (nodeMoleculeRef !=null){
		    		    			String moleculeRef =nodeMoleculeRef.getTextContent().trim();
		    		    			//System.out.println("moleculeRef"+moleculeRef);
		    		    			targetNodeMoleculeRef.add(moleculeRef);
		    		    		}
		    		    		else {
		    		    			targetNodeInteractionRef.remove(interactionRef);
		    		    			targetNodeName.remove(moleculeName);
		    		    		}
		    		    	}
		    		    	else  targetNodeInteractionRef.remove(interactionRef); //targetNodeName.add("N/A");
		    			
		    		    }
		    		}
		    	}          			    	  
		    } 
            
		}
		catch (Exception e){
			System.err.println(e.toString()); 
		}
		
	}//end of constructor
	
	public  String getMoleculeName(){
		return this.moleculeName;
	}
	public  String getMoleculeID (){
		return this.moleculeID;
	}
	public  String getMoleculeType (){
		return this.moleculeType;
	}
	public  String getNameAlias (){
		return this.nameAlias;
	}
	public  String getDescription (){
		return this.description;
	}
	public  String getIds (){
		return this.ids;
	}
	public  String getCellularComponent (){
		return this.cellularComponent;
	}
	public  String getMolecularFunction (){
		return this.molecularFunction;
	}
	public  String getBiologicalProcess (){
		return this.biologicalProcess;
	}
	public  String getDomain (){
		return this.domain;
	}
	public  String getptm (){
		return this.ptm;
	}
	public  String getHomologyue(){
		return this.orthologue;
	}
	public  String getOrganismID(){
		return this.organismID;
	}
	public  String getCommonName(){
		return this.commonName;
	}
	public  ArrayList getTargetNodeName(){
		return this.targetNodeName;
	}
	public  ArrayList getTargetNodeInteractionRef(){
		return this.targetNodeInteractionRef;
	}
	public  ArrayList getTargetNodeMoleculeRef(){
		return this.targetNodeMoleculeRef;
	}
}

