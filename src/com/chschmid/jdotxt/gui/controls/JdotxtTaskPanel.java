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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.InputEvent;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.DefaultEditorKit;

import com.chschmid.jdotxt.gui.JdotxtGUI;
import com.todotxt.todotxttouch.task.Priority;
import com.todotxt.todotxttouch.task.Task;
import com.todotxt.todotxttouch.util.Util;

@SuppressWarnings("serial")
public class JdotxtTaskPanel extends JPanel {
	public final static short ADDNEW             = 0;
	public final static short PRIORITY           = 1;
	public final static short CONTENT            = 2;
	public final static short DATE               = 3;
	public final static short COMPLETED          = 4;
	public final static short DELETE             = 5;
	
	private final static String DONE_ACTION = "done";
	private final static String DELETE_ACTION = "delete";
	
	private JPanel panelTodoInfo;
	private JPanel panelTodoCommands;
	private JdotxtImageButton buttonNewTask;
	
	private JdotxtPriorityField textPriority;
	private JdotxtContentField textContent;
	private JdotxtDateField textDate;

	private JdotxtImageCheckBox checkDone;
	private JdotxtImageButton buttonDelete;
	
	public static final ImageIcon imgComplete   = Util.createImageIcon("/res/drawable/check.png");
	public static final ImageIcon imgIncomplete = Util.createImageIcon("/res/drawable/uncheck.png");
	public static final ImageIcon imgDelete     = Util.createImageIcon("/res/drawable/delete.png");
	
	private Task task;
	private boolean isNewTask;
	
	private boolean compactMode;
	
	private ArrayList<TaskListener> taskListenerList;
	
	private char oldPriority;
	private String oldDate;
	private String oldContent;
	
	public JdotxtTaskPanel(Task task) {
		initJdotxtTaskPanel(task, false, false);
	}
	
	public JdotxtTaskPanel(Task task, boolean isNewTask) {
		initJdotxtTaskPanel(task, isNewTask, false);
	}
	
	public JdotxtTaskPanel(Task task, boolean isNewTask, boolean compactMode) {
		initJdotxtTaskPanel(task, isNewTask, compactMode);
	}
	
	private void initJdotxtTaskPanel(Task task, boolean isNewTask, boolean compactMode) {
		//this.task = new Task(task);
		this.task = task;
		this.isNewTask = isNewTask;
		this.compactMode = compactMode;
		taskListenerList = new ArrayList<TaskListener>();
		
		oldPriority = task.getPriority().getCode().charAt(0);
		oldDate = task.getPrependedDate();
		oldContent = task.getText();
		
		initLayout();
	}
	
	private void initLayout() {
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		this.removeAll();
		
		panelTodoInfo     = new JPanel();
		panelTodoCommands = new JPanel();
		textContent       = new JdotxtContentField(task.getText());
		textDate          = new JdotxtDateField(task.getPrependedDate());
		textPriority      = new JdotxtPriorityField(task.getPriority().getCode().charAt(0));
		checkDone         = new JdotxtImageCheckBox(JdotxtTaskPanel.imgIncomplete, JdotxtTaskPanel.imgComplete);
		buttonDelete      = new JdotxtImageButton(JdotxtTaskPanel.imgDelete);
		
		textPriority.setAlignmentY(TOP_ALIGNMENT);
		textPriority.setBorder(BorderFactory.createLineBorder(Color.WHITE,4));
		textPriority.setFont(JdotxtGUI.fontR);
		textPriority.setColumns(2);
		textPriority.setMaximumSize(new Dimension(textPriority.getPreferredSize().width, textPriority.getPreferredSize().height));
		textPriority.setSelectionColor(JdotxtGUI.COLOR_PRESSED);
		textPriority.setFocusNext(true);
		textPriority.getDocument().addDocumentListener(new PriorityListener());
		textPriority.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke("ENTER"), "ENTER");
		textPriority.getActionMap().put("ENTER", new EnterAction(PRIORITY));
		textPriority.addFocusListener(new FocusListener() {
			@Override
			public void focusLost(FocusEvent arg0) { fireFocusLost(PRIORITY); }
			@Override
			public void focusGained(FocusEvent arg0) { fireFocusGained(PRIORITY); }
		});
		
