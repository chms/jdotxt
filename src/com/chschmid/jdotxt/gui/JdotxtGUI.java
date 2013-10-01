package com.chschmid.jdotxt.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.BadLocationException;

import res.lang.LanguagesController;

import com.chschmid.jdotxt.Jdotxt;
import com.todotxt.todotxttouch.task.Priority;
import com.todotxt.todotxttouch.task.TaskBag;
import com.todotxt.todotxttouch.util.Util;

public class JdotxtGUI extends JFrame {
	private static final long serialVersionUID = 3932863098451412046L;
	
	public static int MIN_WIDTH = 640;
	public static int MIN_HEIGHT = 480;
	
	public static Color COLOR_GRAY_PANEL = new Color(221, 221, 221);
	public static Color COLOR_HOVER      = new Color(136, 201, 225);
	public static Color COLOR_PRESSED    = new Color(51, 181, 229);
	
	public static Font fontR;
	public static Font fontRI;
	public static Font fontB;
	
	public static LanguagesController lang;
	public static ImageIcon icon;
	
	private TaskBag taskBag;
	
	private JdotxtToolbar jdotxtToolbar;
	
	private JPanel filterPanel;
	private JScrollPane projectsPane;
	private JScrollPane contextsPane;
	private JScrollPane tasksPane;
	
	private JList<String> projects;
	private JList<String> contexts;
	private JList<String> loadingp;
	private JList<String> loadingc;
	private JdotxtTasksPanel tasksPanel;
	
	FilterSelectionListener projectsListener, contextsListener;
	
	private ArrayList<Priority> filterPrios  = new ArrayList<Priority>();
	private ArrayList<String> filterContexts = new ArrayList<String>();
	private ArrayList<String> filterProjects = new ArrayList<String>();
	private String search = "";
	
	public JdotxtGUI() {
		super();
		initGUI();
	}
	
