package com.chschmid.jdotxt.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URISyntaxException;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import com.chschmid.jdotxt.Jdotxt;

@SuppressWarnings("serial")
public class JdotxtSettingsDialog extends JDialog{
	JTabbedPane tabbedPane;
	Box okCancelBar;
	JPanel about, settings;
	JTextField directory;
	
	public JdotxtSettingsDialog() {
		super();
		initGUI();
	}

	private void initGUI() {
		this.setModal(true);
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		this.setTitle(JdotxtGUI.lang.getWord("Jdotxt") + " " + JdotxtGUI.lang.getWord("Settings"));
		
		tabbedPane = new JTabbedPane();
		tabbedPane.setFont(JdotxtGUI.fontR);
		about    = getAboutPanel();
		settings = getSettingsPanel();
		
		tabbedPane.addTab(JdotxtGUI.lang.getWord("Settings"), settings);
		tabbedPane.addTab(JdotxtGUI.lang.getWord("About"), about);
		
		okCancelBar = new Box(BoxLayout.X_AXIS);
		okCancelBar.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 3));
		JButton ok = new JButton(JdotxtGUI.lang.getWord("OK"));
		JButton cancel = new JButton(JdotxtGUI.lang.getWord("Cancel"));
		
		ok.setFont(JdotxtGUI.fontR);
		cancel.setFont(JdotxtGUI.fontR);
		ok.setPreferredSize(cancel.getPreferredSize());
		
		okCancelBar.add(Box.createHorizontalGlue());
		okCancelBar.add(ok);
		okCancelBar.add(cancel);
		
		cancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				JdotxtSettingsDialog.this.dispose();
			}
		});
		
		ok.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				Jdotxt.userPrefs.put("dataDir", directory.getText());
				JdotxtSettingsDialog.this.dispose();
			}
		});

		this.add(tabbedPane, BorderLayout.CENTER);
		this.add(okCancelBar, BorderLayout.PAGE_END);
		this.pack();
		
		Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
		int x = (int) ((dimension.getWidth() - this.getWidth()) / 2);
		int y = (int) ((dimension.getHeight() - this.getHeight()) / 2);
		this.setLocation(x, y);
		
		this.setResizable(false);
	}
	
	private JPanel getAboutPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		panel.setBackground(Color.WHITE);
		panel.setOpaque(true);
		
		JLabel labelIcon = new JLabel(new ImageIcon(JdotxtGUI.icon.getImage().getScaledInstance(64, 64, java.awt.Image.SCALE_SMOOTH)));
		labelIcon.setVerticalAlignment(SwingConstants.TOP);
		panel.add(labelIcon, BorderLayout.WEST);
		labelIcon.setPreferredSize(new Dimension(100, 100));
		
		JPanel panelInfo = new JPanel();
		panel.add(panelInfo, BorderLayout.CENTER);
		panelInfo.setLayout(new BoxLayout(panelInfo, BoxLayout.Y_AXIS));
		panelInfo.add(Box.createRigidArea(new Dimension(0, 10)));
		panelInfo.setBackground(Color.WHITE);
		panelInfo.setOpaque(true);
		
		JLabel labelTitle = new JLabel(JdotxtGUI.lang.getWord("Jdotxt") + " (Version " + Jdotxt.VERSION + ")");
		labelTitle.setFont(JdotxtGUI.fontB.deriveFont(16f));
		panelInfo.add(labelTitle);
		
		panelInfo.add(Box.createRigidArea(new Dimension(0, 20)));
		
		JEditorPane textInfo = new JEditorPane();
		textInfo.setContentType("text/html");
		textInfo.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, Boolean.TRUE);
		textInfo.setFont(JdotxtGUI.fontR);
		textInfo.setText(JdotxtGUI.lang.getWord("Text_about"));
		textInfo.setEditable(false);
		textInfo.setFocusable(false);
		textInfo.setAlignmentX(Component.LEFT_ALIGNMENT);
		textInfo.setBorder(BorderFactory.createEmptyBorder());
		textInfo.addHyperlinkListener(new HyperlinkListener() {
		    public void hyperlinkUpdate(HyperlinkEvent e) {
		        if(e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
		        	if(Desktop.isDesktopSupported()) {
		        		try {
							Desktop.getDesktop().browse(e.getURL().toURI());
						} catch (IOException e1) {
							// Let's just not open the website
						} catch (URISyntaxException e1) {
							// Let's just not open the website
						}
		        	}
		        }
		    }
		});
		panelInfo.add(textInfo);
		
		panelInfo.add(Box.createRigidArea(new Dimension(0, 20)));
		
		JLabel labelLicense = new JLabel(JdotxtGUI.lang.getWord("License"));
		labelLicense.setFont(JdotxtGUI.fontB.deriveFont(14f));
		panelInfo.add(labelLicense);
		
		panelInfo.add(Box.createRigidArea(new Dimension(0, 10)));
		
		JEditorPane textLicense = new JEditorPane();
		textLicense.setContentType("text/html");
		textLicense.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, Boolean.TRUE);
		textLicense.setFont(JdotxtGUI.fontR);
		textLicense.setText(JdotxtGUI.lang.getWord("Text_license"));
		textLicense.setEditable(false);
		textLicense.setFocusable(false);
		textLicense.setAlignmentX(Component.LEFT_ALIGNMENT);
		textLicense.setBorder(BorderFactory.createEmptyBorder());
		textLicense.addHyperlinkListener(new HyperlinkListener() {
		    public void hyperlinkUpdate(HyperlinkEvent e) {
		        if(e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
		        	if(Desktop.isDesktopSupported()) {
		        		try {
							Desktop.getDesktop().browse(e.getURL().toURI());
						} catch (IOException e1) {
							// Let's just not open the website
						} catch (URISyntaxException e1) {
							// Let's just not open the website
						}
		        	}
		        }
		    }
		});
		panelInfo.add(textLicense);
		
		panelInfo.add(Box.createRigidArea(new Dimension(0, 20)));
		
		panel.add(Box.createRigidArea(new Dimension(20, 0)), BorderLayout.EAST);
		return panel;
	}
	
	private JPanel getSettingsPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		//panel.add(Box.createVerticalGlue());
		panel.add(Box.createVerticalStrut(15));
		
		JLabel lblTextpath = new JLabel("Path where you want to store your todo.txt and your done.txt files:");
		lblTextpath.setAlignmentX(Component.CENTER_ALIGNMENT);
		lblTextpath.setFont(JdotxtGUI.fontR);
		panel.add(lblTextpath);
		
		panel.add(Box.createVerticalStrut(15));
		
		directory = new JTextField();
		directory.setFont(JdotxtGUI.fontR);
		directory.setColumns(35);
		directory.setMaximumSize(directory.getPreferredSize());
		
		directory.setEnabled(false);
		directory.setText(Jdotxt.userPrefs.get("dataDir", Jdotxt.DEFAULT_DIR));
		
		panel.add(directory);
		panel.add(Box.createVerticalStrut(15));
		JButton btnNewButton = new JButton(JdotxtGUI.lang.getWord("Choose_directory"));
		btnNewButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		btnNewButton.setFont(JdotxtGUI.fontR);
		panel.add(btnNewButton);
		
		btnNewButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser chooser = new JFileChooser();
		        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		        int returnVal = chooser.showDialog(JdotxtSettingsDialog.this, null);
		        if(returnVal == JFileChooser.APPROVE_OPTION) {
		            directory.setText(chooser.getSelectedFile().getAbsolutePath());
		         }
			}
		});
		
		panel.add(Box.createVerticalGlue());
		return panel;
	}
}
