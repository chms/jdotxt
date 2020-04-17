/**
 * This file is part of Todo.txt Touch, an Android app for managing your todo.txt file (http://todotxt.com).
 *
 * Copyright (c) 2009-2013 Todo.txt contributors (http://todotxt.com)
 *
 * LICENSE:
 *
 * Todo.txt Touch is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 2 of the License, or (at your option) any
 * later version.
 *
 * Todo.txt Touch is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with Todo.txt Touch.  If not, see
 * <http://www.gnu.org/licenses/>.
 *
 * @author Todo.txt contributors <todotxt@yahoogroups.com>
 * @license http://www.gnu.org/licenses/gpl.html
 * @copyright 2009-2013 Todo.txt contributors (http://todotxt.com)
 */
package com.todotxt.todotxttouch.task;

import com.todotxt.todotxttouch.task.sorter.PredefinedSorters;

import java.util.*;

/**
 * Implementation of the TaskBag interface
 * 
 * @author Tim Barlotta
 */
class JdotxtTaskBagImpl implements TaskBag {
	private final LocalTaskRepository localRepository;
	private ArrayList<Task> tasks = new ArrayList<Task>();
	private Date lastReload = null;
	private Date lastWrite  = null;
	private Date lastChange = null;
	private byte[] lastSaveChecksum = null;

	public JdotxtTaskBagImpl(LocalTaskRepository localRepository) {
		this.localRepository = localRepository;
	}

	public void store(ArrayList<Task> tasks) {
		if (lastChange != null && (lastWrite == null || lastChange.after(lastWrite))) {
			lastSaveChecksum = localRepository.store(tasks);
			// System.out.println("Saved: " + DatatypeConverter.printHexBinary(lastSaveChecksum));
		}
		
		lastWrite = new Date();
		lastReload = null;
		lastChange = null;
	}

	public void store() {
		store(this.tasks);
	}

	@Override
	public void archive() {
		try {
			localRepository.archive(tasks);
			lastReload = null;
			lastWrite = new Date();
			lastSaveChecksum = null;
		} catch (Exception e) {
			throw new TaskPersistException("An error occurred while archiving",
					e);
		}
	}

	@Override
	public void unarchive(Task task) {
		try {
			reload();
			int index = (int)task.getId();
			if (index >= tasks.size()) {
				index = tasks.size() - 1;
			}
			if (index < 0) {
				index = 0;
			}
			tasks.add(index, task);
			store();
			removeArchivedTask(task);
		} catch (Exception e) {
			throw new TaskPersistException("An error occurred while adding {"
					+ task + "}", e);
		}
	}
	
	private void removeArchivedTask(Task task) {
		ArrayList<Task> doneTasks = localRepository.loadDoneTasks();
		Task found = find(doneTasks, task);
		if (found != null) {
			doneTasks.remove(found);
			localRepository.storeDoneTasks(doneTasks);
		}
	}
	
	@Override
	public void reload() {
		if (lastReload == null || localRepository.todoFileModifiedSince(lastReload)) {
			localRepository.init();
			this.tasks = localRepository.load();
			lastReload = new Date();
			lastWrite  = new Date();
			lastChange = null;
			lastSaveChecksum = null;
		}
	}

	@Override
	public void clear() {
		this.tasks = new ArrayList<Task>();
		localRepository.purge();
		lastReload = null;
		lastWrite  = null;
		lastChange = null;
		lastSaveChecksum = null;
	}

	@Override
	public int size() {
		return tasks.size();
	}

	@Override
	public List<Task> getTasks() {
		return getTasks(null, null);
	}

	@Override
	public List<Task> getTasks(Filter<Task> filter, Comparator<Task> comparator) {
		ArrayList<Task> localTasks = new ArrayList<Task>();
		if (filter != null) {
			for (Task t : tasks) {
				if (filter.apply(t)) {
					localTasks.add(t);
				}
			}
		} else {
			localTasks.addAll(tasks);
		}

		if (comparator == null) {
			comparator = PredefinedSorters.DEFAULT;
		}

		Collections.sort(localTasks, comparator);

		return localTasks;
	}

