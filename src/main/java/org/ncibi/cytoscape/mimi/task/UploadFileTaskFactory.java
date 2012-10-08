package org.ncibi.cytoscape.mimi.task;

import java.io.File;

import org.cytoscape.work.TaskIterator;
import org.cytoscape.work.swing.DialogTaskManager;

public class UploadFileTaskFactory {
	private SearchTaskFactory searchTaskFactory;
	private DialogTaskManager dialogTaskManager;
	
	public UploadFileTaskFactory(SearchTaskFactory searchTaskFactory, DialogTaskManager dialogTaskManager) {
		this.searchTaskFactory = searchTaskFactory;
		this.dialogTaskManager = dialogTaskManager;
	}
	
	public TaskIterator createTaskIterator(File file) {
		return new TaskIterator(new UploadFileTask(file, searchTaskFactory, dialogTaskManager));
	}
}
