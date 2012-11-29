package org.ncibi.cytoscape.mimi.task;

import java.util.HashSet;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.task.AbstractNetworkViewTask;
import org.cytoscape.view.layout.CyLayoutAlgorithm;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;
import org.cytoscape.view.vizmap.VisualMappingManager;
import org.cytoscape.view.vizmap.VisualStyle;
import org.cytoscape.work.TaskMonitor;
import org.ncibi.cytoscape.mimi.visual.MiMIVisualStyleBuilder;

public class ApplyVisualStyleAndLayoutTask extends AbstractNetworkViewTask {
	

	private MiMIVisualStyleBuilder vsBuilder;
	private VisualMappingManager vmm;
	private CyLayoutAlgorithm layout;

	public ApplyVisualStyleAndLayoutTask(CyNetworkView view, 
			MiMIVisualStyleBuilder vsBuilder, VisualMappingManager vmm, 
			CyLayoutAlgorithm layout) {
		super(view);
		this.vsBuilder = vsBuilder;
		this.vmm = vmm;
		this.layout = layout;
	}

	@Override
	public void run(TaskMonitor taskMonitor) throws Exception {
		
		VisualStyle mimiStyle = null;
		for(VisualStyle style: vmm.getAllVisualStyles()) {
			if(style.getTitle().equals("MiMI")) {
				mimiStyle = style;
				break;
			}
		}
		if(mimiStyle == null) {
			mimiStyle = vsBuilder.createVisualStyle();
			vmm.addVisualStyle(mimiStyle);
		}
		mimiStyle.apply(view);
		view.updateView();
		vmm.setVisualStyle(mimiStyle, view);
		insertTasksAfterCurrentTask(layout.createTaskIterator(view, layout.createLayoutContext(), 
				new HashSet<View<CyNode>>(view.getNodeViews()), CyNetwork.NAME));
	}

}
