/**
* Copyright (C) 2013 Christian M. Schmid
*
* This file is part of the jdotxt.
*
* PILight is free software: you can redistribute it and/or modify
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

import javax.swing.JPanel;
import javax.swing.JTextField;

@SuppressWarnings("serial")
public class JdotxtTaskPanel extends JPanel {
	private JPanel panelTodoInfo;
	private JPanel panelTodoCommands;
	private JdotxtImageButton buttonNewTask;
	
	private JdotxtPriorityField textPriority;
	private JTextField textContent;
	private JdotxtDateField textDate;

	private JdotxtImageCheckBox checkDone;
	private JdotxtImageButton buttonDelete;
}
