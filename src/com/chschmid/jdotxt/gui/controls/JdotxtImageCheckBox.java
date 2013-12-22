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

import java.awt.event.ActionEvent;

import javax.swing.ImageIcon;

@SuppressWarnings("serial")
public class JdotxtImageCheckBox extends JdotxtImageButton {
	private ImageIcon unselected;
	private ImageIcon selected;
		
	private boolean isSelected;
		
	public JdotxtImageCheckBox(ImageIcon unselected, ImageIcon selected) {
		super(unselected);
		isSelected = false;

		this.unselected = unselected;
		this.selected = selected;
	}
		
	public boolean isSelected() { return isSelected; }
	public void setSelected(boolean isSelected) {
		if (isSelected) setIcon(selected);
		else setIcon(unselected);
		
		if (this.isSelected == isSelected) return;
		else {
			this.isSelected = isSelected;
			ActionEvent event = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "Toggle");
			super.fireActionPerformed(event);
		}
	}
	
	protected void fireActionPerformed(ActionEvent event) {
		setSelected(!isSelected);
    }
}