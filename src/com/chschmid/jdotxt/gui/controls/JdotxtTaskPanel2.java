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

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.chschmid.jdotxt.Jdotxt;
import com.chschmid.jdotxt.gui.JdotxtGUI;
import com.todotxt.todotxttouch.task.Task;
import com.todotxt.todotxttouch.util.Util;

@SuppressWarnings("serial")
public class JdotxtTaskPanel2 extends JPanel {
	private JPanel panelTodoInfo;
	private JPanel panelTodoCommands;
	private JdotxtImageButton buttonNewTask;
	
	private JdotxtPriorityField textPriority;
	private JTextField textContent;
	private JdotxtDateField textDate;

	private JdotxtImageCheckBox checkDone;
	private JdotxtImageButton buttonDelete;
	
	public static final ImageIcon imgComplete   = Util.createImageIcon("/res/drawable/check.png");
	public static final ImageIcon imgIncomplete = Util.createImageIcon("/res/drawable/uncheck.png");
	public static final ImageIcon imgDelete     = Util.createImageIcon("/res/drawable/delete.png");
	
	private Task task;
	private boolean isNewTask;
	
	public JdotxtTaskPanel2(Task task) {
		this.task = new Task(task);
		isNewTask = false;
		initLayout();
	}
	
	public JdotxtTaskPanel2(Task task, boolean isNewTask) {
		this.task = task;
		this.isNewTask = isNewTask;
		initLayout();
	}
	
	private void initLayout() {
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		
		boolean compactMode = Jdotxt.userPrefs.getBoolean("compactMode", false);

		panelTodoInfo     = new JPanel();
		panelTodoCommands = new JPanel();
		textContent       = new JTextField(task.getText());
		textDate          = new JdotxtDateField(task.getPrependedDate());
		textPriority      = new JdotxtPriorityField(task.getPriority().getCode().charAt(0));
		checkDone         = new JdotxtImageCheckBox(JdotxtTaskPanel2.imgIncomplete, JdotxtTaskPanel2.imgComplete);
		buttonDelete      = new JdotxtImageButton(JdotxtTaskPanel2.imgDelete);
		
		textPriority.setAlignmentY(TOP_ALIGNMENT);
		textPriority.setBorder(BorderFactory.createLineBorder(Color.WHITE,4));
		textPriority.setFont(JdotxtGUI.fontR);
		textPriority.setColumns(2);
		textPriority.setMaximumSize(new Dimension(textPriority.getPreferredSize().width, textPriority.getPreferredSize().height));
		textPriority.setSelectionColor(JdotxtGUI.COLOR_PRESSED);
		
		textContent.setFont(JdotxtGUI.fontR);
		textContent.setBorder(BorderFactory.createLineBorder(Color.WHITE,4));
		textContent.setSelectionColor(JdotxtGUI.COLOR_PRESSED);
		/*textContent.getDocument().addDocumentListener(new TextListener());
		textContent.addFocusListener(new TextFocusListener());
		textContent.addActionListener(new TextActionListener());
		textContent.addKeyListener(new TextKeyListener());*/
		textContent.setPreferredSize(new Dimension(0, textContent.getPreferredSize().height));
		
		textDate.setFont(JdotxtGUI.fontR.deriveFont(12f));
		textDate.setBorder(BorderFactory.createLineBorder(Color.WHITE,4));
		textDate.setSelectionColor(JdotxtGUI.COLOR_PRESSED);
		
		checkDone.setBackground(Color.WHITE);
		checkDone.setAlignmentX(CENTER_ALIGNMENT);
		//checkDone.addActionListener(new DoneListener());
		
		buttonDelete.setBackground(Color.WHITE);
		buttonDelete.setAlignmentX(CENTER_ALIGNMENT);
		//buttonDelete.addActionListener(new DeleteListener());
		
		panelTodoInfo.setLayout(new BoxLayout(panelTodoInfo, BoxLayout.Y_AXIS));
		panelTodoInfo.add(textContent);
		if (!compactMode) panelTodoInfo.add(textDate); // No-Date-mod
		panelTodoInfo.setBorder(BorderFactory.createEmptyBorder());
		panelTodoInfo.setBackground(Color.WHITE);
		panelTodoInfo.setMaximumSize(new Dimension(Integer.MAX_VALUE, panelTodoInfo.getPreferredSize().height));
		panelTodoInfo.setAlignmentY(TOP_ALIGNMENT);
		
		if (!compactMode) panelTodoCommands.setLayout(new BoxLayout(panelTodoCommands, BoxLayout.Y_AXIS));
		else panelTodoCommands.setLayout(new BoxLayout(panelTodoCommands, BoxLayout.X_AXIS));
		
		panelTodoCommands.add(checkDone);
		panelTodoCommands.add(buttonDelete);
		panelTodoCommands.setBorder(BorderFactory.createEmptyBorder());
		panelTodoCommands.setBackground(Color.WHITE);
		panelTodoCommands.setAlignmentY(TOP_ALIGNMENT);
		
		if (isNewTask) {
			if (!compactMode) buttonNewTask = new JdotxtImageButton(Util.createImageIcon("/res/drawable/add.png"));
			else buttonNewTask = new JdotxtImageButton(Util.createImageIcon("/res/drawable/add_25.png"));
			buttonNewTask.setAlignmentY(TOP_ALIGNMENT);
			//buttonNewTask.addActionListener(new AddTaskListener());
			this.setBorder(BorderFactory.createMatteBorder(2, 0, 2, 0, JdotxtGUI.COLOR_HOVER));
		} else this.setBorder(BorderFactory.createMatteBorder(1, 0, 1, 0, JdotxtGUI.COLOR_GRAY_PANEL));
		this.setBackground(Color.WHITE);
		
		if (isNewTask) this.add(buttonNewTask);
		else this.add(Box.createRigidArea(new Dimension(4, 0)));
		
		this.add(textPriority);
		this.add(panelTodoInfo);
		this.add(panelTodoCommands);

		this.setOpaque(true);
		
		//if (task.isCompleted()) markComplete();
	}
}
