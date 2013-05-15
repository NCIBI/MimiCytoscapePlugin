package org.ncibi.cytoscape.mimi.task;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.cytoscape.io.util.StreamUtil;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.subnetwork.CyRootNetwork;
import org.cytoscape.model.subnetwork.CyRootNetworkManager;
import org.cytoscape.task.AbstractNodeViewTaskFactory;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskIterator;
import org.cytoscape.work.TaskMonitor;
import org.ncibi.cytoscape.mimi.MiMI;
import org.ncibi.cytoscape.mimi.enums.NodeType;

public class MiMINodeViewTaskFactory extends AbstractNodeViewTaskFactory {
	
	private ApplyVisualStyleAndLayoutTaskFactory vslTaskFactory;
	private JFrame frame;
	private CyRootNetworkManager rootNetworkManager;
	private StreamUtil streamUtil;
	
	public MiMINodeViewTaskFactory(ApplyVisualStyleAndLayoutTaskFactory vslTaskFactory, 
			JFrame frame, CyRootNetworkManager rootNetworkManager, StreamUtil streamUtil) {
		this.vslTaskFactory = vslTaskFactory;
		this.frame = frame;
		this.rootNetworkManager = rootNetworkManager;
		this.streamUtil = streamUtil;
	}

	public TaskIterator createTaskIterator(View<CyNode> view, CyNetworkView netView) {
		CyNode node = view.getModel();
		CyNetwork network = netView.getModel();
		CyRootNetwork rootNetwork = rootNetworkManager.getRootNetwork(network);
		TaskIterator taskIterator = new TaskIterator();
		String dplymode=rootNetwork.getRow(rootNetwork).get("Displays for results", String.class, "1");
		Integer nodeColor = network.getRow(node).get("Node Color",  Integer.class, 1);

		if(nodeColor==NodeType.SEEDNEIGHBOR.ordinal() || nodeColor==NodeType.EXPANDNEIGHBOR.ordinal()
				|| ((dplymode.equals("2") || dplymode.equals("4")) && nodeColor != NodeType.EXPANDNODE.ordinal() )) {
			try {
				String name = network.getRow(node).get(CyNetwork.NAME, String.class);
				String taxid = network.getRow(node).get("Gene.taxid", String.class);
				String molType = rootNetwork.getRow(rootNetwork).get("Molecule Type", String.class, "All Molecule Types");
				String dataSource = rootNetwork.getRow(rootNetwork).get("Data Source", String.class, "All Data Sources");
				String urlstr =MiMI.PRECOMPUTEEXPAND+"?ID="+name+"&ORGANISMID="+taxid+"&MOLTYPE="+URLEncoder.encode(molType,"UTF-8")+"&DATASOURCE="+URLEncoder.encode(dataSource,"UTF-8");
				URL url = new URL(urlstr);
				URLConnection conn = streamUtil.getURLConnection(url) ;
				conn.setUseCaches(false);
				// Get result        
				BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
				String line=rd.readLine();
				//System.out.println("Node number is "+line);
				rd.close();							

				if(line != null ){
					int nodeNo =Integer.parseInt(line);
					if (nodeNo<=30){
						taskIterator.append(new ExpandNodeTask(node, network, rootNetworkManager, streamUtil));
					}
					else {
						int decision=JOptionPane.showConfirmDialog(frame, "You will expand network with "+nodeNo +" nodes, continue?");
						if (decision==0){
							taskIterator.append(new ExpandNodeTask(node, network, rootNetworkManager, streamUtil));
						}
					}
				}
			}
			catch(Exception e) {}
		}
		else if(nodeColor == NodeType.EXPANDNODE.ordinal()){
			taskIterator.append(new CollapseNodeTask(node, network));
		}
		if(taskIterator.getNumTasks() > 0)
			taskIterator.append(vslTaskFactory.createTaskIterator(netView));
		else
			taskIterator.append(new NullTask());
		
		return taskIterator;
	}
	
	class NullTask extends AbstractTask {
		public void run(TaskMonitor tm) throws Exception {
			return;
		}
	}

}
