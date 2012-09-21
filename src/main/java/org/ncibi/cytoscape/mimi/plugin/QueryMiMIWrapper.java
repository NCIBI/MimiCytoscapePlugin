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
 
package org.ncibi.cytoscape.mimi.plugin;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;

import javax.swing.BoxLayout;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingWorker;
import javax.swing.Timer;

import org.cytoscape.io.util.StreamUtil;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.model.CyNode;
import org.ncibi.cytoscape.mimi.enums.QueryType;
import org.ncibi.cytoscape.mimi.task.AbstractMiMIQueryTask;

/**
 * QueryMiMIWrapper
 *
 * Wraps QueryMiMI to add multi-threading support and dialog boxes for
 * query cancel and error messages
 *
 * @author Alex Ade
 * @date   Thu Nov 16 14:05:11 EST 2006
 */
public class QueryMiMIWrapper {
	private JOptionPane optionPane;
	private static HashMap<QueryType, String> rsc;
	private final int msToPop = 1000;
	public  static JDialog  dialog=new JDialog() ;
	public static Timer popupTimer = new Timer(0,null);

	

	public QueryMiMIWrapper(final int type, final String term, final CyNetworkFactory cyNetworkFactory, 
			final CyNetworkManager cyNetworkManager, final JFrame frame, final StreamUtil streamUtil) {
		SwingWorker<Object, Object> swingWorker = new SwingWorker<Object,Object>() {
			public Object doInBackground() {	
				return AbstractMiMIQueryTask.query(type, term, cyNetworkFactory, cyNetworkManager, frame, streamUtil);
			}

		};
		this.createDialog(type, term, swingWorker);
		this.spawnThread(swingWorker, frame);
	}
	
	public QueryMiMIWrapper(final String term, final CyNode node, final CyNetwork network, 
			final JFrame frame, final StreamUtil streamUtil) {
		SwingWorker<Object, Object> swingWorker = new SwingWorker<Object,Object>() {
			public Object doInBackground() {	
				return AbstractMiMIQueryTask.query(term, node, network, frame, streamUtil);
			}

		};
		this.createDialog(AbstractMiMIQueryTask.QUERY_BY_EXPAND, term, swingWorker);
		this.spawnThread(swingWorker, frame);
	}

	public void spawnThread(final SwingWorker<Object, Object> swingWorker, final JFrame frame) {
		//final JDialog dialog = optionPane.createDialog(Cytoscape.getDesktop(), "Querying MiMI...");
		dialog = optionPane.createDialog(frame, "Querying MiMI...");
		dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		dialog.pack();

		//final Timer popupTimer = new Timer(msToPop, null);
		popupTimer = new Timer(msToPop, null);
		popupTimer.setRepeats(false);      
		

		ActionListener doPopup = new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				dialog.setVisible(true);
			}
		};
		popupTimer.addActionListener(doPopup);

		swingWorker.execute();
		popupTimer.start();
	}
	
	private void createDialog(final int type, final String term, final SwingWorker<Object, Object> swingWorker) {
		final Object[] options = {"Cancel Query"};
		//String labelStr=(type==DetailsPanel.DOMEAD)? ((DetailsPanel.SortsentencesNumber >= DetailsPanel.SORTLIMIT)? "Sort First "+ DetailsPanel.SORTLIMIT + " Sentences with MEAD" :"Sort "+DetailsPanel.SortsentencesNumber +" Sentences with MEAD" ): "Querying MiMI Database by "+ rsc.get(type);
		String labelStr="Querying MiMI Database by "+ rsc.get(type);
		
		JLabel label = new JLabel(labelStr);
		label.setAlignmentX(Component.LEFT_ALIGNMENT);

		JProgressBar pbar = new JProgressBar(JProgressBar.HORIZONTAL);
		pbar.setIndeterminate(true);
		pbar.setAlignmentX(Component.LEFT_ALIGNMENT);

		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.add(label);
		panel.add(javax.swing.Box.createRigidArea(new Dimension(0, 15)));
		panel.add(pbar);
		panel.add(javax.swing.Box.createRigidArea(new Dimension(0, 20)));

		optionPane = new JOptionPane(panel, JOptionPane.QUESTION_MESSAGE, JOptionPane.YES_OPTION, null, options);
		optionPane.addPropertyChangeListener(
			new PropertyChangeListener() {
				public void propertyChange(PropertyChangeEvent e) {
					String prop = e.getPropertyName();
					if ((e.getSource() == optionPane) && (prop.equals(JOptionPane.VALUE_PROPERTY))) {
						if (e.getNewValue() instanceof String && e.getNewValue().equals(options[0])) {
							if (swingWorker != null) {	
								try{
									if (AbstractMiMIQueryTask.rd !=null)
										 AbstractMiMIQueryTask.rd.close();
									}catch(Exception ee){
										//System.out.println(ee);
									}
								swingWorker.cancel(true);
								
								
							}
						}
					}
				}
			}
		);
	}

}
