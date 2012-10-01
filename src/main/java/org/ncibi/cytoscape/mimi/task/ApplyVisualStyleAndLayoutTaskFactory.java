package org.ncibi.cytoscape.mimi.task;

import org.cytoscape.task.AbstractNetworkViewTaskFactory;
import org.cytoscape.view.layout.CyLayoutAlgorithm;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.vizmap.VisualMappingManager;
import org.cytoscape.work.TaskIterator;
import org.ncibi.cytoscape.mimi.visual.MiMIVisualStyleBuilder;

public class ApplyVisualStyleAndLayoutTaskFactory extends AbstractNetworkViewTaskFactory {
	
	private MiMIVisualStyleBuilder vsBuilder;
	private VisualMappingManager vmm;
	private CyLayoutAlgorithm layout;

	public ApplyVisualStyleAndLayoutTaskFactory(MiMIVisualStyleBuilder vsBuilder, VisualMappingManager vmm, 
			CyLayoutAlgorithm layout) {
		this.vsBuilder = vsBuilder;
		this.vmm = vmm;
		this.layout = layout;
	}
	
	public TaskIterator createTaskIterator(CyNetworkView view) {
		return new TaskIterator(new ApplyVisualStyleAndLayoutTask(view,vsBuilder, vmm, layout));
	}

}
