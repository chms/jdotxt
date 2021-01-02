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
import com.chschmid.jdotxt.gui.JdotxtGUI;
import com.todotxt.todotxttouch.util.Util;

import javax.swing.*;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

public class JdotxtToolbar extends Box{

	private JdotxtImageButton buttonSave;
	private JdotxtImageButton buttonReload;
	private JdotxtImageButton buttonArchive;
	private JdotxtImageButton buttonSettings;
	private JdotxtImageButton buttonSort;
	private JdotxtCombobox savedSortCombobox;
	private JdotxtCombobox fileLocationCombobox;
	private JdotxtToggleImageButton toggleHide;
	private JdotxtToggleImageButton toggleFuture;
	private JdotxtToggleImageButton togglePrepend;
	private JdotxtToggleImageButton toggleCopy;
	private SearchField textfieldSearch;
	
	private boolean enabled;
	
	private static final long serialVersionUID = 4375297894805216012L;
	
	public JdotxtToolbar() {
		super(BoxLayout.X_AXIS);
		initGUI();
		refreshSettingsToggles();
	}

	private void initGUI() {
		// Load images
		ImageIcon iconSave     = Util.createImageIcon("/res/drawable/save.png");
		ImageIcon iconReload   = Util.createImageIcon("/res/drawable/reload.png");
		ImageIcon iconArchive  = Util.createImageIcon("/res/drawable/archive.png");
		ImageIcon iconSettings = Util.createImageIcon("/res/drawable/settings.png");
		ImageIcon iconSort = Util.createImageIcon("/res/drawable/sort.png");
		ImageIcon iconCopy = Util.createImageIcon("/res/drawable/copy.png");
		ImageIcon iconHide = Util.createImageIcon("/res/drawable/hide.png");
		ImageIcon iconPrepend = Util.createImageIcon("/res/drawable/prepend.png");
		ImageIcon iconFuture = Util.createImageIcon("/res/drawable/future.png");
		//ImageIcon border       = Util.createImageIcon("/res/drawable/toolbar-border.png");
		
		// Style toolbar
		this.setBackground(JdotxtGUI.COLOR_GRAY_PANEL);
		//this.setBorder(BorderFactory.createMatteBorder(0, 0, 3, 0, border));
		this.setOpaque(true);
		
		// Create GUI elements
		buttonSave      = new JdotxtImageButton(iconSave);
		styleJdotxtImageButton(buttonSave, JdotxtGUI.lang.getWord("Save"));
		buttonReload    = new JdotxtImageButton(iconReload);
		styleJdotxtImageButton(buttonReload, JdotxtGUI.lang.getWord("Reload"));
		toggleCopy 		= new JdotxtToggleImageButton(iconCopy);
		styleJdotxtImageButton(toggleCopy, JdotxtGUI.lang.getWord("Copy_projects_contexts"));
		togglePrepend	= new JdotxtToggleImageButton(iconPrepend);
		styleJdotxtImageButton(togglePrepend, JdotxtGUI.lang.getWord("Prepend_projects_contexts"));
		toggleHide 		= new JdotxtToggleImageButton(iconHide);
		styleJdotxtImageButton(toggleHide, JdotxtGUI.lang.getWord("Show_hidden_tasks"));
		toggleFuture	= new JdotxtToggleImageButton(iconFuture);
		styleJdotxtImageButton(toggleFuture, JdotxtGUI.lang.getWord("Show_tasks_with_threshold"));
		buttonArchive   = new JdotxtImageButton(iconArchive);
		savedSortCombobox = new JdotxtCombobox("Select a predefined sort");
		fileLocationCombobox = new JdotxtCombobox("Select file location", "Open new location...");
		styleJdotxtImageButton(buttonArchive, JdotxtGUI.lang.getWord("Archive"));
		buttonSort 		= new JdotxtImageButton(iconSort);
		styleJdotxtImageButton(buttonSort, JdotxtGUI.lang.getWord("Save"));

		textfieldSearch = new SearchField(JdotxtGUI.lang.getWord("Search..."));
		buttonSettings  = new JdotxtImageButton(iconSettings);
		styleJdotxtImageButton(buttonSettings, JdotxtGUI.lang.getWord("Preferences"));
		
        // Add GUI elements to toolbar
		this.add(buttonSave);
		this.add(buttonReload);
		this.add(buttonArchive);
		this.add(fileLocationCombobox);
		this.add(toggleCopy);
		this.add(togglePrepend);
		this.add(toggleFuture);
		this.add(toggleHide);
		this.add(Box.createHorizontalGlue());
		this.add(savedSortCombobox);
		this.add(buttonSort);
		this.add(textfieldSearch);
		this.add(Box.createRigidArea(new Dimension(4,1)));
		this.add(buttonSettings);
	}

	public void refreshSettingsToggles() {
		toggleHide.setToggle(Jdotxt.userPrefs.getBoolean("showHidden", true));
		toggleFuture.setToggle(Jdotxt.userPrefs.getBoolean("showThreshold", true));
		toggleCopy.setToggle(Jdotxt.userPrefs.getBoolean("copyMetadata", false));
		togglePrepend.setToggle(Jdotxt.userPrefs.getBoolean("prependMetadata", false));
	}
	
	private void styleJdotxtImageButton(JdotxtImageButton button, String toolTipText) {
		button.setToolTipText(toolTipText);
		button.setHoverColor(JdotxtGUI.COLOR_HOVER);
		button.setPressedColor(JdotxtGUI.COLOR_PRESSED);
		button.setBackgroundColor(JdotxtGUI.COLOR_GRAY_PANEL);
	}
	
