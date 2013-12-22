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
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

import com.chschmid.jdotxt.gui.JdotxtGUI;

@SuppressWarnings("serial")
public class JdotxtPriorityField extends JTextField {
	public final static char DEFAULT_PRIORITY  = '-';
	public final static String NAV_PREV_ACTION = "NAV_PREV_ACTION";
	public final static String NAV_NEXT_ACTION = "NAV_NEXT_ACTION";
	
	private Color foreground;
	private DocumentListener listener;
	
	private boolean focusNext     = false;
	private boolean focusPrevious = false;
	private boolean focused       = false;
	private boolean enabled       = true;
	
	public JdotxtPriorityField(char priority) {
		initPriorityField(priority);
	}
	
	public JdotxtPriorityField() {
		initPriorityField(DEFAULT_PRIORITY);
	}
	
	private void initPriorityField(char priority) {
		foreground = getForeground();
		setDocument(new PriorityDocument());
		setPriority(priority);
		this.addFocusListener(new JdotxtPriorityFieldFocusListener());
		this.getDocument().addDocumentListener(new JdotxtPriorityDocumentListener());
		
		InputMap im = this.getInputMap(JComponent.WHEN_FOCUSED);
        ActionMap am = this.getActionMap();
        im.put(KeyStroke.getKeyStroke("LEFT"), NAV_PREV_ACTION);
        im.put(KeyStroke.getKeyStroke("RIGHT"), NAV_NEXT_ACTION);
        am.put(NAV_PREV_ACTION, new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) { if (focusPrevious) KeyboardFocusManager.getCurrentKeyboardFocusManager().focusPreviousComponent(); }
		});
        am.put(NAV_NEXT_ACTION, new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) { if (focusNext) KeyboardFocusManager.getCurrentKeyboardFocusManager().focusNextComponent(); }
		});
	}
	
	private class JdotxtPriorityFieldFocusListener implements FocusListener {
		@Override
		public void focusGained(FocusEvent arg0) {
			focused = true;
			selectPriority();
		}

		@Override
		public void focusLost(FocusEvent arg0) {
			focused = false;
			selectPriority();
		}
	}
	
	private class JdotxtPriorityDocumentListener implements DocumentListener {
		@Override
		public void insertUpdate(DocumentEvent e) {
			if (isValidPriority(getText()) && listener != null) listener.insertUpdate(e);
		}
		@Override
		public void removeUpdate(DocumentEvent e) {
			if (isValidPriority(getText()) && listener != null) listener.removeUpdate(e);
			selectPriority();
		}
		@Override
		public void changedUpdate(DocumentEvent e) {
			if (isValidPriority(getText()) && listener != null) listener.changedUpdate(e);
			selectPriority();
		}
	}
	
	private void selectPriority() {
		if (focused) {
			setSelectionStart(1);
			setSelectionEnd(2);
		} else {
			setSelectionStart(0);
			setSelectionEnd(0);
		}
	}
	
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
		if (!enabled) setPriority(DEFAULT_PRIORITY);
	}
	
	public void setPriority(char priority) {
		priority = Character.toUpperCase(priority);
		if (!isValidPriority(priority)) priority = DEFAULT_PRIORITY;
		setText("(" + priority + ')');
		setColor();
	}
	
	private void setColor() {
		if (getText().length() < 2) return;
		if (getText().charAt(1) == '-') super.setForeground(JdotxtGUI.COLOR_GRAY_PANEL);
		else super.setForeground(foreground);
	}
	
	public void setFocusNext(boolean focusNext) { this.focusNext = focusNext; }
	public void setFocusPrevious(boolean focusPrevious) { this.focusPrevious = focusPrevious; }
	
	public void setForeground(Color fg) {
		foreground = fg;
		super.setForeground(foreground);
	}
	
	public void setPriorityListener(DocumentListener listener) { this.listener = listener; }
	
	public static boolean isValidPriority(char priority){
		boolean isValid = true;
		isValid = (priority == '-');
		priority = Character.toUpperCase(priority);
		isValid = isValid || (priority >= 'A' && priority <= 'Z');
		return isValid;
	}
	
	public static boolean isValidPriority(String priority){
		boolean isValid = true;
		isValid = (priority.length() == 3);
		isValid = isValid && priority.charAt(0) == '(';
		isValid = isValid && priority.charAt(2) == '(';
		isValid = isValid && priority.charAt(0) >= 'A' && priority.charAt(0) <= 'Z';
		return isValid;
	}
	
	private class PriorityDocument extends PlainDocument {
		boolean replace = false;
		
	    @Override
	    public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
	    	if (str.length() > 1) str = str.substring(1);
	    	if (!enabled) str = "(-)";
	    	else {
	    		char priority = Character.toUpperCase(str.charAt(0));
	    		if (!isValidPriority(priority)) priority = '-';
	    		str = "(" + priority + ")";
	    	}
	    	super.remove(0, getDocument().getLength());
	    	super.insertString(0, str, a);
	    	
	    	setColor();
	    	selectPriority();
	    }
	    
	    @Override
	    public void remove(int offs, int len) throws BadLocationException {
	    	if (!replace) {
	    		String str = "(-)";
		    	super.remove(0, getDocument().getLength());
		    	super.insertString(0, str, null);
		    	setColor();
		    	selectPriority();
	    	}
	    }
	    
	    @Override
	    public void replace(int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
	    	replace = true;
	    	super.replace(offset, length, text, attrs);
	    	replace = false;
	    }
	}
}