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

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

@SuppressWarnings("serial")
public class JdotxtImageButton extends JLabel {
	private boolean mouseIsOverButton;
	private boolean mouseIsDown;
	private boolean keyIsPressed;
	private boolean focused;
	
	private Color cHover;
	private Color cPressed;
	private Color cBackground;
	
	public JdotxtImageButton() {
		initGUI();
	}
	
	public JdotxtImageButton(ImageIcon icon) {
		initGUI();
		this.setIcon(icon);
	}
	
	private void initGUI() {
		setFocusable(true);
		this.setBorder(null);
		
		MouseAdapter mouseListener = new MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
            	mouseIsOverButton = true;
            	setBackground();
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
            	mouseIsOverButton = false;
            	setBackground();
            }
            
            @Override
            public void mousePressed(java.awt.event.MouseEvent evt) {
            	if (SwingUtilities.isLeftMouseButton(evt)) {
            		mouseIsDown = true;
            		setBackground();
            	}
            }
            
            @Override
            public void mouseReleased(java.awt.event.MouseEvent evt) {
            	mouseIsDown = false;
            	if (!isEnabled() || !SwingUtilities.isLeftMouseButton(evt)) return;
            	if (mouseIsOverButton) fireActionPerformed(new ActionEvent(JdotxtImageButton.this, ActionEvent.ACTION_PERFORMED, "Click"));
            	setBackground();
            }
        };
        addMouseListener(mouseListener);
        
        FocusListener focusListener = new FocusListener() {
			@Override
			public void focusLost(FocusEvent arg0) {
				focused = false;
				setBackground();
			}
			
			@Override
			public void focusGained(FocusEvent arg0) {
				focused = true;
				setBackground();
			}
		};
		addFocusListener(focusListener);
		
		KeyListener keyListener = new KeyListener() {
			
			@Override
			public void keyTyped(KeyEvent e) {
			}
			
			@Override
			public void keyReleased(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_SPACE) {
					keyIsPressed = false;
					setBackground();
					fireActionPerformed(new ActionEvent(JdotxtImageButton.this, ActionEvent.ACTION_PERFORMED, "Click"));
				}
			}
			
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_SPACE) {
					keyIsPressed = true;
					setBackground();
				}
			}
		};
		addKeyListener(keyListener);
	}
	
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		setBackground();
	}
	
	public void setHoverColor(Color color) { cHover = color; setBackground(); }
	public void setPressedColor(Color color) { cPressed = color; setBackground(); }
	public void setBackgroundColor(Color color) { cBackground = color; setBackground(); }
	
	public Color getHoverColor() { return cHover; }
	public Color getPressedColor() { return cHover; }
	public Color getBackgroundColor() { return cBackground; }
	
    public void addActionListener(ActionListener l) {
        listenerList.add(ActionListener.class, l);
    }

    public void removeActionListener(ActionListener l) {
    	listenerList.remove(ActionListener.class, l);
    }
    
    public ActionListener[] getActionListeners() {
        return listenerList.getListeners(ActionListener.class);
    }
    
    private void setBackground() {
    	if (keyIsPressed || (mouseIsOverButton && mouseIsDown)) setBackground(cPressed);
    	else if (mouseIsOverButton || focused) setBackground(cHover);
    	else setBackground(cBackground);
    	if (isEnabled()) setOpaque(true);
    	else setOpaque(false);
	}
    
    protected void fireActionPerformed(ActionEvent event) {
    	Object[] listeners = listenerList.getListeners(ActionListener.class);
        
        for (int i = 0; i < listeners.length; i++) {
            ((ActionListener)listeners[i]).actionPerformed(event);
        }
    }
}