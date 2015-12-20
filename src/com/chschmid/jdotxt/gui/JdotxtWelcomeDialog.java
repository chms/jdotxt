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

package com.chschmid.jdotxt.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import com.chschmid.jdotxt.Jdotxt;

@SuppressWarnings("serial")
public class JdotxtWelcomeDialog extends JDialog{
	public static final short P_WELCOME        = 0;
	public static final short P_PATH_NOT_FOUND = 1;
	
	private JTextField directory;
	private short purpose;
	
	public JdotxtWelcomeDialog(short purpose) {
		super(new WelcomeFrame());
		this.purpose = purpose;
		initGUI();
	}
	
	void initGUI() {
		this.setModal(true);
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        switch(purpose) {
	        case P_WELCOME:
	        	setTitle(JdotxtGUI.lang.getWord("Welcome")); break;
	        case P_PATH_NOT_FOUND:
	        	setTitle(JdotxtGUI.lang.getWord("Text_file_not_found")); break;
        }
		this.setIconImage(JdotxtGUI.icon.getImage());
		this.getContentPane().setBackground(Color.WHITE);

		// Icon
		JLabel labelIcon = new JLabel(new ImageIcon(JdotxtGUI.icon.getImage().getScaledInstance(64, 64, java.awt.Image.SCALE_SMOOTH)));
		labelIcon.setVerticalAlignment(SwingConstants.TOP);
		labelIcon.setPreferredSize(new Dimension(100, 100));
		
		// Welcome / directory picker	
		JPanel panelInfo = new JPanel();
		panelInfo.setLayout(new BoxLayout(panelInfo, BoxLayout.Y_AXIS));
		panelInfo.add(Box.createVerticalStrut(10));
		panelInfo.setBackground(Color.WHITE);
		panelInfo.setOpaque(true);
		
		JLabel labelTitle = new JLabel(JdotxtGUI.lang.getWord("Text_thank_you"));
		if (purpose == P_PATH_NOT_FOUND) labelTitle.setText(JdotxtGUI.lang.getWord("Text_path_not_found"));
		labelTitle.setFont(JdotxtGUI.fontB.deriveFont(24f));

		JLabel labelFoss = new JLabel(JdotxtGUI.lang.getWord("Text_welcome_foss"));
		labelFoss.setFont(JdotxtGUI.fontR);
		
		JEditorPane labelWhere = new JEditorPane();
		labelWhere.setContentType("text/html");
		labelWhere.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, Boolean.TRUE);
		labelWhere.setFont(JdotxtGUI.fontR);
		labelWhere.setText(JdotxtGUI.lang.getWord("Text_welcome_where"));
		labelWhere.setEditable(false);
		labelWhere.setFocusable(false);
		labelWhere.setAlignmentX(Component.LEFT_ALIGNMENT);
		labelWhere.setBorder(BorderFactory.createEmptyBorder());
		labelWhere.setMaximumSize(labelWhere.getPreferredSize());
		
		directory = new JTextField();
		directory.setFont(JdotxtGUI.fontR);
		directory.setColumns(45);
		directory.setMaximumSize(directory.getPreferredSize());
		
		directory.setEnabled(false);
		directory.setText(Jdotxt.userPrefs.get("dataDir", Jdotxt.DEFAULT_DIR));
		directory.setAlignmentX(LEFT_ALIGNMENT);
		
		JButton btnChooseDir = new JButton(JdotxtGUI.lang.getWord("Choose_directory"));
		btnChooseDir.setFont(JdotxtGUI.fontR);
		btnChooseDir.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser chooser = new JFileChooser();
		        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		        chooser.setCurrentDirectory(new File(directory.getText()));
		        int returnVal = chooser.showDialog(JdotxtWelcomeDialog.this, null);
		        if(returnVal == JFileChooser.APPROVE_OPTION) {
		            directory.setText(chooser.getSelectedFile().getAbsolutePath());
		         }
			}
		});
		
		JEditorPane labelRecommendation = new JEditorPane();
		labelRecommendation.setContentType("text/html");
		labelRecommendation.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, Boolean.TRUE);
		labelRecommendation.setFont(JdotxtGUI.fontR);
		labelRecommendation.setText(JdotxtGUI.lang.getWord("Text_welcome_recommendation"));
		labelRecommendation.setEditable(false);
		labelRecommendation.setFocusable(false);
		labelRecommendation.setAlignmentX(Component.LEFT_ALIGNMENT);
		labelRecommendation.setBorder(BorderFactory.createEmptyBorder());
		labelRecommendation.setMaximumSize(labelRecommendation.getPreferredSize());
		
		panelInfo.add(labelTitle);
		if (purpose == P_WELCOME) {
			panelInfo.add(Box.createVerticalStrut(20));
			panelInfo.add(labelFoss);
		}
		panelInfo.add(Box.createVerticalStrut(20));
		panelInfo.add(labelWhere);
		panelInfo.add(Box.createVerticalStrut(30));
		panelInfo.add(directory);
		panelInfo.add(Box.createVerticalStrut(10));
		panelInfo.add(btnChooseDir);
		panelInfo.add(Box.createVerticalStrut(30));
		panelInfo.add(labelRecommendation);
		panelInfo.add(Box.createVerticalStrut(20));
		
		// Start button
		JButton start = new JButton(JdotxtGUI.lang.getWord("Start"));
		start.setFont(JdotxtGUI.fontR);
		start.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				Jdotxt.userPrefs.put("dataDir", directory.getText());
				JdotxtWelcomeDialog.this.dispose();
			}
		});
		start.setPreferredSize(new Dimension(start.getPreferredSize().width + 40, start.getPreferredSize().height));
		
		Box startBar = new Box(BoxLayout.X_AXIS);
		startBar.add(Box.createHorizontalGlue());
		startBar.add(start);
		startBar.setBackground(JdotxtGUI.COLOR_GRAY_PANEL);
		startBar.setOpaque(true);
		startBar.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		
		this.add(labelIcon, BorderLayout.WEST);
		this.add(panelInfo, BorderLayout.CENTER);
		this.add(Box.createHorizontalStrut(20), BorderLayout.EAST);
		this.add(startBar, BorderLayout.SOUTH);
		
		this.getContentPane().setMaximumSize(new Dimension(500,450));
		pack();
		
		Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
		int x = (int) ((dimension.getWidth() - this.getWidth()) / 2);
		int y = (int) ((dimension.getHeight() - this.getHeight()) / 2);
		this.setLocation(x, y);
		
		this.setResizable(false);
		
		this.addWindowListener(new WindowAdapter() {
			public void windowClosed(WindowEvent e) {
				WelcomeFrame parent = ((WelcomeFrame)getParent());
				if (parent.isVisible()) parent.dispose();
			}
		});
		
		start.requestFocus();
	}
	
	static final class WelcomeFrame extends JFrame {
		WelcomeFrame() {
	    	super();
	        setUndecorated(true);
	        setLocationRelativeTo(null);
	        setIconImage(JdotxtGUI.icon.getImage());
	        setVisible(true);
	    }
	}
}
