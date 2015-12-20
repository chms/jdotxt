/**
* Copyright (C) 2013-2014 Christian M. Schmid
*
* This file is part of the jdotxt.
*
* jdotxt is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.

* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with this program. If not, see <http://www.gnu.org/licenses/>.
*/

package com.chschmid.jdotxt.gui.controls;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Rectangle;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Scrollable;

import com.chschmid.jdotxt.gui.JdotxtGUI;
import com.todotxt.todotxttouch.task.Filter;
import com.todotxt.todotxttouch.task.FilterFactory;
import com.todotxt.todotxttouch.task.Priority;
import com.todotxt.todotxttouch.task.Sort;
import com.todotxt.todotxttouch.task.Task;
import com.todotxt.todotxttouch.task.TaskBag;

@SuppressWarnings("serial")
public class JdotxtTaskList extends JPanel implements Scrollable {
	private Filter<Task> filter;
	private List<Task> tasks;

	private TaskBag taskBag;
	
	private ArrayList<GUItask> guiTaskPanelList = new ArrayList<GUItask>();
	private ListUpdater listUpdater;
	
	private JdotxtTaskPanel newTaskPanel;
	private JLabel loading;
	
	private String emptyTaskString = "";
	private String newTask = "";
	private String metaData = "";
	
	private boolean prependMetadata = false;
	private boolean copyMetadata = false;
	
	TasksPanelListener taskPanelListener = new TasksPanelListener();
	NewTaskPanelListener newTaskPanelListener = new NewTaskPanelListener();
	
	private ArrayList<TaskListener> taskListenerList = new ArrayList<TaskListener>();
	
	private boolean compactMode = false;
	
	public JdotxtTaskList() {
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
		
		newTaskPanel = new JdotxtTaskPanel(new Task(0, JdotxtGUI.lang.getWord("New_task"), new Date()), true, compactMode);
		newTaskPanel.addTaskListener(new NewTaskPanelListener());
	}
	
	public void reset() {
		stopListUpdater();
		taskBag = null;
		tasks = null;
		guiTaskPanelList = null;
		this.removeAll();
		this.add(loading);
		revalidate();
		repaint();
	}
	
	public void setCompactMode(boolean compactMode) {
		this.compactMode = compactMode;
		newTaskPanel = new JdotxtTaskPanel(new Task(0, JdotxtGUI.lang.getWord("New_task"), new Date()), true, compactMode);
		newTaskPanel.addTaskListener(new NewTaskPanelListener());
	}
	
	public void setPrependMetadata(boolean prependMetadata) {
		this.prependMetadata = prependMetadata;
		if (prependMetadata) {
			if (metaData.isEmpty()) emptyTaskString = newTask;
			else emptyTaskString = metaData + " " + newTask;
		} else {
			if (metaData.isEmpty()) emptyTaskString = newTask;
			else emptyTaskString = newTask + " " + metaData;
		}
		newTaskPanel.setTask(new Task(0, emptyTaskString, new Date()));
	}
	
	public void setCopyProjectsContexts2NewTask(boolean copyMetadata) {
		this.copyMetadata = copyMetadata;
	}
	
	public void setTaskBag(TaskBag taskBag) {
		reset();
		this.taskBag = taskBag;
		guiTaskPanelList = new ArrayList<GUItask>();
		guiTaskPanelList.ensureCapacity(taskBag.size());
		//updateTaskList();
	}
	
	public int getNumOfTasks() {
		if (tasks == null) return 0;
		return tasks.size();
	}
	
	public int getNumOfOpenTasks() {
		if (tasks == null) return 0;
		int n = 0;
		for(Task t: tasks) {
			if (!t.isCompleted()) n++;
		}
		return n;
	}
	
	public int getNumOfCompletedTasks() {
		return tasks.size() - getNumOfOpenTasks();
	}
	
	public void requestFocusNewTask() {
		if (newTaskPanel.isVisible()) newTaskPanel.requestFocus(JdotxtTaskPanel.CONTENT);
	}
	
	public void requestFocusTask(Task task, short control) {
		GUItask gt = getGUItask(task);
		if (gt != null) gt.panel.requestFocus(control);
	}
	
	public void setFilter(ArrayList<Priority> filterPrios, ArrayList<String> filterContexts, ArrayList<String> filterProjects, String search) {
		filter = FilterFactory.generateAndFilter(filterPrios, filterContexts, filterProjects, search, false);
		
		if (search.isEmpty()) {
			newTask = JdotxtGUI.lang.getWord("New_task");
			createMetaData(filterProjects, filterContexts);
			setPrependMetadata(this.prependMetadata);
			newTaskPanel.setVisible(true);
		} else newTaskPanel.setVisible(false);
	}
	
	private void createMetaData(List<String> projects, List<String> contexts) {
		metaData = "";
		for (String p: projects) if (!p.equals("-")) metaData = metaData + " +" + p;
		for (String c: contexts) if (!c.equals("-")) metaData = metaData + " @" + c;
		if (!metaData.isEmpty()) metaData = metaData.substring(1);
	}
	
