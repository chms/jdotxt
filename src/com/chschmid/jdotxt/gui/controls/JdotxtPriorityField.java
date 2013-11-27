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
import java.awt.KeyboardFocusManager;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.chschmid.jdotxt.gui.JdotxtGUI;

@SuppressWarnings("serial")
public class JdotxtPriorityField extends JTextField {
	private char DEFAULT_PRIORITY = '-';
	private char priority;
	
	private Color foreground;
	private DocumentListener listener;
	
	private boolean focusNext     = false;
	private boolean focusPrevious = false;
	private boolean enabled       = true;
	
	public JdotxtPriorityField(char priority) {
		super();
		initPriorityField(priority);
		
	}
	
	public JdotxtPriorityField() {
		super();
		initPriorityField(DEFAULT_PRIORITY);
	}
	
	private void initPriorityField(char priority) {
		foreground = getForeground();
		setPriority(priority);
		this.addFocusListener(new JdotxtPriorityFieldFocusListener());
		this.addKeyListener(new JdotxtPriorityFieldKeyListener());
		this.getDocument().addDocumentListener(new JdotxtDateDocumentListener());
	}
	
	private class JdotxtPriorityFieldFocusListener implements FocusListener {
		@Override
		public void focusGained(FocusEvent arg0) {
			selectPriority();
		}

		@Override
		public void focusLost(FocusEvent arg0) {
			setSelectionStart(0);
			setSelectionEnd(0);
		}
	}
	
	private class JdotxtPriorityFieldKeyListener implements KeyListener {
		@Override
		public void keyPressed(KeyEvent event) {
			
			int kc = event.getKeyCode();
			if (kc == KeyEvent.VK_RIGHT && focusNext) KeyboardFocusManager.getCurrentKeyboardFocusManager().focusNextComponent();
			if (kc == KeyEvent.VK_LEFT && focusPrevious) KeyboardFocusManager.getCurrentKeyboardFocusManager().focusPreviousComponent();
			event.consume();
		}

		@Override
		public void keyReleased(KeyEvent event) {
			event.consume();
		}

		@Override
		public void keyTyped(KeyEvent event) {
			event.consume();
			if (!enabled) return;
			Character c = event.getKeyChar();
			if (c == 127 || c == 8 || c == 32) c = '-';
			c = Character.toUpperCase(c);
			if ((c >= 'A' && c <= 'Z') || c == '-') selectPriority(c);
		}
	}
	
	private class JdotxtDateDocumentListener implements DocumentListener {
		@Override
		public void insertUpdate(DocumentEvent e) { if (isValidPriority(priority) && listener != null) listener.insertUpdate(e); }
		@Override
		public void removeUpdate(DocumentEvent e) { if (isValidPriority(priority) && listener != null) listener.removeUpdate(e); }
		@Override
		public void changedUpdate(DocumentEvent e) { if (isValidPriority(priority) && listener != null) listener.changedUpdate(e); }
	}
	
	private void selectPriority(char priority) {
		setPriority(priority);
		selectPriority();
	}
	
	private void selectPriority() {
		this.requestFocus();
		setSelectionStart(1);
		setSelectionEnd(2);
	}
	
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
		if (!enabled) setPriority(DEFAULT_PRIORITY);
	}
	
	public void setPriority(char priority) {
		priority = Character.toUpperCase(priority);
		
		if (isValidPriority(priority)) this.priority = priority;
		else this.priority = DEFAULT_PRIORITY;
		
		if (isValidPriority(priority) && priority != DEFAULT_PRIORITY) {
			super.setForeground(foreground);
			setText("(" + priority + ')');
		} else {
			setText("(" + DEFAULT_PRIORITY + ')');
			super.setForeground(JdotxtGUI.COLOR_GRAY_PANEL);
		}
	}
	
	public char getPriority() { return priority; }
	
	public void setFocusNext(boolean focusNext) { this.focusNext = focusNext; }
	public void setFocusPrevious(boolean focusPrevious) { this.focusPrevious = focusPrevious; }
	
	public void setForeground(Color fg) {
		foreground = fg;
		super.setForeground(foreground);
	}
	
	public void setPriorityListener(DocumentListener listener) { this.listener = listener; }
	
	public boolean isValidPriority(char priority){
		boolean isValid = true;
		isValid = (priority == '-');
		priority = Character.toUpperCase(priority);
		isValid = isValid || (priority >= 'A' && priority <= 'Z');
		return isValid;
	}
	
}