	@Override
	public Task addAsTask(String input) {
		try {
			//reload();
			Task task = new Task(tasks.size(), input, new Date());
			tasks.add(task);
			lastChange = new Date();
			return task;
			//store();
		} catch (Exception e) {
			throw new TaskPersistException("An error occurred while adding {"
					+ input + "}", e);
		}
	}

	@Override
	public void update(Task task) {
		lastChange = new Date();
		if (task == null) return;
		try {
			//reload();
			Task found = JdotxtTaskBagImpl.find(tasks, task);
			if (found != null) {
				task.copyInto(found);
				// Log.i(TAG, "copied into found {" + found + "}");
				// store();
			} else {
				throw new TaskPersistException("Task not found, not updated");
			}
		} catch (Exception e) {
			throw new TaskPersistException(
					"An error occurred while updating Task {" + task + "}", e);
		}
	}

	@Override
	public void delete(Task task) {
		try {
			//reload();
			Task found = JdotxtTaskBagImpl.find(tasks, task);
			if (found != null) {
				tasks.remove(found);
				lastChange = new Date();
				//store();
			} else {
				throw new TaskPersistException("Task not found, not deleted");
			}
		} catch (Exception e) {
			throw new TaskPersistException(
					"An error occurred while deleting Task {" + task + "}", e);
		}
	}

	@Override
	public void pushToRemote(boolean overwrite) { /* Remote API not implemented*/ }
	@Override
	public void pushToRemote(boolean overridePreference, boolean overwrite) { /* Remote API not implemented*/ }
	@Override
	public void pullFromRemote() { /* Remote API not implemented*/ }
	@Override
	public void pullFromRemote(boolean overridePreference) { /* Remote API not implemented*/ }

	@Override
	public ArrayList<Priority> getPriorities() {
		// TODO cache this after reloads?
		Set<Priority> res = new HashSet<Priority>();
		for (Task item : tasks) {
			res.add(item.getPriority());
		}
		ArrayList<Priority> ret = new ArrayList<Priority>(res);
		Collections.sort(ret);
		return ret;
	}

	@Override
	public ArrayList<String> getContexts(boolean includeNone) {
		// TODO cache this after reloads?
		Set<String> res = new HashSet<String>();
		for (Task item : tasks) {
			res.addAll(item.getContexts());
		}
		ArrayList<String> ret = new ArrayList<String>(res);
		Collections.sort(ret);
		if (includeNone) {
			ret.add(0, "-");
		}
		return ret;
	}

	@Override
	public ArrayList<String> getProjects(boolean includeNone) {
		// TODO cache this after reloads?
		Set<String> res = new HashSet<String>();
		for (Task item : tasks) {
			res.addAll(item.getProjects());
		}
		ArrayList<String> ret = new ArrayList<String>(res);
		Collections.sort(ret);
		if (includeNone) {
			ret.add(0, "-");
		}
		return ret;
	}

	private static Task find(List<Task> tasks, Task task) {
		Task partialMatch1 = null;
		Task partialMatch2 = null;
		for (Task task2 : tasks) {
			if (task2 == task) {
				return task2;
			}
			if (task2.getText().equals(task.getOriginalText())) {
				if (task2.getPriority() == task.getOriginalPriority()) {
					partialMatch1 = task2;
					if (task2.getId() == task.getId()) return task2;
				}

				// We prefer to find an exact match (both text and priority are
				// the same), but it is possible that priority has been lost
				// because the task has been completed, so we will consider
				// partial matches as a last resort.
				partialMatch2 = task2;
			}
		}
		if (partialMatch1 != null) return partialMatch1;
		return partialMatch2;
	}
	
	public boolean hasChanged(){
		return (lastChange != null);
	}

	public byte[] getLastSaveChecksum() {
		return lastSaveChecksum;
	}
}
