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
 
 package org.ncibi.cytoscape.mimi.action;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URLEncoder;

import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import org.cytoscape.io.util.StreamUtil;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyNetworkManager;
import org.ncibi.cytoscape.mimi.enums.QueryType;
import org.ncibi.cytoscape.mimi.enums.SearchMethod;
import org.ncibi.cytoscape.mimi.plugin.CyActivator;
import org.ncibi.cytoscape.mimi.plugin.MiMIURL;
import org.ncibi.cytoscape.mimi.plugin.QueryMiMIWrapper;
import org.ncibi.cytoscape.mimi.util.URLConnect;

/**
 * 
 * invoke QueryMiMIWrapper to show progress bar when query
 * @author Jing Gao
 */
public class ExecuteSearchAction implements ActionListener{
	private JTextField textField;
	private JComboBox jcbOrganism;
	private JComboBox jcbMt;
	private JComboBox jcbDr;
	private JComboBox jcbIL;
	//private JCheckBox jcheckBox;
	private JFrame frame;	
	private SearchMethod searchMethod;
	private StreamUtil streamUtil;
	private CyNetworkFactory cyNetworkFactory;
	private CyNetworkManager cyNetworkManager;
	
	
	public ExecuteSearchAction (JTextField textField ,JComboBox jcbOrganism ,JComboBox jcbMt,JComboBox jcbDr, JComboBox jcbIL, JFrame frame, 
			StreamUtil streamUtil, CyNetworkFactory cyNetworkFactory, CyNetworkManager cyNetworkManager ){
		//freetextSearch=false;
		searchMethod=null;
		this.textField= textField;
		this.jcbOrganism=jcbOrganism;
		this.jcbMt=jcbMt;
		this.jcbDr=jcbDr;
		this.jcbIL=jcbIL;
		//jcheckBox=jcheckbox;
		this.frame=frame;
		this.streamUtil = streamUtil;
		this.cyNetworkFactory = cyNetworkFactory;
		this.cyNetworkManager = cyNetworkManager;
	}
	
	public ExecuteSearchAction (SearchMethod searchMethod, JTextField textField ,JComboBox jcbOrganism ,JComboBox jcbMt,JComboBox jcbDr, JComboBox jcbIL, JFrame frame,
			StreamUtil streamUtil, CyNetworkFactory cyNetworkFactory, CyNetworkManager cyNetworkManager){
		//freetextSearch=FreetextSearch;
		this.searchMethod=searchMethod;
		this.textField= textField;
		this.jcbOrganism=jcbOrganism;
		this.jcbMt=jcbMt;
		this.jcbDr=jcbDr;
		this.jcbIL=jcbIL;
		//jcheckBox=jcheckbox;
		this.frame=frame;
		this.streamUtil = streamUtil;
		this.cyNetworkFactory = cyNetworkFactory;
		this.cyNetworkManager = cyNetworkManager;		
	}
	
	public void actionPerformed(ActionEvent event) {		
		//System.out.println("text is "+textField.getText());
		frame.setVisible(false);
		if (textField.getText().length()>0){
			String keywords="";
			if (searchMethod != null){				
				try{
					if (searchMethod == SearchMethod.FREETEXT){
						String query="query="+URLEncoder.encode(textField.getText(),"UTF-8");
						//System.out.println("query is "+query);
						URLConnect uc=new URLConnect();
						uc.doURLConnect(MiMIURL.FREETEXTSEARCH, query);
						String line="";
						if ((line=uc.getBrd().readLine()) !=null)
							keywords=line;	
						uc.closebrd();
					}
					if (searchMethod == SearchMethod.MESHTERM){
						String query="?qtype=mesh&term=" + URLEncoder.encode(textField.getText(),"UTF-8");
						String strURL =MiMIURL.MESHSEARCH+query;
//						System.out.println("query is " + strURL);
						URLConnect uc=new URLConnect();
						uc.doURLConnect(strURL);
						String line="";
						if ((line=uc.getBrd().readLine()) !=null)
							keywords=line;	
						uc.closebrd();
					}
				}			
				catch (Exception e){
//					System.out.println(e.getMessage());
				}				
			}
			else keywords=textField.getText();	
			
			String inputStr=keywords+"/////"+jcbOrganism.getSelectedItem()+"/////"+jcbMt.getSelectedItem()+"/////"+jcbDr.getSelectedItem()+"/////"+jcbIL.getSelectedItem();
			//System.out.println("inputstr is "+inputStr);
			//generate network
			//run existing task...
			//new QueryMiMIWrapper(QueryType.QUERY_BY_NAME, inputStr, cyNetworkFactory, cyNetworkManager, frame, streamUtil);			
		}
		else{
			JOptionPane.showMessageDialog(frame,"Please enter a search term."); 			
		}
		
	}

}
