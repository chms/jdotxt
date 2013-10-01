package com.chschmid.jdotxt.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

@SuppressWarnings("serial")
public class ImageButton extends JLabel {
		
		private boolean enabled = true;
		
		private MouseAdapter mouseListener;
		private ActionListener actionListener;
		private boolean isOn;
		
		public ImageButton(ImageIcon icon) {
			super();
			this.setFocusable(false);
			this.setBorder(null);
			this.setIcon(icon);
			
			mouseListener = new MouseAdapter() {
				
	            @Override
	            public void mouseEntered(java.awt.event.MouseEvent evt) {
	            	if (enabled) {
	            		setBackground(JdotxtGUI.COLOR_HOVER);
	            		setOpaque(true);
	            	}
	            	isOn = true;
	            }

	            @Override
	            public void mouseExited(java.awt.event.MouseEvent evt) {
	            	setBackground(null);
	            	setOpaque(false);
	            	isOn = false;
	            }
	            
	            @Override
	            public void mousePressed(java.awt.event.MouseEvent evt) {
	            	if (enabled) {
	            		setBackground(JdotxtGUI.COLOR_PRESSED);
	            		setOpaque(true);
	            	}
	            }
	            
	            @Override
	            public void mouseReleased(java.awt.event.MouseEvent evt) {
	            	if (isOn && enabled) {
	            		setBackground(JdotxtGUI.COLOR_HOVER);
	            		setOpaque(true);
	            		if (actionListener != null) actionListener.actionPerformed(new ActionEvent(evt.getSource(), evt.getID(), ""));
	            	}
	            }
	        };
	        addMouseListener(mouseListener);
		}
		
		public void setActionListener (ActionListener actionListener) { this.actionListener = actionListener; }
		
		public void setEnabled(boolean enabled) {
			if (isOn) setBackground(JdotxtGUI.COLOR_HOVER);
			setOpaque(enabled && isOn);
			super.setEnabled(enabled);
			this.enabled = enabled;
		}
		
		public boolean isEnabled() { return enabled; }
    }