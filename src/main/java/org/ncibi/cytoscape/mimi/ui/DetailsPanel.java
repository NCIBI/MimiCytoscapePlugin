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
 
package org.ncibi.cytoscape.mimi.ui;
import cytoscape.Cytoscape;
import java.awt.Container;
import java.awt.Dimension;
import java.net.URL;
import java.util.HashMap;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.event.HyperlinkListener;
import javax.swing.event.HyperlinkEvent;

import org.ncibi.cytoscape.mimi.plugin.MiMIPlugin;
import org.ncibi.cytoscape.mimi.util.BareBonesBrowserLaunch;


/**
 * @author Jing Gao
 */
@SuppressWarnings("serial")
public class DetailsPanel extends JFrame implements  HyperlinkListener{
	
	 public static final int WIDTH =800;
	 public static final int HEIGHT = 350;
	 public static HashMap<Integer, String> Id2Sent;
	 public static HashMap<Integer, String>Id2PubmedID;
	 public static HashMap<Integer, String>Id2Section;
	 private JEditorPane editorPane;
	 private String strResult;
	 //private String molecule1, molecule2, moleculeID1, moleculeID2;
	 public static String molecule1, molecule2, moleculeID1, moleculeID2;
	 public static URL url;
	 public static int SortsentencesNumber;
	 public final static int   SORTLIMIT=20;
	 //private int sentenceNumber;
	 
	 public DetailsPanel(String str, String mol1, String mol2, String molID1, String molID2){		
		 super("Cytoscape MiMI Plugin Query BioNLP");	
		    strResult =str;
		    molecule1=mol1;
		    molecule2=mol2;
		    moleculeID1=molID1;
		    moleculeID2=molID2;
	        Container contentPane = getContentPane();   	       
	        //Create an editor pane.	       
	        editorPane = new JEditorPane();
	        editorPane.setEditable(false); 
	        editorPane.setContentType("text/html"); 
	        editorPane.setPreferredSize(new Dimension(WIDTH, HEIGHT));	        
	        editorPane.addHyperlinkListener(this);	        
	        editorPane.setText(getHtml());   
	        editorPane.setCaretPosition(0);
	        JScrollPane editorScrollPane = new JScrollPane(editorPane);
	        editorScrollPane.setVerticalScrollBarPolicy(
	                        JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
	        editorScrollPane.setHorizontalScrollBarPolicy(
	        				JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS); 	     
	        contentPane.add(editorScrollPane);	     
	        pack();
	        setVisible(true);
	        setLocationRelativeTo(Cytoscape.getDesktop());
    }
    
    private String getHtml() {    	
        StringBuffer html = new StringBuffer();
        Id2Sent=new HashMap<Integer, String> ();
        Id2PubmedID=new HashMap<Integer, String>();
        Id2Section=new HashMap<Integer, String>();
        String[] lines=strResult.split("\n");
        int sentenceNumber = lines.length;
        SortsentencesNumber=(sentenceNumber >= SORTLIMIT) ? SORTLIMIT : sentenceNumber;
        html.append("<HTML><BODY><CENTER><H1><FONT COLOR='navy'>"+sentenceNumber+" Sentences Related To ["+molecule1+"] And ["+molecule2+"] From BioNLP</FONT></H1></CENTER><FONT FACE=ARIAL><TABLE>");
        html.append("<TR><TD><STRONG>PubmedID</STRONG></TD><TD><STRONG>Section</STRONG></TD><TD><STRONG>Symbol</TD><TD><STRONG>Symbol</STRONG></TD><TD><STRONG>Sentence</STRONG></TD></TR>");
       

        for (int i=0;i<sentenceNumber;i++){
        	String[] chunk=lines[i].split("\t");        	
        	html.append("<TR><TD><A HREF='http://www.ncbi.nlm.nih.gov/entrez/query.fcgi?db=pubmed&cmd=search&term="+chunk[0]+"'>"+chunk[0]+"</A></TD>");
        	html.append("<TD>"+chunk[1]+"</TD>");
        	html.append("<TD><A HREF='"+MiMIPlugin.MIMINODELINK+"?geneid="+moleculeID1+"'>"+chunk[2]+"</A></TD>");
        	html.append("<TD><A HREF='"+MiMIPlugin.MIMINODELINK+"?geneid="+moleculeID2+"'>"+chunk[3]+"</A></TD>");        	
        	html.append("<TD>"+chunk[4]+"</TD>");        	       	
        	html.append("</TR>");  
        	
        	if(i<SortsentencesNumber){
        	Id2Sent.put(i, chunk[4]);
        	Id2PubmedID.put(i,chunk[0]);
        	Id2Section.put(i, chunk[1]);
        	//System.out.println("["+chunk[4]+"]");
        	}
        }        
        html.append("</TABLE></FONT></BODY><HTML>");
        return html.toString();
       
    }
  
    public void hyperlinkUpdate(HyperlinkEvent evt) {
        url = evt.getURL();        
        if (url != null) {        	
            if (evt.getEventType()
                    == HyperlinkEvent.EventType.ACTIVATED) {
            	BareBonesBrowserLaunch.openURL(url.toString());
            }
        }
    }    
}