	// GUI element getters
	public JdotxtImageButton getButtonSave()      { return buttonSave; }
	public JdotxtImageButton getButtonReload()   { return buttonReload; }
	public JdotxtImageButton getButtonArchive()   { return buttonArchive; }
	public JdotxtImageButton getButtonSettings()  { return buttonSettings; }
	public SearchField       getTextfieldSearch() { return textfieldSearch; }
	public JdotxtImageButton getButtonSort() 	  { return buttonSort; }
	public JdotxtToggleImageButton getToggleHide() {
		return toggleHide;
	}

	public JdotxtToggleImageButton getToggleFuture() {
		return toggleFuture;
	}

	public JdotxtToggleImageButton getTogglePrepend() {
		return togglePrepend;
	}

	public JdotxtToggleImageButton getToggleCopy() {
		return toggleCopy;
	}

	public JdotxtCombobox getFileLocationCombobox() {
		return fileLocationCombobox;
	}

	// Enable/disable all controls
	public void setEnabled (boolean enabled){
		this.enabled = enabled;
		buttonSave.setEnabled(enabled);
		buttonReload.setEnabled(enabled);
		buttonArchive.setEnabled(enabled);
		buttonSettings.setEnabled(enabled);
		textfieldSearch.setEnabled(enabled);
		super.setEnabled(enabled);
	}
	public boolean isEnabled() {return enabled;}
	
	public void setVisibleSaveReload(boolean visible) {
		buttonSave.setVisible(visible);
		buttonReload.setVisible(visible);
	}

	public JdotxtCombobox getSavedSortCombobox() {
		return savedSortCombobox;
	}

	// The search box
    @SuppressWarnings("serial")
	public static class SearchField extends Box{
    	
		private final static String CANCEL_ACTION = "cancel_search";
		private JTextField search;
		private JdotxtImageButton button;
		
		private ImageIcon imgSearch;
		private ImageIcon imgX;
		
		private String emptyText;
		private boolean searchActive;
		private boolean enabled = true;
		
		private DocumentListener listener = null;
		
		public SearchField(String emptyText) {
			super(BoxLayout.X_AXIS);
			this.emptyText = emptyText;
			initGUI();
		}
		
		private void initGUI() {
            loadIcons();
			
            // Elements
			search = new JTextField(20);
			button = new JdotxtImageButton(imgSearch);
			
			// Style elements
			search.setBorder(BorderFactory.createLineBorder(Color.WHITE,4));
			search.setFont(search.getFont().deriveFont(14f));
			search.setMaximumSize(search.getPreferredSize());
			search.addFocusListener(new SearchFocusListener());
			search.setSelectionColor(JdotxtGUI.COLOR_PRESSED);
			InputMap im = search.getInputMap(JComponent.WHEN_FOCUSED);
	        ActionMap am = search.getActionMap();
	        im.put(KeyStroke.getKeyStroke("ESCAPE"), CANCEL_ACTION);
	        am.put(CANCEL_ACTION, new CancelAction());
			
	        button.setFocusable(true);
			button.setBorder(null);
			button.addActionListener(new SearchButtonListener());
			
			// Add elements to search field
			this.add(Box.createRigidArea(new Dimension(4,1)));
			this.add(search);
			this.add(button);
			this.add(Box.createRigidArea(new Dimension(6,1)));
			this.setBackground(Color.WHITE);
			this.setOpaque(true);
			this.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY,1));
			
			// Fix size once and for all, so that font change doesn't change the width
			this.setMaximumSize(this.getPreferredSize());
			this.setPreferredSize(this.getPreferredSize());
			
			clearSearch();
		}
		
		private void loadIcons() {
			imgSearch  = Util.createImageIcon("/res/drawable/search.png");
			imgX       = Util.createImageIcon("/res/drawable/x.png");
		}
		
		public void clearSearch() { clearSearch(true); }
		public void clearSearch(boolean focusButton) {
			search.setText("");
			searchActive = false;
			if (listener != null) search.getDocument().removeDocumentListener(listener);
			search.setForeground(Color.GRAY);
			search.setFont(JdotxtGUI.fontRI);
			//search.setFont(JdotxtGUI.fontR);
			search.setText(emptyText);
			button.setIcon(imgSearch);
			if (focusButton) button.requestFocus();
		}
		
		public void setDocumentListener(DocumentListener listener) { this.listener = listener; }
		
		public void requestFocus() { search.requestFocus(); }
		
		public void setEnabled(boolean enabled) {
			this.enabled = enabled;
			search.setEnabled(enabled);
			
			if (!enabled) {
				clearSearch();
				search.setBackground(Color.WHITE);
			}
		}
		public boolean isEnabled() { return enabled; }
		
		private class SearchFocusListener implements FocusListener {
			@Override
			public void focusGained(FocusEvent arg0) {
				if (!searchActive) { 
					search.setText("");
					if (listener != null) search.getDocument().addDocumentListener(listener);
				}
				search.setForeground(Color.BLACK);
				search.setFont(JdotxtGUI.fontR);
				button.setIcon(imgX);
				searchActive = true;
			}

			@Override
			public void focusLost(FocusEvent arg0) {
				if (search.getText().isEmpty()) clearSearch(false);
			}
		}
		
		private class SearchButtonListener implements ActionListener {
			@Override
			public void actionPerformed(ActionEvent ae) {
				if (ae.getSource() == button) {
					clearSearch();
					search.setFocusable(false);
					search.setFocusable(true);
				}
			}
		}
		
		private class CancelAction extends AbstractAction {
	        public void actionPerformed(ActionEvent ev) { search.setText(""); }
	    }
    }

	public void switchTodoFile() {
		if (fileLocationCombobox.getItemCount() <= 1)
			return;
		fileLocationCombobox.setSelectedItem(fileLocationCombobox.getItemAt(1));
	}
}
