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
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;

import org.cytoscape.io.util.StreamUtil;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyIdentifiable;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.ncibi.cytoscape.mimi.plugin.MiMIURL;


/** 
 * @author jinggao/ViewPubAnno
 * @date May 7, 2008
 */
@SuppressWarnings("serial")
public class ViewPublicAnnotation extends JFrame{	
	private String table;
	private String id;
	public ViewPublicAnnotation (CyIdentifiable obj, CyNetwork network, JFrame frame, StreamUtil streamUtil){		
		super("Annotation");
		try{
			String nodedgename=" ";
			if (obj instanceof CyNode ){				
				table="Node";
				CyNode node= (CyNode)obj;
				id=network.getRow(node).get(CyNetwork.NAME, String.class);
				nodedgename=network.getRow(node).get("Gene.name", String.class);				
			}
			if (obj instanceof CyEdge){				
				table="Edge";
				CyEdge edge= (CyEdge)obj;
				id=network.getRow(edge).get(CyNetwork.NAME, String.class);
				nodedgename=network.getRow(edge).get("Gene.name", String.class);	
			}
			URL url =new URL(MiMIURL.GETSHAREDANNOT);
			String query= "TABLE="+table+"&ID="+URLEncoder.encode(id,"UTF-8");
			URLConnection conn = streamUtil.getURLConnection(url);
			conn.setUseCaches(false);
			conn.setDoOutput(true);
			OutputStreamWriter wr= new OutputStreamWriter(conn.getOutputStream());
			wr.write(query);
			wr.flush();
			wr.close();
			BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));	
			String inputLine1;
			String result="";
			while ((inputLine1 = rd.readLine()) != null){
				//System.out.println("here ["+inputLine1);
				String[] elems=inputLine1.split("/////");
				int len = elems.length;
				if (len ==4 && !(elems[1].equals("")||elems[1].equals(" ")))
					result +="Annotation Created By "+elems[0]+":"+elems[3]+" on "+elems[2].substring(0, 11)+"\n"+elems[1]+"\n";
			}
			if (result.equals("")||result.equals(" "))
				result="There is no shared annotation for this "+table;
			
			rd.close();	
			//System.out.println("result is "+result);
			JPanel p1=new JPanel();
			p1.setBorder(new EmptyBorder(0,8,0,8));
			p1.setLayout(new GridLayout(0,1));			
			JLabel label= new JLabel("Shared Annotation for "+table+" "+nodedgename);			
			p1.add(label);
			p1.setPreferredSize(new Dimension(600,22));
			
			JPanel p2 =new JPanel();
			p2.setBorder(new EmptyBorder(0,8,0,8));
			p2.setLayout(new GridLayout(0,1));
			JTextArea content = new JTextArea(result);			
			JScrollPane scrollPane1 = 
			    new JScrollPane(content,
			                    JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
			                    JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
			content.setEditable(false);
			content.setText(result);			
			p2.add(scrollPane1);
			p2.setPreferredSize(new Dimension(600,180));
			
			Container cPane = getContentPane(); 
			JPanel parentPanel= new JPanel();
			parentPanel.setLayout(new BoxLayout(parentPanel,BoxLayout.Y_AXIS));
			parentPanel.add(p1);
			parentPanel.add(p2);			
			JScrollPane scrollCPane = 
			    new JScrollPane(parentPanel,
			                    JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
			                    JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			cPane.add(scrollCPane);	
			cPane.setPreferredSize(new Dimension(620,220));
			pack();
	        setVisible(true);
	        setLocationRelativeTo(frame);
		}
		catch (Exception e){
		}
	}

}
