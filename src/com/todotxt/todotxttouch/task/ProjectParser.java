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
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class ProjectParser {
	private final static Pattern PROJECT_PATTERN = Pattern
			.compile("(?:^|\\s)\\+([\\w_\\.\\-\\:\\/]+)", Pattern.UNICODE_CHARACTER_CLASS);
	private static final ProjectParser INSTANCE = new ProjectParser();

	private ProjectParser() {
	}

	public static ProjectParser getInstance() {
		return INSTANCE;
	}

	public List<String> parse(String inputText) {
		if (inputText == null) {
			return Collections.emptyList();
		}
		Matcher m = PROJECT_PATTERN.matcher(inputText);
		List<String> projects = new ArrayList<String>();
		while (m.find()) {
			String project = m.group(1).trim();
			projects.add(project);
		}
		return projects;
	}
}
