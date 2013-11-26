package com.chschmid.jdotxt.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.chschmid.jdotxt.Jdotxt;
import com.chschmid.jdotxt.gui.controls.JdotxtImageButton;
import com.todotxt.todotxttouch.task.Priority;
import com.todotxt.todotxttouch.task.Task;
import com.todotxt.todotxttouch.util.Util;

public class JdotxtTaskPanel extends JPanel {
	
	private static final long serialVersionUID = -2593295565154552733L;
	
	private JPanel panelTodoInfo;
	private JPanel panelTodoCommands;
	private JdotxtImageButton buttonNewTask;
	
	private PriorityTextField textPriority;
	private JTextField textContent;
	private DateTextField textDate;

	private JdotxtImageButton checkDone;
	private JdotxtImageButton buttonDelete;
	
	private Task task;
	
	private TaskListener tasklistener;
	private boolean isNewTask;
	
	private int iD;
	
	public static final ImageIcon imgComplete   = Util.createImageIcon("/res/drawable/check.png");
	public static final ImageIcon imgIncomplete = Util.createImageIcon("/res/drawable/uncheck.png");
	public static final ImageIcon imgDelete     = Util.createImageIcon("/res/drawable/delete.png");
	
	public JdotxtTaskPanel(Task task) {
		this.task = task;
		isNewTask = false;
		initLayout();
	}
	
	public JdotxtTaskPanel(Task task, boolean isNewTask) {
		this.task = task;
		this.isNewTask = isNewTask;
		initLayout();
	}
	
	private void initLayout() {
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		
		boolean compactMode = Jdotxt.userPrefs.getBoolean("compactMode", false);

		panelTodoInfo     = new JPanel();
		panelTodoCommands = new JPanel();
		textContent       = new JTextField(task.getText());
		textDate          = new DateTextField(task.getPrependedDate());
		textPriority      = new PriorityTextField(task.getPriority().getCode().charAt(0));
		checkDone         = new JdotxtImageButton(JdotxtTaskPanel.imgIncomplete);
		buttonDelete      = new JdotxtImageButton(JdotxtTaskPanel.imgDelete);
		
		textPriority.setAlignmentY(TOP_ALIGNMENT);
		
		textContent.setFont(JdotxtGUI.fontR);
		textContent.setBorder(BorderFactory.createLineBorder(Color.WHITE,4));
		textContent.setSelectionColor(JdotxtGUI.COLOR_PRESSED);
		textContent.getDocument().addDocumentListener(new TextListener());
		textContent.addFocusListener(new TextFocusListener());
		textContent.addActionListener(new TextActionListener());
		textContent.addKeyListener(new TextKeyListener());
		textContent.setPreferredSize(new Dimension(0, textContent.getPreferredSize().height));
		
		textDate.setFont(JdotxtGUI.fontR.deriveFont(12f));
		textDate.setBorder(BorderFactory.createLineBorder(Color.WHITE,4));
		textDate.setSelectionColor(JdotxtGUI.COLOR_PRESSED);
		
		checkDone.setBackground(Color.WHITE);
		checkDone.setAlignmentX(CENTER_ALIGNMENT);
		checkDone.addActionListener(new DoneListener());
		
		buttonDelete.setBackground(Color.WHITE);
		buttonDelete.setAlignmentX(CENTER_ALIGNMENT);
		buttonDelete.addActionListener(new DeleteListener());
		
		panelTodoInfo.setLayout(new BoxLayout(panelTodoInfo, BoxLayout.Y_AXIS));
		panelTodoInfo.add(textContent);
		if (!compactMode) panelTodoInfo.add(textDate); // No-Date-mod
		panelTodoInfo.setBorder(BorderFactory.createEmptyBorder());
		panelTodoInfo.setBackground(Color.WHITE);
		panelTodoInfo.setMaximumSize(new Dimension(Integer.MAX_VALUE, panelTodoInfo.getPreferredSize().height));
		panelTodoInfo.setAlignmentY(TOP_ALIGNMENT);
		
		if (!compactMode) panelTodoCommands.setLayout(new BoxLayout(panelTodoCommands, BoxLayout.Y_AXIS));
		else panelTodoCommands.setLayout(new BoxLayout(panelTodoCommands, BoxLayout.X_AXIS));
		
		panelTodoCommands.add(checkDone);
		panelTodoCommands.add(buttonDelete);
		panelTodoCommands.setBorder(BorderFactory.createEmptyBorder());
		panelTodoCommands.setBackground(Color.WHITE);
		panelTodoCommands.setAlignmentY(TOP_ALIGNMENT);
		
		if (isNewTask) {
			if (!compactMode) buttonNewTask = new JdotxtImageButton(Util.createImageIcon("/res/drawable/add.png"));
			else buttonNewTask = new JdotxtImageButton(Util.createImageIcon("/res/drawable/add_25.png"));
			buttonNewTask.setAlignmentY(TOP_ALIGNMENT);
			buttonNewTask.addActionListener(new AddTaskListener());
			this.setBorder(BorderFactory.createMatteBorder(2, 0, 2, 0, JdotxtGUI.COLOR_HOVER));
		} else this.setBorder(BorderFactory.createMatteBorder(1, 0, 1, 0, JdotxtGUI.COLOR_GRAY_PANEL));
		this.setBackground(Color.WHITE);
		
		if (isNewTask) this.add(buttonNewTask);
		else this.add(Box.createRigidArea(new Dimension(4, 0)));
		
		this.add(textPriority);
		this.add(panelTodoInfo);
		this.add(panelTodoCommands);

		this.setOpaque(true);
		
		if (task.isCompleted()) markComplete();
	}
	
