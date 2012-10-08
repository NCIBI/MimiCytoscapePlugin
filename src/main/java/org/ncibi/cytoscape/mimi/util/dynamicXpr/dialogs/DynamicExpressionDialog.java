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
 
package org.ncibi.cytoscape.mimi.util.dynamicXpr.dialogs;

/**  Copyright (c) 2003 Institute for Systems Biology
 **  This program is free software; you can redistribute it and/or modify
 **  it under the terms of the GNU General Public License as published by
 **  the Free Software Foundation; either version 2 of the License, or
 **  any later version.
 **
 **  This program is distributed in the hope that it will be useful,
 **  but WITHOUT ANY WARRANTY; without even the implied warranty of
 **  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  The software and
 **  documentation provided hereunder is on an "as is" basis, and the
 **  Institute for Systems Biology has no obligations to provide maintenance, 
 **  support, updates, enhancements or modifications.  In no event shall the
 **  Institute for Systems Biology be liable to any party for direct, 
 **  indirect, special,incidental or consequential damages, including 
 **  lost profits, arising out of the use of this software and its 
 **  documentation, even if the Institute for Systems Biology 
 **  has been advised of the possibility of such damage. See the
 **  GNU General Public License for more details.
 **   
 **  You should have received a copy of the GNU General Public License
 **  along with this program; if not, write to the Free Software
 **  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 **/


import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.text.DecimalFormat;
import java.util.Dictionary;
import java.util.Hashtable;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.ncibi.cytoscape.mimi.plugin.CyActivator;
import org.ncibi.cytoscape.mimi.util.BareBonesBrowserLaunch;
import org.ncibi.cytoscape.mimi.util.dynamicXpr.DynamicExpression;


/**
 * A user interface for DynamicExpression.java
 * 
 * @author Iliana Avila-Campillo iavila@systemsbiology.net
 * @version %I%, %G%
 * @since 1.1
 */

public class DynamicExpressionDialog extends JFrame {//implements WindowListener{

	//protected final DynamicExpression listener;
	protected JTextField geneT;

	JPanel mainPanel;

	JPanel edgedPanel;

	JPanel filePanel;

	JLabel fileLabel;

	JTextField fileField;

	JButton browseButton;

	JPanel conditionsPanel;

	JLabel conditionsLabel;

	JSlider conditionsSlider;

	//ConditionsSliderListener conditionsListener;

	JPanel speedPanel;

	JLabel speedLabel;

	JSlider speedSlider;

	JPanel buttonPanel;

	JButton playButton;

	JButton stopButton;

	JButton pauseButton;

	JPanel dismissPanel;

	JButton dismissButton;

	Border etched;

	Border paneEdge;

	File currentDirectory;
	
	JTextField searchField;
	
	JLabel dataRangeLabel;

	boolean nodeColorChanged = false;

