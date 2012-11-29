package org.ncibi.cytoscape.mimi.task;

import java.io.File;

import javax.swing.JFrame;

import org.cytoscape.work.TaskIterator;
import org.cytoscape.work.swing.DialogTaskManager;

public class UploadFileTaskFactory {
	private SearchTaskFactory searchTaskFactory;
	private DialogTaskManager dialogTaskManager;
	private JFrame frame;
	
	public UploadFileTaskFactory(SearchTaskFactory searchTaskFactory, DialogTaskManager dialogTaskManager, JFrame frame) {
		this.searchTaskFactory = searchTaskFactory;
		this.dialogTaskManager = dialogTaskManager;
		this.frame=frame;
	}
	
	public TaskIterator createTaskIterator(File file) {
		return new TaskIterator(new UploadFileTask(file, searchTaskFactory, dialogTaskManager, frame));
	}
}