	private void markComplete() {
		textContent.setForeground(Color.GRAY);
		textDate.setForeground(Color.GRAY);
		textPriority.setPriority('-');
		checkDone.setIcon(JdotxtTaskPanel.imgComplete);
	}
	
	private void markIncomplete() {
		textContent.setForeground(Color.BLACK);
		textDate.setForeground(Color.BLACK);
		textPriority.setPriority(task.getPriority().getCode().charAt(0));
		checkDone.setIcon(JdotxtTaskPanel.imgIncomplete);
	}
	
	public Dimension getMaximumSize() {
		Dimension d = getPreferredSize();
		d.setSize(Integer.MAX_VALUE, d.height);
		return d;
	}

	public Task getTask() { return task; }
	public void setTask(Task task) {
		this.task = task;
		textContent.setText(task.getText());
		textPriority.setPriority(task.getPriority().getCode().charAt(0));
		textDate.setText(task.getPrependedDate());
	}
	
	public void setTaskListener(TaskListener tasklistener) { this.tasklistener = tasklistener; }
	
	public void setFocusPriority() {
		textPriority.requestFocus();
		textPriority.setSelectionStart(1);
		textPriority.setSelectionEnd(2);
	}
	public void setFocusText() { textContent.requestFocus(); }
	public void setFocusText(boolean setToEnd) {
		textContent.requestFocus();
		if (setToEnd) textContent.setCaretPosition(textContent.getDocument().getLength());
	}
	public void setFocusDate() { textDate.requestFocus(); }
	
	public void setID(int iD) { this.iD = iD; }
	public int getID() { return iD; }

	private void setTaskToggleComplete() {
		if (task.isCompleted()) {
			task.markIncomplete();
			markIncomplete();
		} else {
			task.markComplete(new Date());
			markComplete();
		}
		if (tasklistener != null) tasklistener.onCompletionUpdate(task);
	}
	
	private class TextListener implements DocumentListener {
		@Override
		public void changedUpdate(DocumentEvent arg0) {
		}
		@Override
		public void insertUpdate(DocumentEvent arg0) {
			task.update(task.inFileFormatHeader() + textContent.getText());
			if (tasklistener != null) tasklistener.onTextUpdate(task);
		}
		@Override
		public void removeUpdate(DocumentEvent arg0) {
			task.update(task.inFileFormatHeader() + textContent.getText());
			if (tasklistener != null) tasklistener.onTextUpdate(task);
		}
	}
	
	private class TextKeyListener implements KeyListener {
		public void keyPressed(KeyEvent event) {
			processShortcuts(event);
			if (textContent.getCaretPosition() == 0 && event.getKeyCode() == KeyEvent.VK_LEFT) setFocusPriority();
			if (textContent.getCaretPosition() == textContent.getDocument().getLength() && event.getKeyCode() == KeyEvent.VK_RIGHT) setFocusDate();
		}
		public void keyReleased(KeyEvent event) {}
		public void keyTyped(KeyEvent event) {}
	}
	
	private class TextFocusListener implements FocusListener {
		@Override
		public void focusGained(FocusEvent arg0) {
			if (isNewTask) {
				if (textContent.getText().startsWith(JdotxtGUI.lang.getWord("New_task"))){
					textContent.setSelectionStart(0);
					textContent.setSelectionEnd(JdotxtGUI.lang.getWord("New_task").length());
				}
			}
		}

		@Override
		public void focusLost(FocusEvent arg0) {
			textContent.setCaretPosition(0);
			if (tasklistener != null) tasklistener.onForceTextUpdate(task);
		}
	}
	