	public void updateTaskList() {
		updateTaskList(null);
	}
	
	public void updateTaskList(Runnable postProcessing) {
		if (taskBag == null) return;
		List<Task> tasks = taskBag.getTasks(filter, Sort.PRIORITY_DESC.getComparator());
		if (!isUpToDate(tasks)) forceUpdateTaskList(tasks, postProcessing);
	}
	
	public void addTaskListener(TaskListener taskListener) { taskListenerList.add(taskListener); }
	public void removeTaskListener(TaskListener taskListener) { taskListenerList.remove(taskListener); }
	
	private void forceUpdateTaskList(List<Task> tasks, Runnable postProcessing) {
		stopListUpdater();
		this.tasks = tasks;
		startListUpdater(postProcessing);
	}
	
	private boolean isUpToDate(List<Task> tasks) {
		boolean inOrder = true;
		if (this.tasks == null || tasks == null) return false;
		if (tasks.size() != this.tasks.size()) return false;
		for (int k1 = 0; inOrder && k1 < tasks.size(); k1++) inOrder = (tasks.get(k1).equals(this.tasks.get(k1)));
		return inOrder;
	}
	
	private void startListUpdater(Runnable postProcessing) {
		listUpdater = new ListUpdater(postProcessing);
		listUpdater.start();
	}
	
	private void stopListUpdater() {
		if (listUpdater != null) {
			listUpdater.interrupt();
			try {
				listUpdater.join();
			} catch (InterruptedException e) {
			}
		}
		listUpdater = null;
		revalidate();
		repaint();
	}
	
	private class TaskPanelAdder implements Runnable {
		Task t;
		
		public TaskPanelAdder(Task t) {
			this.t = t;
		}
		@Override
		public void run() {
			GUItask gt;
			gt = getGUItask(t);
			if (gt == null) {
				gt = new GUItask(t, new JdotxtTaskPanel(t, false, compactMode));
				guiTaskPanelList.add(gt);
				gt.panel.addTaskListener(taskPanelListener);
			}
			JdotxtTaskList.this.add(gt.panel);
		}
	}
	
	private class ListUpdater extends Thread {
		Runnable postProcessing;
		
		public ListUpdater(Runnable postProcessing) {
			this.postProcessing = postProcessing;
		}
		
		public void run() {
			EventQueue.invokeLater(new Runnable() {
				@Override
				public void run() {
					removeAll();
					add(newTaskPanel);
				}
			});
			for (int k1 = 0; !isInterrupted() && k1 < tasks.size(); k1++){
				Task t = tasks.get(k1);
				try {
					EventQueue.invokeAndWait(new TaskPanelAdder(t));
				} catch (InvocationTargetException | InterruptedException e) {
					interrupt();
				}
			}
			EventQueue.invokeLater(new Runnable() {
				@Override
				public void run() {
					revalidate();
					repaint();
				}
			});
			if (!isInterrupted()) {
				if (postProcessing != null) EventQueue.invokeLater(postProcessing);
			} else {
				
			}
		}
	}
	
	private GUItask getGUItask(Task t) {
		for (GUItask gt: guiTaskPanelList) if (gt.task == t) return gt;
		for (GUItask gt: guiTaskPanelList) if (gt.task.equals(t)) return gt;
		return null;
	}
	
	private class TasksPanelListener implements TaskListener {
		@Override
		public void taskCreated(Task t) {
		}
		@Override
		public void taskUpdated(Task t, short field) {
			taskBag.update(t);
			if (field != JdotxtTaskPanel.CONTENT) updateTaskList(t, field);
			for (int i = taskListenerList.size()-1; i >= 0; i--) taskListenerList.get(i).taskUpdated(t, field);
			modifyNewTaskField(t);
		}
		
		@Override
		public void taskDeleted(Task t) {
			//System.out.println("taskDeleted: " + "-, " + t.getId() + ", " + t.inFileFormat());
			taskBag.delete(t);
			tasks = taskBag.getTasks(filter, Sort.PRIORITY_DESC.getComparator());
			JdotxtTaskList.this.remove(getGUItask(t).panel);
			revalidate();
			repaint();
			for (int i = taskListenerList.size()-1; i >= 0; i--) taskListenerList.get(i).taskDeleted(t);
		}
		
		@Override
		public void enterPressed(Task t, short field) {
			//System.out.println("enterPressed: " + field + ", " + t.getId() + ", " + t.inFileFormat());
			updateTaskList(t, field);
			for (int i = taskListenerList.size()-1; i >= 0; i--) taskListenerList.get(i).enterPressed(t, field);
		}
		
