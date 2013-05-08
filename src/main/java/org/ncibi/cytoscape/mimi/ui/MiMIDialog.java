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
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import org.cytoscape.work.TaskIterator;
import org.cytoscape.work.swing.DialogTaskManager;
import org.ncibi.cytoscape.mimi.MiMI;
import org.ncibi.cytoscape.mimi.enums.SearchMethod;
import org.ncibi.cytoscape.mimi.task.SearchTaskFactory;
import org.ncibi.cytoscape.mimi.task.UploadFileTaskFactory;




/**
 * Create a dialog for user to enter search term and get help etc information
 * execute query mimi
 * @author Jing Gao
 * update on Aug 27 for uploading gene list file
 * redesigned dialog box for multiple input method(by file, by query, by table)
 * update on Oct 11 07
 */
@SuppressWarnings("serial")
public class MiMIDialog extends JFrame{
	private int TAB0=0;
	private int TAB1=1;
	private int TAB2=2;		
	private int TAB3=3;
	private JComboBox JCBorganismList;
	private JComboBox jcbMt;
	private JComboBox jcbDR;
	private JComboBox jcbIL;
	private JLabel label;
	private JButton searchButton;
	private JTextField textField;
	private JFrame parent;
	private SearchTaskFactory searchTaskFactory;
	private UploadFileTaskFactory uploadFileTaskFactory;
	private DialogTaskManager dialogTaskManager;
	
	
	
