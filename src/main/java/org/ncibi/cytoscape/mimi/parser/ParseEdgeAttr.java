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

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import org.w3c.dom.NodeList;


/**
 * 
 *
 * @author Jing Gao
 */
public class ParseEdgeAttr {
	private  String partnerAName="";
	private  String partnerAID="";
	private  String partnerBName="";
	private  String partnerBID="";
	private  String type="";
	private  String description="";
	private  String bioExp="";
	private  String likelyhood="";
	private  String expProvenance="";
	private  String condition="";	
	private  String interactionSite="";
	private  String pubmedID="";
    private  String bindID="";
	
	public ParseEdgeAttr(org.w3c.dom.Node nodeInteraction,XPath xpath){
		try{
			//get partnerA's molecule name and molecule id			
		    org.w3c.dom.Node nodePartnerA=(org.w3c.dom.Node) xpath.evaluate("partnerA", nodeInteraction, XPathConstants.NODE);
		    if (nodePartnerA != null){
		    	org.w3c.dom.Node nodePartnerAName=(org.w3c.dom.Node) xpath.evaluate("moleculeName", nodePartnerA, XPathConstants.NODE);
		    	if (nodePartnerAName !=null)
		    		partnerAName=nodePartnerAName.getFirstChild().getTextContent().trim();		    	
		    	org.w3c.dom.Node nodePartnerAID=(org.w3c.dom.Node) xpath.evaluate("moleculeRefA", nodePartnerA, XPathConstants.NODE);
		    	if(nodePartnerAID !=null)
		    		partnerAID=nodePartnerAID.getFirstChild().getTextContent().trim();		    	
		    	//System.out.println("partnerA name is: "+partnerAName);
		    	//System.out.println("partnerA ID is: "+partnerAID);
		    }
		    
		    //get partner B's molecule name and molecule id
		    org.w3c.dom.Node nodePartnerB=(org.w3c.dom.Node) xpath.evaluate("partnerB", nodeInteraction, XPathConstants.NODE);
		    if (nodePartnerB!=null){
		    	org.w3c.dom.Node nodePartnerBName =(org.w3c.dom.Node) xpath.evaluate("moleculeName", nodePartnerB, XPathConstants.NODE);
		    	if (nodePartnerBName !=null)		    	
		    		partnerBName =nodePartnerBName.getFirstChild().getTextContent().trim();
		    	org.w3c.dom.Node nodePartnerBID=(org.w3c.dom.Node) xpath.evaluate("moleculeRefB", nodePartnerB, XPathConstants.NODE);		    	
		    	if(nodePartnerBID !=null)
		    		partnerBID=nodePartnerBID.getFirstChild().getTextContent().trim();		    	
		    	//System.out.println("partnerB name is: "+partnerBName);
		    	//System.out.println("partnerB ID is: "+partnerBID);
		    }
		    
		    //get interaction type
		    org.w3c.dom.Node nodeType=(org.w3c.dom.Node) xpath.evaluate("interactionType", nodeInteraction, XPathConstants.NODE);
		    if (nodeType !=null){
		    	type =nodeType.getFirstChild().getTextContent().trim();
		    	//get type provenance //		    			
  			  	org.w3c.dom.Node nodeProvenanceInType =(org.w3c.dom.Node) xpath.evaluate("prov/im", nodeType, XPathConstants.NODE);
  			  	if (nodeProvenanceInType !=null){
  			  	  String pubmedProvenanceInType =(String) xpath.evaluate("arg[@id='0'][@atype='10']", nodeProvenanceInType);
				  if (pubmedProvenanceInType.length()!=0)
					type +=" PubMed:"+pubmedProvenanceInType+";";  	
  				  String bindProvenanceInType=(String) xpath.evaluate("arg[@id='1'][@atype='20']", nodeProvenanceInType);
  				  if (bindProvenanceInType.length() !=0)		    					 
  					type +=" Bind:"+bindProvenanceInType+";";  				  			  
  			  	}		    	
		        //System.out.println("edge type is "+type);
		    }
		    
		    //get edge description       			      
		    org.w3c.dom.Node nodeDescription=(org.w3c.dom.Node) xpath.evaluate("interactionDescription", nodeInteraction, XPathConstants.NODE);
		    if (nodeDescription!=null){
		    	description =nodeDescription.getFirstChild().getTextContent().trim();
		    	//get edge description provenance 		    			
  			  	org.w3c.dom.Node nodeProvenanceInDescription =(org.w3c.dom.Node) xpath.evaluate("prov/im", nodeDescription, XPathConstants.NODE);
  			  	if (nodeProvenanceInDescription !=null){
  			  	  String pubmedProvenanceInDescription =(String) xpath.evaluate("arg[@id='0'][@atype='10']", nodeProvenanceInDescription);
				  if (pubmedProvenanceInDescription.length()!=0)
					description +=" PubMed:"+pubmedProvenanceInDescription+";"; 	
  				  String bindProvenanceInDescription=(String) xpath.evaluate("arg[@id='1'][@atype='20']", nodeProvenanceInDescription);
  				  if (bindProvenanceInDescription.length() !=0)		    					 
  					description +=" Bind:"+bindProvenanceInDescription+";";  				   				  
  			  	}		    	
		    //System.out.println("description is "+description);
		    }
		    
		    //get experiment
		    org.w3c.dom.Node nodeExperiment=(org.w3c.dom.Node) xpath.evaluate("experiment", nodeInteraction, XPathConstants.NODE);
		    if (nodeExperiment !=null){
		    	//get experiment provenance //		    			
  			  	org.w3c.dom.Node nodeProvenanceInExp =(org.w3c.dom.Node) xpath.evaluate("prov/im", nodeExperiment, XPathConstants.NODE);
  			  	if (nodeProvenanceInExp !=null){
  			  	  String pubmedProvenanceInExp =(String) xpath.evaluate("arg[@id='0'][@atype='10']", nodeProvenanceInExp);
				  if (pubmedProvenanceInExp.length()!=0)
					  expProvenance +=" PubMed:"+pubmedProvenanceInExp+";";  	
  				  String bindProvenanceInExp=(String) xpath.evaluate("arg[@id='1'][@atype='20']", nodeProvenanceInExp);
  				  if (bindProvenanceInExp.length() !=0)		    					 
  					expProvenance +=" Bind:"+bindProvenanceInExp+";";  				  			  
  			  	}		    	
		    	bioExp =((org.w3c.dom.Node) xpath.evaluate("BioExp", nodeExperiment, XPathConstants.NODE)).getTextContent().trim();		    	
		    	likelyhood=((org.w3c.dom.Node) xpath.evaluate("prob/val", nodeExperiment, XPathConstants.NODE)).getTextContent().trim();
		    }
		    
		    //get condition
		    NodeList conditionList = (NodeList) xpath.evaluate("condition", nodeInteraction, XPathConstants.NODESET);		    
		    if (conditionList != null)
		    	for(int j=0; j<conditionList.getLength(); j++) {
		    		  org.w3c.dom.Node nodeCondition=conditionList.item(j);
		    		  if(nodeCondition !=null){		    			  
		    			  //get condition description 
		    			 org.w3c.dom.Node nodeDescrInCondition= (org.w3c.dom.Node) xpath.evaluate("descr",nodeCondition, XPathConstants.NODE);
		    			 if (nodeDescrInCondition !=null)
		    				 condition +=nodeDescrInCondition.getFirstChild().getTextContent().trim();
		    			 else
		    				 condition +=nodeCondition.getFirstChild().getTextContent().trim();
		    			 
		    			 //get condition provenance //		    			
		    			  org.w3c.dom.Node nodeProvenanceInCondition =(org.w3c.dom.Node) xpath.evaluate("prov/im", nodeCondition, XPathConstants.NODE);
		    			  if (nodeProvenanceInCondition !=null){
		    				  String bindProvenanceInCondition=(String) xpath.evaluate("arg[@id='1'][@atype='20']", nodeProvenanceInCondition);
		    				  if (bindProvenanceInCondition.length() !=0)		    					 
		    					  condition +=" Bind:"+bindProvenanceInCondition+";";
		    				  String pubmedProvenanceInCondition =(String) xpath.evaluate("arg[@id='0'][@atype='10']", nodeProvenanceInCondition);
		    				  if (pubmedProvenanceInCondition.length()!=0)
		    					  condition +=" PubMed:"+pubmedProvenanceInCondition+";";
		    				  
		    			  }
		    			 //System.out.println("condition is "+condition);	    				 
		    		  }		    	     		    	      
		    	}
		    
		    //get interaction sites
		    NodeList interactionSiteList = (NodeList) xpath.evaluate("interactionSite/BioSite", nodeInteraction, XPathConstants.NODESET);		  
		    if (interactionSiteList != null) 
		    	for(int j=0; j<interactionSiteList.getLength(); j++) {
		    		String interactionSiteValue=interactionSiteList.item(j).getFirstChild().getTextContent().trim();
		    		String interactionSiteBtypeAttribute=(String) xpath.evaluate("@btype", interactionSiteList.item(j));
		    		String interactionSiteStartAttribute=(String) xpath.evaluate("@start", interactionSiteList.item(j));
		    		String interactionSiteEndAttribute=(String) xpath.evaluate("@end", interactionSiteList.item(j));
		    		interactionSite += interactionSiteValue +"["+interactionSiteBtypeAttribute+", "+interactionSiteStartAttribute+"-"+ interactionSiteEndAttribute +"];  ";
		    		//get interactionSite provenance 		    			
	  			  	org.w3c.dom.Node nodeProvenanceInInteractionSite =(org.w3c.dom.Node) xpath.evaluate("prov/im", interactionSiteList.item(j), XPathConstants.NODE);
	  			  	if (nodeProvenanceInInteractionSite !=null){
	  			  	  String pubmedProvenanceInInteractionSite =(String) xpath.evaluate("arg[@id='0'][@atype='10']", nodeProvenanceInInteractionSite);
					  if (pubmedProvenanceInInteractionSite.length()!=0)
						  interactionSite +=" PubMed:"+pubmedProvenanceInInteractionSite+";";  	
	  				  String bindProvenanceInInteractionSite=(String) xpath.evaluate("arg[@id='1'][@atype='20']", nodeProvenanceInInteractionSite);
	  				  if (bindProvenanceInInteractionSite.length() !=0)		    					 
	  					type +=" Bind:"+bindProvenanceInInteractionSite+";";  				  			  
	  			  	}		    	
		    		//System.out.println("interactionSite"+interactionSite);
		        }
		    
		    //get pubmed id and bind id form path interaction/prove/im
		    NodeList proveList = (NodeList) xpath.evaluate("prov/im", nodeInteraction, XPathConstants.NODESET);		    
		    if (proveList != null)
		    	for(int j=0; j<proveList.getLength(); j++) {
		    		  org.w3c.dom.Node nodeProve=proveList.item(j);
		    	      pubmedID += "PubMed:"+(String) xpath.evaluate("arg[@id='0'][@atype='10']", nodeProve)+";";
		    	      bindID +="Bind:"+(String) xpath.evaluate("arg[@id='1'][@atype='20']", nodeProve)+";";
		    	}
		    
		}
		catch (Exception e){
			System.err.println(e.toString()); 
		}
		
	}
	
	public  String getPartnerAName(){
		return this.partnerAName;
	}
	public  String getPartnerAID(){
		return this.partnerAID;
	}	
	public  String getPartnerBName(){
		return this.partnerBName;
	}
	public String getPartnerBID(){
		return this.partnerBID;
	}
	public  String getType(){
		return this.type;
	}
	public  String getDescription(){
		return this.description;
	}
	public  String getBioExp(){
		return this.bioExp;
	}
	public  String getLikelyhood(){
		return this.likelyhood;
	}
	public  String getExpProvenance(){
		return this.expProvenance;
	}	
	public  String getCondition(){
		return this.condition;
	}
	public  String getInteractionSiteEndAttribute(){
		return this.interactionSite;
	}
	public  String getPubmedID(){
		return this.pubmedID;
	}
	public  String getBindID(){
		return this.bindID;
	}
}
