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
import java.nio.channels.ClosedByInterruptException;
import java.util.HashMap;

import javax.swing.BoxLayout;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.Timer;

import org.ncibi.cytoscape.mimi.action.ListenMouse;
import org.ncibi.cytoscape.mimi.ui.AddLegend;
import org.ncibi.cytoscape.mimi.ui.DetailsPanel;

import cytoscape.CyEdge;
import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.util.SwingWorker;

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
	private SwingWorker doQuery;
	private static HashMap<Integer, String> rsc;
	private final int msToPop = 1000;
	public  static JDialog  dialog=new JDialog() ;
	public static Timer popupTimer = new Timer(0,null);

	static {
		rsc = new HashMap<Integer, String>();
		rsc.put(QueryMiMI.QUERY_BY_NAME, "Gene Symbol");
		rsc.put(QueryMiMI.QUERY_BY_ID, "Gene ID");
		rsc.put(QueryMiMI.QUERY_BY_INTERACTION, "Interaction ID");
		rsc.put(QueryMiMI.QUERY_BY_FILE,"File");
		rsc.put(QueryMiMI.QUERY_BY_EXPAND, "Expand Node ID");
		rsc.put(QueryMiMI.QUERY_BY_REMOTEFILE, "Remote File");
	}
   
	public QueryMiMIWrapper(String term) {
		this(QueryMiMI.QUERY_BY_NAME, term);
	}

	public QueryMiMIWrapper(int type, String term) {
		this.createDialog(type, term);
		this.spawnThread(type, term);
	}

	public QueryMiMIWrapper(CyNode node) {
		this.createDialog(QueryMiMI.QUERY_BY_ID, node.getIdentifier());
		this.spawnThread(QueryMiMI.QUERY_BY_ID, node);
	}

	public QueryMiMIWrapper(CyEdge edge) {
		this.createDialog(QueryMiMI.QUERY_BY_INTERACTION, edge.getIdentifier());
		this.spawnThread(QueryMiMI.QUERY_BY_INTERACTION, edge);
	}

	public void spawnThread(final int type, final Object obj) {
		//final JDialog dialog = optionPane.createDialog(Cytoscape.getDesktop(), "Querying MiMI...");
		dialog = optionPane.createDialog(Cytoscape.getDesktop(), "Querying MiMI...");
		dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		dialog.pack();

		//final Timer popupTimer = new Timer(msToPop, null);
		popupTimer = new Timer(msToPop, null);
		popupTimer.setRepeats(false);      
        	doQuery = new SwingWorker() {
			public Object construct() {	
				if (obj instanceof String) {
					return QueryMiMI.query(type, (String)obj);
				} 
				else {
					return new Exception("Unknown Query Object");
				}
			}
				
			public void finished() {
				popupTimer.stop();
				dialog.setVisible(false);

				if (this.get() instanceof ClosedByInterruptException) {
					// do nothing, user cancelled query

				} else if (this.get() instanceof Exception) {
					Exception e = (Exception)this.get();

					String name = e.getClass().getName();
					String msg = e.getMessage();
					StackTraceElement[] ste = e.getStackTrace();
					String stack = "";
					for (int i = 0; i < ste.length; i++) {
						stack += ste[i].toString() + "<br>";
					}
					String txt = "";
					if (name != null) txt += ": " + name;
					txt += "<br>";
					if (msg != null && !msg.equals("")) txt += "<br>" + msg + "<br>";
					if (!stack.equals("")) txt += "<br>" + stack;
					//txt += "</html>";

					//JOptionPane.showMessageDialog(Cytoscape.getDesktop(), txt,
					//	"Error", JOptionPane.ERROR_MESSAGE);
					//System.out.println(txt);
				} else {
					//System.out.println("wrapper success.");						
					new ListenMouse(); // enable expand network, modified on April 2008
					new AddLegend();
				}
			}
		};

		ActionListener doPopup = new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				dialog.setVisible(true);
			}
		};
		popupTimer.addActionListener(doPopup);

		doQuery.start();
		popupTimer.start();
	}
	
	private void createDialog(int type, String term) {
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
							if (doQuery != null) {	
								try{
									if (QueryMiMI.rd !=null)
										 QueryMiMI.rd.close();
									}catch(Exception ee){
										//System.out.println(ee);
									}
								doQuery.interrupt();
								
								
							}
						}
					}
				}
			}
		);
	}

}
