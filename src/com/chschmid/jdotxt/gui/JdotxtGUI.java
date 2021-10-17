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

import com.chschmid.jdotxt.Jdotxt;
import com.chschmid.jdotxt.gui.controls.*;
import com.chschmid.jdotxt.gui.utils.SortUtils;
import com.chschmid.jdotxt.util.DelayedActionHandler;
import com.chschmid.jdotxt.util.FileModifiedListener;
import com.chschmid.jdotxt.util.DTHelper;
import com.todotxt.todotxttouch.task.Priority;
import com.todotxt.todotxttouch.task.Task;
import com.todotxt.todotxttouch.task.TaskBag;
import com.todotxt.todotxttouch.task.sorter.Sorters;
import com.todotxt.todotxttouch.util.Util;
import res.lang.LanguagesController;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

@SuppressWarnings("serial")
// The application main window
public class JdotxtGUI extends JFrame {
	
	// Minimal and maximal window dimension settings
	public static int MIN_WIDTH = 640;
	public static int MIN_HEIGHT = 480;
	
	// Lines to scroll using the mouse wheel
	public static int SCROLL_AMOUNT = 1;
	
	// Languages
	public static final String[] languages = { "English" };
	
	// Colors
	public static Color COLOR_GRAY_PANEL = new Color(221, 221, 221);
	public static Color COLOR_HOVER      = new Color(136, 201, 225);
	public static Color COLOR_PRESSED    = new Color(51, 181, 229);
	
	// Fonts
	public static Font fontR;
	public static Font fontRI;
	public static Font fontB;
	
	// Language support
	public static LanguagesController lang;
	
	// Application icon
	public static ImageIcon icon;
	
	// The tasks - from the todo.txt Android client
	private TaskBag taskBag;
	
	// GUI elements
	private JdotxtToolbar toolbar;
	private JdotxtFilterPanel filterPanel;
	private JScrollPane tasksPane;
	private JdotxtTaskList taskList;
	private JdotxtStatusBar statusBar;

	private Map<String, Map<Sorters, Boolean>> savedSorts = new HashMap<>();
	private LinkedList<String> locations = new LinkedList<>();
	
	// Task filters
	private ArrayList<Priority> filterPrios  = new ArrayList<Priority>();
	private ArrayList<String> filterContexts;
	private boolean showHidden = Jdotxt.userPrefs.getBoolean("showHidden", true);
	private boolean showThreshold = Jdotxt.userPrefs.getBoolean("showThreshold", true);
	private ArrayList<String> filterProjects;
	private String search = "";
	
	private boolean reloadDialogVisible, unresolvedFileModification;
	private AutoSaveListener autoSaveListener;
	private DelayedActionHandler autoSaver;
	
	public JdotxtGUI() {
		super();
		initGUI();
	}
	
