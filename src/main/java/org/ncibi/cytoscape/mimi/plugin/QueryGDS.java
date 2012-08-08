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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import org.ncibi.cytoscape.mimi.util.URLConnect;
import org.ncibi.cytoscape.mimi.util.dynamicXpr.DynamicExpression;

import cytoscape.CyNode;
import cytoscape.Cytoscape;

/** 
 * @author jinggao/QueryGDS
 * @date Oct 19, 2008
 */
public class QueryGDS {
	protected String fileName="";	
	protected static String [] conditions;//={"gal1RG","gal4RG","gal80R"};
	protected static Vector <String> condV;
	protected static HashMap<String, String> exprvalHash;
	protected static HashMap<String,String> significanceHash;
	protected static String curcondition;
	private static   double maxExpr;
	private static   double minExpr;
		
	public static void doQuery(String gdsID){
		maxExpr=Double.MAX_VALUE;
		minExpr=Double.MIN_VALUE;	
		condV=new Vector<String>();
		exprvalHash=new HashMap<String, String>();
		significanceHash=new HashMap<String, String>();
		conditions=null;
		curcondition=null;		
		String urlStr=MiMIPlugin.QUERYGDS;
		URLConnect urlconn=new URLConnect();		
		String geneList=getNtwrkNodeID();
		if (gdsID != null && !gdsID.equals(" ") && geneList!=null && !geneList.equals(" ")){
		String query="GDSDATASET="+gdsID+"&GENEIDLIST="+geneList;
		//System.out.println("query is "+query);
		urlconn.doURLConnect(urlStr, query);
		//urlconn.doURLConnect(urlStr+"?"+ query);
		String line;
		try {
			while( (line=urlconn.getBrd().readLine())!=null){
				//System.out.println(line);
				processData(line);
			}			
			urlconn.closebrd();
			setConditions();
			resetDisplayFramesAction();
		}catch (Exception e){
			//System.out.println(e);
		}
		urlconn.closebrd();
		}	
	}
	private static String getNtwrkNodeID(){
		String nodeIdList="";
		Iterator <CyNode> it;
		Set <CyNode> nodeSet=Cytoscape.getCurrentNetwork().getSelectedNodes();		
		if (nodeSet.size()==0){
			List <CyNode> nodeList=Cytoscape.getCurrentNetwork().nodesList();
			it=nodeList.iterator();
		}
		else
			it=nodeSet.iterator();
			
			
		
		while (it.hasNext()) {
			CyNode node = it.next();
			String nid = node.getIdentifier();
			if (nid == null) {
				System.err
						.println("DynamicExpression.displayCondition, no unique identifier for node "
								+ node);
				continue;
			}
			nodeIdList += nid+" ";
			//System.out.println("all node ids ["+nodeIdList);
		}
		//System.out.println("all node ids ["+nodeIdList);
		return nodeIdList;
	}
	private static void processData(String line){
		String [] elems=line.split("/////");
		//System.out.println(line+"><len is :"+elems.length);
		if (elems.length !=MiMIPlugin.RTRNGDSEXPSIGCOLUMNNO){
			//System.out.println("Wrong line");
			return;
		}	
		//String subsetID= elems[0];
		String geneID=elems[1];		
		String condition=elems[2];
		String expr=elems[3];
		String sig ="0";
		if (!elems[4].equalsIgnoreCase(" ") && !elems[4].equalsIgnoreCase("null"))
			sig=elems[4];		
		//Sytem.out.println(geneID+condition+expr+sig);
		
		setMaxMinExpr(expr);			
		if (curcondition==null)	{		
			curcondition=condition;
			condV.add(condition);
		}
		else if (!curcondition.equalsIgnoreCase(condition)){
			curcondition=condition;		
			condV.add(condition);
		}
		
		String key=geneID+"#!"+condition;
		exprvalHash.put(key, expr);
		significanceHash.put(key,sig);
		
	}
	public static String [] getConditions(){		
		
		return conditions;
	}
	
	public static HashMap<String, String> getExprval(){
		return exprvalHash;
	}
	
	public static HashMap<String, String> getSignificance(){
		return significanceHash;
	}
	
	public String getExprTmpFileName(){
		return this.fileName;
	}

	public static double getMaxExpr(){
		return maxExpr;
	}
	
	public static double getMinExpr(){
		return minExpr;
	}
	
	private static void setMaxMinExpr(String sExpr){
		double dExpr=new Double(sExpr);		
		if (maxExpr==Double.MAX_VALUE && minExpr==Double.MIN_VALUE){
			maxExpr=dExpr;
			minExpr=maxExpr;
		}
		else{
			if (dExpr>maxExpr ) maxExpr=dExpr;
			if (dExpr<minExpr)  minExpr=dExpr;
		}
	}
	private static void setConditions(){
		conditions=new String[condV.size()];
		int i =0;
		for(String str : condV) {
			conditions[i++] = str;
		 }			
	}
	private static void resetDisplayFramesAction(){
		DynamicExpression.action=null;
	}
}
