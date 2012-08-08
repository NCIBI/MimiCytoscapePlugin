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

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.*;
//import javax.swing.JCheckBox;
//import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
//import org.ncibi.cytoscape.plugin.mimiR2.plugin.QueryMiMIWrapper;
import org.ncibi.cytoscape.mimi.plugin.QueryMiMI;

import cytoscape.Cytoscape;

/** 
 * @author jinggao/ExcuteUploadFile
 * @date Oct 11, 2007
 * update on Nov 19, 2007 accept species etc via both GUI and file header
 */
public class ExcuteUploadFile implements ActionListener{
	private  String organism;
	private  String moleculeType;
	private  String dataSource;
	private  String interactionLevel;		
	private  JFrame frame;	
	private  String MoleculeTypes=" protein mRNA gene pseudo chemical DNA";
	private  String DataSources=" BIND CCSB DIP HPRD IntAct KEGG MDC MINT PubMed reactome";
	
	public ExcuteUploadFile(JFrame Frame){		
		frame=Frame;			
	}
	public void actionPerformed(ActionEvent e){			
  		frame.setVisible(false);  		
  		JFileChooser fc=new JFileChooser();
  		int returnVal = fc.showOpenDialog(frame);  		
  		boolean fileheader =false; 
  		
        if (returnVal == JFileChooser.APPROVE_OPTION) {
        	try{
          			File file = fc.getSelectedFile();
          			BufferedReader br=new BufferedReader (new FileReader(file));
          			String line;	                  			
          			String genelist="";
          			organism="";
          			moleculeType="";
          			dataSource="";
          			interactionLevel="";		
          			
          			while ((line=br.readLine()) !=null){ 
          				//System.out.println("line is "+line);
          				if (line.contains("=")){
          					String [] line1=line.split("=");
          					if (line1.length==2 && line1[0].trim().equalsIgnoreCase("taxonomyID")){
          						if (QueryMiMI.verorgnismHM.containsKey(line1[1].trim()))
          							organism=QueryMiMI.verorgnismHM.get(line1[1].trim());          						
           						else organism="All Organisms";  
          						fileheader=true;
          					}
          					if (line1.length==2 && line1[0].trim().equalsIgnoreCase("moleculeType")){
          						if (MoleculeTypes.contains(" " +line1[1].trim().toLowerCase()+" "))
          							moleculeType=line1[1].trim();           							
          						else moleculeType="All Molecule Types";
          						fileheader=true;
          					}
          					if (line1.length==2 && line1[0].trim().equalsIgnoreCase("dataSource")){
          						if (DataSources.toLowerCase().contains(" " +line1[1].trim().toLowerCase() +" "))
          							dataSource=line1[1].trim();          							
          						else dataSource="All Data Sources";
          						fileheader=true;
          					}
          					if (line1.length==2 && line1[0].trim().equalsIgnoreCase("DisplaysForResult")){
          						if (Integer.parseInt(line1[1].trim())>=1 && Integer.parseInt(line1[1].trim())<=2)
          							interactionLevel=line1[1].trim();          							
          						else interactionLevel="1";
          						fileheader=true;
          					}
          					
          				}
          				else if (!line.trim().equals(""))
          					genelist +=line.trim()+" ";		                  					             					
          			}
          			br.close();          			
          			new org.ncibi.cytoscape.mimi.ui.UploadFileDialog(fileheader,genelist,organism,moleculeType,dataSource,interactionLevel);
          			
          	}
          	catch(Exception ex){
          		if (ex.toString().startsWith("java.lang.NumberFormatException"))
          			JOptionPane.showMessageDialog(Cytoscape.getDesktop(),"Please check your upload file Format!");
//          		System.out.println(ex);
          	}
          }
  	} 
	
	
}
