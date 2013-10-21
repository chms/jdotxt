package com.chschmid.jdotxt;

import java.awt.EventQueue;
import java.io.File;
import java.util.prefs.Preferences;

import com.chschmid.jdotxt.gui.JdotxtGUI;
import com.chschmid.jdotxt.gui.JdotxtSettingsDialog;
import com.sun.jna.Function;
import com.sun.jna.NativeLibrary;
import com.sun.jna.NativeLong;
import com.sun.jna.WString;
import com.todotxt.todotxttouch.task.LocalFileTaskRepository;
import com.todotxt.todotxttouch.task.TaskBag;
import com.todotxt.todotxttouch.task.TaskBagFactory;

public class Jdotxt {
	public static final String VERSION = "0.1.6";
	public static final String APPID = "chschmid.jdotxt";
	
	public static TaskBag taskBag;
	public static Preferences userPrefs;
	
	public static final String DEFAULT_DIR = System.getProperty("user.home") + File.separator + "jdotxt";
	
	private static native NativeLong SetCurrentProcessExplicitAppUserModelID(WString appID);
	private static boolean firstRun;
	
	public static void main( String[] args )
	{
		loadPreferences();
		JdotxtGUI.loadLookAndFeel(userPrefs.get("lang", "English"));
		
		if (isWindows()) provideAppUserModelID();

		if (firstRun) {
			JdotxtSettingsDialog settingsDialog = new JdotxtSettingsDialog();
			settingsDialog.setVisible(true);
		}

		Runnable viewGUI = new Runnable() {
			@Override
			public void run() {
				JdotxtGUI mainGUI = new JdotxtGUI();
				mainGUI.setVisible(true);
			}
		};

		EventQueue.invokeLater(viewGUI);
	}
	
	public static void loadTodos() {
		String dataDir  = userPrefs.get("dataDir", DEFAULT_DIR);
		String todoFile = dataDir + File.separator + "todo.txt";
		String doneFile = dataDir + File.separator + "done.txt";
		
		LocalFileTaskRepository.TODO_TXT_FILE = new File(todoFile);
		LocalFileTaskRepository.DONE_TXT_FILE = new File(doneFile);
		LocalFileTaskRepository.initFiles();
		taskBag = TaskBagFactory.getTaskBag();
		taskBag.reload();
	}
	
	public static void archiveTodos() {
		taskBag.archive();
	}
	
	public static void loadPreferences() {
		userPrefs = Preferences.userNodeForPackage(Jdotxt.class);
		firstRun = userPrefs.getBoolean("firstRun", true);
	}
	
	public static void provideAppUserModelID() {
		try {
			NativeLibrary lib = NativeLibrary.getInstance("shell32");
		    Function function = lib.getFunction("SetCurrentProcessExplicitAppUserModelID");
		    Object[] args = {new WString(APPID)}; 
		    function.invokeInt(args);
		} catch (Error e) {
		    return;
		} catch (Exception x) {
			return;
		}
	}

	public static boolean isWindows() {	
		return System.getProperty("os.name").startsWith("Windows");
	}
}