	private class TextActionListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			if (isNewTask && tasklistener != null) tasklistener.onNewTask(task);
			else if (tasklistener != null) tasklistener.onForceTextUpdate(task);
		}
	}
	
	private class DoneListener implements ActionListener {
		public void actionPerformed(ActionEvent event) { setTaskToggleComplete(); }
	}
	
	private class DeleteListener implements ActionListener {
		public void actionPerformed(ActionEvent event) { if (tasklistener != null) tasklistener.onTaskDeleted(task); }
	}
	
	private class AddTaskListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			if (tasklistener != null) tasklistener.onNewTask(task);
		}
		
	}
	
	private class PriorityTextField extends JTextField {
		private static final long serialVersionUID = -5738423123767908749L;
		
		private char p;

		public PriorityTextField(char p) {
			super();
			this.setFont(JdotxtGUI.fontR);
			this.setColumns(2);
			this.setBorder(BorderFactory.createLineBorder(Color.WHITE,4));
			this.setAlignmentY(TOP_ALIGNMENT);
			this.setMaximumSize(new Dimension(30, this.getPreferredSize().height));
			setSelectionColor(JdotxtGUI.COLOR_PRESSED);
			setPriority(p);
			
			this.addKeyListener(new PriorityTextFieldKeyListener());
			this.addFocusListener(new PriorityTextFieldFocusListener());
		}
		
		public void setPriority(char p) {
			this.p = p;
			if (task.isCompleted()) p = '-';
			task.setPriority(Priority.toPriority(Character.toString(p)));
			setText("(" + p + ")");
			if (p == '-') setForeground(JdotxtGUI.COLOR_GRAY_PANEL);
			else setForeground(Color.BLACK);
		}
		
		private void selectPriority(char p) {
			setPriority(p);
			this.requestFocus();
			setSelectionStart(1);
			setSelectionEnd(2);
		}
		
		private class PriorityTextFieldFocusListener implements FocusListener {
			@Override
			public void focusGained(FocusEvent arg0) {
				selectPriority(p);
			}

			@Override
			public void focusLost(FocusEvent arg0) {
				setSelectionStart(0);
				setSelectionEnd(0);
			}
		}
		
		private class PriorityTextFieldKeyListener implements KeyListener {
			@Override
			public void keyPressed(KeyEvent event) {
				processShortcuts(event);
				int kc = event.getKeyCode();
				if (kc == KeyEvent.VK_RIGHT) textContent.requestFocus();
				if (kc == KeyEvent.VK_UP || kc == KeyEvent.VK_DOWN) return;
				event.consume();
			}

			@Override
			public void keyReleased(KeyEvent event) {
				event.consume();
			}

			@Override
			public void keyTyped(KeyEvent event) {
				Character c = event.getKeyChar();
				if (c == 127 || c == 8 || c == 32) c = '-';
				c = Character.toUpperCase(c);
				if ((c >= 65 && c <= 90) || c == '-') {
					selectPriority(c);
					if (tasklistener != null) tasklistener.onPriorityUpdate(task);
				}
				event.consume();
			}
		}
	}
	
	@SuppressWarnings("serial")
	private class DateTextField extends JTextField {
		private String DEFAULT_DATE_STRING = "----------";
		private String date;
		
		public DateTextField(String date) {
			super();
			setDate(date);
			this.addKeyListener(new DateTextFieldKeyListener());
			this.addFocusListener(new DateTextFieldFocusListener());
		}
		
		private class DateTextFieldFocusListener implements FocusListener {
			@Override
			public void focusGained(FocusEvent arg0) {
			}

			@Override
			public void focusLost(FocusEvent arg0) {
				setDate(getText());
				setCaretPosition(0);
			}
		}
		
		private class DateTextFieldKeyListener implements KeyListener {
			@Override
			public void keyPressed(KeyEvent event) {
				JTextField text = (JTextField)event.getSource();
				StringBuilder tempDate = new StringBuilder(text.getText());
				int cc = event.getKeyCode();
				
				if (textDate.getCaretPosition() == 0 && cc == KeyEvent.VK_LEFT) setFocusText(true);
				if (cc == KeyEvent.VK_RIGHT || cc == KeyEvent.VK_KP_RIGHT || cc == KeyEvent.VK_LEFT || cc == KeyEvent.VK_KP_RIGHT) return;
				if (cc == KeyEvent.VK_UP || cc == KeyEvent.VK_DOWN || cc == KeyEvent.VK_HOME || cc == KeyEvent.VK_END) return;
				
				int pos = text.getCaretPosition();
				
				if (cc == KeyEvent.VK_SPACE) {
					if (pos >= 10) event.consume();
					else {
						if (pos == 4 || pos == 7) pos++;
						tempDate.setCharAt(pos, '-');
						setDate(tempDate.toString());
						updateTask();
						pos++;
						if (pos == 4 || pos == 7) pos++;
						text.setCaretPosition(pos);
					}
				}
				
				if (cc == KeyEvent.VK_BACK_SPACE) {
					if (pos == 0) event.consume();
					else {
						if (pos == 5 || pos == 8) pos--;
						pos--;
						tempDate.setCharAt(pos, '-');
						setDate(tempDate.toString());
						updateTask();
						if (pos == 5 || pos == 8) pos--;
						text.setCaretPosition(pos);
					}
				}
				
				processShortcuts(event);
				event.consume();
			}

			@Override
			public void keyReleased(KeyEvent event) {
			}

			@Override
			public void keyTyped(KeyEvent event) {
				JTextField text = (JTextField)event.getSource();
				StringBuilder tempDate = new StringBuilder(text.getText());
				int pos = text.getCaretPosition();
				Character c = event.getKeyChar();
				if (isNumeric(c)) {
					if (pos >= 10) event.consume();
					else {
						if (pos == 4 || pos == 7) pos++;
						tempDate.setCharAt(pos, c);
						setDate(tempDate.toString());
						updateTask();
						pos++;
						if (pos == 4 || pos == 7) pos++;
						text.setCaretPosition(pos);
					}
				}
				event.consume();
			}
		}
		
		private boolean isValidDate(String date){
			boolean isValid;
			if (date.length() != 10) return false;
			isValid = isNumeric(date.charAt(0));
			isValid = isValid && isNumeric(date.charAt(1));
			isValid = isValid && isNumeric(date.charAt(2));
			isValid = isValid && isNumeric(date.charAt(3));
			isValid = isValid && (date.charAt(4) == '-');
			isValid = isValid && isNumeric(date.charAt(5));
			isValid = isValid && isNumeric(date.charAt(6));
			isValid = isValid && (date.charAt(7) == '-');
			isValid = isValid && isNumeric(date.charAt(8));
			isValid = isValid && isNumeric(date.charAt(9));
			return isValid;
		}
		
		private boolean isValidEditingDate(String date){
			boolean isValid;
			if (date.length() != 10) return false;
			isValid = isNumeric(date.charAt(0)) || date.charAt(0) == '-';
			isValid = isValid && (isNumeric(date.charAt(1)) || date.charAt(1) == '-');
			isValid = isValid && (isNumeric(date.charAt(2)) || date.charAt(2) == '-');
			isValid = isValid && (isNumeric(date.charAt(3)) || date.charAt(3) == '-');
			isValid = isValid && (date.charAt(4) == '-');
			isValid = isValid && (isNumeric(date.charAt(5)) || date.charAt(5) == '-');
			isValid = isValid && (isNumeric(date.charAt(6)) || date.charAt(6) == '-');
			isValid = isValid && (date.charAt(7) == '-');
			isValid = isValid && (isNumeric(date.charAt(8)) || date.charAt(8) == '-');
			isValid = isValid && (isNumeric(date.charAt(9)) || date.charAt(9) == '-');
			isValid = isValid && (date.charAt(0) != '-' || date.charAt(1) != '-' || date.charAt(2) != '-' || date.charAt(3) != '-' || date.charAt(5) != '-' || date.charAt(6) != '-' || date.charAt(7) != '-' || date.charAt(8) != '-');
			return isValid;
		}
		
		//public String getDate() { return date; }
		
		public void setDate(String date) {
			if (isValidEditingDate(date)) {
				if (!task.isCompleted()) setForeground(Color.BLACK);
				if (isValidDate(date)) this.date = date;
			} else {
				setForeground(JdotxtGUI.COLOR_GRAY_PANEL);
				date = DEFAULT_DATE_STRING;
				this.date = date;
			}
			setText(date);
		}
		
		private void updateTask() {
			if (isValidDate(date)) {
				String rawText = task.inFileFormatHeaderNoDate() + date + " " + textContent.getText();
				task.update(rawText);
				if (tasklistener != null) tasklistener.onDateUpdate(task);
			}
			if (date == DEFAULT_DATE_STRING) {
				String rawText = task.inFileFormatHeaderNoDate() + textContent.getText();
				task.update(rawText);
				if (tasklistener != null) tasklistener.onDateUpdate(task);
			}
			
		}
		
		private boolean isNumeric(char c) { return ((c >= '0') && (c <= '9')); }
	}
	
	private void processShortcuts(KeyEvent event) {
		if (event.isControlDown() && event.isShiftDown() && event.getKeyCode() == KeyEvent.VK_D) { if (tasklistener != null) tasklistener.onTaskDeleted(task); } //Delete
		else if (event.isControlDown() && event.getKeyCode() == KeyEvent.VK_D) setTaskToggleComplete(); //Toggle complete
		else if (event.isControlDown() && event.getKeyCode() == KeyEvent.VK_UP);   //Priority goes up
		else if (event.isControlDown() && event.getKeyCode() == KeyEvent.VK_DOWN); //Priority goes down
	}
}
