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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import javax.swing.JComboBox;
import javax.swing.JTextField;

import org.cytoscape.io.util.StreamUtil;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskMonitor;
import org.ncibi.cytoscape.mimi.enums.QueryType;
import org.ncibi.cytoscape.mimi.enums.SearchMethod;
import org.ncibi.cytoscape.mimi.plugin.MiMIURL;

/**
 * 
 * invoke QueryMiMIWrapper to show progress bar when query
 * @author Jing Gao
 */
public class SearchTask extends AbstractTask{
	private SearchMethod searchMethod;
	private JTextField textField;
	private JComboBox jcbOrganism;
	private JComboBox jcbMt;
	private JComboBox jcbDr;
	private JComboBox jcbIL;
	private StreamUtil streamUtil;
	private BuildNetworkTaskFactory buildNetworkTaskFactory;
	
	public SearchTask (SearchMethod searchMethod, JTextField textField ,JComboBox jcbOrganism ,JComboBox jcbMt,JComboBox jcbDr, JComboBox jcbIL,
			BuildNetworkTaskFactory buildNetworkTaskFactory, StreamUtil streamUtil){
		this.searchMethod=searchMethod;
		this.textField= textField;
		this.jcbOrganism=jcbOrganism;
		this.jcbMt=jcbMt;
		this.jcbDr=jcbDr;
		this.jcbIL=jcbIL;
		this.buildNetworkTaskFactory = buildNetworkTaskFactory;
		this.streamUtil = streamUtil;
	}

	@Override
	public void run(TaskMonitor taskMonitor) throws Exception {
		//System.out.println("text is "+textField.getText());
		if (textField.getText().length()>0){
			String keywords="";
			if (searchMethod != null){				
				if (searchMethod == SearchMethod.FREETEXT){
					String query="query="+URLEncoder.encode(textField.getText(),"UTF-8");
					//System.out.println("query is "+query);
					URLConnection uc = streamUtil.getURLConnection(new URL(MiMIURL.FREETEXTSEARCH));
					OutputStreamWriter wr = new OutputStreamWriter(uc.getOutputStream());
					wr.write(query);
					wr.close();
					BufferedReader rd = new BufferedReader(new InputStreamReader(uc.getInputStream()));
					String line="";
					if ((line=rd.readLine()) !=null)
						keywords=line;	
					rd.close();
				}
				if (searchMethod == SearchMethod.MESHTERM){
					String query="?qtype=mesh&term=" + URLEncoder.encode(textField.getText(),"UTF-8");
					String strURL =MiMIURL.MESHSEARCH+query;
					//								System.out.println("query is " + strURL);
					InputStream stream = streamUtil.getInputStream(strURL);
					BufferedReader rd = new BufferedReader(new InputStreamReader(stream));
					String line="";
					if ((line=rd.readLine()) !=null)
						keywords=line;	
					rd.close();
				}			
			}
			else keywords=textField.getText();	

			String inputStr=keywords+"/////"+jcbOrganism.getSelectedItem()+"/////"+jcbMt.getSelectedItem()+"/////"+jcbDr.getSelectedItem()+"/////"+jcbIL.getSelectedItem();
			//System.out.println("inputstr is "+inputStr);
			//generate network
			//run existing task...
			insertTasksAfterCurrentTask(buildNetworkTaskFactory.createTaskIterator(QueryType.QUERY_BY_NAME, inputStr));
		}
		else {
			throw new Exception("Please enter a search term."); 			
		}
	}

}