	private void initGUI() {
		// Style main window
		this.setTitle(lang.getWord("Jdotxt"));
		this.setIconImage(icon.getImage());
		this.setBackground(Color.WHITE);
		
		// Create GUI elements
		jdotxtToolbar = new JdotxtToolbar();
		filterPanel = new JPanel(new GridLayout(1, 2));
		projects = new JList<String>();
		contexts = new JList<String>();
		loadingp = new JList<String>();
		loadingc = new JList<String>();
		projectsPane = new JScrollPane();
		contextsPane = new JScrollPane();
		tasksPanel = new JdotxtTasksPanel(this);
		tasksPane = new JScrollPane();
		
		jdotxtToolbar.getTextfieldSearch().setDocumentListener(new SearchListener());
		jdotxtToolbar.setSaveListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {
				Thread t = new Thread(new Runnable() {
					@Override
					public void run() {
						taskBag.store();
					}
				});
				t.start();
			}
		});
		jdotxtToolbar.setReloadListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				reset();
				reloadTasks();
			}
		});
		jdotxtToolbar.setArchiveListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				reset();
				Thread starter = new Thread(new TaskLoader(true));
		    	starter.start();
			}
		});
		jdotxtToolbar.setSettingsListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				JdotxtSettingsDialog settingsDialog = new JdotxtSettingsDialog();
				String currentPath = Jdotxt.userPrefs.get("dataDir", Jdotxt.DEFAULT_DIR); 
				settingsDialog.setVisible(true);
				String newPath = Jdotxt.userPrefs.get("dataDir", Jdotxt.DEFAULT_DIR);
				if (!currentPath.equals(newPath)) {
					reset();
					reloadTasks();
				}
			}
		});
		
		filterPanel.setPreferredSize(new Dimension(300,100));
		
		projects.setFont(fontR);
		projects.setSelectionBackground(COLOR_PRESSED);
		projects.setCellRenderer(new FilterCellRenderer());
		projectsListener = new FilterSelectionListener(projects, filterProjects);
		projects.addListSelectionListener(projectsListener);
		
		contexts.setFont(fontR);
		contexts.setSelectionBackground(COLOR_PRESSED);
		contexts.setCellRenderer(new FilterCellRenderer());
		contextsListener = new FilterSelectionListener(contexts, filterContexts);
		contexts.addListSelectionListener(contextsListener);
		
		String[] loadingString = new String[1];
	    loadingString[0] = lang.getWord("Loading...");
		loadingp.setListData(loadingString);
		loadingp.setFont(fontRI);
		loadingp.setEnabled(false);
		loadingc.setListData(loadingString);
		loadingc.setFont(fontRI);
		loadingc.setEnabled(false);
		
		projectsPane.setBorder(BorderFactory.createEmptyBorder());
		projectsPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		projectsPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		projectsPane.setViewportBorder(null);
		projectsPane.getVerticalScrollBar().setBackground(Color.WHITE);
		projectsPane.getVerticalScrollBar().setOpaque(true);
		
		contextsPane.setBorder(BorderFactory.createEmptyBorder());
		contextsPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		contextsPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		contextsPane.setViewportBorder(null);
		contextsPane.getVerticalScrollBar().setBackground(Color.WHITE);
		contextsPane.getVerticalScrollBar().setOpaque(true);
		
		filterPanel.add(projectsPane);
		filterPanel.add(contextsPane);
		
		tasksPane.setBorder(BorderFactory.createEmptyBorder());
		tasksPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		tasksPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		tasksPane.setViewportBorder(null);
		tasksPane.getVerticalScrollBar().setBackground(Color.WHITE);
		tasksPane.getVerticalScrollBar().setOpaque(true);
		tasksPane.setViewportView(tasksPanel);
		
		this.add(jdotxtToolbar, BorderLayout.PAGE_START);
		this.add(filterPanel, BorderLayout.LINE_START);
		this.add(tasksPane, BorderLayout.CENTER);
		
		this.setMinimumSize(new Dimension(MIN_WIDTH, MIN_HEIGHT));
		this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		
		if (Jdotxt.userPrefs.getBoolean("isMaximized", false)) this.setExtendedState(this.getExtendedState() | JFrame.MAXIMIZED_BOTH);
		else {
			this.setLocation(new Point(Jdotxt.userPrefs.getInt("x", 0), Jdotxt.userPrefs.getInt("y", 0)));
			this.setSize(Jdotxt.userPrefs.getInt("width", MIN_WIDTH), Jdotxt.userPrefs.getInt("height", MIN_HEIGHT));
		}
		
		reset();
		
		this.addWindowListener( new WindowAdapter() {
		    public void windowOpened( WindowEvent e ){
		    	projectsPane.requestFocus();
		    	reloadTasks();
		    }
		});
		
		this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
            	int result = 1;
            	if (taskBag.hasChanged()) {
            		result = JOptionPane.showOptionDialog(JdotxtGUI.this, lang.getWord("Text_save_changes"), lang.getWord("Jdotxt"), JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE, null, null, null);
            		if (result == 0) taskBag.store();
            	}
            	
            	if (result < 2){
            		Jdotxt.userPrefs.putInt("x", JdotxtGUI.this.getX());
                	Jdotxt.userPrefs.putInt("y", JdotxtGUI.this.getY());
                	Jdotxt.userPrefs.putInt("width", JdotxtGUI.this.getWidth());
                	Jdotxt.userPrefs.putInt("height", JdotxtGUI.this.getHeight());
                	Jdotxt.userPrefs.putBoolean("isMaximized", (JdotxtGUI.this.getExtendedState() & JFrame.MAXIMIZED_BOTH) != 0);
                	Jdotxt.userPrefs.putBoolean("firstRun", false);
                	Jdotxt.userPrefs.put("version", Jdotxt.VERSION);
                    System.exit(0);
            	}
            }
        });
	}
	
	public void reset() {
		jdotxtToolbar.setEnabled(false);
		projectsPane.setViewportView(loadingp);
		contextsPane.setViewportView(loadingc);
		projects.removeAll();
		contexts.removeAll();
		tasksPanel.reset();
	}
	
	public void reloadTasks() {
		Thread starter = new Thread(new TaskLoader());
    	starter.start();
	}
	
	public void setTaskBag(TaskBag taskBag) {
		reset();
		this.taskBag = taskBag;
		tasksPanel.setTaskbag(taskBag);
		
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				forceUpdateFilterPanes();
				jdotxtToolbar.setEnabled(true);
			}
		});
	}
	
	public static void loadLookAndFeel(String language) {
		fontR  = new Font("Ubuntu Light", Font.PLAIN, 14);
    	fontRI = new Font("Ubuntu Light", Font.ITALIC, 14);
    	fontB  = new Font("Ubuntu", Font.PLAIN, 14);
    	
    	// Fonts are not available
    	if (!fontR.getFamily().equals("Ubuntu Light") || !fontB.getFamily().equals("Ubuntu")) {
    		try {
            	fontR  = Font.createFont(Font.TRUETYPE_FONT, Jdotxt.class.getResourceAsStream("/res/fonts/Ubuntu-R.ttf")).deriveFont(14f);
            	fontRI = Font.createFont(Font.TRUETYPE_FONT, Jdotxt.class.getResourceAsStream("/res/fonts/Ubuntu-MI.ttf")).deriveFont(14f);
            	fontB  = Font.createFont(Font.TRUETYPE_FONT, Jdotxt.class.getResourceAsStream("/res/fonts/Ubuntu-B.ttf")).deriveFont(14f);
    		} catch (FontFormatException e) {
    			// No problem, will also work with another L&F
    		} catch (IOException e) {
    			// No problem, will also work with another L&F
    		}
    	}
    	
		// Set System Look & Feel
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException e) {
			// No problem, will also work with another L&F
		} catch (InstantiationException e) {
			// No problem, will also work with another L&F
		} catch (IllegalAccessException e) {
			// No problem, will also work with another L&F
		} catch (UnsupportedLookAndFeelException e) {
			// No problem, will also work with another L&F
		}
        
        // Get language
        lang = new LanguagesController(language);
        icon = Util.createImageIcon("/res/drawable/jdo256.png");
	}
	
	public void updateFilterPanes() {
		if (!isFilterPaneUpToDate()) forceUpdateFilterPanes();
	}
	
	public void forceUpdateFilterPanes() {
		projects.removeListSelectionListener(projectsListener);
		contexts.removeListSelectionListener(contextsListener);
		
		List<String> selectedProjects = (List<String>) projects.getSelectedValuesList();
		List<String> selectedContexts = (List<String>) contexts.getSelectedValuesList();
		
		ArrayList<String> myProjects = taskBag.getProjects(false);
		ArrayList<String> myContexts = taskBag.getContexts(false);
		
		Util.prependString(myProjects, "+");
		Util.prependString(myContexts, "@");
		
		myProjects.add(0, lang.getWord("Uncategorized"));
		myProjects.add(0, lang.getWord("All"));
		
		myContexts.add(0, lang.getWord("Uncategorized"));
		myContexts.add(0, lang.getWord("All"));
		
		String[] projectsString = new String[myProjects.size()];
		myProjects.toArray(projectsString);
		
		String[] contextsString = new String[myContexts.size()];
		myContexts.toArray(contextsString);
		
		projects.setListData(projectsString);
		contexts.setListData(contextsString);
		
		ArrayList<Integer> selectedProjectsIndices = new ArrayList<Integer>();
		ArrayList<Integer> selectedContextsIndices = new ArrayList<Integer>();
		
		for (int k1 = 0; k1 < projectsString.length; k1++) if (selectedProjects.contains(projectsString[k1])) selectedProjectsIndices.add(k1);
		for (int k1 = 0; k1 < contextsString.length; k1++) if (selectedContexts.contains(contextsString[k1])) selectedContextsIndices.add(k1);
		
		if (selectedProjectsIndices.size() == 0) projects.setSelectedIndex(0);
		else projects.setSelectedIndices(Util.integerList2IntArray(selectedProjectsIndices));
		if (selectedContextsIndices.size() == 0) contexts.setSelectedIndex(0);
		else contexts.setSelectedIndices(Util.integerList2IntArray(selectedContextsIndices));
		
		if (projectsPane.getViewport().getView() != projects) {
			projectsPane.setViewportView(projects);
			contextsPane.setViewportView(contexts);
		}
		projectsListener.forceValueChanged();
		projects.addListSelectionListener(projectsListener);
		contexts.addListSelectionListener(contextsListener);
	}
	
	public boolean isFilterPaneUpToDate() {
		ArrayList<String> myProjects = taskBag.getProjects(false);
		ArrayList<String> myContexts = taskBag.getContexts(false);
		
		if ((projects.getModel().getSize() != myProjects.size() + 2) || (contexts.getModel().getSize() != myContexts.size() + 2)) return false;

		for (int k1 = 0; k1 < myProjects.size(); k1++) if (!projects.getModel().getElementAt(k1+2).substring(1).equals(myProjects.get(k1))) return false;
		for (int k1 = 0; k1 < myContexts.size(); k1++) if (!contexts.getModel().getElementAt(k1+2).substring(1).equals(myContexts.get(k1))) return false;
		
		return true;
	}
	
	public void updateTasksPane(boolean changeFocus) {
		tasksPanel.setFilter(filterPrios, filterContexts, filterProjects, search);
		if (changeFocus) tasksPanel.requestFocus();
		tasksPanel.updateTaskPanel();
	}

	public class FilterSelectionListener implements ListSelectionListener {
		private List<String> filter;
		private JList<String> list;
		
		public FilterSelectionListener(JList<String> list, List<String> filter) {
			this.filter = filter;
			this.list   = list;
		}
		
		@Override
		public void valueChanged(ListSelectionEvent event) {
			if (!event.getValueIsAdjusting()) {
				forceValueChanged();
			}
		}
		
		public void forceValueChanged() {
			filter.clear();
			List<String> selection = list.getSelectedValuesList();
			
			if (selection.contains(lang.getWord("All"))) list.setSelectedIndex(0);
			else {
				if (selection.contains(lang.getWord("Uncategorized"))) {
					filter.add("-");
					selection.remove(lang.getWord("Uncategorized"));
				}
				for (String s: selection) filter.add(s.substring(1));
			}
			updateTasksPane(false);
		}
	}
	
	public class SearchListener implements DocumentListener {
		@Override
		public void changedUpdate(DocumentEvent e) { /* No action */}
		@Override
		public void insertUpdate(DocumentEvent e) {updateSearch(e);}
		@Override
		public void removeUpdate(DocumentEvent e) {updateSearch(e);}
		
		private void updateSearch(DocumentEvent e) {
			try {
				search = e.getDocument().getText(0, e.getDocument().getLength());
			} catch (BadLocationException e1) {
				search = "";
			}
			updateTasksPane(false);
		}
	}
	
	public class FilterCellRenderer extends DefaultListCellRenderer {
		private static final long serialVersionUID = -7795431329110866824L;
		
		@Override
		public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
	        JLabel c = new JLabel(value.toString());
	        c.setBorder(new EmptyBorder(4, 4, 4, 4));
	        c.setOpaque(true);
	        
	        if (JdotxtGUI.lang.getWord("All").equals(value) || JdotxtGUI.lang.getWord("Uncategorized").equals(value)) {
	            c.setFont(JdotxtGUI.fontRI);
	        } else {
	        	c.setFont(JdotxtGUI.fontR);
	        }
	        
	        if (isSelected) {
	        	c.setBackground(list.getSelectionBackground());
	        	c.setForeground(list.getSelectionForeground());
	        } else {
	        	c.setBackground(list.getBackground());
	        	c.setForeground(list.getForeground());
	        }
	        return c;
	    }
	}
	
	public class TaskLoader implements Runnable {
		private boolean doArchive = false;
		
		public TaskLoader(){
			super();
		}
		
		public TaskLoader(boolean doArchive){
			super();
			this.doArchive = doArchive;
		}
		
		@Override
		public void run() {
			if (doArchive) Jdotxt.archiveTodos();
			Jdotxt.loadTodos();
			setTaskBag(Jdotxt.taskBag);
		}
	}
}
