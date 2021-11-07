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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Interface for interacting with the tasks in aggregate
 * 
 * @author Tim Barlotta
 */
public interface TaskBag {
	void store();
	
	void store(ArrayList<Task> tasks);

	void archive();

	void unarchive(Task task);

	void reload();

	void clear();

	Task addAsTask(String input);

	void update(Task task);

	void delete(Task task);

	List<Task> getTasks();

	List<Task> getTasks(Filter<Task> filter, Comparator<Task> comparator);

	int size();

	ArrayList<String> getProjects(boolean includeNone);

	ArrayList<String> getContexts(boolean includeNone);

	ArrayList<Priority> getPriorities();

	boolean renameProject(String from, String to);

	/* REMOTE APIs */
	// FUTURE make this syncWithRemote()
	/**
	 * Push tasks in localRepository into remoteRepository if you're not working
	 * offline
	 */
	void pushToRemote(boolean overwrite);

	/**
	 * Force-push tasks in localRepository into remoteRepository disregarding
	 * Work Offline status
	 */
	void pushToRemote(boolean overridePreference, boolean overwrite);

	/**
	 * Pulls tasks from remoteRepository, stores in localRepository
	 */
	void pullFromRemote();

	/**
	 * Force-pull tasks from remoteRepository into localRepository disregarding
	 * Work Offline status
	 */
	void pullFromRemote(boolean overridePreference);

	/* END REMOTE APIs */
	
	// CS MODIFICATIONS
	boolean hasChanged();

	byte[] getLastSaveChecksum();
}