		textContent.setFont(JdotxtGUI.fontR);
		textContent.setBorder(BorderFactory.createLineBorder(Color.WHITE,4));
		textContent.setSelectionColor(JdotxtGUI.COLOR_PRESSED);
		textContent.setPreferredSize(new Dimension(0, textContent.getPreferredSize().height));
		textContent.getDocument().addDocumentListener(new TextListener());
		textContent.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke("ENTER"), "ENTER");
		textContent.getActionMap().put("ENTER", new EnterAction(CONTENT));
		textContent.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke("LEFT"), "PREV");
		textContent.getActionMap().put("PREV", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (textContent.getCaretPosition() == 0) KeyboardFocusManager.getCurrentKeyboardFocusManager().focusPreviousComponent();
				((AbstractAction) textContent.getActionMap().get(DefaultEditorKit.backwardAction)).actionPerformed(e);
			}
		});
		textContent.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke("RIGHT"), "NEXT");
		textContent.getActionMap().put("NEXT", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (textContent.getCaretPosition() == textContent.getText().length() && !compactMode) KeyboardFocusManager.getCurrentKeyboardFocusManager().focusNextComponent();
				((AbstractAction) textContent.getActionMap().get(DefaultEditorKit.forwardAction)).actionPerformed(e);
			}
		});
		textContent.addFocusListener(new FocusListener() {
			@Override
			public void focusLost(FocusEvent arg0) { fireFocusLost(CONTENT); }
			@Override
			public void focusGained(FocusEvent arg0) {
				if(isNewTask) selectNewTask();
				fireFocusGained(CONTENT);
			}
		});
		
		textDate.setFont(JdotxtGUI.fontR.deriveFont(12f));
		textDate.setBorder(BorderFactory.createLineBorder(Color.WHITE,4));
		textDate.setSelectionColor(JdotxtGUI.COLOR_PRESSED);
		textDate.setFocusPrevious(true);
		textDate.getDocument().addDocumentListener(new DateListener());
		textDate.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke("ENTER"), "ENTER");
		textDate.getActionMap().put("ENTER", new EnterAction(DATE));
		textDate.addFocusListener(new FocusListener() {
			@Override
			public void focusLost(FocusEvent arg0) { fireFocusLost(DATE); }
			@Override
			public void focusGained(FocusEvent arg0) { fireFocusGained(DATE); }
		});
		
		checkDone.setBackground(Color.WHITE);
		checkDone.setAlignmentX(CENTER_ALIGNMENT);
		checkDone.setHoverColor(JdotxtGUI.COLOR_HOVER);
		checkDone.setPressedColor(JdotxtGUI.COLOR_PRESSED);
		checkDone.setBackgroundColor(null);
		DoneListener doneListener = new DoneListener();
		checkDone.addActionListener(doneListener);
		checkDone.addFocusListener(new FocusListener() {
			@Override
			public void focusLost(FocusEvent arg0) { fireFocusLost(COMPLETED); }
			@Override
			public void focusGained(FocusEvent arg0) { fireFocusGained(COMPLETED); }
		});
		
		buttonDelete.setBackground(Color.WHITE);
		buttonDelete.setAlignmentX(CENTER_ALIGNMENT);
		buttonDelete.setHoverColor(JdotxtGUI.COLOR_HOVER);
		buttonDelete.setPressedColor(JdotxtGUI.COLOR_PRESSED);
		buttonDelete.setBackgroundColor(null);
		buttonDelete.addActionListener(new DeleteListener());
		buttonDelete.addFocusListener(new FocusListener() {
			@Override
			public void focusLost(FocusEvent arg0) { fireFocusLost(DELETE); }
			@Override
			public void focusGained(FocusEvent arg0) { fireFocusGained(DELETE); }
		});
		
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
			buttonNewTask.setHoverColor(JdotxtGUI.COLOR_HOVER);
			buttonNewTask.setPressedColor(JdotxtGUI.COLOR_PRESSED);
			buttonNewTask.setBackgroundColor(null);
			buttonNewTask.addActionListener(new AddTaskListener());
			this.setBorder(BorderFactory.createMatteBorder(2, 0, 2, 0, JdotxtGUI.COLOR_HOVER));
		} else this.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, JdotxtGUI.COLOR_GRAY_PANEL));
		this.setBackground(Color.WHITE);
		
		if (isNewTask) this.add(buttonNewTask);
		else this.add(Box.createRigidArea(new Dimension(4, 0)));
		
		this.add(textPriority);
		this.add(panelTodoInfo);
		this.add(panelTodoCommands);
		this.setOpaque(true);
		
		InputMap im = this.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        ActionMap am = this.getActionMap();
        im.put(KeyStroke.getKeyStroke('D', InputEvent.CTRL_DOWN_MASK), DONE_ACTION);
        im.put(KeyStroke.getKeyStroke('D', InputEvent.CTRL_DOWN_MASK + InputEvent.SHIFT_DOWN_MASK), DELETE_ACTION);
        am.put(DONE_ACTION, new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) { checkDone.setSelected(!checkDone.isSelected()); }
		});
        am.put(DELETE_ACTION, new DeleteListener());
		
		if (task.isCompleted()) markComplete();
	}
	
	public void markComplete() {
		textContent.setForeground(Color.GRAY);
		textDate.setForeground(Color.GRAY);
		textPriority.setEnabled(false);
		if (!checkDone.isSelected()) checkDone.setSelected(true);
	}
	
	public void markIncomplete() {
		textContent.setForeground(Color.BLACK);
		textDate.setForeground(Color.BLACK);
		textPriority.setEnabled(true);
		textPriority.setPriority(task.getPriority().getCode().charAt(0));
		if (checkDone.isSelected()) checkDone.setSelected(false);
	}
	
	public void requestFocus(short control) {
		if (control == ADDNEW && buttonNewTask != null) buttonNewTask.requestFocus();
		if (control == PRIORITY)  textPriority.requestFocus();
		if (control == CONTENT)   textContent.requestFocus();
		if (control == DATE)      textDate.requestFocus();
		if (control == COMPLETED) checkDone.requestFocus();
		if (control == DELETE)    buttonDelete.requestFocus();
	}
	
	public void selectNewTask() {
		String taskText = textContent.getText();
	    String newTask = JdotxtGUI.lang.getWord("New_task");
	    
		int pos = taskText.indexOf(newTask);
		
		if (pos != -1) {
			textContent.setSelectionStart(pos);
			textContent.setSelectionEnd(pos + newTask.length());
		}
	}
	
	public void setTask(Task task) {
		this.task = new Task(task);
		textContent.setText(task.getText());
		textDate.setDate(task.getPrependedDate());
		if(task.isCompleted()) markComplete();
		else markIncomplete();
	}
	
	public Task getTask() {
		return task;
	}
	
	public void addTaskListener(TaskListener taskListener) { taskListenerList.add(taskListener); }
	public void removeTaskListener(TaskListener taskListener) { taskListenerList.remove(taskListener); }
	
	private void fireTaskCreated() {
		//System.out.println("task created");
        for (int i = taskListenerList.size()-1; i >= 0; i--) taskListenerList.get(i).taskCreated(task);
	}
	
	private void fireTaskUpdated(short field) {
		//System.out.println("task updated ("  + field +")");
        for (int i = taskListenerList.size()-1; i >= 0; i--) taskListenerList.get(i).taskUpdated(task, field);
	}
	
	private void fireTaskDeleted() {
		//System.out.println("task deleted: ");
        for (int i = taskListenerList.size()-1; i >= 0; i--) taskListenerList.get(i).taskDeleted(task);
	}
	
	private void fireEnterPressed(short field) {
		//System.out.println("enter ("  + field +")");
        for (int i = taskListenerList.size()-1; i >= 0; i--) taskListenerList.get(i).enterPressed(task, field);
	}
	
	private void fireFocusLost(short field) {
		//System.out.println("enter ("  + field +")");
        for (int i = taskListenerList.size()-1; i >= 0; i--) taskListenerList.get(i).focusLost(task, field);
	}
	
	private void fireFocusGained(short field) {
		//System.out.println("enter ("  + field +")");
        for (int i = taskListenerList.size()-1; i >= 0; i--) taskListenerList.get(i).focusGained(task, field);
	}
	
	private class AddTaskListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			fireTaskCreated();
		}
	}
	
	private class PriorityListener implements DocumentListener {
		@Override
		public void changedUpdate(DocumentEvent e) {}
		@Override
		public void insertUpdate(DocumentEvent e) { updatePriority(e); }
		@Override
		public void removeUpdate(DocumentEvent e) {}
		
		private void updatePriority(DocumentEvent e) {
			if (oldPriority != textPriority.getText().charAt(1)){
				oldPriority = textPriority.getText().charAt(1);
				task.setPriority(Priority.toPriority(Character.toString(oldPriority)));
				fireTaskUpdated(PRIORITY);
			}
		}
	}
	
	private class TextListener implements DocumentListener {
		@Override
		public void changedUpdate(DocumentEvent e) { }
		@Override
		public void insertUpdate(DocumentEvent e) { updateText(e); }
		@Override
		public void removeUpdate(DocumentEvent e) { updateText(e); }
		
		private void updateText(DocumentEvent e) {
			if (!oldContent.equals(textContent.getText())) {
				oldContent = textContent.getText();
				task.update(task.inFileFormatHeader() + textContent.getText());
				fireTaskUpdated(CONTENT);
			}
		}
	}
	
	private class DateListener implements DocumentListener {
		@Override
		public void changedUpdate(DocumentEvent arg0) {}
		@Override
		public void insertUpdate(DocumentEvent arg0) { updateDate(arg0); }
		@Override
		public void removeUpdate(DocumentEvent arg0) {}
		
		private void updateDate(DocumentEvent arg0) {
			if (JdotxtDateField.isValidDate(textDate.getText())) {
				if (!oldDate.equals(textDate.getText())) {
					oldDate = textDate.getText();
					
					String rawText;
					if (oldDate.startsWith("-")) rawText = task.inFileFormatHeaderNoDate() + textContent.getText();
					else rawText = task.inFileFormatHeaderNoDate() + oldDate + " " + textContent.getText();
					task.update(rawText);
					fireTaskUpdated(DATE);
				}
			}
		}
	}
	
	private class DoneListener extends AbstractAction {
		@Override
		public void actionPerformed(ActionEvent e) {
			if (checkDone.isSelected()) {
				task.markComplete(new Date());
				markComplete();
			} else {
				task.markIncomplete();
				markIncomplete();
			}
			fireTaskUpdated(COMPLETED);
		}
	}
	
	private class DeleteListener extends AbstractAction {
		public void actionPerformed(ActionEvent e) {
			fireTaskDeleted();
		}
	}
	
	private class EnterAction extends AbstractAction {
		private short field;
		public EnterAction(short field){
			this.field = field;
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			fireEnterPressed(field);
		}
	}
}