	/**
	 * Constructor
	 * 
	 * @param listener
	 *            the <code>DynamicExpression</code> object that is listening
	 *            to the button requests of the dialog
	 * @param title
	 *            the title for the dialog to use
	 */
//	public DynamicExpressionDialog(final DynamicExpression listener, String title) {
//		//super(Cytoscape.getDesktop(), false);
//		this.listener = listener;
//		setTitle(title);
//		createUI();
//		this.currentDirectory = new File(System.getProperty("user.dir"));
//		addWindowListener(new WindowAdapter() {
//            public void windowClosing(WindowEvent e) {
//            	listener.restoreOldNodeColorCalculator();
//    			DynamicExpressionDialog.this.dispose();
//    			nodeColorChanged = false;
//            }
//		});
//	}// DynamicExpressionDialog
//
//	/**
//	 * Creates the dialog
//	 */
//	protected void createUI() {
//		if (mainPanel != null) {
//			mainPanel.removeAll();
//		}
//
//		etched = BorderFactory.createEtchedBorder();
//		paneEdge = BorderFactory.createEmptyBorder(5, 5, 5, 5);
//
//		mainPanel = new JPanel();
//		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
//
//		mainPanel.setBorder(paneEdge);
//
//		edgedPanel = new JPanel();
//		edgedPanel.setLayout(new BoxLayout(edgedPanel, BoxLayout.Y_AXIS));
//		edgedPanel.setBorder(etched);	
//		
//		filePanel = new JPanel();
//		filePanel.setLayout(new BoxLayout(filePanel, BoxLayout.X_AXIS));
//		filePanel
//				.setBorder(BorderFactory.createTitledBorder("Expression File"));				
//		fileLabel = new JLabel("File:");		
//		filePanel.add(fileLabel);
//		ExpressionData expData = Cytoscape.getExpressionData();
//		String fileName = null;
//		if (expData != null) {
//			fileName = expData.getFileName();
//		}
//
//		fileField = new JTextField(15);
//		if (fileName != null) {
//			fileField.setText(fileName);
//		}
//		filePanel.add(Box.createRigidArea(new Dimension(8, 0)));
//		filePanel.add(fileField);
//		browseButton = new JButton("Browse");
//		browseButton.addActionListener(new BrowserButtonListener());
//		filePanel.add(browseButton);	
//		filePanel.setVisible(false);
//		edgedPanel.add(filePanel);
//		
//		
//		/****begin *****/		
//		//add expression data panel and set unused filepanel invisible
//		JPanel exprPanel =new JPanel();
//		exprPanel.setLayout(new BoxLayout(exprPanel, BoxLayout.Y_AXIS));
//		exprPanel.setBorder(BorderFactory.createTitledBorder("Expression Data"));			
//		//load expression data panel
//		JPanel searchPanel = new JPanel();		
//		searchPanel.setLayout(new BoxLayout(searchPanel, BoxLayout.X_AXIS));
//		searchPanel.setBorder(BorderFactory.createTitledBorder("Load Expression Dataset"));
//		JLabel searchLabel=new JLabel("GDSxxxx:");
//		searchPanel.add(searchLabel);
//		searchField=new JTextField(15);
//		searchPanel.add(searchField);
//		JButton searchButton=new JButton("Query NCIBIGDS");
//		searchButton.addActionListener(new ActionListener(){
//    		public void actionPerformed(ActionEvent ae){  
//    			//System.out.println("call do query with gdsid <"+searchField.getText());
//    			//new QueryGDS().doQuery(searchField.getText());
//    			QueryGDS.doQuery(searchField.getText());
//    			//loadTempGDSFile(gds.getExprTmpFileName()); 
//    			updateDialogPanel();
//    		}
//    	});		
//		searchPanel.add(searchButton);
//		
//		//search data set for gene panel
//		JPanel gds4Gene=new JPanel();
//		gds4Gene.setLayout(new BoxLayout(gds4Gene, BoxLayout.X_AXIS));
//		gds4Gene.setBorder(BorderFactory.createTitledBorder("Search Datasets for Genes"));
//		JLabel geneL=new JLabel("Enter Gene Symbol(s):e.g. sod1,ezh2");
//		geneT=new JTextField(15);
//		JButton geneB=new JButton("Get Datasets for Genes");
//		geneB.addActionListener(new ActionListener(){
//    		public void actionPerformed(ActionEvent ae){
//    			//System.out.println("gene list:"+geneT.getText());
//    			String geneList=getGeneList(geneT.getText());
//    			BareBonesBrowserLaunch.openURL(CyActivator.BROWSEGDS+"?GENELIST="+geneList);  
//    			
//    		}
//    	});		
//		
//		JButton BrowseGDS =new JButton("Browse ALL Dataset");
//		BrowseGDS.addActionListener(new ActionListener(){
//    		public void actionPerformed(ActionEvent ae){  
//    			BareBonesBrowserLaunch.openURL(CyActivator.BROWSEGDS+"?GENELIST=");  
//    			//new BrowseGDS();
//    		}
//    	} );
//		gds4Gene.add(geneL);
//		gds4Gene.add(geneT);
//		gds4Gene.add(geneB);
//		gds4Gene.add(BrowseGDS);
//		
//		
//		exprPanel.add(searchPanel);
//		exprPanel.add(gds4Gene);
//		edgedPanel.add(exprPanel);
//		/********end*/		
//				
//		conditionsPanel = new JPanel();
//		conditionsPanel.setLayout(new BoxLayout(conditionsPanel,
//				BoxLayout.Y_AXIS));
//		conditionsPanel.setBorder(BorderFactory
//				.createTitledBorder("Conditions (may not be dependent)"));
//		String[] conditions = null;
//		if (expData != null) {
//			dataRangeLabel=new JLabel("Data Range["+new DecimalFormat("0.####").format(QueryGDS.getMinExpr())+" "+new DecimalFormat("0.####").format(QueryGDS.getMaxExpr())+"] Color[Green Red]");
//			conditions = expData.getConditionNames();
//			conditionsLabel = new JLabel(conditions[0]);
//		} else {
//			dataRangeLabel=new JLabel("Data Range[] Color[]");
//			conditionsLabel = new JLabel("No Expression Data");
//		}
//		conditionsPanel.add(dataRangeLabel);
//		conditionsPanel.add(conditionsLabel);
//		int numConditions = 81; // So that the speed slider and the conditions
//								// slider are
//		// of the same size by default
//		if (expData != null) {
//			numConditions = expData.getNumberOfConditions();
//		}
//		conditionsSlider = new JSlider(JSlider.HORIZONTAL, 0,
//				numConditions - 1, 0);
//		conditionsSlider.setMajorTickSpacing(1);
//		conditionsSlider.setSnapToTicks(true);
//		conditionsSlider.setPaintTicks(true);
//		if (expData != null) {
//			conditionsSlider.setPaintLabels(true);
//		} else {
//			conditionsSlider.setPaintLabels(false);
//		}
//		conditionsListener = new ConditionsSliderListener();
//		conditionsSlider.addChangeListener(conditionsListener);
//		conditionsPanel.add(conditionsSlider);
//		conditionsPanel.add(Box.createRigidArea(new Dimension(8, 0)));
//		edgedPanel.add(conditionsPanel);
//
//		speedPanel = new JPanel();
//		speedPanel.setLayout(new BoxLayout(speedPanel, BoxLayout.Y_AXIS));
//		speedPanel.setBorder(BorderFactory.createTitledBorder("Speed"));
//		speedSlider = new JSlider(JSlider.HORIZONTAL, 0, 90, 45);
//		speedSlider.setMajorTickSpacing(10);
//		speedSlider.setMinorTickSpacing(1);
//		speedSlider.setSnapToTicks(true);
//		speedSlider.setPaintTicks(true);
//		speedSlider.addChangeListener(new SpeedSliderListener());
//		Dictionary labelTable = new Hashtable();
//		labelTable.put(new Integer(0), new JLabel("Slow"));
//		labelTable.put(new Integer(45), new JLabel("Medium"));
//		labelTable.put(new Integer(90), new JLabel("Fast"));
//		speedSlider.setLabelTable(labelTable);
//		speedSlider.setPaintLabels(true);
//		speedPanel.add(speedSlider);
//		speedPanel.add(Box.createRigidArea(new Dimension(8, 0)));
//		edgedPanel.add(speedPanel);
//
//		buttonPanel = new JPanel();
//		buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
//		buttonPanel.setBorder(BorderFactory.createTitledBorder("Play"));
//		ImageIcon playIcon = createImageIcon("play.jpg");
//		if (playIcon == null) {
//			playButton = new JButton("Play");
//		} else {
//			playButton = new JButton(playIcon);
//		}
//		playButton.addActionListener(new PlayActionListener());
//		buttonPanel.add(playButton);
//		buttonPanel.add(Box.createRigidArea(new Dimension(16, 0)));
//		ImageIcon pauseIcon = createImageIcon("pause.jpg");
//		if (pauseIcon == null) {
//			pauseButton = new JButton("Pause");
//		} else {
//			pauseButton = new JButton(pauseIcon);
//		}
//		pauseButton.addActionListener(new PauseActionListener());
//		buttonPanel.add(pauseButton);
//		buttonPanel.add(Box.createRigidArea(new Dimension(16, 0)));
//		ImageIcon stopIcon = createImageIcon("stop.jpg");
//		if (stopIcon == null) {
//			stopButton = new JButton("Stop");
//		} else {
//			stopButton = new JButton(stopIcon);
//		}
//		stopButton.setVerticalTextPosition(AbstractButton.CENTER);
//		stopButton.setHorizontalTextPosition(AbstractButton.LEADING);
//		stopButton.addActionListener(new StopActionListener());
//		buttonPanel.add(stopButton);
//		edgedPanel.add(buttonPanel);
//
//		dismissPanel = new JPanel();
//		dismissPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
//		dismissButton = new JButton("Dismiss");
//		dismissButton.addActionListener(new DismissActionListener());
//		dismissPanel.add(dismissButton);
//
//		mainPanel.add(edgedPanel);
//		mainPanel.add(dismissPanel);
//
//		setContentPane(mainPanel);
//	}// createUI
//
//	/**
//	 * Enables or disables the components of this dialog.
//	 */
//	public void enable(boolean enable) {
//		conditionsSlider.setEnabled(enable);
//		speedSlider.setEnabled(enable);
//		playButton.setEnabled(enable);
//		stopButton.setEnabled(enable);
//		pauseButton.setEnabled(enable);
//	}// enable
//
//	/**
//	 * Updates the conditions slider bar to reflect a new expression data set.
//	 */
//	public void update() {//done
//		String[] conditions = null;
//		//ExpressionData expData = Cytoscape.getExpressionData();
//		//if (expData != null) {
//		if (QueryGDS.getConditions()!=null){
//			//conditions = expData.getConditionNames();
//			conditions =QueryGDS.getConditions();
//			conditionsLabel.setText(conditions[0]);
//			dataRangeLabel.setText("Data Range["+new DecimalFormat("0.####").format(QueryGDS.getMinExpr())+" "+new DecimalFormat("0.####").format(QueryGDS.getMaxExpr())+"] Color[Green Red]");			
//			listener.adjustForNewExpressionData();
//		} else {
//			conditionsLabel.setText("No Expression Data");
//		}
//		int numConditions = 81;
//		if (QueryGDS.getConditions()!=null) {
//			numConditions = QueryGDS.getConditions().length;
//			System.out.println("in dlg update numcondition is "+numConditions);//expData.getNumberOfConditions();
//			conditionsSlider.setPaintLabels(true);
//		} else {
//			conditionsSlider.setPaintLabels(false);
//		}
//		conditionsSlider.setMinimum(0);
//		conditionsSlider.setMaximum(numConditions - 1);
//		conditionsSlider.setValue(0);
//	}// update
//
//	/**
//	 * @return the <code>String</code> currently showing in the Expression
//	 *         Data file-name field.
//	 */
//	public String getFileName() {
//		return this.fileField.getText();
//	}// getFileName
//
//	/**
//	 * Sets the file name of the currently loaded ExpressionData.
//	 */
//	public void setFileName(String file_name) {
//		this.fileField.setText(file_name);
//		update();
//	}// setFileName
//
//	/**
//	 * Enables or disables the listener for the condition slider
//	 */
//	public void conditionsSliderEnabled(boolean enable) {
//		if (enable) {
//			conditionsSlider.addChangeListener(conditionsListener);
//		} else {
//			conditionsSlider.removeChangeListener(conditionsListener);
//		}
//	}// conditionsSliderEnabled
//
//	/**
//	 * Sets the conditionsSlider to the given condition
//	 */
//	public void updateConditionsSlider(int condition, String conditionName) {
//		conditionsSlider.setValue(condition);
//		conditionsLabel.setText(conditionName);
//		dataRangeLabel.setText("Data Range["+new DecimalFormat("0.####").format(QueryGDS.getMinExpr())+" "+new DecimalFormat("0.####").format(QueryGDS.getMaxExpr())+"] Color[Green Red]");
//		
//	}// updateConditionsSlider
//
//	// ----------- internal classes -------------
//	class BrowserButtonListener extends AbstractAction {
//		BrowserButtonListener() {
//			super("");
//		}
//
//		public void actionPerformed(ActionEvent e) {
//			JFileChooser chooser = new JFileChooser(currentDirectory);
//			if (chooser.showOpenDialog(DynamicExpressionDialog.this) == JFileChooser.APPROVE_OPTION) {
//				currentDirectory = chooser.getCurrentDirectory();
//				String fileName = chooser.getSelectedFile().toString();
//				//System.out.println("currentDirectory is from chooser["+currentDirectory.getName() );
//				//System.out.println("file name is from chooser["+fileName);
//				if (!Cytoscape.loadExpressionData(fileName, false)) { // try
//																		// true
//					// Unsuccesful data load
//					JOptionPane.showMessageDialog(chooser,
//							"The expression file you loaded created an error. "
//									+ System.getProperty("line.separator"),
//							"Error", JOptionPane.ERROR_MESSAGE);
//				} else {
//					// Succesful data load
//					DynamicExpressionDialog.this.pack();
//					fileField.setText(fileName);
//					DynamicExpressionDialog.this.update();
//					DynamicExpressionDialog.this.enable(true);
//					nodeColorChanged = true;
//				}
//			}
//
//		}
//	}// BrowserButtonListener
//
//	class PlayActionListener extends AbstractAction {
//		PlayActionListener() {
//			super("");
//		}
//
//		public void actionPerformed(ActionEvent e) {
//			//deselect nodes
//			Cytoscape.getCurrentNetwork().setSelectedNodeState(Cytoscape.getCurrentNetwork().getSelectedNodes(), false);
//			nodeColorChanged = true;
//			if (listener.isPaused() == 1) {
//				// Was paused, change back the pause icon to black
//				ImageIcon pauseIcon = createImageIcon("pause.jpg");
//				if (pauseIcon != null) {
//					pauseButton.setIcon(pauseIcon);
//				}
//			} else if (listener.isPaused() == 0) {
//				// Not paused, playing
//				return; // wait to finish
//			}
//			int delay = speedSlider.getValue();
//			delay = 100 - delay;
//			delay *= 30;
//			conditionsSliderEnabled(false);
//			listener.play(delay);
//		}
//	}// PlayActionListener
//
//	class PauseActionListener extends AbstractAction {
//		PauseActionListener() {
//			super("");
//		}
//
//		public void actionPerformed(ActionEvent e) {
//			if (listener.isPaused() == 0) {
//				// not paused, but about to become paused
//				ImageIcon pauseIcon = createImageIcon("redPause.jpg");
//				if (pauseIcon != null) {
//					pauseButton.setIcon(pauseIcon);
//				}
//				listener.pause();
//			} else if (listener.isPaused() == 1) {
//				// paused, about to play again
//				ImageIcon pauseIcon = createImageIcon("pause.jpg");
//				if (pauseIcon != null) {
//					pauseButton.setIcon(pauseIcon);
//				}
//				listener.pause();
//			}
//
//		}
//	}// PauseActionListener
//
//	class StopActionListener extends AbstractAction {
//		StopActionListener() {
//			super("");
//		}
//
//		public void actionPerformed(ActionEvent e) {
//			listener.stop();
//			conditionsSliderEnabled(true);
//		}
//	}// StopActionListener
//
//	public class DismissActionListener extends AbstractAction {
//		DismissActionListener() {
//			super("");
//		}
//
//		public void actionPerformed(ActionEvent e) {
//			/*if (nodeColorChanged) {
//				int n = JOptionPane.showConfirmDialog(null,
//						"Restore previous node colors?", "",
//						JOptionPane.YES_NO_OPTION);
//				if (n == JOptionPane.YES_OPTION) {
//					listener.restoreOldNodeColorCalculator();
//
//				}
//			}// if nodeColorChanged*/
//			listener.restoreOldNodeColorCalculator();
//			DynamicExpressionDialog.this.dispose();
//			nodeColorChanged = false;
//			
//			
//		}
//
//	} // DismissActionListener
//
//	public class SpeedSliderListener implements ChangeListener {
//		SpeedSliderListener() {}
//
//		public void stateChanged (ChangeEvent e) {
//			JSlider jslider = (JSlider) e.getSource();
//			int delay = jslider.getValue();
//			delay = 100 - delay;
//			delay *= 30;
//			listener.setTimerDelay(delay);
//		}// stateChanged
//
//	}// ConditionsSliderListener
//
//	public class ConditionsSliderListener implements ChangeListener {
//		ConditionsSliderListener() {
//		}
//
//		public void stateChanged (ChangeEvent e) {
//			JSlider jslider = (JSlider) e.getSource();
//			if (jslider.getValueIsAdjusting()) {
//				return;
//			}
//			int i = jslider.getValue(); // this is a value between 0-100
//			ExpressionData data = Cytoscape.getExpressionData();
//			if(data != null){
//				String[] conditions = data.getConditionNames();
//				DynamicExpression.displayCondition(conditions[i], i);
//				conditionsLabel.setText(conditions[i]);
//				dataRangeLabel.setText("Data Range["+new DecimalFormat("0.####").format(QueryGDS.getMinExpr())+" "+new DecimalFormat("0.####").format(QueryGDS.getMaxExpr())+"] Color[Green Red]");			
//				
//			}
//		}// stateChanged
//
//	}// ConditionsSliderListener
//
//	/** Returns an ImageIcon, or null if the path was invalid. */
//	protected static ImageIcon createImageIcon(String path) {
//
//		java.net.URL imgURL = DynamicExpression.class.getResource(path);
//		if (imgURL != null) {
//			return new ImageIcon(imgURL);
//		} else {
//			System.err.println("----- Couldn't find file: " + path
//					+ " --------");
//			System.err.flush();
//			return null;
//		}
//	}
//	
//	/*******Begin******************/
//	
//	protected void updateDialogPanel(){
//		if (QueryGDS.getConditions()==null || QueryGDS.getExprval()==null ){
//			JOptionPane.showMessageDialog(this,
//					"Query NCIBIGDS failed."
//					+ System.getProperty("line.separator"),
//					"Error", JOptionPane.ERROR_MESSAGE);
//		}else {
//			DynamicExpressionDialog.this.pack();			
//			DynamicExpressionDialog.this.update();
//			DynamicExpressionDialog.this.enable(true);
//			nodeColorChanged = true;
//			closeFrame();			
//		}
//		
//	}
//	private void closeFrame(){
//		DynamicExpressionDialog.this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
//		// what happens when user closes the JFrame.
//		/*WindowListener windowListener = new WindowAdapter(){
//			public void windowClosing ( WindowEvent w ){
//				DynamicExpressionDialog.this.setVisible( false );
//				DynamicExpressionDialog.this.dispose();
//				int yn=JOptionPane.showConfirmDialog(Cytoscape.getDesktop(), "Go back to original color?");
//				if (yn==JOptionPane.YES_OPTION)
//					listener.restoreOldNodeColorCalculator();
//				DynamicExpressionDialog.this.dispose();
//				nodeColorChanged = false;
//					
//				
//		    } 
//		};
//		DynamicExpressionDialog.this.addWindowListener( windowListener );*/
//		
//	}
//	private String getGeneList(String str){	
//		String rest=str.replace(',', ' ');
//		rest=rest.replace(';', ' ');
//		return rest;
//	}
//	
//	
//	/*********end*********************/
//
}// DynamicExpression

