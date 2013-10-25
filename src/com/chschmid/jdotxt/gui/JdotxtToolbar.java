package com.chschmid.jdotxt.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.event.DocumentListener;

import com.todotxt.todotxttouch.util.Util;

public class JdotxtToolbar extends Box{

	private ImageButton buttonSave;
	private ImageButton buttonReload;
	private ImageButton buttonArchive;
	private ImageButton buttonSettings;
	private SearchField textfieldSearch;
	
	private boolean enabled;
	
	private static final long serialVersionUID = 4375297894805216012L;
	
	public JdotxtToolbar() {
		super(BoxLayout.X_AXIS);
		
		ImageIcon iconSave     = Util.createImageIcon("/res/drawable/save.png");
		ImageIcon iconReload   = Util.createImageIcon("/res/drawable/reload.png");
		ImageIcon iconArchive  = Util.createImageIcon("/res/drawable/archive.png");
		ImageIcon iconSettings = Util.createImageIcon("/res/drawable/settings.png");
		ImageIcon border       = Util.createImageIcon("/res/drawable/toolbar-border.png");
		
		this.setBackground(JdotxtGUI.COLOR_GRAY_PANEL);
		this.setBorder(BorderFactory.createMatteBorder(0, 0, 3, 0, border));
		
		buttonSave      = new ImageButton(iconSave);
		buttonSave.setToolTipText(JdotxtGUI.lang.getWord("Save"));
		buttonReload    = new ImageButton(iconReload);
		buttonReload.setToolTipText(JdotxtGUI.lang.getWord("Reload"));
		buttonArchive   = new ImageButton(iconArchive);
		buttonArchive.setToolTipText(JdotxtGUI.lang.getWord("Archive"));
		textfieldSearch = new SearchField(JdotxtGUI.lang.getWord("Search..."));
		buttonSettings  = new ImageButton(iconSettings);
				
		this.add(buttonSave);
		this.add(buttonReload);
		this.add(buttonArchive);
		this.add(Box.createHorizontalGlue());
		this.add(textfieldSearch);
		this.add(Box.createRigidArea(new Dimension(4,1)));
		this.add(buttonSettings);
		
		this.setOpaque(true);
	}
	
	public ImageButton getButtonSave()        { return buttonSave; }
	public ImageButton getButtonSettings()    { return buttonSettings; }
	public SearchField   getTextfieldSearch() { return textfieldSearch; }
	
	public void setEnabled (boolean enabled){
		this.enabled = enabled;
		buttonSave.setEnabled(enabled);
		buttonReload.setEnabled(enabled);
		buttonArchive.setEnabled(enabled);
		buttonSettings.setEnabled(enabled);
		textfieldSearch.setEnabled(enabled);
		super.setEnabled(enabled);
	}
	public void setEnableSave(boolean enabled) { buttonSave.setEnabled(enabled);}
	
	public boolean isEnabled() {return enabled;}
	
	public void setSaveListener(ActionListener actionListener) { buttonSave.setActionListener(actionListener); }
	public void setReloadListener(ActionListener actionListener) { buttonReload.setActionListener(actionListener); }
	public void setArchiveListener(ActionListener actionListener) { buttonArchive.setActionListener(actionListener); }
	public void setSettingsListener(ActionListener actionListener) { buttonSettings.setActionListener(actionListener); }
	
    public static class SearchField extends Box{

		private static final long serialVersionUID = -304940174540744161L;
    	
		private final static String CANCEL_ACTION = "cancel_search";
		private JTextField search;
		private JButton button;
		
		private ImageIcon buttonSearch;
		private ImageIcon buttonSearchX;
		
		private String emptyText;
		private boolean searchActive;
		private boolean enabled = true;
		
		private DocumentListener listener = null;
		
		public SearchField(String emptyText) {
			super(BoxLayout.X_AXIS);

			loadIcons();
			
			this.emptyText = emptyText;
			
			search = new JTextField(20);
			button = new JButton(buttonSearch);
			
			search.setBorder(BorderFactory.createLineBorder(Color.WHITE,4));
			search.setFont(search.getFont().deriveFont(14f));
			search.setMaximumSize(search.getPreferredSize());
			search.addFocusListener(new SearchFocusListener());
			search.setSelectionColor(JdotxtGUI.COLOR_PRESSED);
			InputMap im = search.getInputMap(JComponent.WHEN_FOCUSED);
	        ActionMap am = search.getActionMap();
	        im.put(KeyStroke.getKeyStroke("ESCAPE"), CANCEL_ACTION);
	        am.put(CANCEL_ACTION, new CancelAction());
			
			button.setFocusable(false);
			button.setBorder(null);
			button.setContentAreaFilled(false);
			button.addActionListener(new SearchButtonListener());
			
			this.add(Box.createRigidArea(new Dimension(4,1)));
			this.add(search);
			this.add(button);
			this.add(Box.createRigidArea(new Dimension(6,1)));
			this.setBackground(Color.WHITE);
			this.setOpaque(true);
			this.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY,1));
			this.setMaximumSize(this.getPreferredSize());
			
			clearSearch();
		}
		
		public void clearSearch() {
			search.setText("");
			searchActive = false;
			if (listener != null) search.getDocument().removeDocumentListener(listener);
			search.setForeground(Color.GRAY);
			search.setFont(JdotxtGUI.fontRI);
			search.setText(emptyText);
			button.setIcon(buttonSearch);
		}
		
		public void setDocumentListener(DocumentListener listener) {
			this.listener = listener;
			
		}
		
		public void requestFocus() { search.requestFocus(); }
		
		private void loadIcons() {
			buttonSearch  = Util.createImageIcon("/res/drawable/search.png");
			buttonSearchX = Util.createImageIcon("/res/drawable/x.png");
		}
		
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
				button.setIcon(buttonSearchX);
				searchActive = true;
			}

			@Override
			public void focusLost(FocusEvent arg0) {
				if (search.getText().isEmpty()) {
					clearSearch();
				}
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
		
		@SuppressWarnings("serial")
		private class CancelAction extends AbstractAction {
	        public void actionPerformed(ActionEvent ev) {
	        	search.setText("");
	        }
	    }
    }
}