		private void updateTaskList(Task t, short field) {
			if (taskBag == null) return;
			List<Task> tasks = taskBag.getTasks(filter, Sort.PRIORITY_DESC.getComparator());
			if (!isUpToDate(tasks)) {
				JdotxtTaskList.this.requestFocus();
				Runnable postprocessing ;
				if (field == JdotxtTaskPanel.COMPLETED && t.isCompleted()) postprocessing = new RequestFocus(null, (short)0);
				else postprocessing = new RequestFocus(t, field);
				forceUpdateTaskList(tasks, postprocessing);
			}
		}
		
		@Override
		public void focusLost(Task t, short field) {
			enterPressed(t, field);
		}
		
		@Override
		public void focusGained(Task t, short field) { modifyNewTaskField(t); }
		
		private void modifyNewTaskField(Task t) {
			if (copyMetadata) {
				createMetaData(t.getProjects(), t.getContexts());
				setPrependMetadata(prependMetadata);
			}
		}
	}
	
	private class NewTaskPanelListener implements TaskListener {
		@Override
		public void taskCreated(Task t) {
			addNewTask(t);
			for (int i = taskListenerList.size()-1; i >= 0; i--) taskListenerList.get(i).taskCreated(t);
		}
		@Override
		public void taskUpdated(Task t, short field) {
		}
		@Override
		public void taskDeleted(Task t) {
			newTaskPanel.setTask(new Task(0, emptyTaskString, new Date()));
			newTaskPanel.selectNewTask();
			for (int i = taskListenerList.size()-1; i >= 0; i--) taskListenerList.get(i).taskDeleted(t);
		}
		@Override
		public void enterPressed(Task t, short field) {
			addNewTask(t);
			for (int i = taskListenerList.size()-1; i >= 0; i--) taskListenerList.get(i).taskCreated(t);
		}
		
		private void addNewTask(Task t) {
			t = taskBag.addAsTask(t.inFileFormat());
			JdotxtTaskList.this.requestFocus();
			newTaskPanel.setTask(new Task(0, emptyTaskString, new Date()));
			modifyNewTaskField(t);
			updateTaskList(new Runnable() {
				@Override
				public void run() { 
					requestFocusNewTask();
				}
			});
		}
		@Override
		public void focusLost(Task t, short field) { }
		@Override
		public void focusGained(Task t, short field) { }
		
		private void modifyNewTaskField(Task t) {
			createMetaData(t.getProjects(), t.getContexts());
			setPrependMetadata(prependMetadata);
		}
	}
	
	private class GUItask {
		public Task task;
		public JdotxtTaskPanel panel;
		
		public GUItask(Task task, JdotxtTaskPanel panel) {
			this.task  = task;
			this.panel = panel;
		}
	}
	
	private class RequestFocus implements Runnable {
		short field;
		Task t;
		
		public RequestFocus (Task t, short i) {
			this.t = t;
			this.field = i;
		}
		@Override
		public void run() {
			if (t == null) requestFocusNewTask();
			requestFocusTask(t, field);
		}
	}

	@Override
	public Dimension getPreferredScrollableViewportSize() {
		return getPreferredSize();
	}
	

	@Override
	public boolean getScrollableTracksViewportHeight() {
		return false;
	}

	@Override
	public boolean getScrollableTracksViewportWidth() {
		return true;
	}

	@Override
	public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
		int hNew = 10, hTask = 10, increment = 0, currentPosition;
		
		hNew  = newTaskPanel.getHeight();
		if (guiTaskPanelList != null) hTask = guiTaskPanelList.get(0).panel.getHeight();
		
		currentPosition = visibleRect.y;
		
		if (newTaskPanel.isVisible()) {
			if (currentPosition <= hNew) {
				if (direction < 0) increment = currentPosition;
				else {
					increment = hNew - currentPosition;
				}
			} else {
				currentPosition = currentPosition - hNew;
				increment = currentPosition - (currentPosition / hTask) * hTask;
				if (direction > 0) increment = hTask - increment;
			}
		} else {
			increment = currentPosition - (currentPosition / hTask) * hTask;
			if (direction > 0) increment = hTask - increment;
		}
		if (increment == 0) increment = hTask;
		
		return increment;
	}

	@Override
	public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
		int pageIncrement = 0, hTask = 10;
		
		if (guiTaskPanelList != null) hTask = guiTaskPanelList.get(0).panel.getHeight();

		if (direction < 0) {
			visibleRect.y = visibleRect.y - visibleRect.height;
			pageIncrement = - getScrollableUnitIncrement(visibleRect, orientation, 1);
			if (pageIncrement <= - hTask) pageIncrement = 0;
			pageIncrement = pageIncrement + visibleRect.height;
		} else {
			visibleRect.y = visibleRect.y + visibleRect.height;
			pageIncrement = - getScrollableUnitIncrement(visibleRect, orientation, -1);
			if (pageIncrement <= - hTask) pageIncrement = 0;
			pageIncrement = pageIncrement + visibleRect.height;
		}

		return pageIncrement;
	}
}
