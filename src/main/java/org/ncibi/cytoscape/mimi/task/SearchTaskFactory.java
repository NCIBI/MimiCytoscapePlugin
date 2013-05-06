package org.ncibi.cytoscape.mimi.task;

import javax.swing.JComboBox;
import javax.swing.JTextField;

import org.cytoscape.io.util.StreamUtil;
import org.cytoscape.work.TaskIterator;
import org.ncibi.cytoscape.mimi.enums.SearchMethod;

public class SearchTaskFactory {
	
	private BuildNetworkTaskFactory buildNetworkTaskFactory;
	private StreamUtil streamUtil;
	
	public SearchTaskFactory(BuildNetworkTaskFactory buildNetworkTaskFactory, StreamUtil streamUtil){
		this.buildNetworkTaskFactory = buildNetworkTaskFactory;
		this.streamUtil = streamUtil;
	}
	
	public TaskIterator createTaskIterator(JTextField textField ,JComboBox jcbOrganism ,JComboBox jcbMt,JComboBox jcbDr, JComboBox jcbIL) {
		return createTaskIterator(null, textField, jcbOrganism, jcbMt, jcbDr, jcbIL);
	}
	
	public TaskIterator createTaskIterator(SearchMethod searchMethod, JTextField textField ,JComboBox jcbOrganism ,JComboBox jcbMt,JComboBox jcbDr, JComboBox jcbIL) {
		return new TaskIterator(new SearchTask(searchMethod, textField, jcbOrganism, jcbMt, jcbDr, jcbIL, buildNetworkTaskFactory, streamUtil));
	}

}