	private void initGUI() {
		// Style main window
		this.setTitle(lang.getWord("jdotxt"));
		this.setIconImage(icon.getImage());
		this.setBackground(Color.WHITE);
		
		// Create GUI elements
		toolbar     = new JdotxtToolbar();
		filterPanel = new JdotxtFilterPanel();
		taskList    = new JdotxtTaskList();
		tasksPane   = new JScrollPane();
		statusBar   = new JdotxtStatusBar();
		
		// Toolbar listeners
		toolbar.getTextfieldSearch().setDocumentListener(new SearchListener());
		toolbar.getButtonSave().addActionListener(new ActionListener() { public void actionPerformed(ActionEvent arg0) { saveTasks(false); } });
		toolbar.getButtonReload().addActionListener(new ActionListener() { public void actionPerformed(ActionEvent arg0) { reloadTasks(); } });
		toolbar.getButtonArchive().addActionListener(new ActionListener() { public void actionPerformed(ActionEvent arg0) { archiveTasks(); } });
		toolbar.getButtonSettings().addActionListener(new ActionListener() { public void actionPerformed(ActionEvent arg0) { showSettingsDialog(); } });
		toolbar.getButtonSort().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent actionEvent) {
				final JdotxtSortDialog d = new JdotxtSortDialog(taskList.getSortMap(), false);
				d.getOk().addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent actionEvent) {
                        Jdotxt.userPrefs.put("sort", SortUtils.writeSort(new ArrayList<>(d.getSort().entrySet())));
                    }
                });
				d.getSaveButton().addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent actionEvent) {
						saveSort(d.getSortName(), d.getSort());
						toolbar.getSavedSortCombobox().setValues(savedSorts.keySet());
					}
				});
				d.setVisible(true);

				taskList.refreshSort();
				taskList.updateTaskList();
			}
		});
        loadToolboxData();
        toolbar.getSavedSortCombobox().setValues(savedSorts.keySet());
		toolbar.getSavedSortCombobox().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent actionEvent) {
				Object item = toolbar.getSavedSortCombobox().getSelectedItem();
				if (toolbar.getSavedSortCombobox().getMore().equals(item)) {
                    final JdotxtSavedSortDialog d = new JdotxtSavedSortDialog(savedSorts);
                    d.getOk().addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent actionEvent) {
                            savedSorts = d.getSorts();
                            writeSorts();
							toolbar.getSavedSortCombobox().setValues(savedSorts.keySet());
                        }
                    });
                    d.setVisible(true);
                    return;
                }
				Map<Sorters, Boolean> sort = savedSorts.get(item);
				Jdotxt.userPrefs.put("sort", SortUtils.writeSort(new ArrayList<>(sort.entrySet())));

				taskList.refreshSort();
				taskList.updateTaskList();
			}
		});
        toolbar.getFileLocationCombobox().setValues(locations);
		toolbar.getFileLocationCombobox().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                Object item = toolbar.getFileLocationCombobox().getSelectedItem();
				String old = Jdotxt.userPrefs.get("dataDir", "");
				String location = null;
                if (toolbar.getFileLocationCombobox().getMore().equals(item)) {
					JFileChooser chooser = new JFileChooser();
					chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
					chooser.setCurrentDirectory(new File(old));
					int returnVal = chooser.showDialog(JdotxtGUI.this, null);
					if (returnVal == JFileChooser.APPROVE_OPTION) location = chooser.getSelectedFile().getAbsolutePath();
                } else {
                	location = item.toString();
				}
                if (location == null || old.equals(location))
                	return;

                if (taskBag.hasChanged()) {
					int result = JOptionPane.showOptionDialog(
							JdotxtGUI.this,
							lang.getWord("Text_save_changes_before_changes"),
							lang.getWord("jdotxt"),
							JOptionPane.YES_NO_CANCEL_OPTION,
							JOptionPane.WARNING_MESSAGE,
							null,
							null,
							null);
					if (result == JOptionPane.OK_OPTION) Jdotxt.storeTodos();
					if (result == JOptionPane.CANCEL_OPTION) return;
				}

				addLocation(location);
				Jdotxt.userPrefs.put("dataDir", location);
				reloadTasks();
				toolbar.getFileLocationCombobox().setValues(locations);
				toolbar.getFileLocationCombobox().setSelectedItem(location);
            }
        });
		toolbar.getToggleCopy().setToggleAction(new Runnable() {
            @Override
            public void run() {
                boolean toggle = toolbar.getToggleCopy().isToggle();
                Jdotxt.userPrefs.putBoolean("copyMetadata", toggle);
                taskList.setCopyProjectsContexts2NewTask(toggle);
            }
        });
		toolbar.getToggleFuture().setToggleAction(new Runnable() {
            @Override
            public void run() {
                boolean toggle = toolbar.getToggleFuture().isToggle();
                Jdotxt.userPrefs.putBoolean("showThreshold", toggle);
                showThreshold = toggle;
                forwardFilter2TaskList();
            }
        });
		toolbar.getToggleHide().setToggleAction(new Runnable() {
			@Override
			public void run() {
				boolean toggle = toolbar.getToggleHide().isToggle();
				Jdotxt.userPrefs.putBoolean("showHide", toggle);
				showHidden = toggle;
				forwardFilter2TaskList();
			}
		});
		toolbar.getTogglePrepend().setToggleAction(new Runnable() {
			@Override
			public void run() {
				boolean toggle = toolbar.getTogglePrepend().isToggle();
				Jdotxt.userPrefs.putBoolean("prependMetadata", toggle);
				taskList.setPrependMetadata(toggle);
			}
		});
		//toolbar.setVisibleSaveReload(!Jdotxt.userPrefs.getBoolean("autosave", false));
		
		// Other GUI element listeners
		// What to do when the filters change
		filterPanel.addFilterChangeListener(new MyFilterChangeListener()); 
		// What to do when some task changes
		taskList.addTaskListener(new StatusUpdateListener());
		taskList.addTaskListener(new FilterUpdateListener());
		
		// Autosave
		autoSaver = new DelayedActionHandler(Jdotxt.AUTOSAVE_DELAY, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) { 
				if (!unresolvedFileModification) saveTasks(false);
			}
		});
		autoSaveListener = new AutoSaveListener();
		if (Jdotxt.userPrefs.getBoolean("autosave", false)) taskList.addTaskListener(autoSaveListener);
		
		// Style taskPane
		tasksPane.setBorder(BorderFactory.createEmptyBorder());
		tasksPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		tasksPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		tasksPane.setViewportBorder(null);
		tasksPane.getVerticalScrollBar().setBackground(Color.WHITE);
		tasksPane.getVerticalScrollBar().setOpaque(true);
		tasksPane.getVerticalScrollBar().putClientProperty("JScrollBar.fastWheelScrolling", false);
		tasksPane.setViewportView(taskList);
		tasksPane.getViewport().setBackground(Color.WHITE);
		MyMouseWheelListener myMouseWheelListener = new MyMouseWheelListener(tasksPane.getMouseWheelListeners(), SCROLL_AMOUNT);
		for (MouseWheelListener listener: tasksPane.getMouseWheelListeners()) tasksPane.removeMouseWheelListener(listener);
		tasksPane.addMouseWheelListener(myMouseWheelListener);
		
		
		// Add GUI elements to main window
		this.add(toolbar, BorderLayout.PAGE_START);
		this.add(filterPanel, BorderLayout.LINE_START);
		this.add(tasksPane, BorderLayout.CENTER);
		this.add(statusBar, BorderLayout.PAGE_END);
		
		// Set window position and dimensions
		this.setMinimumSize(new Dimension(MIN_WIDTH, MIN_HEIGHT));
		if (Jdotxt.userPrefs.getBoolean("isMaximized", false)) this.setExtendedState(this.getExtendedState() | JFrame.MAXIMIZED_BOTH);
		else {
			Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
			int x = (int) ((dimension.getWidth() - this.getWidth()) / 2);
			int y = (int) ((dimension.getHeight() - this.getHeight()) / 2);
			this.setLocation(new Point(Jdotxt.userPrefs.getInt("x", x), Jdotxt.userPrefs.getInt("y", y)));
			this.setSize(Jdotxt.userPrefs.getInt("width", MIN_WIDTH), Jdotxt.userPrefs.getInt("height", MIN_HEIGHT));
		}

		// Set window opening behavior
		this.addWindowListener( new WindowAdapter() {
		    public void windowOpened( WindowEvent e ){
		    	filterPanel.requestFocus();
		    	reloadTasks();
		    }
		});
		
		// Set window/app closing behavior
		this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
            	int result = 1;
            	if (taskBag.hasChanged()) {
            		if (Jdotxt.userPrefs.getBoolean("autosave", false)) Jdotxt.storeTodos();
            		else {
	            		result = JOptionPane.showOptionDialog(JdotxtGUI.this, lang.getWord("Text_save_changes"), lang.getWord("jdotxt"), JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE, null, null, null);
	            		if (result == 0) Jdotxt.storeTodos();
            		}
            	}
            	
            	if (result < 2){
            		Jdotxt.userPrefs.putInt("x", JdotxtGUI.this.getX());
                	Jdotxt.userPrefs.putInt("y", JdotxtGUI.this.getY());
                	Jdotxt.userPrefs.putInt("width", JdotxtGUI.this.getWidth());
                	Jdotxt.userPrefs.putInt("height", JdotxtGUI.this.getHeight());
                	Jdotxt.userPrefs.putBoolean("isMaximized", (JdotxtGUI.this.getExtendedState() & JFrame.MAXIMIZED_BOTH) != 0);
                	Jdotxt.userPrefs.put("version", Jdotxt.VERSION);
                    System.exit(0);
            	}
            }
        });
		
		this.addWindowFocusListener(new WindowFocusListener() {
			@Override
			public void windowLostFocus(WindowEvent arg0) { }
			@Override
			public void windowGainedFocus(WindowEvent arg0) {
				if (unresolvedFileModification && !reloadDialogVisible) {
					// System.out.println("[" + DTHelper.getTimeString() + "] windowGainedFocus");
					showReloadDialog();
				}
			}
		});
		
		// Add a listener to file modifications
		Jdotxt.addFileModifiedListener(new FileModifiedListener() {
			@Override
			public void fileModified() {
				unresolvedFileModification = true;
				EventQueue.invokeLater(new Runnable() {
					@Override
					public void run() {
						// System.out.println("[" + DTHelper.getTimeString() + "] Entering fileModified:");
						// First, check whether the checksum has changed. Since
						// recently, something has changed with Dropbox, and
						// the app now constantly shows "file modified" dialog,
						// on each and every modification to task list.  So,
						// adding this check.
						try {
							byte[] lastSaveChecksum = taskBag.getLastSaveChecksum();
							byte[] newChecksum = Jdotxt.getTodoFileChecksum();
							// System.out.printf( "[%s] Checking: %s vs. %s is-equal? %b%n", DTHelper.getTimeString(), DatatypeConverter.printHexBinary(lastSaveChecksum), DatatypeConverter.printHexBinary(newChecksum), Arrays.equals(lastSaveChecksum, newChecksum));
							if (Arrays.equals(lastSaveChecksum, newChecksum)) {
								unresolvedFileModification = false;
								return;
							}
						} catch (Exception e) {
							System.out.println(e.getMessage());
						}
						if (reloadDialogVisible == false && JdotxtGUI.this.isFocused()) {
							// System.out.println("[" + DTHelper.getTimeString() + "] in fileModified");
							showReloadDialog();
						}
					}
				});
			}
		});
		
		// Reset window to defaults
		reset();
		this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		
		// KeyDispatcher for global shortcuts
		KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
		manager.addKeyEventDispatcher(new KeyDispatcher());
	}

    private void loadToolboxData() {
        loadSorts();
        loadLocations();
    }

    private void showReloadDialog() {
		reloadDialogVisible = true;
		int result = JOptionPane.showOptionDialog(JdotxtGUI.this, lang.getWord("Text_modified"), lang.getWord("Reload"), JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, null, null);
		reloadDialogVisible = false;
		unresolvedFileModification = false;
		if (result == 0) reloadTasks();
		else {
			taskBag.update(null); // Fake a change so that a save really leads to a save
			toolbar.getButtonSave().setEnabled(true);
			if (Jdotxt.userPrefs.getBoolean("autosave", false)) {
				saveTasks(true);
				System.out.println("Save update");
			}
		}
	}
	
	// Sets the status bar to the default text "open and total tasks"
	public void setDefaultStatusText() {
		String status;
		status = JdotxtGUI.lang.getWord("Open") + ": " + taskList.getNumOfOpenTasks();
		status = status + "   " + JdotxtGUI.lang.getWord("Total") + ": " + taskList.getNumOfTasks();
		JdotxtGUI.this.statusBar.setText(status);
	}
    
	// Forward the filter settings to the Task List
	public void forwardFilter2TaskList() {
		taskList.setFilter(
				JdotxtGUI.this.filterPrios,
				JdotxtGUI.this.filterContexts,
				JdotxtGUI.this.filterProjects,
				JdotxtGUI.this.search,
				JdotxtGUI.this.showHidden,
				JdotxtGUI.this.showThreshold
		);
		taskList.updateTaskList();
	}
	
	// Reset all GUI elements to default (= "Loading")
	public void reset() {
		toolbar.setEnabled(false);
		taskList.reset();
		taskList.setCompactMode(Jdotxt.userPrefs.getBoolean("compactMode", false));
		taskList.setPrependMetadata(Jdotxt.userPrefs.getBoolean("prependMetadata", false));
		taskList.setCopyProjectsContexts2NewTask(Jdotxt.userPrefs.getBoolean("copyMetadata", false));
		filterPanel.reset();
		filterPanel.setVisible(calculateVisibility());
		filterPanel.setSwitchPanels(Jdotxt.userPrefs.getBoolean("switchPanels", false));
		statusBar.setText(lang.getWord("Loading..."));
	}
	
	// Toolbar functions
	private void saveTasks(boolean forceSave) {
		if (forceSave) taskBag.update(null);
		toolbar.getButtonSave().setEnabled(false);
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				Jdotxt.storeTodos();
			}
		});
		t.start();
	}
	
	private void reloadTasks() {
		reset();
		Thread starter = new Thread(new TaskLoader(TaskLoader.RELOAD));
    	starter.start();
	}
	
	private void refreshGUI() {
		taskList.setCompactMode(Jdotxt.userPrefs.getBoolean("compactMode", false));
		taskList.setPrependMetadata(Jdotxt.userPrefs.getBoolean("prependMetadata", false));
		setTaskBag(taskBag);
	}
	
	private void archiveTasks() {
		reset();
		Thread starter = new Thread(new TaskLoader(TaskLoader.ARCHIVE));
    	starter.start();
	}
	
	public void showSettingsDialog() {
		JdotxtPreferencesDialog settingsDialog = new JdotxtPreferencesDialog();

		settingsDialog.addPreferenceFilterChangeListener(new PreferenceFilterChangeListener());

		// Backup settings before the dialog is shown
		String currentPath = Jdotxt.userPrefs.get("dataDir", Jdotxt.DEFAULT_DIR);
		boolean currentCompactMode = Jdotxt.userPrefs.getBoolean("compactMode", false);
		boolean currentAutoSave = Jdotxt.userPrefs.getBoolean("autosave", false);
		
		settingsDialog.setVisible(true);
        toolbar.refreshSettingsToggles();
		
		// Settings after the dialog was closed
		String newPath = Jdotxt.userPrefs.get("dataDir", Jdotxt.DEFAULT_DIR);
		boolean newCompactMode = Jdotxt.userPrefs.getBoolean("compactMode", false);
		boolean prependMetadata = Jdotxt.userPrefs.getBoolean("prependMetadata", false);
		boolean copyMetadata = Jdotxt.userPrefs.getBoolean("copyMetadata", false);
		boolean autoSave = Jdotxt.userPrefs.getBoolean("autosave", false);
		// Update stuff, according to the new settings
		taskList.setPrependMetadata(prependMetadata);
		taskList.setCopyProjectsContexts2NewTask(copyMetadata);
		filterPanel.setVisible(calculateVisibility());
		filterPanel.setSwitchPanels(Jdotxt.userPrefs.getBoolean("switchPanels", false));
		if (!currentPath.equals(newPath)) {
			reloadTasks();
			addLocation(newPath);
			toolbar.getFileLocationCombobox().setValues(locations);
			toolbar.getFileLocationCombobox().setSelectedItem(newPath);
		}
		else if (currentCompactMode != newCompactMode) refreshGUI();
		if (currentAutoSave != autoSave) {
			if (!autoSave) taskList.removeTaskListener(autoSaveListener);
			else {
				saveTasks(true);
				taskList.addTaskListener(autoSaveListener);
			}
		}
		//toolbar.setVisibleSaveReload(!Jdotxt.userPrefs.getBoolean("autosave", false));
	}
	
    // Let the GUI elements know of newly loaded tasks
	public void setTaskBag(TaskBag taskBag) {
		this.taskBag = taskBag;
		filterPanel.setTaskBag(taskBag);
		taskList.setTaskBag(taskBag);
		filterPanel.updateFilterPanes();
		taskList.updateTaskList();
		toolbar.setEnabled(true);
		toolbar.getButtonSave().setEnabled(taskBag.hasChanged());
		setDefaultStatusText();
	}
	
	// Fonts and stuff
	public static void loadLookAndFeel(String language) {
		fontR  = new Font("Ubuntu", Font.PLAIN, 14);
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
	
	private short calculateVisibility() { 
		short visibility = 0;
		if (Jdotxt.userPrefs.getBoolean("showProjectsPanel", true)) visibility = JdotxtFilterPanel.VISIBILITY_PROJECTS;
		if (Jdotxt.userPrefs.getBoolean("showContextsPanel", true)) visibility = (short) (visibility + JdotxtFilterPanel.VISIBILITY_CONTEXTS);
		return visibility;
	}

	private String saveSort(String name, Map<Sorters, Boolean> sort) {
		if (savedSorts.containsKey(name))
			return "Saved sort with such name already exists";
		savedSorts.put(name, sort);
        writeSorts();
		return "OK";
	}

	private void writeSorts() {
        toolbar.getSavedSortCombobox().setValues(savedSorts.keySet());
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Map<Sorters, Boolean>> e : savedSorts.entrySet()) {
            sb.append(e.getKey());
            sb.append(';');
            sb.append(SortUtils.writeSort(new ArrayList<>(e.getValue().entrySet())));
            sb.append(System.lineSeparator());
        }
        Jdotxt.userPrefs.put("savedSorts", sb.toString());
    }

	private void loadSorts() {
		String sorts = Jdotxt.userPrefs.get("savedSorts", "");
		if (sorts == null || "".equals(sorts)) {
			this.savedSorts = new HashMap<>();
			return;
		}
		String[] savedArray = sorts.split(System.lineSeparator());
		for (String saved : savedArray) {
			String[] nameAndSort = saved.split(";");
			Map<Sorters, Boolean> sort = SortUtils.parseSort(nameAndSort[1]);
			this.savedSorts.put(nameAndSort[0], sort);
		}
	}

	private void addLocation(String location) {
		locations.remove(location);
		locations.push(location);
		while (locations.size() > 10)
			locations.removeLast();
		writeLocations();
	}

	private void loadLocations() {
	    String locations = Jdotxt.userPrefs.get("recentlyUsed", "");
        if (locations == null || "".equals(locations)) {
            this.locations.clear();
            return;
        }
        this.locations = new LinkedList<>();
        this.locations.addAll(Arrays.asList(locations.split(";")));
    }

    private void writeLocations() {
	    if (locations.isEmpty()) {
            Jdotxt.userPrefs.put("recentlyUsed", "");
            return;
        }
	    StringBuilder sb = new StringBuilder(locations.get(0));
	    for (int i = 1; i < locations.size(); i++) {
	        sb.append(";");
	        sb.append(locations.get(i));
        }
        Jdotxt.userPrefs.put("recentlyUsed", sb.toString());
    }
	
	// File operations can be done in a separate thread, so that the GUI thread is not blocked by file IO
	private class TaskLoader implements Runnable {
		private short mode = REFRESH_GUI;
		
		public static final short REFRESH_GUI = 0;
		public static final short RELOAD = 1;
		public static final short ARCHIVE = 2;
		
		public TaskLoader(short mode){
			super();
			this.mode = mode;
		}
		
		public void run() {
			if (mode == ARCHIVE) Jdotxt.archiveTodos();
			if (mode != REFRESH_GUI) Jdotxt.loadTodos();
			EventQueue.invokeLater(new Runnable() { public void run() { setTaskBag(Jdotxt.taskBag); } });
		}
	}
	
	// Custom KeyDispatcher for global shortcuts
	private class KeyDispatcher implements KeyEventDispatcher {
		public boolean dispatchKeyEvent(KeyEvent e) {
	        if(e.getID() == KeyEvent.KEY_PRESSED) {
	        	if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_S) saveTasks(false);
	        	if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_R) reloadTasks(); // Reload tasks
	        	if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_F) toolbar.getTextfieldSearch().requestFocus(); // Jump to search bar
	        	if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_N) {
	        		toolbar.getTextfieldSearch().clearSearch();
	        		tasksPane.getVerticalScrollBar().setValue(0);
	        		taskList.requestFocusNewTask(); // New task
	        	}
				if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_D) {
					toolbar.switchTodoFile();
				}
	        }
	        //Allow the event to be redispatched
	        return false;
	    }
	}
	
	// What to do when someone enters some search String
	private class SearchListener implements DocumentListener {
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
			JdotxtGUI.this.forwardFilter2TaskList();
			setDefaultStatusText();
		}
	}
	
    // After a task has been updated: update the status bar
	private class StatusUpdateListener implements TaskListener {
		@Override
		public void taskCreated(Task t) { setDefaultStatusText(); toolbar.getButtonSave().setEnabled(taskBag.hasChanged());}
		@Override
		public void taskUpdated(Task t, short field) { setDefaultStatusText(); toolbar.getButtonSave().setEnabled(taskBag.hasChanged());}
		@Override
		public void taskDeleted(Task t) { setDefaultStatusText(); toolbar.getButtonSave().setEnabled(taskBag.hasChanged());}
		@Override
		public void enterPressed(Task t, short field) { }
		@Override
		public void focusLost(Task t, short field) { }
		@Override
		public void focusGained(Task t, short field) { }
	}
	
	// After a task has been updated: update the filter panes (maybe some projects/contexts have changed, or have been added/removed)
	private class FilterUpdateListener implements TaskListener {
		@Override
		public void taskCreated(Task t) { filterPanel.updateFilterPanes(); }
		@Override
		public void taskUpdated(Task t, short field) { if (field != JdotxtTaskPanel.CONTENT) filterPanel.updateFilterPanes(); }
		@Override
		public void taskDeleted(Task t) { filterPanel.updateFilterPanes(); }
		@Override
		public void enterPressed(Task t, short field) { filterPanel.updateFilterPanes(); }
		@Override
		public void focusLost(Task t, short field) { filterPanel.updateFilterPanes(); }
		@Override
		public void focusGained(Task t, short field) { }
	}
	
	// After a task has been updated: Trigger saving to file if autosave is activated.
	private class AutoSaveListener implements TaskListener {
		@Override
		public void taskCreated(Task t) { autoSaver.triggerAction(); }
		@Override
		public void taskUpdated(Task t, short field) { autoSaver.triggerAction(); }
		@Override
		public void taskDeleted(Task t) { autoSaver.triggerAction(); }
		@Override
		public void enterPressed(Task t, short field) { }
		@Override
		public void focusLost(Task t, short field) { }
		@Override
		public void focusGained(Task t, short field) { }
	}
	
	// After someone has selected some filters
	public class MyFilterChangeListener implements com.chschmid.jdotxt.gui.controls.JdotxtFilterPanel.FilterChangeListener {
		@Override
		public void filterChanged(ArrayList<String> filterContexts, ArrayList<String> filterProjects) {
			JdotxtGUI.this.filterContexts = filterContexts;
			JdotxtGUI.this.filterProjects = filterProjects;
			JdotxtGUI.this.forwardFilter2TaskList();
			setDefaultStatusText();
		}
	}

	public class PreferenceFilterChangeListener implements JdotxtPreferencesDialog.PreferencesFilterChangeListener {
		@Override
		public void preferenceFilterChanged(boolean showHidden, boolean showThreshold) {
			JdotxtGUI.this.showHidden = showHidden;
			JdotxtGUI.this.showThreshold = showThreshold;
			JdotxtGUI.this.forwardFilter2TaskList();
		}
	}
	
	// Modified MouseWheelListener to allow single line scrolling
	public class MyMouseWheelListener implements MouseWheelListener {
		private MouseWheelListener[] originalListeners;
		private int scrollAmount = 0;
		
		public MyMouseWheelListener(MouseWheelListener[] originalListeners, int scrollAmount) {
			this.originalListeners = originalListeners;
			this.scrollAmount = scrollAmount;
		}
		
		@Override
		public void mouseWheelMoved(MouseWheelEvent originalEvent) {
			MouseWheelEvent event = new MouseWheelEvent( originalEvent.getComponent(),
					                                     originalEvent.getID(),
					                                     originalEvent.getWhen(),
					                                     originalEvent.getModifiers(),
					                                     originalEvent.getX(),
					                                     originalEvent.getY(),
					                                     originalEvent.getXOnScreen(),
					                                     originalEvent.getYOnScreen(),
					                                     originalEvent.getClickCount(),
					                                     originalEvent.isPopupTrigger(),
					                                     originalEvent.getScrollType(),
					                                     scrollAmount,
					                                     originalEvent.getWheelRotation());
			for (MouseWheelListener listener : originalListeners) listener.mouseWheelMoved(event);
		}
	}
}
