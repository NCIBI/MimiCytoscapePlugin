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

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import org.ncibi.cytoscape.mimi.plugin.MiMIPlugin;
import org.ncibi.cytoscape.mimi.ui.MiMIDialog;

import cytoscape.layout.CyLayoutAlgorithm;
import cytoscape.layout.CyLayouts;
import cytoscape.layout.Tunable;
import cytoscape.view.CytoscapeDesktop;
import cytoscape.Cytoscape;


/**
 * Create a dialog for user to enter search term and get help and about mimi information
 * 
 * @author Jing Gao
 */
public class ImportFromMiMI implements ActionListener{
	
	private MiMIDialog mimiDialog;    
    
    public void actionPerformed(ActionEvent e) { 	
    	
    	if (mimiDialog == null) {
            CytoscapeDesktop desktop = Cytoscape.getDesktop();
            //check new version
            try{
            	URL url = new URL(MiMIPlugin.CHECKPLUGINVERSION);        	
            	URLConnection conn = url.openConnection();
            	conn.setUseCaches(false);			    
            	BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));	 
            	String line;
            	if ((line = rd.readLine()) != null) {
            		if (line.compareTo(MiMIPlugin.CURRENTPLUGINVERSION)>0){
			  				JOptionPane.showMessageDialog(desktop, "You are using old version, Please update to "+"MiMI Plugin "+ line+" from within Cytoscape (Plugins->Update Plugins->MiMIPlugin "+line+")");		
			  			}
			  			
            	}
            	rd.close();	
            }
            catch(Exception ve){
//            	System.out.println(ve);
            }
            CyLayoutAlgorithm layout = CyLayouts.getLayout("force-directed");
    		Tunable discrete = layout.getSettings().get("discrete");
    		if(discrete != null) {
    			discrete.setValue(true);
    			layout.updateSettings();
    		}
            mimiDialog = new MiMIDialog((JFrame) desktop);            
           // mimiDialog=null;
        }
    	// if there is a current network and current view,
    	// and if any nodes are selected... 
    	// use the selected nodes to prime the serach
		//mimiDialog.getSearchGenesFromSelection();

    	mimiDialog.setVisible(true);
    	
   }

}
