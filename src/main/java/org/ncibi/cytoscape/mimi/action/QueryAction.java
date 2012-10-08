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
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import javax.swing.JOptionPane;

import org.cytoscape.application.swing.AbstractCyAction;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.io.util.StreamUtil;
import org.cytoscape.work.swing.DialogTaskManager;
import org.ncibi.cytoscape.mimi.plugin.MiMIState;
import org.ncibi.cytoscape.mimi.plugin.MiMIURL;
import org.ncibi.cytoscape.mimi.task.SearchTaskFactory;
import org.ncibi.cytoscape.mimi.task.UploadFileTaskFactory;
import org.ncibi.cytoscape.mimi.ui.MiMIDialog;


/**
 * Create a dialog for user to enter search term and get help and about mimi information
 * 
 * @author Jing Gao
 */
@SuppressWarnings("serial")
public class QueryAction extends AbstractCyAction{
	
	private MiMIDialog mimiDialog;
	private StreamUtil streamUtil;
	private CySwingApplication desktopApp;
	private SearchTaskFactory searchTaskFactory;
	private UploadFileTaskFactory uploadFileTaskFactory;
	private DialogTaskManager dialogTaskManager;
	
	public QueryAction(SearchTaskFactory searchTaskFactory, UploadFileTaskFactory uploadFileTaskFactory, DialogTaskManager dialogTaskManager, CySwingApplication desktopApp, StreamUtil streamUtil){
		super("Query");
		setPreferredMenu("Apps.MiMI Plugin");	
		
		this.searchTaskFactory = searchTaskFactory;
		this.uploadFileTaskFactory = uploadFileTaskFactory;
		this.dialogTaskManager = dialogTaskManager;
		this.desktopApp = desktopApp;
		this.streamUtil = streamUtil;
	}
    
    public void actionPerformed(ActionEvent e) { 	
    	
    	if (mimiDialog == null) {
            try{
            	URL url = new URL(MiMIURL.CHECKPLUGINVERSION);        	
            	URLConnection conn = streamUtil.getURLConnection(url);
            	conn.setUseCaches(false);			    
            	BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));	 
            	String line;
            	if ((line = rd.readLine()) != null) {
            		if (line.compareTo(MiMIState.CURRENTPLUGINVERSION)>0){
			  				JOptionPane.showMessageDialog(desktopApp.getJFrame(), "You are using old version, Please update to "+"MiMI Plugin "+ line+" from within Cytoscape (Plugins->Update Plugins->MiMIPlugin "+line+")");		
			  			}
			  			
            	}
            	rd.close();	
            }
            catch(Exception ve){
//            	System.out.println(ve);
            }
//            CyLayoutAlgorithm layout = CyLayouts.getLayout("force-directed");
//    		Tunable discrete = layout.getSettings().get("discrete");
//    		if(discrete != null) {
//    			discrete.setValue(true);
//    			layout.updateSettings();
//    		}
            mimiDialog = new MiMIDialog(searchTaskFactory, uploadFileTaskFactory, dialogTaskManager, desktopApp.getJFrame());            
           // mimiDialog=null;
        }
    	// if there is a current network and current view,
    	// and if any nodes are selected... 
    	// use the selected nodes to prime the serach
		//mimiDialog.getSearchGenesFromSelection();

    	mimiDialog.setVisible(true);
    	
   }

}
