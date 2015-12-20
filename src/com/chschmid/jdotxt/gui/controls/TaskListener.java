/**
* Copyright (C) 2013-2015 Christian M. Schmid
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

import com.todotxt.todotxttouch.task.Task;

public interface TaskListener {
	public void taskCreated(Task t);
	public void taskUpdated(Task t, short field);
	public void taskDeleted(Task t);
	public void enterPressed(Task t, short field);
	public void focusGained(Task t, short field);
	public void focusLost(Task t, short field);
}
