/**
* jdotxt
*
* Copyright (C) 2013-2014 Christian M. Schmid
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

package com.chschmid.jdotxt;

import java.awt.EventQueue;
import java.io.File;
import java.util.prefs.Preferences;

import com.chschmid.jdotxt.gui.JdotxtGUI;
import com.chschmid.jdotxt.gui.JdotxtWelcomeDialog;
import com.sun.jna.Function;
import com.sun.jna.NativeLibrary;
import com.sun.jna.NativeLong;
import com.sun.jna.WString;
import com.todotxt.todotxttouch.task.LocalFileTaskRepository;
import com.todotxt.todotxttouch.task.TaskBag;
import com.todotxt.todotxttouch.task.TaskBagFactory;

public class Jdotxt {
	public static final String VERSION = "0.3.9";
	public static final String APPID = "chschmid.jdotxt";
	
	public static TaskBag taskBag;
	public static Preferences userPrefs;
	
	public static final String DEFAULT_DIR = System.getProperty("user.home") + File.separator + "jdotxt";
	
	private static native NativeLong SetCurrentProcessExplicitAppUserModelID(WString appID);
	
	public static void main( String[] args )
	{
		loadPreferences();

		JdotxtGUI.loadLookAndFeel(userPrefs.get("lang", "English"));
		
		// Windows taskbar fix
		if (isWindows()) onWindows();
		
		// Mac OS X fixes
		if (isMacOSX()) onMacOSX();
		
		// Start GUI
		Runnable viewGUI = new Runnable() {
			@Override
			public void run() {
				// Show settings on first run
				if (userPrefs.getBoolean("firstRun", true)) {
					JdotxtWelcomeDialog welcomeDialog = new JdotxtWelcomeDialog();
					welcomeDialog.setVisible(true);
				}
				
				// Main window
				JdotxtGUI mainGUI = new JdotxtGUI();
				//JdotxtGUItest mainGUI = new JdotxtGUItest();
				mainGUI.setVisible(true);
			}
		};
		EventQueue.invokeLater(viewGUI);
	}
	
	public static void loadPreferences() { userPrefs = Preferences.userNodeForPackage(Jdotxt.class); }
	
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
	public static void archiveTodos() { taskBag.archive(); }
	
	// Detect OS
	public static boolean isWindows() {	return System.getProperty("os.name").startsWith("Windows"); }
	public static boolean isMacOSX() { return System.getProperty("os.name").startsWith("Mac OS X"); }
	
	public static void onWindows() {
		// Windows taskbar fix: provideAppUserModelID
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
	
	public static void onMacOSX() {
		// Nothing so far
	}
	
	public static String insertReplaceString(String original, String replace, int offset) {
		String a =  original.substring(0, Math.min(offset, original.length()));
		String b;
		if (original.length() > (offset + replace.length())) b = original.substring(offset + replace.length(), original.length());
		else b = "";
		return a + replace + b;
	  }
}
