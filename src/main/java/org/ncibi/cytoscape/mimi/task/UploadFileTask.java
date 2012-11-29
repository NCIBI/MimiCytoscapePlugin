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
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JFrame;

import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.swing.DialogTaskManager;

/** 
 * @author jinggao/ExcuteUploadFile
 * @date Oct 11, 2007
 * update on Nov 19, 2007 accept species etc via both GUI and file header
 */
public class UploadFileTask extends AbstractTask{
	private static Map<String,String> verorgnismHM = new HashMap<String, String>();
	private File file;
	private SearchTaskFactory searchTaskFactory;
	private DialogTaskManager dialogTaskManager;
	private JFrame frame;
	private String organism;
	private String moleculeType;
	private String dataSource;
	private String interactionLevel;		
	private String MoleculeTypes=" protein mRNA gene pseudo chemical DNA";
	private String DataSources=" BIND CCSB DIP HPRD IntAct KEGG MDC MINT PubMed reactome";
	
	static {
		verorgnismHM.put("6239","Caenorhabditis elegans");
		verorgnismHM.put("7227","Drosophila melanogaster");
		verorgnismHM.put("562","Escherichia coli");	
		verorgnismHM.put("9606","Homo sapiens");
		verorgnismHM.put("10090","Mus musculus");
		verorgnismHM.put("10116","Rattus norvegicus");
		verorgnismHM.put("4932","Saccharomyces cerevisiae");	
		verorgnismHM.put("3702","Arabidopsis thaliana");
	}
	
	public UploadFileTask(File file, SearchTaskFactory searchTaskFactory, DialogTaskManager dialogTaskManager, JFrame frame) {
		this.file = file;
		this.searchTaskFactory = searchTaskFactory;
		this.dialogTaskManager = dialogTaskManager;
		this.frame = frame;
	}
	
	@Override
	public void run(TaskMonitor taskMonitor) throws Exception { 
		try{
			BufferedReader br=new BufferedReader (new FileReader(file));
			boolean fileheader=false;
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
						if (verorgnismHM.containsKey(line1[1].trim()))
							organism=verorgnismHM.get(line1[1].trim());          						
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
			new org.ncibi.cytoscape.mimi.ui.UploadFileDialog(fileheader,genelist,organism,moleculeType,dataSource,interactionLevel,searchTaskFactory,dialogTaskManager,frame);
		}

		catch(Exception ex){
			if (ex.toString().startsWith("java.lang.NumberFormatException"))
				throw new Exception("Please check your upload file Format!");
			else
				throw ex;
		}
	}
} 
