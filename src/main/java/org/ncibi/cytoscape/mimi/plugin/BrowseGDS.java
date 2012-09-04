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

import java.awt.Container;
import java.awt.Dimension;
import java.net.URL;

import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import org.cytoscape.io.util.StreamUtil;
import org.ncibi.cytoscape.mimi.util.BareBonesBrowserLaunch;
import org.ncibi.cytoscape.mimi.util.URLConnect;

/** 
 * @author jinggao/BrowseGDS
 * @date Oct 19, 2008
 */
public class BrowseGDS extends JFrame implements  HyperlinkListener{
	public static final int WIDTH =1024;
	public static final int HEIGHT = 768;
	private JEditorPane editorPane;
	private StringBuffer html;
	private URL url;
	
	
	 
	public BrowseGDS(JFrame frame){
		super("Browse GDS");
		Container contentPane = getContentPane();   	       
        //Create an editor pane.	       
        editorPane = new JEditorPane();
        editorPane.setEditable(false); 
        editorPane.setContentType("text/html"); 
        editorPane.setPreferredSize(new Dimension(WIDTH, HEIGHT));	        
        editorPane.addHyperlinkListener(this);	        
        editorPane.setText(retrieveData());   
        editorPane.setCaretPosition(0);
        JScrollPane editorScrollPane = new JScrollPane(editorPane);
        editorScrollPane.setVerticalScrollBarPolicy(
                        JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        editorScrollPane.setHorizontalScrollBarPolicy(
        				JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS); 	     
        contentPane.add(editorScrollPane);	     
        pack();
        setVisible(true);
        setLocationRelativeTo(frame);
		//System.out.println("Browse GDS Dataset");
	}
	private String retrieveData(){
		String urlStr=MiMIPlugin.BROWSEGDS;
		html = new StringBuffer();
		html.append("<HTML><BODY><CENTER><H1><FONT COLOR='navy'>GDS DATASET</FONT></H1><TABLE border='1'>");
	    html.append("<TR><TD><STRONG>DatasetID</STRONG></TD><TD><STRONG>Title</STRONG></TD><TD><STRONG>Description</TD><TD><STRONG>Type</STRONG></TD><TD><STRONG>Pubmed</STRONG></TD>");
	    html.append("<TD><STRONG>Organism</STRONG></TD><TD><STRONG>SampleType</STRONG></TD><TD><STRONG>SampleCount</STRONG></TD><TD><STRONG>ValueType</TD><TD><STRONG>Update</STRONG></TD></TR>");
	    URLConnect urlconn=new URLConnect();
	    urlconn.doURLConnect(urlStr);
	    String line;
	    try {
	    	while ((line=urlconn.getBrd().readLine()) != null)
	    		appendLine(line);	           
	    	html.append("</TABLE></FONT></BODY><HTML>");
	    	//System.out.println(html.toString());	    	
	    }
	    catch(Exception e){
	    	//System.out.println(e);
	    }
	    urlconn.closebrd();
	    return html.toString();
	}
	private void appendLine (String line){
		String [] elems=line.split("/////");
		//System.out.println(line);
		//System.out.println(elems.length);
		//System.out.println("pubmed is <"+elems[4]+">");
		html.append("<TR><TD><a href='http://www.ncbi.nlm.nih.gov/geo/gds/gds_browse.cgi?gds="+elems[0].substring(3)+"'>"+elems[0]+"</a></TD><TD>"+elems[1]+"</TD><TD>"+elems[2]+"</TD><TD>"+elems[3]+"</TD>");
		//if (!elems[4].equals(""))
			html.append("<TD a href='http://www.ncbi.nlm.nih.gov/sites/entrez?db=pubmed&cmd=search&term="+elems[4]+"'>"+elems[4]+"</a></TD>");
		//else html.append("<TD>"+elems[4]+"</TD>");
	    html.append("<TD>"+elems[5]+"</TD><TD>"+elems[6]+"</TD><TD>"+elems[7]+"</TD><TD>"+elems[8]+"</TD><TD>"+elems[9]+"</TD></TR>");
	    
		
	}
	public void hyperlinkUpdate(HyperlinkEvent evt){
		url = evt.getURL();        
        if (url != null)         	
            if (evt.getEventType()
                    == HyperlinkEvent.EventType.ACTIVATED) 
            	 BareBonesBrowserLaunch.openURL(url.toString());        
	}

}
