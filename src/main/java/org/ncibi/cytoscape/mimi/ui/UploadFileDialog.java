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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.cytoscape.work.TaskIterator;
import org.cytoscape.work.swing.DialogTaskManager;
import org.ncibi.cytoscape.mimi.task.SearchTaskFactory;

/** 
 * @author jinggao/UploadFileDialog
 * @date Mar 22, 2008
 */
@SuppressWarnings("serial")
public class UploadFileDialog extends JFrame  {
	private JComboBox JCBorganismList;
	private JComboBox jcbMt;
	private JComboBox jcbDR;
	private JComboBox jcbIL;
	//private	JCheckBox jcheckbox;
	private JLabel label1, label2;
	private JTextField textField;
	
	
public UploadFileDialog (final boolean findheader, final String genelist, final String organism, final String moleculeType, 
		final String dataSource, final String interactionLevel, final SearchTaskFactory searchTaskFactory, final DialogTaskManager dialogTaskManager, final JFrame frame){
		super ("MiMI");		
		Container cPane = getContentPane();		
		textField = new JTextField(genelist);			
		if (findheader)
			label1 = new JLabel("Parameters set here are from upload file, you can change them and/or continue SEARCH");
		else label1=new JLabel("Use default parameters or change them");		
		//create organism combobox
	    String [] organismList= {"All Organisms","Arabidopsis thaliana","Caenorhabditis elegans","Drosophila melanogaster","Escherichia coli","Homo sapiens","Mus musculus","Rattus norvegicus","Saccharomyces cerevisiae"};
	    JCBorganismList=new JComboBox(organismList);
	    int index=5;
	    if (findheader)
	    	for (int i=0;i<organismList.length; i++)
	    		   if (organismList[i].equalsIgnoreCase(organism)){
	    			   index =i;
	    			   break;
	    		   }           	   
	    JCBorganismList.setSelectedIndex(index);		      
	    //create molecule type combobox
	    String [] moleType={"All Molecule Types","protein","mRNA","gene","pseudo","chemical","DNA"};
	    jcbMt=new JComboBox(moleType);
	    index =0;
	    if (findheader)
	    	 for (int i=0;i<moleType.length; i++)
	    		   if (moleType[i].equalsIgnoreCase(moleculeType)){
	    			   index =i;
	    			   break;
	    		   } 
	    jcbMt.setSelectedIndex(index);       
	    //create data source combobox 
	   	String[] databaseList={"All Data Sources","BIND","CCSB","DIP","HPRD","IntAct","KEGG","MDC","MINT","PubMed","reactome"};	//interaction	  
	    jcbDR=new JComboBox(databaseList);
	    index =0;
	    if (findheader)
	    	 for (int i=0;i<databaseList.length; i++)
	    		   if (databaseList[i].equalsIgnoreCase(dataSource)){
	    			   index =i;
	    			   break;
	    		   } 
		jcbDR.setSelectedIndex(index);			
		//create dispalys for resuults combobox 
		label2 =new JLabel ("What to retrieve...");
		String [] intLevelList={"1. Query genes + nearest neighbors","2. Interactions among query genes (query at least two genes e.g. csf1r,cbl)", "3. Query genes + neighbors' neighbors (It may take several minutes for a long gene list)","4. Nearest neighbors shared by more than one query genes (query at least two genes)"};
		jcbIL=new JComboBox(intLevelList);
		jcbIL.setSelectedIndex(0);
		if (findheader && interactionLevel.equalsIgnoreCase("2"))jcbIL.setSelectedIndex(1);	
		if (findheader && interactionLevel.equalsIgnoreCase("3"))jcbIL.setSelectedIndex(2);	
		if (findheader && interactionLevel.equalsIgnoreCase("4"))jcbIL.setSelectedIndex(3);	
		jcbIL.setEnabled(true);
		//create search button	    
    	JButton searchButton = new JButton("Search");    	    	
	    searchButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
				TaskIterator ti = searchTaskFactory.createTaskIterator(textField,JCBorganismList,jcbMt,jcbDR, jcbIL);
				dialogTaskManager.execute(ti);
			}
	    });
	    
	    JPanel panel1= new JPanel();
	    panel1.add(JCBorganismList);
	    panel1.add(jcbMt);
	    panel1.add(jcbDR);
	    panel1.add(Box.createRigidArea(new Dimension(218,0)));
	    
	    JPanel panel2= new JPanel();
	    panel2.add(label2);
	    panel2.add(jcbIL);
	    panel2.add(Box.createRigidArea(new Dimension(37,0)));
	    
	    JPanel panel3 = new JPanel();
	    panel3.add(label1);
	    panel3.add(Box.createRigidArea(new Dimension(150,0)));
	    
	    JPanel panel = new JPanel();		
	    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
	    panel.add(panel3);
	    panel.add(panel1);
	    panel.add(panel2);
	    panel.add(searchButton);
	    
		cPane.add(panel);         
	    pack();
	    setVisible(true);
	    setLocationRelativeTo(frame);
	}
	

}
