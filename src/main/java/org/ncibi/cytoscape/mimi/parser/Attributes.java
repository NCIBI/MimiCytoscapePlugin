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
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.Proxy;

import org.ncibi.cytoscape.mimi.plugin.QueryMiMI;

import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;
import cytoscape.util.ProxyHandler;

/** 
 * @author jinggao/Attributs
 * @date Jun 12, 2007
 */

/* query mimirdb to get all the attributes info*/
public abstract class Attributes {
	private static BufferedReader rd; 
	private static URL url;
	private final static int NAME_A=10;
	private final static int DESRP_A=11;
	private final static int BIOLOGICALPROCESS_A=12;
	private final static int CELLULARCOMPONENT_A=13;
	private final static int MOLECULARFUNCTION_A=14;
	private final static int ORTHOLOGUE_A=15;
	private final static int PROVENANCE_A=16;
	private final static int PTM_A=17;
	private final static int BIOSITE_A=18;
	private final static int TYPE_AND_TAXID=19;
	private final static int I_BIOSITE_A=20;
	private final static int I_CONDITION_A=21;
	private final static int I_DESCR_A=22;
	private final static int I_EXP_A=23;
	private final static int I_LOCT_A=24;
	private final static int I_PROV_A=25;
	private final static int I_TYPE_A=26;
	private final static int I_INTERACTION_A=27;
	private static String Keyword;
	private static String Organism;	
	private static String MoleType;
	private static String DataSource;
	private static String Steps;
	private static int Condition;
	private static String Filter;
		
