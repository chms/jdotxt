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

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;

import com.chschmid.jdotxt.gui.JdotxtGUI;

@SuppressWarnings("serial")
public class JdotxtStatusBar extends Box {
	
	private JLabel label;

	public JdotxtStatusBar() {
		super(BoxLayout.X_AXIS);
		initGUI();
	}
	
	public JdotxtStatusBar(String text) {
		super(BoxLayout.X_AXIS);
		initGUI();
		setText(text);
	}
	
	private void initGUI() {
		//ImageIcon border  = Util.createImageIcon("/res/drawable/statusbar-border.png");
		//this.setBorder(BorderFactory.createMatteBorder(3, 0, 0, 0, border));
		this.setBackground(JdotxtGUI.COLOR_GRAY_PANEL);
		this.setOpaque(true);
		
		label = new JLabel();
		label.setFont(JdotxtGUI.fontR);
		label.setBackground(JdotxtGUI.COLOR_GRAY_PANEL);
		label.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
		
		this.add(label);
	}
	
	public void setText(String text) { label.setText(text); }
	public String getText() { return label.getText(); }
}
