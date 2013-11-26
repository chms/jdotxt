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
public class JdotxtDateField extends JTextField {
	private String DEFAULT_DATE_STRING = "----------";
	private String date;
	private Color foreground;
	private DocumentListener listener;
	
	public JdotxtDateField(String date) {
		super();
		initDateField(date);
	}
	
	public JdotxtDateField() {
		super();
		initDateField(DEFAULT_DATE_STRING);
	}
	
	private void initDateField(String date) {
		setDate(date);
		foreground = getForeground();
		this.addFocusListener(new JdotxtDateFieldFocusListener());
		this.addKeyListener(new JdotxtDateFieldKeyListener());
		this.getDocument().addDocumentListener(new JdotxtDateDocumentListener());
		setCaretPosition(0);
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
	
	private class JdotxtDateFieldKeyListener implements KeyListener {
		@Override
		public void keyPressed(KeyEvent event) {
			JTextField text = (JTextField)event.getSource();
			StringBuilder tempDate = new StringBuilder(text.getText());
			int cc = event.getKeyCode();
			
			if (getCaretPosition() == 0 && cc == KeyEvent.VK_LEFT) {
				KeyboardFocusManager.getCurrentKeyboardFocusManager().focusPreviousComponent();
				return;
			}
			if (cc == KeyEvent.VK_RIGHT || cc == KeyEvent.VK_KP_RIGHT || cc == KeyEvent.VK_LEFT || cc == KeyEvent.VK_KP_RIGHT) return;
			if (cc == KeyEvent.VK_UP || cc == KeyEvent.VK_DOWN || cc == KeyEvent.VK_HOME || cc == KeyEvent.VK_END) return;
			
			int pos = text.getCaretPosition();
			
			if (cc == KeyEvent.VK_SPACE) {
				if (pos >= 10) event.consume();
				else {
					if (pos == 4 || pos == 7) pos++;
					tempDate.setCharAt(pos, '-');
					setDate(tempDate.toString());
					pos++;
					if (pos == 4 || pos == 7) pos++;
					text.setCaretPosition(pos);
				}
			}
			
			if (cc == KeyEvent.VK_BACK_SPACE) {
				if (pos == 0) event.consume();
				else {
					if (pos == 5 || pos == 8) pos--;
					pos--;
					tempDate.setCharAt(pos, '-');
					setDate(tempDate.toString());
					if (pos == 5 || pos == 8) pos--;
					text.setCaretPosition(pos);
				}
			}
			
			event.consume();
		}

		@Override
		public void keyReleased(KeyEvent event) { }

		@Override
		public void keyTyped(KeyEvent event) {
			JTextField text = (JTextField)event.getSource();
			StringBuilder tempDate = new StringBuilder(text.getText());
			int pos = text.getCaretPosition();
			Character c = event.getKeyChar();
			if (isNumeric(c)) {
				if (pos >= 10) event.consume();
				else {
					if (pos == 4 || pos == 7) pos++;
					tempDate.setCharAt(pos, c);
					setDate(tempDate.toString());
					pos++;
					if (pos == 4 || pos == 7) pos++;
					text.setCaretPosition(pos);
				}
			}
			event.consume();
		}
	}
	
	private class JdotxtDateDocumentListener implements DocumentListener {
		@Override
		public void insertUpdate(DocumentEvent e) { if (isValidDate(date) && listener != null) listener.insertUpdate(e); }
		@Override
		public void removeUpdate(DocumentEvent e) { if (isValidDate(date) && listener != null) listener.removeUpdate(e); }
		@Override
		public void changedUpdate(DocumentEvent e) { if (isValidDate(date) && listener != null) listener.changedUpdate(e); }
	}
	
	public void setDate(String date) {
		if (isValidDate(date)) this.date = date;
		else this.date = DEFAULT_DATE_STRING;
		
		if (isValidEditingDate(date) && !date.equals(DEFAULT_DATE_STRING)) {
			super.setForeground(foreground);
			setText(date);
		} else {
			setText(DEFAULT_DATE_STRING);
			super.setForeground(JdotxtGUI.COLOR_GRAY_PANEL);
		}
	}
	
	public String getDate() { return date; }
	
	public void setForeground(Color fg) {
		foreground = fg;
		super.setForeground(foreground);
	}
	
	public void setDateListener(DocumentListener listener) { this.listener = listener; }
	
	private boolean isNumeric(char c) { return ((c >= '0') && (c <= '9')); }
	
	public boolean isValidDate(String date){
		boolean isValid;
		if (date.length() != 10) return false;
		isValid = isNumeric(date.charAt(0));
		isValid = isValid && isNumeric(date.charAt(1));
		isValid = isValid && isNumeric(date.charAt(2));
		isValid = isValid && isNumeric(date.charAt(3));
		isValid = isValid && (date.charAt(4) == '-');
		isValid = isValid && isNumeric(date.charAt(5));
		isValid = isValid && isNumeric(date.charAt(6));
		isValid = isValid && (date.charAt(7) == '-');
		isValid = isValid && isNumeric(date.charAt(8));
		isValid = isValid && isNumeric(date.charAt(9));
		isValid = isValid || date.equals(DEFAULT_DATE_STRING);
		return isValid;
	}
	
	private boolean isValidEditingDate(String date){
		boolean isValid;
		if (date.length() != 10) return false;
		isValid = isNumeric(date.charAt(0)) || date.charAt(0) == '-';
		isValid = isValid && (isNumeric(date.charAt(1)) || date.charAt(1) == '-');
		isValid = isValid && (isNumeric(date.charAt(2)) || date.charAt(2) == '-');
		isValid = isValid && (isNumeric(date.charAt(3)) || date.charAt(3) == '-');
		isValid = isValid && (date.charAt(4) == '-');
		isValid = isValid && (isNumeric(date.charAt(5)) || date.charAt(5) == '-');
		isValid = isValid && (isNumeric(date.charAt(6)) || date.charAt(6) == '-');
		isValid = isValid && (date.charAt(7) == '-');
		isValid = isValid && (isNumeric(date.charAt(8)) || date.charAt(8) == '-');
		isValid = isValid && (isNumeric(date.charAt(9)) || date.charAt(9) == '-');
		return isValid;
	}
}