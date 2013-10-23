package com.chschmid.jdotxt.gui;

import com.todotxt.todotxttouch.task.Task;

public interface TaskListener {
	public void onPriorityUpdate(Task t);
	public void onDateUpdate(Task t);
	public void onCompletionUpdate(Task t);
	public void onTextUpdate(Task t);
	public void onForceTextUpdate(Task t);
	public void onNewTask(Task t);
	public void onTaskDeleted(Task t);
}
