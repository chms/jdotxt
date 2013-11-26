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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

import com.chschmid.jdotxt.gui.JdotxtGUI;

@SuppressWarnings("serial")
public class JdotxtImageCheckBox extends JLabel {
		
		private MouseAdapter mouseListener;
		private boolean mouseIsOverButton;
		
		private ImageIcon unselected;
		private ImageIcon selected;
		
		private boolean isSelected;
		
		public JdotxtImageCheckBox(ImageIcon unselected, ImageIcon selected) {
			super();
			this.setFocusable(false);
			this.setBorder(null);
			this.setIcon(unselected);
			
			this.unselected = unselected;
			this.selected = selected;
			
			isSelected = false;
			
			mouseListener = new MouseAdapter() {
	            @Override
	            public void mouseEntered(java.awt.event.MouseEvent evt) {
	            	setButtonBackground(JdotxtGUI.COLOR_HOVER);
	            	mouseIsOverButton = true;
	            }

	            @Override
	            public void mouseExited(java.awt.event.MouseEvent evt) {
	            	setButtonBackground(null);
	            	mouseIsOverButton = false;
	            }
	            
	            @Override
	            public void mousePressed(java.awt.event.MouseEvent evt) {
	            	setButtonBackground(JdotxtGUI.COLOR_PRESSED);
	            }
	            
	            @Override
	            public void mouseReleased(java.awt.event.MouseEvent evt) {
	            	if (mouseIsOverButton && isEnabled()) {
	            		setButtonBackground(JdotxtGUI.COLOR_HOVER);
	            		setSelected(!isSelected);
	            		fireActionPerformed(new ActionEvent(JdotxtImageCheckBox.this, ActionEvent.ACTION_PERFORMED, "Click"));
	            	}
	            }
	        };
	        addMouseListener(mouseListener);
		}
		
		public void setEnabled(boolean enabled) {
			super.setEnabled(enabled);
			if (mouseIsOverButton) setButtonBackground(JdotxtGUI.COLOR_HOVER);
		}
		
		public boolean isSelected() { return isSelected; }
		public void setSelected(boolean isSelected) {
			this.isSelected = isSelected;
			if (isSelected) setIcon(selected);
			else setIcon(unselected);
		}
		
		private void setButtonBackground(Color color) {
			setBackground(color);
			if (color == null || !isEnabled()) setOpaque(false);
			else setOpaque(true);
		}
		
	    public void addActionListener(ActionListener l) {
	        listenerList.add(ActionListener.class, l);
	    }

	    public void removeActionListener(ActionListener l) {
	    	listenerList.remove(ActionListener.class, l);
	    }
	    
	    public ActionListener[] getActionListeners() {
	        return listenerList.getListeners(ActionListener.class);
	    }
	    
	    protected void fireActionPerformed(ActionEvent event) {
	    	Object[] listeners = listenerList.getListeners(ActionListener.class);
	        
	        for (int i = 0; i < listeners.length; i++) {
	            ((ActionListener)listeners[i]).actionPerformed(event);
	        }
	    }
    }