	public MiMIDialog(SearchTaskFactory searchTaskFactory, UploadFileTaskFactory uploadFileTaskFactory, DialogTaskManager dialogTaskManager, JFrame parent){
		super("Welcome to MiMI " +MiMI.VERSION);
		this.parent = parent;
		this.searchTaskFactory = searchTaskFactory;
		this.uploadFileTaskFactory = uploadFileTaskFactory;
		this.dialogTaskManager = dialogTaskManager;
		Container cPane = getContentPane(); 
		JTabbedPane tabbedPane=new JTabbedPane();  

		JComponent tab0=createPanel(TAB0);
		tabbedPane.addTab("Enter Gene Symbol(s)", tab0);

		JComponent tab1=createPanel(TAB1);       
		//tabbedPane.addTab("Enter Gene Symbol(s)", tab1);  
		tabbedPane.addTab("Enter keyword(s)", tab1);

		JComponent tab2 = createPanel(TAB2);       
		tabbedPane.addTab("From File", tab2);  

		JComponent tab3=createPanel(TAB3);
		tabbedPane.addTab("For MeSH term", tab3);


		tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
		//Add the tabbed pane to this panel.
		cPane.add(tabbedPane);         
		pack();
		setVisible(true);
		setLocationRelativeTo(parent);      

	}
	protected JComponent createPanel(int tab) {		
		   //create organism combobox
	       String [] organismList= {"All Organisms","Arabidopsis thaliana","Caenorhabditis elegans","Drosophila melanogaster","Escherichia coli","Homo sapiens","Mus musculus","Rattus norvegicus","Saccharomyces cerevisiae"};
	       JCBorganismList=new JComboBox(organismList);
	       JCBorganismList.setSelectedIndex(5);	
	       //create molecule type combobox
	       String [] moleType={"All Molecule Types","protein","mRNA","gene","pseudo","chemical","DNA"};
	       jcbMt=new JComboBox(moleType);
	       jcbMt.setSelectedIndex(0);
	       //create data source combobox
	       String[] databaseList={"All Data Sources","BIND","CCSB","DIP","GRID","HPRD","IntAct","KEGG","MDC","MINT","PubMed","reactome"};	//interaction	  
	       jcbDR=new JComboBox(databaseList);
		   jcbDR.setSelectedIndex(0);
		   //create display result filter		  
		   JLabel fltL = new JLabel("What to retrieve...");
		   String [] intLevelList={"1. Query genes + nearest neighbors","2. Interactions among query genes (query at least two genes e.g. csf1r,cbl)", "3. Query genes + neighbors' neighbors (It may take several minutes for a long gene list)","4. Nearest neighbors shared by more than one query genes (query at least two genes)"};
		   jcbIL=new JComboBox(intLevelList);	
		   jcbIL.setSelectedIndex(0);
		   jcbIL.setEnabled(true);
		   
		   JPanel panel1 = new JPanel();
		   panel1.add(JCBorganismList);
		   panel1.add(jcbMt);
		   panel1.add(jcbDR);
		   panel1.add(Box.createRigidArea(new Dimension(226,0)));
		   
		   JPanel panel2 = new JPanel();
		   panel2.add(fltL);
		   panel2.add(jcbIL);
		   panel2.add(Box.createRigidArea(new Dimension(40,0)));
		   
		
		   JPanel panel = new JPanel();        
	       panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));	      
	       //Titled Border
	       TitledBorder border = new TitledBorder("Query MiMI");
	       panel.setBorder(border);
		  
	       
	       if(tab==TAB0 || tab==TAB1 || tab==TAB3){	//freetext or mesh term search
	    	   
	    	  if (tab==TAB0){
	    		  textField=new JTextField("",50);
	    		  label=new JLabel("Enter Gene Symbol (s): e.g. csf1r, ccnt2");
	    		  searchButton =new JButton("Search");
	    		  searchButton.addActionListener(createSearchAction(textField,JCBorganismList,jcbMt,jcbDR,jcbIL));
	    		  textField.addActionListener(createSearchAction(textField,JCBorganismList,jcbMt,jcbDR, jcbIL));
	    	  }
	    	   
	    	  if (tab==TAB1){
	    		  //Create Search Text Field
	    		  textField = new JTextField("",50);
	    		  //JLabel label=new JLabel("(Official Gene Symbols: e.g. csf1r,ccnt2)");
	    		  label=new JLabel("Enter keyword (s) to do free text search");
	    		  //create search button		      
	    		  searchButton = new JButton("Search");	    	   
	    		  //add listener to search button and textfiled
	    		  searchButton.addActionListener(createSearchAction(SearchMethod.FREETEXT,textField,JCBorganismList,jcbMt,jcbDR, jcbIL));
	    		  textField.addActionListener(createSearchAction(SearchMethod.FREETEXT,textField,JCBorganismList,jcbMt,jcbDR, jcbIL));
	    	  }
	    	   if (tab==TAB3){
	    		 //Create Search Text Field
	    		   textField = new JTextField("",44);	    		   
	    		   label=new JLabel("Get genes for MeSH term: e.g. prostatic neoplasms");
	    		   //create search button		      
	    		   searchButton = new JButton("Search");	    	   
	    		   //add listener to search button and textfiled
	    		   searchButton.addActionListener(createSearchAction(SearchMethod.MESHTERM,textField,JCBorganismList,jcbMt,jcbDR, jcbIL));
	    		   textField.addActionListener(createSearchAction(SearchMethod.MESHTERM,textField,JCBorganismList,jcbMt,jcbDR, jcbIL));
	    	   }
		       JPanel panel3 = new JPanel();
		       panel3.add(textField);
		       panel3.add(label);
		       
		       JPanel panel4 = new JPanel();
		       panel4.add(searchButton);
		       
		       panel.add(panel3);		     
		       panel.add(panel1); 
		       panel.add(panel2);
		       panel.add(panel4);	
		       	
		      
	       }
	       if(tab==TAB2){
	    	   JButton loadFileButton=new JButton("Load Gene File...");	
		       //loadFileButton.addActionListener(new ExcuteUploadFile(JCBorganismList,jcbMt,jcbDR,jcbIL,jcheckbox,(JFrame) this ));
		       loadFileButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						setVisible(false);
						JFileChooser fc=new JFileChooser();
						int returnVal = fc.showOpenDialog(parent); 
						if(returnVal == JFileChooser.APPROVE_OPTION) {
							TaskIterator ti = uploadFileTaskFactory.createTaskIterator(fc.getSelectedFile());
							dialogTaskManager.execute(ti);
						}
						
					}
				});
		       JButton fileFormat=new JButton("A Sample File");
		       fileFormat.addActionListener(new FileFormatTemplate());
		       JPanel loadFilePanel= new JPanel();
		       loadFilePanel.add(fileFormat);	
		       loadFilePanel.add(loadFileButton);    		      
		       panel.add(loadFilePanel);
		       
	    	   /*JLabel uploadfileL=new JLabel ("Please Upload Gene File");
	    	   JTextField uploadfileT=new JTextField ("", 50);
	    	   uploadfileT.addActionListener();
	    	   JButton loadFileButton=new JButton("Browse");			       
		       loadFileButton.addActionListener(new ExcuteUploadFile((JFrame) this ));	
		       JPanel labelpanel= new JPanel();
		       labelpanel.add(uploadfileL);
		       JPanel loadFilePanel= new JPanel();
		       loadFilePanel.add(uploadfileT);
		       loadFilePanel.add(loadFileButton);    
		       panel.add(labelpanel);
		       panel.add(loadFilePanel);*/
		      
	       }
	       
	      
	       return panel;
	}
	
	protected ActionListener createSearchAction(final JTextField textField ,final JComboBox jcbOrganism ,final JComboBox jcbMt,final JComboBox jcbDr, final JComboBox jcbI) {
		return createSearchAction(null, textField, jcbOrganism, jcbMt, jcbDr, jcbI);
	}
	
	protected ActionListener createSearchAction(final SearchMethod searchMethod, final JTextField textField ,final JComboBox jcbOrganism ,final JComboBox jcbMt,final JComboBox jcbDr, final JComboBox jcbI) {
		return new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
				TaskIterator ti = searchTaskFactory.createTaskIterator(searchMethod, textField, jcbOrganism, jcbMt, jcbDr, jcbI);
				dialogTaskManager.execute(ti);
			}
		};
	}
	
}

