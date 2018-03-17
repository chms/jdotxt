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

import com.chschmid.jdotxt.Jdotxt;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

@SuppressWarnings("serial")
public class JdotxtContentField extends JTextField{

	private AutocompletionModes mode = AutocompletionModes.PLAIN;

	private List<Character> separators = Arrays.asList('-', ':', '/', '_', '.');

	private AutocompletionList list;
	private Action a = null;

	StringBuilder filter = new StringBuilder();

	public JdotxtContentField(String text) {
		super(text);
		addKeyListener(new KeyAdapter(){
			@Override
			public void keyPressed(KeyEvent keyEvent) {
				if (mode != AutocompletionModes.PLAIN && (keyEvent.getKeyCode() == KeyEvent.VK_DOWN || keyEvent.getKeyCode() == KeyEvent.VK_UP))
					keyEvent.consume();
			}

			@Override
			public void keyReleased(KeyEvent keyEvent) {
				processKey(keyEvent);
			}
		});
		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent mouseEvent) {
				hideAutocomplete();
				if (getCaretPosition() != 0) {
					char c = getText().charAt(getCaretPosition() - 1);
					if (!tryToAutocomplete(c)) {
						//if previous symbol is not '@' or '+' we should see if previous word starts with such symbol
						tryToMoveBack(c);
					}
				}
			}
		});
		addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent focusEvent) {
				if (list == null || !list.hasFocus())
					hideAutocomplete();
			}
		});
	}

	private void processKey (KeyEvent keyEvent) {
		if (keyEvent.getKeyCode() == KeyEvent.VK_SHIFT || keyEvent.getKeyCode() == KeyEvent.VK_DELETE) {
			return;
		}
		if (getCaretPosition() == 0) {
			hideAutocomplete();
			return;
		}
		char c = getText().charAt(getCaretPosition() - 1);
		if (mode == AutocompletionModes.PLAIN) {
			//we try to enter autocomplete
			if (!tryToAutocomplete(c) && isMovingBack(keyEvent)) {
				//if previous symbol is not '@' or '+' but we deleted previous symbol or pressed left arrow
				tryToMoveBack(c);
			}
		} else {
			//we are in autocomplete
			if (keyEvent.getKeyCode() == KeyEvent.VK_ENTER) {
				// we chosen autocomplete
				String t = getText();
				setText(t.substring(0, getCaretPosition() - filter.length()) + list.getSelection() + " " + t.substring(getCaretPosition()));
				hideAutocomplete();
			} else if (isMovingBack(keyEvent)) {
				//we pressed backspace or arrow left
				int l = filter.length();
				if (l == 0) {
					hideAutocomplete();
				} else {
					filter.setLength(l - 1);
					filterAutocomplete();
				}
			} else if (keyEvent.getKeyCode() == KeyEvent.VK_DOWN) {
				list.nextSelection();
			} else if (keyEvent.getKeyCode() == KeyEvent.VK_UP) {
				list.previousSelection();
			} else if (isValid(c)) {
				//new character added to autocomplete list
				filter.append(c);
				filterAutocomplete();
			} else {
				hideAutocomplete();
			}
		}
	}

	private void tryToMoveBack(char start) {
		char c = start;
		int i = 0;
		while (mode == AutocompletionModes.PLAIN && isValid(c) && getCaretPosition() - 3 - i >= 0) {
			i++;
			c = getText().charAt(getCaretPosition() - 1 - i);
			tryToAutocomplete(c, i);
		}
		if (mode != AutocompletionModes.PLAIN) {
			filter.append(getText(), getCaretPosition() - i, getCaretPosition());
			filterAutocomplete();
		}
	}

	private boolean tryToAutocomplete(char c) {
		return tryToAutocomplete(c, 0);
	}

	private boolean tryToAutocomplete(char c, int shiftLeft) {
		boolean possibleStart = getCaretPosition() == 1 || Character.isWhitespace(getText().charAt(getCaretPosition() - 2 - shiftLeft));
		if (c == '+' && possibleStart) {
			mode = AutocompletionModes.PROJECT;
			showAutocomplete();
			return true;
		} else if (c == '@' && possibleStart) {
			mode = AutocompletionModes.CONTEXT;
			showAutocomplete();
			return true;
		}
		return false;
	}

	private boolean isValid(char c) {
		return Character.isLetterOrDigit(c) || separators.contains(c);
	}

	private boolean isMovingBack(KeyEvent keyEvent) {
		return keyEvent.getKeyCode() == KeyEvent.VK_LEFT || keyEvent.getKeyCode() == KeyEvent.VK_BACK_SPACE;
	}

	private void showAutocomplete() {
		list = new AutocompletionList();
		list.setAutocomplete(getFullListOfCompletions());
		Point p = getLocationOnScreen();
		String before = getText().substring(0, getCaretPosition());
		list.setLocation((int) p.getX() + getFontMetrics(getFont()).stringWidth(before), (int) p.getY() + getHeight());
		list.setAlwaysOnTop(true);
		list.setFocusableWindowState(false);
		list.setFocusable(false);
		list.setVisible(true);
		requestFocusInWindow(true);
		a = JdotxtContentField.this.getActionMap().get("ENTER");
		JdotxtContentField.this.getActionMap().remove("ENTER");
	}

	private void hideAutocomplete() {
		if (list == null)
			return;
		mode = AutocompletionModes.PLAIN;
		list.setVisible(false);
		filter.setLength(0);
		list.dispose();
		list = null;
		if (a != null)
			JdotxtContentField.this.getActionMap().put("ENTER", a);
	}

	private void filterAutocomplete() {
		String f = filter.toString().toLowerCase();
		List<String> filtered = new ArrayList<>();
		for (String s : getFullListOfCompletions()) {
			if (s.toLowerCase().contains(f) && ! f.equals(s)) {
				filtered.add(s);
			}
		}
		if (filtered.isEmpty())
			hideAutocomplete();
		else
			list.setAutocomplete(filtered);
	}

	private List<String> getFullListOfCompletions() {
		switch (mode) {
			case PROJECT: return Jdotxt.taskBag.getProjects(false);
			case CONTEXT: return Jdotxt.taskBag.getContexts(false);
		}
		return Collections.emptyList();
	}

	enum AutocompletionModes {
		PLAIN, PROJECT, CONTEXT
	}

	class AutocompletionList extends JDialog {
		Vector<String> autocomplete = new Vector<>();
		JList<String> list;
		int selectionIndex = 0;

		public AutocompletionList() {
			super();
			initGUI();
		}

		private void initGUI() {
			setUndecorated(true);
			list = new JList<>(autocomplete);
			list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			list.setLayoutOrientation(JList.VERTICAL);
			list.setVisibleRowCount(-1);
			list.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
			add(list, BorderLayout.CENTER);
		}

		void setAutocomplete(List<String> autocomplete) {
			this.autocomplete = new Vector<>(autocomplete);
			list.setListData(this.autocomplete);
			list.updateUI();
			list.repaint();
			pack();
			selectionIndex = 0;
			list.setSelectedIndex(selectionIndex);
		}

		void nextSelection() {
			selectionIndex++;
			list.setSelectedIndex(selectionIndex);
		}

		void previousSelection() {
			selectionIndex--;
			list.setSelectedIndex(selectionIndex);
		}

		String getSelection() {
			return autocomplete.get(selectionIndex);
		}
	}
}