	public static void GetAttribute (String keyword,String organism, String moleType,String dataSource,String steps,int condition, String filter){
		Keyword=keyword;
		Organism=organism;
		MoleType=moleType;
		DataSource=dataSource;
		Steps=steps;
		Condition=condition;
		Filter=filter;
		CyAttributes nodeAttr=Cytoscape.getNodeAttributes();
		CyAttributes edgeAttr=Cytoscape.getEdgeAttributes();		
		
		try{
			url=new URL(QueryMiMI.urlStr);			
			String line;
			String mid1;
			String mid2;
			
			
			//get molecule type and organism 
			line="";
			doQuery(TYPE_AND_TAXID);   
			String typeAtt="";
			String orgAtt="";
			String orgNameAtt="";
			mid1="";
			mid2="";			
            while ((line = rd.readLine()) != null) {
            	String [] res=line.split("/////");
            	if (res.length==4){
            		mid2=res[0];
            		if (!mid2.equals(mid1)){ 
            			if (!mid1.equals("")){            		
            				nodeAttr.setAttribute(mid1, "Gene.gene type",typeAtt);
            				nodeAttr.setAttribute(mid1, "Gene.organismID",orgAtt);  
            				nodeAttr.setAttribute(mid1, "Gene.organism",orgNameAtt); 
            			}
            			mid1=mid2;  
            			typeAtt =res[1];
            			orgAtt =res[2]; 
            			orgNameAtt=res[3];
            		}
            		else{            			 
            			typeAtt +="; "+res[1];
            			orgAtt +="; "+res[2];
            			orgNameAtt +="; "+res[3];
            		}            		
            	} 
            }
            nodeAttr.setAttribute(mid1, "Gene.gene type",typeAtt);
			nodeAttr.setAttribute(mid1, "Gene.organismID",orgAtt);
			nodeAttr.setAttribute(mid1, "Gene.organism",orgNameAtt);
            rd.close();
			
			//get molecule Alias 
            line="";
			doQuery(NAME_A);  
			mid1="";
			mid2="";
			String AliasAtt="";			
            while ((line = rd.readLine()) != null) {
            	String [] res=line.split("/////");
            	if (res.length==3){
            		mid2=res[0];
            		if (!mid2.equals(mid1)){ 
            			if (!mid1.equals(""))         		
            				nodeAttr.setAttribute(mid1, "Gene.alias",AliasAtt);            				
            			mid1=mid2;  
            			AliasAtt ="["+res[1]+"]"+res[2];            			     			
            		}
            		else            			 
            			AliasAtt +="; ["+res[1]+"]"+res[2];            			
            	}        		 
            }  
            nodeAttr.setAttribute(mid1, "Gene.alias",AliasAtt);
            rd.close();
            
            //get molecule description 
            line="";
            doQuery(DESRP_A);
            mid1="";
			mid2="";
            String DescriptionAtt="";
            while ((line = rd.readLine()) != null){
            	String [] res=line.split("/////");
            	if (res.length==2){
            		mid2=res[0];
            		if (!mid2.equals(mid1)){ 
            			if (!mid1.equals(""))         		
            				nodeAttr.setAttribute(mid1, "Gene.description",DescriptionAtt);            				
            			mid1=mid2;  
            			DescriptionAtt =res[1];            			     			
            		}
            		else            			 
            			DescriptionAtt +="; "+res[1];            			
            	}            	
            }
            nodeAttr.setAttribute(mid1, "Gene.description",DescriptionAtt);
            rd.close();
            
            //get BiologicalProcess
            line="";
            doQuery(BIOLOGICALPROCESS_A);
            mid1="";
			mid2="";
            String BioProAtt="";
            while ((line = rd.readLine()) != null){
            	String [] res=line.split("/////");
            	if (res.length==2){
            		mid2=res[0];
            		if (!mid2.equals(mid1)){ 
            			if (!mid1.equals(""))         		
            				nodeAttr.setAttribute(mid1, "Gene.biologicalProcess",BioProAtt);            				
            			mid1=mid2;  
            			BioProAtt =res[1];            			     			
            		}
            		else            			 
            			BioProAtt +="; "+res[1];             		       		
            	}
            }
            nodeAttr.setAttribute(mid1, "Gene.biologicalProcess",BioProAtt);
            rd.close();
            
            //get CELLULARCOMPONENT
            line="";
            doQuery(CELLULARCOMPONENT_A);
            mid1="";
			mid2="";
            String comptAtt="";
            while ((line = rd.readLine()) != null){
            	String [] res=line.split("/////");
            	//System.out.println(line);
            	if (res.length==2){
            		mid2=res[0];
            		if (!mid2.equals(mid1)){ 
            			if (!mid1.equals(""))         		
            				nodeAttr.setAttribute(mid1, "Gene.cellularComponent",comptAtt);            				
            			mid1=mid2;  
            			comptAtt =res[1];            			     			
            		}
            		else            			 
            			comptAtt +="; "+res[1];              	          		
            	}
            }
            nodeAttr.setAttribute(mid1, "Gene.cellularComponent",comptAtt);
            rd.close();
            
            //get MOLECULARFUNCTION
            line="";
            doQuery(MOLECULARFUNCTION_A);
            mid1="";
			mid2="";
            String FunctionAtt="";
            while ((line = rd.readLine()) != null){
            	String [] res=line.split("/////");
            	if (res.length==2){
            		mid2=res[0];
            		if (!mid2.equals(mid1)){ 
            			if (!mid1.equals(""))         		
            				nodeAttr.setAttribute(mid1, "Gene.molecularFunction",FunctionAtt);            				
            			mid1=mid2;  
            			FunctionAtt =res[1];            			     			
            		}
            		else            			 
            			FunctionAtt +="; "+res[1];        		
            	}
            }
            nodeAttr.setAttribute(mid1, "Gene.molecularFunction",FunctionAtt);
            rd.close();
            
            //get ORTHOLOGUE
            line="";
            doQuery(ORTHOLOGUE_A);
            mid1="";
			mid2="";
            String orthologAtt="";
            while ((line = rd.readLine()) != null){
            	String [] res=line.split("/////");
            	if (res.length==3){
            		mid2=res[0];
            		if (!mid2.equals(mid1)){ 
            			if (!mid1.equals(""))         		
            				nodeAttr.setAttribute(mid1, "Gene.homology",orthologAtt);            				
            			mid1=mid2;  
            			orthologAtt ="["+res[1]+"]"+res[2];            			     			
            		}
            		else            			 
            			orthologAtt +="; ["+res[1]+"]"+res[2];              		
            	}
            }
            nodeAttr.setAttribute(mid1, "Gene.homology",orthologAtt);
            rd.close();
            
            //get PROVENANCE
            line="";
            doQuery(PROVENANCE_A);
            mid1="";
			mid2="";
            String provenanceAtt="";
            while ((line = rd.readLine()) != null){
            	String [] res=line.split("/////");
            	if (res.length==4){
            		mid2=res[0].trim();
            		if (!mid2.equals(mid1)){ 
            			if (!mid1.equals(""))         		
            				nodeAttr.setAttribute(mid1, "Gene.provenance",provenanceAtt);            				
            			mid1=mid2;             			
            			if(QueryMiMI.sourceData.containsKey(res[1]))
            				provenanceAtt =QueryMiMI.sourceData.get(res[1]); 
            			if (res[2].equals("10"))
            				provenanceAtt +=" [Pubmed]"+res[3];
            		}
            		else {            			
            			if ((QueryMiMI.sourceData.containsKey(res[1]))&& !provenanceAtt.contains(QueryMiMI.sourceData.get(res[1])))
            				provenanceAtt +="; "+QueryMiMI.sourceData.get(res[1]);  
            			if (res[2].equals("10"))
            				provenanceAtt +=" [Pubmed]"+res[3];
            		}
            			
            	}
            }
            nodeAttr.setAttribute(mid1, "Gene.provenance", provenanceAtt);
            rd.close();
            
            //get PTM
            line="";
            doQuery(PTM_A);
            mid1="";
			mid2="";
            String ptmAttr="";
            while ((line = rd.readLine()) != null){
            	String [] res=line.split("/////");
            	if (res.length==5){
            		mid2=res[0];
            		if (!mid2.equals(mid1)){ 
            			if (!mid1.equals(""))         		
            				nodeAttr.setAttribute(mid1, "Gene.postTransModifications",ptmAttr);            				
            			mid1=mid2;  
            			ptmAttr ="[ENZYME]"+res[1]+" [GENERAL]"+res[2]+" [EFFECT]"+res[3]+" [DESRPT]"+res[4];            			     			
            		}
            		else            			 
            			ptmAttr +="; [ENZYME]"+res[1]+" [GENERAL]"+res[2]+" [EFFECT]"+res[3]+" [DESRPT]"+res[4];            		           		
            	}
            }
            nodeAttr.setAttribute(mid1, "Gene.postTransModifications",ptmAttr);
            rd.close();
            
            //get BIOSITE
            line="";
            doQuery(BIOSITE_A);
            mid1="";
			mid2="";
            String BioSiteAtt="";
            while ((line = rd.readLine()) != null){
            	String [] res=line.split("/////");
            	if (res.length==2){
            		mid2=res[0];
            		if (!mid2.equals(mid1)){ 
            			if (!mid1.equals(""))         		
            				nodeAttr.setAttribute(mid1, "Gene.domain",BioSiteAtt);            				
            			mid1=mid2;  
            			BioSiteAtt =res[1];            			     			
            		}
            		else            			 
            			BioSiteAtt +="; "+res[1];          		
            	}
            }
            nodeAttr.setAttribute(mid1, "Gene.domain",BioSiteAtt);
            rd.close();
            
            
            //edge attributes 
            String id1="";
            String id2="";
            
            //get interaction id attribute
            line="";
            doQuery(I_INTERACTION_A);
            id1="";            
            while((line = rd.readLine()) != null){
            	String [] res=line.split("/////");
            	if (res.length==7){
            		id1=res[1]+" ( ) "+res[4];
            		edgeAttr.setAttribute(id1, "Interaction.id",res[6]);   
            	}
            }
            
            //get biosite attribute
            line="";
            doQuery(I_BIOSITE_A);
            id1="";
            id2="";
            String i_bioSite_a="";
            while ((line = rd.readLine()) != null){
            	String [] res=line.split("/////");
            	if (res.length==4){
            		id2=res[0]+" ( ) "+res[1];
            		if (!id2.equals(id1)){ 
            			if (!id1.equals(""))         		
            				edgeAttr.setAttribute(id1, "Interaction.domain",i_bioSite_a);            				
            			id1=id2;  
            			i_bioSite_a =res[3];            			     			
            		}
            		else            			 
            			i_bioSite_a +="; "+res[3];          		
            	}            	
            }
            
            //get condition attribute
            line="";
            doQuery(I_CONDITION_A);
            id1="";
            id2="";
            String i_condition_a="";
            while ((line = rd.readLine()) != null){
            	String [] res=line.split("/////");
            	if (res.length==10){
            		id2=res[0]+" ( ) "+res[1];
            		if (!id2.equals(id1)){ 
            			if (!id1.equals(""))         		
            				edgeAttr.setAttribute(id1, "Interaction.condition",i_condition_a);            				
            			id1=id2;  
            			i_condition_a ="[descr]"+res[3]+"[general]"+res[4]+"[effect]"+res[5]+"[molecularFormSelfForm]"+res[6]+"[molecularFormSelfDescr]"+res[7]+"[molecularFormPartnerForm]"+res[8]+"[molecularFormPartnerDescr]"+res[9];            			     			
            		}
            		else            			 
            			i_condition_a +="; "+"[descr]"+res[3]+"[general]"+res[4]+"[effect]"+res[5]+"[molecularFormSelfForm]"+res[6]+"[molecularFormSelfDescr]"+res[7]+"[molecularFormPartnerForm]"+res[8]+"[molecularFormPartnerDescr]"+res[9];          		
            	}            	
            }
            
            //get description attribute
            line="";
            doQuery(I_DESCR_A);
            id1="";
            id2="";
            String i_descr_a="";
            while ((line = rd.readLine()) != null){
            	String [] res=line.split("/////");
            	if (res.length==4){
            		id2=res[0]+" ( ) "+res[1];
            		if (!id2.equals(id1)){ 
            			if (!id1.equals(""))         		
            				edgeAttr.setAttribute(id1, "Interaction.description",i_descr_a);            				
            			id1=id2;  
            			i_descr_a =res[3];
            		}
            		else            			 
            			i_descr_a +="; "+res[3];
            	}            	
            }
            
            //get BioExp attribute
            line="";
            doQuery(I_EXP_A);
            id1="";
            id2="";
            String i_exp_a="";
            while ((line = rd.readLine()) != null){
            	String [] res=line.split("/////");
            	if (res.length==4){
            		id2=res[0]+" ( ) "+res[1];
            		if (!id2.equals(id1)){ 
            			if (!id1.equals(""))         		
            				edgeAttr.setAttribute(id1, "Interaction.bioExp",i_exp_a);            				
            			id1=id2;  
            			i_exp_a =res[3];
            		}
            		else            			 
            			i_exp_a +="; "+res[3];
            	}            	
            }
            
            //get location attribute
            line="";
            doQuery(I_LOCT_A);
            id1="";
            id2="";
            String i_loct_a="";
            while ((line = rd.readLine()) != null){
            	String [] res=line.split("/////");
            	if (res.length==4){
            		id2=res[0]+" ( ) "+res[1];
            		if (!id2.equals(id1)){ 
            			if (!id1.equals(""))         		
            				edgeAttr.setAttribute(id1, "Interaction.location",i_loct_a);            				
            			id1=id2;  
            			i_loct_a =res[3];
            		}
            		else            			 
            			i_loct_a +="; "+res[3];
            	}            	
            }
            
            //get provenance attribute
            line="";
            doQuery(I_PROV_A);
            id1="";
            id2="";
            String i_prov_a="";            
            while ((line = rd.readLine()) != null){            	
            	String [] res=line.split("/////");
            	if (res.length==6){            		
            		id2=res[0]+" ( ) "+res[1];
            		if (!id2.equals(id1)){ 
            			if (!id1.equals(""))         		
            				edgeAttr.setAttribute(id1, "Interaction.provenance",i_prov_a);            				
            			id1=id2;              			
            			if(QueryMiMI.sourceData.containsKey(res[3]))
            				i_prov_a =QueryMiMI.sourceData.get(res[3]);  
            			if (res[4].equals("10"))
            				i_prov_a +="[PubMed]"+res[5];
            			
            		}
            		else {               			
            			if ((QueryMiMI.sourceData.containsKey(res[3]))&& !i_prov_a.contains(QueryMiMI.sourceData.get(res[3])) )
            				i_prov_a +="; "+QueryMiMI.sourceData.get(res[3]);   
            			if (res[4].equals("10"))
            				i_prov_a +="[PubMed]"+res[5];
            		}            		
            	}  
            	
            }
            
            //get type attribute
            line="";
            doQuery(I_TYPE_A);
            id1="";
            id2="";
            String i_type_a="";
            while ((line = rd.readLine()) != null){
            	String [] res=line.split("/////");
            	if (res.length==4){
            		id2=res[0]+" ( ) "+res[1];
            		if (!id2.equals(id1)){ 
            			if (!id1.equals(""))         		
            				edgeAttr.setAttribute(id1, "Interaction.interactiontype",i_type_a);            				
            			id1=id2;  
            			i_type_a =res[3];
            		}
            		else            			 
            			i_type_a +="; "+res[3];
            	}            	
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
			//System.out.println(txt);
		}
		
	}
	
	
	private static void doQuery (int attrType){		
		String query="ID="+Keyword+"&TYPE="+attrType+"&ORGANISMID="+Organism+"&MOLECULETYPE="+MoleType+"&DATASOURCE="+DataSource+"&STEPS="+Steps+"&CONDITION="+Condition+"&FILTER="+Filter;
		//System.out.println("query is "+query) ;
		try {
			Proxy CytoProxyHandler = ProxyHandler.getProxyServer();
			URLConnection conn ;
			if (CytoProxyHandler == null) 
				conn = url.openConnection();
			else conn = url.openConnection(CytoProxyHandler);
			conn.setUseCaches(false);
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

}
