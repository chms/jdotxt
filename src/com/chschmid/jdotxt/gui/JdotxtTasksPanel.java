package com.chschmid.jdotxt.gui;

import java.awt.Color;
import java.awt.EventQueue;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.todotxt.todotxttouch.task.Filter;
import com.todotxt.todotxttouch.task.FilterFactory;
import com.todotxt.todotxttouch.task.Priority;
import com.todotxt.todotxttouch.task.Sort;
import com.todotxt.todotxttouch.task.Task;
import com.todotxt.todotxttouch.task.TaskBag;

public class JdotxtTasksPanel extends JPanel {
	private static final long serialVersionUID = 829057714773624777L;
	
	private JdotxtGUI jdotxtgui;
	
	private Filter<Task> filter;
	private List<Task> tasks;
	
	private TaskBag taskBag;
	
	private long[] tasksDisplayed;
	
	private ArrayList<JdotxtTaskPanel> taskPanels = new  ArrayList<JdotxtTaskPanel>();
	private TasksPanelTaskListener tasksPanelTaskListener = new TasksPanelTaskListener();
	
	private JdotxtTaskPanel addTaskPanel;
	private JLabel loading;
	String emptyTaskString;
	
	private boolean isSearchEmpty = false;
	private boolean newTaskDisplayed = true;
	
	public JdotxtTasksPanel(JdotxtGUI jdotxtgui) {
		this.jdotxtgui = jdotxtgui;
		initGUI();
	}
	
	private void initGUI() {
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		this.setBackground(Color.WHITE);
		this.setOpaque(true);
		
		loading = new JLabel(JdotxtGUI.lang.getWord("Loading..."));
		loading.setEnabled(false);
		loading.setFont(JdotxtGUI.fontRI);
		loading.setBorder(BorderFactory.createEmptyBorder(5, 5, 0, 0));
		
		addTaskPanel = new JdotxtTaskPanel(new Task(0, "", new Date()), true);
		addTaskPanel.setTaskListener(new addNewTaskPanelListener());
	}
	
	public void reset() {
		tasksDisplayed = null;
		taskBag = null;
		this.removeAll();
		this.add(loading);
		revalidate();
		repaint();
	}
	
	public void setTaskbag(TaskBag taskBag) {
		ArrayList<JdotxtTaskPanel> taskPanels = new  ArrayList<JdotxtTaskPanel>(taskBag.size());
		
		JdotxtTaskPanel tp;
		
		for (Task t: taskBag.getTasks()) {
			tp = new JdotxtTaskPanel(t);
			tp.setTaskListener(tasksPanelTaskListener);
			taskPanels.add(tp);
		}
		
		EventQueue.invokeLater(new SetTaskBag(taskPanels, taskBag));
	}
	
	private class SetTaskBag implements Runnable {
		ArrayList<JdotxtTaskPanel> tp;
		TaskBag tb;
		
		public SetTaskBag(ArrayList<JdotxtTaskPanel> tp, TaskBag tb) {
			this.tp = tp;
			this.tb = tb;
		}
		
		public void run() {
			taskBag = tb;
			taskPanels = tp;
		}
		
	}
	
	public void updateTaskPanel() {
		if (taskBag == null) return;
		tasks = taskBag.getTasks(filter, Sort.PRIORITY_DESC.getComparator());
		if (!isUpToDate()) forceUpdateTaskPanel();
	}
	
	private void forceUpdateTaskPanel() {
		this.removeAll();
		tasksDisplayed = new long[tasks.size()];
		
		newTaskDisplayed = false;
		if (isSearchEmpty) {
			this.add(addTaskPanel);
			newTaskDisplayed = true;
		}
		
		int k1 = 0;
		for (Task t: tasks){ 
			this.add(findTaskPanel(t));
			tasksDisplayed[k1] = t.getId();
			k1++;
		}
		
		revalidate();
		repaint();
	}

	public JdotxtTaskPanel findTaskPanel(Task task) {
		for (JdotxtTaskPanel t: taskPanels){
			if (t.getTask().equals(task)) return t;
		}
		return null;
	}
	
