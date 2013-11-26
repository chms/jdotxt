package com.chschmid.jdotxt.gui.elements;

import java.awt.Color;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JTextField;

import com.chschmid.jdotxt.gui.JdotxtGUI;

@SuppressWarnings("serial")
public class JdoDateField extends JTextField {
	private String DEFAULT_DATE_STRING = "----------";
	private String date;
	private Color foreground;
	
	public JdoDateField(String date) {
		super();
		foreground = getForeground();
		setDate(date);
		this.addFocusListener(new JdoDateFieldFocusListener());
		this.addKeyListener(new JdoDateFieldKeyListener());
	}
	
	private class JdoDateFieldFocusListener implements FocusListener {
		@Override
		public void focusGained(FocusEvent arg0) {
		}

		@Override
		public void focusLost(FocusEvent arg0) {
			setDate(getText());
			setCaretPosition(0);
		}
	}
	
	private class JdoDateFieldKeyListener implements KeyListener {
		@Override
		public void keyPressed(KeyEvent event) {
			JTextField text = (JTextField)event.getSource();
			StringBuilder tempDate = new StringBuilder(text.getText());
			int cc = event.getKeyCode();
			
			//if (textDate.getCaretPosition() == 0 && cc == KeyEvent.VK_LEFT) setFocusText(true);
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
			
			//processShortcuts(event);
			event.consume();
		}

		@Override
		public void keyReleased(KeyEvent event) {
		}

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
	
	private boolean isValidDate(String date){
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
		isValid = isValid && (date.charAt(0) != '-' || date.charAt(1) != '-' || date.charAt(2) != '-' || date.charAt(3) != '-' || date.charAt(5) != '-' || date.charAt(6) != '-' || date.charAt(7) != '-' || date.charAt(8) != '-');
		return isValid;
	}
	
	public String getDate() { return date; }
	
	public void setDate(String date) {
		if (isValidEditingDate(date)) {
			super.setForeground(foreground);
			if (isValidDate(date)) this.date = date;
		} else {
			super.setForeground(JdotxtGUI.COLOR_GRAY_PANEL);
			date = DEFAULT_DATE_STRING;
			this.date = date;
		}
		setText(date);
	}
	
	public void setForeground(Color fg) {
		foreground = fg;
		super.setForeground(foreground);
	}
	
	private void updateDate() {
		if (isValidDate(date)) {
			String rawText = task.inFileFormatHeaderNoDate() + date + " " + textContent.getText();
			task.update(rawText);
			if (tasklistener != null) tasklistener.onDateUpdate(task);
		}
		if (date == DEFAULT_DATE_STRING) {
			String rawText = task.inFileFormatHeaderNoDate() + textContent.getText();
			task.update(rawText);
			if (tasklistener != null) tasklistener.onDateUpdate(task);
		}
	}
	
	private boolean isNumeric(char c) { return ((c >= '0') && (c <= '9')); }
}