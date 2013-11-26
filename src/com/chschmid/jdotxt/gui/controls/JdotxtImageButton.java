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
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

import com.chschmid.jdotxt.gui.JdotxtGUI;

@SuppressWarnings("serial")
public class JdotxtImageButton extends JLabel {
	private boolean mouseIsOverButton;
	private boolean mouseIsDown;
	private boolean focused;
	
	public JdotxtImageButton() {
		super();
		initGUI();
	}
	
	public JdotxtImageButton(ImageIcon icon) {
		super();
		initGUI();
		this.setIcon(icon);
	}
	
	private void initGUI() {
		this.setFocusable(false);
		this.setBorder(null);
		
		MouseAdapter mouseListener = new MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
            	mouseIsOverButton = true;
            	if (mouseIsDown) setButtonBackground(JdotxtGUI.COLOR_PRESSED);
            	else setButtonBackground(JdotxtGUI.COLOR_HOVER);
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
            	mouseIsOverButton = false;
            	if (!focused) setButtonBackground(null);
            	else setButtonBackground(JdotxtGUI.COLOR_HOVER);
            }
            
            @Override
            public void mousePressed(java.awt.event.MouseEvent evt) {
            	if (SwingUtilities.isLeftMouseButton(evt)) {
            		mouseIsDown = true;
            		setButtonBackground(JdotxtGUI.COLOR_PRESSED);
            	}
            }
            
            @Override
            public void mouseReleased(java.awt.event.MouseEvent evt) {
            	mouseIsDown = false;
            	if (!isEnabled() || !SwingUtilities.isLeftMouseButton(evt)) return;
            	if (mouseIsOverButton) fireActionPerformed(new ActionEvent(JdotxtImageButton.this, ActionEvent.ACTION_PERFORMED, "Click"));
            	if (mouseIsOverButton || focused) setButtonBackground(JdotxtGUI.COLOR_HOVER);
            	else setButtonBackground(null);
            }
        };
        addMouseListener(mouseListener);
        
        FocusListener focusListener = new FocusListener() {
			@Override
			public void focusLost(FocusEvent arg0) {
				focused = false;
				if (!mouseIsOverButton) setButtonBackground(null);
			}
			
			@Override
			public void focusGained(FocusEvent arg0) {
				focused = true;
				setButtonBackground(JdotxtGUI.COLOR_HOVER);
			}
		};
		addFocusListener(focusListener);
	}
	
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		setButtonBackground(null);
		if (mouseIsOverButton || focused) setButtonBackground(JdotxtGUI.COLOR_HOVER);
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