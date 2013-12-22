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
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.PlainDocument;

import com.chschmid.jdotxt.gui.JdotxtGUI;

@SuppressWarnings("serial")
public class JdotxtDateField extends JTextField {
	public final static String DEFAULT_DATE_STRING = "----------";
	public final static String NAV_PREV_ACTION = "NAV_PREV_ACTION";
	public final static String NAV_NEXT_ACTION = "NAV_NEXT_ACTION";
	private Color foreground;

	private boolean focusNext     = false;
	private boolean focusPrevious = false;
	
	public JdotxtDateField(String date) {
		initDateField(date);
	}
	
	public JdotxtDateField() {
		initDateField(DEFAULT_DATE_STRING);
	}
	
	private void initDateField(String date) {
		setDocument(new DateDocument());
		setDate(date);
		foreground = getForeground();
		this.addFocusListener(new JdotxtDateFieldFocusListener());
		this.getDocument().addDocumentListener(new JdotxtDateDocumentListener());
		setCaretPosition(0);
		
		InputMap im = this.getInputMap(JComponent.WHEN_FOCUSED);
        ActionMap am = this.getActionMap();
        im.put(KeyStroke.getKeyStroke("LEFT"), NAV_PREV_ACTION);
        im.put(KeyStroke.getKeyStroke("RIGHT"), NAV_NEXT_ACTION);
        am.put(NAV_PREV_ACTION, new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (focusPrevious && getCaretPosition() == 0) KeyboardFocusManager.getCurrentKeyboardFocusManager().focusPreviousComponent();
				((AbstractAction) getActionMap().get(DefaultEditorKit.backwardAction)).actionPerformed(e);
			}
		});
        am.put(NAV_NEXT_ACTION, new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (focusNext && getCaretPosition() == getText().length()) KeyboardFocusManager.getCurrentKeyboardFocusManager().focusNextComponent();
				((AbstractAction) getActionMap().get(DefaultEditorKit.forwardAction)).actionPerformed(e);
			}
		});
	}
	
	private class JdotxtDateFieldFocusListener implements FocusListener {
		@Override
		public void focusGained(FocusEvent arg0) { }
		@Override
		public void focusLost(FocusEvent arg0) {
			if (!isValidDate(getText())) setDate(DEFAULT_DATE_STRING);
			setCaretPosition(0);
		}
	}
	
	private class JdotxtDateDocumentListener implements DocumentListener {
		@Override
		public void insertUpdate(DocumentEvent e) {
			setColors();
		}
		@Override
		public void removeUpdate(DocumentEvent e) {
			setColors();
		}
		@Override
		public void changedUpdate(DocumentEvent e) {
			setColors();
		}
	}
	
	public void setDate(String date) {
		String newDate = DEFAULT_DATE_STRING;
		if (isValidDate(date)) newDate = date;
		setText(newDate);
	}
	
	private void setColors() {
		if (isValidEditingDate(getText()) && !getText().equals(DEFAULT_DATE_STRING)) {
			super.setForeground(foreground);
		} else {
			super.setForeground(JdotxtGUI.COLOR_GRAY_PANEL);
		}
	}
	
	public void setFocusNext(boolean focusNext) { this.focusNext = focusNext; }
	public void setFocusPrevious(boolean focusPrevious) { this.focusPrevious = focusPrevious; }
	
	public void setForeground(Color fg) {
		foreground = fg;
		super.setForeground(foreground);
	}
	
	//public void setDateListener(DocumentListener listener) { this.listener = listener; }
	
	public static boolean isValidDate(String date){
		boolean isValid;
		if (date.length() != 10) return false;
		isValid = Character.isDigit(date.charAt(0));
		isValid = isValid && Character.isDigit(date.charAt(1));
		isValid = isValid && Character.isDigit(date.charAt(2));
		isValid = isValid && Character.isDigit(date.charAt(3));
		isValid = isValid && (date.charAt(4) == '-');
		isValid = isValid && Character.isDigit(date.charAt(5));
		isValid = isValid && Character.isDigit(date.charAt(6));
		isValid = isValid && (date.charAt(7) == '-');
		isValid = isValid && Character.isDigit(date.charAt(8));
		isValid = isValid && Character.isDigit(date.charAt(9));
		isValid = isValid || date.equals(DEFAULT_DATE_STRING);
		return isValid;
	}
	
	private static boolean isValidEditingDate(String date){
		boolean isValid;
		if (date.length() != 10) return false;
		isValid = Character.isDigit(date.charAt(0)) || date.charAt(0) == '-';
		isValid = isValid && (Character.isDigit(date.charAt(1)) || date.charAt(1) == '-');
		isValid = isValid && (Character.isDigit(date.charAt(2)) || date.charAt(2) == '-');
		isValid = isValid && (Character.isDigit(date.charAt(3)) || date.charAt(3) == '-');
		isValid = isValid && (date.charAt(4) == '-');
		isValid = isValid && (Character.isDigit(date.charAt(5)) || date.charAt(5) == '-');
		isValid = isValid && (Character.isDigit(date.charAt(6)) || date.charAt(6) == '-');
		isValid = isValid && (date.charAt(7) == '-');
		isValid = isValid && (Character.isDigit(date.charAt(8)) || date.charAt(8) == '-');
		isValid = isValid && (Character.isDigit(date.charAt(9)) || date.charAt(9) == '-');
		return isValid;
	}
	
	private class DateDocument extends PlainDocument {
	    @Override
	    public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
	    	if (str.equals(" ")) {
	    		insertString(offs, "-", a);
	    		return;
	    	}
	    	String newDate = replaceSubstring(this.getText(0, this.getLength()), str, offs);
	    	
	    	if (isValidEditingDate(newDate)) {
	    		super.remove(offs, Math.min(str.length(), getLength() - offs));
				super.insertString(offs, str, a);
				int endPos = offs + str.length();
				if (endPos == 4 || endPos == 7) setCaretPosition(endPos + 1);
			}
	    }
	    
	    public void remove(int offs, int len) throws BadLocationException {
	    	super.remove(offs, len);
	    	super.insertString(offs, DEFAULT_DATE_STRING.substring(0, len), null);
	    	if (offs == 5 || offs == 8) setCaretPosition(offs - 1);
	    	else setCaretPosition(offs);
	    }
	    
	    private String replaceSubstring(String original, String replace, int offs) {
	    	if (original.length() < offs) return "";
	    	String a = original.substring(0, offs);
	    	String b = original.substring(Math.min(original.length(), offs+replace.length()), original.length());
	    	return a + replace + b;
	    }
	}
}
