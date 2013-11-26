package com.chschmid.jdotxt.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.chschmid.jdotxt.gui.controls.JdotxtDateField;
import com.chschmid.jdotxt.gui.controls.JdotxtImageButton;
import com.chschmid.jdotxt.gui.controls.JdotxtImageCheckBox;
import com.chschmid.jdotxt.gui.controls.JdotxtPriorityField;
import com.chschmid.jdotxt.gui.controls.JdotxtTaskPanel2;
import com.todotxt.todotxttouch.task.Task;
import com.todotxt.todotxttouch.util.Util;

@SuppressWarnings("serial")
public class JdotxtGUItest extends JFrame {
	public static int MIN_WIDTH = 640;
	public static int MIN_HEIGHT = 480;
	
	ImageIcon iconSave     = Util.createImageIcon("/res/drawable/save.png");
	public static final ImageIcon selected   = Util.createImageIcon("/res/drawable/check.png");
	public static final ImageIcon unselected = Util.createImageIcon("/res/drawable/uncheck.png");
	
	JdotxtImageCheckBox cb;
	
	public JdotxtGUItest() {
		super();
		initGUI();
	}
	
	private void initGUI() {
		testJdotxtImageButton();
		this.setMinimumSize(new Dimension(MIN_WIDTH, MIN_HEIGHT));
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	private void testJdotxtDateField() {
		JdotxtDateField df = new JdotxtDateField("2013-11-26");
		df.setDateListener(new JdotxtDateFieldListener("DateField"));
		JdotxtPriorityField pf = new JdotxtPriorityField('F');
		pf.setPriorityListener(new JdotxtDateFieldListener("PriorityField"));
		
		this.add(pf, BorderLayout.LINE_START);
		this.add(df, BorderLayout.CENTER);
		this.add(new JTextField("Test3"), BorderLayout.LINE_END);
	}
	
	private class JdotxtDateFieldListener implements DocumentListener {
		private String element;
		
		public JdotxtDateFieldListener(String element) { this.element = element; }
		
		@Override
		public void insertUpdate(DocumentEvent e) {
			System.out.println(element + ": insertUpdate");
		}

		@Override
		public void removeUpdate(DocumentEvent e) {
			System.out.println(element + ": removeUpdate");
		}

		@Override
		public void changedUpdate(DocumentEvent e) {
			System.out.println(element + ": changedUpdate");
		}
	}
	
	private void testJdotxtImageButton() {
		JdotxtImageButton ib = new JdotxtImageButton(iconSave);
		ib.setFocusable(true);
		ib.addActionListener(new JdotxtImageButtonListener("save"));
		this.add(ib, BorderLayout.LINE_START);
		cb = new JdotxtImageCheckBox(unselected, selected);
		cb.addActionListener(new JdotxtImageCheckBoxListener("cb"));
		this.add(cb, BorderLayout.LINE_END);
		
		JdotxtTaskPanel2 tp = new JdotxtTaskPanel2(new Task(1,"2013-12-01 blub"));
		this.add(tp, BorderLayout.CENTER);
		//this.add(new JTextField("Test3"), BorderLayout.LINE_END);
	}
	
	private class JdotxtImageButtonListener implements ActionListener {
		String element;
		public JdotxtImageButtonListener(String element) {this.element = element;}
		@Override
		public void actionPerformed(ActionEvent arg0) {
			System.out.println(element + ": actionPerformed");
		}
	}
	
	private class JdotxtImageCheckBoxListener implements ActionListener {
		String element;
		public JdotxtImageCheckBoxListener(String element) {this.element = element;}
		@Override
		public void actionPerformed(ActionEvent arg0) {
			System.out.println(element + ": " + cb.isSelected());
		}
	}
}
