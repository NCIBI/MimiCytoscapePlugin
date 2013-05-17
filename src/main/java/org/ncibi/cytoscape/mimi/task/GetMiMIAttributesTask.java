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
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.Collection;
import java.util.HashMap;

import org.cytoscape.io.util.StreamUtil;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyRow;
import org.cytoscape.model.CyTable;
import org.cytoscape.model.subnetwork.CyRootNetwork;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskMonitor;
import org.ncibi.cytoscape.mimi.MiMI;

/**
 * @author jinggao/AttributesByIDs
 * @date Oct 1, 2007
 */
public class GetMiMIAttributesTask extends AbstractTask
{
    private BufferedReader rd;
    private Collection<CyNode> nodes;
    private Collection<CyEdge> edges;
    private CyNetwork network;
    private StreamUtil streamUtil;
    private CyTable nodeTable;
    private CyTable edgeTable;
    private CyTable geneTable;
    private CyTable interactionTable;

    // GENE related attributes
    private final static int GENE_BASIC_ATTR = 1;
    private final static int GENE_GO_ATTR = 2;
    private final static int GENE_GENEALIAS_ATTR = 3;
    private final static int GENE_PATHWAY_ATTR = 4;

    // INTERACTION related attributes
    private final static int INT_TYPE_ATTR = 21;
    private final static int INT_GO_ATTR = 22;

    
    private final static HashMap<Integer, String> ATTRTYPE;

    static
    {
        ATTRTYPE = new HashMap<Integer, String>();
        ATTRTYPE.put(GENE_BASIC_ATTR, "[GENE] Organism, type, description etc");
        ATTRTYPE.put(GENE_GO_ATTR, "[GENE] Go term");
        ATTRTYPE.put(GENE_GENEALIAS_ATTR, "[GENE] Alias");
        ATTRTYPE.put(GENE_PATHWAY_ATTR, "[GENE] Pathway");
        ATTRTYPE.put(INT_TYPE_ATTR, "[INTERACTION] type, provenance, pubmed ");
        ATTRTYPE.put(INT_GO_ATTR, "[INTERACTION] Go term");
    }
    
    public GetMiMIAttributesTask(Collection<CyNode> nodes, Collection<CyEdge> edges, CyNetwork network, StreamUtil streamUtil) {
		this.nodes = nodes;
		this.edges = edges;
		this.network = network;
		this.streamUtil = streamUtil;
		
		this.nodeTable = network.getDefaultNodeTable();
    	this.edgeTable = network.getDefaultEdgeTable();
    	this.geneTable = nodeTable.getColumn("ID").getVirtualColumnInfo().getSourceTable();
    	this.interactionTable = edgeTable.getColumn("ID").getVirtualColumnInfo().getSourceTable();
	}
    
