package com.chschmid.jdotxt.gui;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

import com.todotxt.todotxttouch.util.Util;

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
		label.setText(text);
	}
	
	public void setText(String text) {
		label.setText(text);
	}
	
	private void initGUI() {
		ImageIcon border  = Util.createImageIcon("/res/drawable/statusbar-border.png");
		this.setBorder(BorderFactory.createMatteBorder(3, 0, 0, 0, border));
		this.setBackground(JdotxtGUI.COLOR_GRAY_PANEL);
		this.setOpaque(true);
		
		label = new JLabel();
		label.setFont(JdotxtGUI.fontR);
		label.setBackground(JdotxtGUI.COLOR_GRAY_PANEL);
		label.setBorder(BorderFactory.createEmptyBorder(2, 4, 4, 4));
		
		this.add(label);
	}
}