	public void setFilter(ArrayList<Priority> filterPrios, ArrayList<String> filterContexts, ArrayList<String> filterProjects, String search) {
		filter = FilterFactory.generateAndFilter(filterPrios, filterContexts, filterProjects, search, false);
		
		emptyTaskString = JdotxtGUI.lang.getWord("New_task");
		for (String p: filterProjects) if (!p.equals("-")) emptyTaskString = emptyTaskString + " +" + p;
		for (String c: filterContexts) if (!c.equals("-")) emptyTaskString = emptyTaskString + " @" + c;
		addTaskPanel.setTask(new Task(0, emptyTaskString, new Date()));
		isSearchEmpty = search.isEmpty();
	}
	
	private boolean isUpToDate() {
		boolean inOrder = true;
		if (tasksDisplayed == null) return false;
		if (tasks.size() != tasksDisplayed.length) return false;
		if (isSearchEmpty != newTaskDisplayed) return false;
		for (int k1 = 0; inOrder && k1 < tasks.size(); k1++) inOrder = (tasks.get(k1).getId() == tasksDisplayed[k1]);
		return inOrder;
	}
	
	public class TasksPanelTaskListener implements TaskListener {

		@Override
		public void onPriorityUpdate(Task t) {
			JdotxtTasksPanel.this.requestFocus();
			taskBag.update(t);
			updateTaskPanel();
			for (int k1 = 0; k1 < tasksDisplayed.length; k1++) if (taskPanels.get(k1).getTask().equals(t)) taskPanels.get(k1).setFocusPriority();
			jdotxtgui.setEnableSave(true);
		}

		@Override
		public void onDateUpdate(Task t) {
			taskBag.update(t);
			jdotxtgui.setEnableSave(true);
		}

		@Override
		public void onCompletionUpdate(Task t) {
			JdotxtTasksPanel.this.requestFocus();
			taskBag.update(t);
			updateTaskPanel();
			jdotxtgui.setEnableSave(true);
		}

		@Override
		public void onTextUpdate(Task t) {
			taskBag.update(t);
			jdotxtgui.setEnableSave(true);
		}
		
		@Override
		public void onForceTextUpdate(Task t) {
			if (taskBag != null) {
				jdotxtgui.updateFilterPanes();
				updateTaskPanel();
			}
		}

		@Override
		public void onNewTask(Task t) {
		}

		@Override
		public void onTaskDeleted(Task t) {
			JdotxtTasksPanel.this.remove(findTaskPanel(t));
			taskPanels.remove(findTaskPanel(t));
			taskBag.delete(t);
			JdotxtTasksPanel.this.requestFocus();
			revalidate();
			repaint();
			
			jdotxtgui.updateFilterPanes();
			jdotxtgui.setEnableSave(true);
			
		}
	}
	
	public class addNewTaskPanelListener implements TaskListener {

		@Override
		public void onPriorityUpdate(Task t) {}
		@Override
		public void onDateUpdate(Task t) {}
		@Override
		public void onCompletionUpdate(Task t) {}
		@Override
		public void onTextUpdate(Task t) {}
		@Override
		public void onForceTextUpdate(Task t) {}

		@Override
		public void onNewTask(Task t) {
			t = taskBag.addAsTask(t.inFileFormat());
			JdotxtTaskPanel tp = new JdotxtTaskPanel(t);
			tp.setTaskListener(tasksPanelTaskListener);
			taskPanels.add(tp);
			
			addTaskPanel.setTask(new Task(0, emptyTaskString, new Date()));
			JdotxtTasksPanel.this.requestFocus();
			jdotxtgui.updateFilterPanes();
			jdotxtgui.setEnableSave(true);
			updateTaskPanel();
			addTaskPanel.setFocusText();
		}

		@Override
		public void onTaskDeleted(Task t) { addTaskPanel.setTask(new Task(0, emptyTaskString, new Date())); }
	}
	
}