    @Override
	public void run(TaskMonitor arg0) throws Exception {
    	String line;
    	Integer geneid;
    	Integer intid;
    	CyRow row;
    	
    	String geneIDs = "";
		for(CyNode node: nodes) {
			geneIDs += network.getRow(node).get("ID", Integer.class) + " ";
		}
		geneIDs = geneIDs.trim();
		
		String interIDs = "";
		for(CyEdge edge: edges) {
			interIDs += network.getRow(edge).get("ID", Integer.class) + " ";
		}
		interIDs = interIDs.trim();

    	// ************GET GENE related attributes
    	// get gene basic attributes: taxID, organism, chromosome, map_loc,
    	// locustag, type, description
    	line = "";
    	doQuery(GENE_BASIC_ATTR, geneIDs, interIDs);

    	while ((line = rd.readLine()) != null)
    	{
    		String[] res = line.split("/////");
    		if (res.length == 3)
    		{
    			try {
    				geneid = Integer.valueOf(res[0]);
    			}
    			catch(NumberFormatException e) {
    				continue;
    			}
    			String geneattr = Character.toUpperCase(res[1].charAt(0)) + res[1].substring(1);
				if(nodeTable.getColumn(geneattr) == null) {
					geneTable.createColumn(geneattr, String.class, true);
					nodeTable.addVirtualColumn(geneattr, geneattr, geneTable, CyRootNetwork.SHARED_NAME, true);
				}
				if (!geneid.equals("")) {
    				Collection<CyRow> rows=nodeTable.getMatchingRows("ID", geneid);
    				row = rows.iterator().next();
    				row.set(geneattr, res[2]);
    			}
    		}
    	}

    	rd.close();

    	// get gene GO info
    	line = "";
    	row = null;
    	Integer curgeneid = null;
    	String curctgry = "";
    	String goterm = "";
    	boolean isfirstline = true;
    	doQuery(GENE_GO_ATTR, geneIDs, interIDs);
    	while ((line = rd.readLine()) != null)
    	{
    		String[] res = line.split("/////");
    		if (res.length == 3)
    		{
    			try {
    				geneid = Integer.valueOf(res[0]);
    			}
    			catch(NumberFormatException e) {
    				continue;
    			}
    			String category = res[2].trim();
    			String go_term = res[1].trim();
    			if (!isfirstline)
    			{
    				if (curgeneid == geneid && curctgry.equalsIgnoreCase(category))
    					goterm += go_term + "; ";

    					if (curgeneid!=geneid || !curctgry.equalsIgnoreCase(category))
    					{
    						if(nodeTable.getColumn(curctgry) == null) {
    							geneTable.createColumn(curctgry, String.class, true);
    							nodeTable.addVirtualColumn(curctgry, curctgry, geneTable, CyRootNetwork.SHARED_NAME, true);
    						}
    						row.set(curctgry, goterm.substring(0, goterm.length() - 2));
    						goterm = go_term + "; ";
    						curgeneid = geneid;
    						curctgry = category;
    					}
    			}
    			else
    			{
    				Collection<CyRow> rows=nodeTable.getMatchingRows("ID", geneid);
    				row = rows.iterator().next();
    				isfirstline = false;
    				curgeneid = geneid;
    				curctgry = category;
    				goterm = go_term + "; ";
    			}
    		}
    	}
    	rd.close();
    	if (row != null && goterm.length() > 2) {
			if(nodeTable.getColumn(curctgry) == null) {
				geneTable.createColumn(curctgry, String.class, true);
				nodeTable.addVirtualColumn(curctgry, curctgry, geneTable, CyRootNetwork.SHARED_NAME, true);
			}
			row.set(curctgry, goterm.substring(0, goterm.length() - 2));
    	}

    	// get gene alias
    	line = "";
    	curgeneid = null;
    	row = null;
    	String aliasList = "";
    	isfirstline = true;
    	doQuery(GENE_GENEALIAS_ATTR, geneIDs, interIDs);
    	if(nodeTable.getColumn("Other Names") == null) {
    		geneTable.createColumn("Other Names", String.class, true);
    		nodeTable.addVirtualColumn("Other Names", "Other Names", geneTable, CyRootNetwork.SHARED_NAME, true);
    	}
    	while ((line = rd.readLine()) != null)
    	{
    		String[] res = line.split("/////");
    		if (res.length == 2)
    		{
    			try {
    				geneid = Integer.valueOf(res[0]);
    			}
    			catch(NumberFormatException e) {
    				continue;
    			}
    			String alias = res[1].trim();
    			if (!isfirstline)
    			{
    				if (curgeneid == geneid)
    					aliasList += alias + "; ";
    					else
    					{
    						row.set("Other Names",
    								aliasList.substring(0, aliasList.length() - 2) + " ]");
    						aliasList = "[" + alias + "; ";
    						curgeneid = geneid;
    					}
    			}
    			else
    			{
    				Collection<CyRow> rows = nodeTable.getMatchingRows("ID", geneid);
					row = rows.iterator().next();
    				isfirstline = false;
    				curgeneid = geneid;
    				aliasList = "[" + alias + "; ";
    			}
    		}
    	}
    	rd.close();
    	if (aliasList.length() > 2)
    		row.set("Other Names",
					aliasList.substring(0, aliasList.length() - 2) + " ]");

    	// get gene pathway info
    	doQuery(GENE_PATHWAY_ATTR, geneIDs, interIDs);
    	setPathwayAttributes();

    	// ***************get INTERACTION related attributes
    	// get interaction type
    	line = " ";
    	row = null;
    	Integer curintid = null;
    	String curtattr = " ";
    	String curattrV = " ";
    	boolean isfirstlineType = true;
    	doQuery(INT_TYPE_ATTR, geneIDs, interIDs);
    	while ((line = rd.readLine()) != null)
    	{
    		String[] res = line.split("/////");
    		if (res.length == 3)
    		{
    			try {
    				intid = Integer.valueOf(res[0]);
    			}
    			catch(NumberFormatException e) {
    				continue;
    			}
    			String attr = res[1];
    			String attrV = res[2];
    			if (!isfirstlineType)
    			{
    				if (curintid == intid && curtattr.equalsIgnoreCase(attr))
    					curattrV += attrV + "; ";

    					if (curintid != intid || !curtattr.equalsIgnoreCase(attr))
    					{
    						if(edgeTable.getColumn(curtattr) == null) {
    							interactionTable.createColumn(curtattr, String.class, true);
    							edgeTable.addVirtualColumn(curtattr, curtattr, interactionTable, CyRootNetwork.SHARED_NAME, true);
    						}
    						row.set(curtattr,"[" + curattrV.substring(0, curattrV.length() - 2) + "]");
    						curintid = intid;
    						curtattr = attr;
    						curattrV = attrV + "; ";
    					}
    			}
    			else
    			{
    				Collection<CyRow> rows = edgeTable.getMatchingRows("ID", intid);
    				row = rows.iterator().next();
    				isfirstlineType = false;
    				curintid = intid;
    				curtattr = attr;
    				curattrV = attrV + "; ";
    			}

    		}
    	}

    	rd.close();
    	if (curattrV.length() > 2)
    		row.set(curtattr,
    				"[" + curattrV.substring(0, curattrV.length() - 2) + "]");
	}

    private void setPathwayAttributes() throws IOException
    {
        // get gene pathway info
        String line = "";
        String description = "";
        Integer geneid = null;
        Integer curgeneid = null;
        CyRow row = null;
        Boolean isfirstline = true;
        
        if(nodeTable.getColumn("Pathway") == null) {
        	geneTable.createColumn("Pathway", String.class, true);
        	nodeTable.addVirtualColumn("Pathway", "Pathway", geneTable, CyRootNetwork.SHARED_NAME, true);
        }
        while ((line = rd.readLine()) != null)
        {
            String[] res = line.split("/////");
            if (res.length == 3)
            {
            	try {
    				geneid = Integer.valueOf(res[0]);
    			}
    			catch(NumberFormatException e) {
    				continue;
    			}
                String attrValue = res[2].trim();
                if (!isfirstline)
                {
                    if (curgeneid == geneid)
                    {
                        description += "; " + attrValue;
                    }
                    else if (curgeneid != geneid)
                    {
                        row.set("Pathway", description);
                        curgeneid = geneid;
                        description = attrValue;
                    }
                }
                else
                {	
                	Collection<CyRow> rows = network.getDefaultNodeTable().getMatchingRows("ID",geneid);
    				row = rows.iterator().next();
                    isfirstline = false;
                    curgeneid = geneid;
                    description += attrValue;
                }
            }
        }
        rd.close();
        if (description.length() > 2)
        	row.set("Pathway", description);
    }

    private void doQuery(int attrType, String gene_IDs, String inter_IDs) throws Exception
    {
        String query;
        if (attrType < 20)
            query = "TYPE=" + attrType + "&GENEIDS=" + gene_IDs + "&INTERACTIONIDS=";
        else
            query = "TYPE=" + attrType + "&GENEIDS=" + "&INTERACTIONIDS=" + inter_IDs;
            URLConnection conn = streamUtil.getURLConnection(new URL(MiMI.GETATTRIBUTES));
            conn.setDoOutput(true);
            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            wr.write(query);
            wr.flush();

            // Get the response
            rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            wr.close();
    }
}
