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

package com.chschmid.jdotxt.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.ArrayList;

public class FileModifiedWatcher {
	private File file;
	private WatchKey key;
	
	private final WatchService watcher;
	private ArrayList<FileModifiedListener> fileModifiedListenerList = new ArrayList<FileModifiedListener>();
	
	private boolean processing = false;
	private Thread t;
	
	public FileModifiedWatcher() throws IOException {
		watcher  = FileSystems.getDefault().newWatchService();
		file = null;
		key  = null;
	}
	
	public File registerFile(File file) throws IOException {
		File oldFile = this.file;
		
		if (key != null) key.cancel();
		Path path = file.getParentFile().toPath();
		
		key = path.register(watcher, StandardWatchEventKinds.ENTRY_MODIFY, StandardWatchEventKinds.ENTRY_CREATE);
	    this.file = file;
		return oldFile;
	}
	
	public File unRegisterFile() {
		File oldfile = file;
		if (key != null) key.cancel();
		key  = null;
		file = null;
		return oldfile;
	}
	
	public File getFile() { return file; }
	
	public void addFileModifiedListener(FileModifiedListener fileModifiedListener) { fileModifiedListenerList.add(fileModifiedListener); }
	public void removeFileModifiedListener(FileModifiedListener fileModifiedListener) { fileModifiedListenerList.remove(fileModifiedListener); }
	
	private void fireFileModified() {
		for (int i = fileModifiedListenerList.size()-1; i >= 0; i--) fileModifiedListenerList.get(i).fileModified();
	}
	
	public synchronized void startProcessingEvents() {
		if (processing) return;
		t = new Thread(new Watcher());
		processing = true;
		t.start();
	}
	
	public synchronized void stopProcessingEvents() {
		if (!processing) return;
		processing = false;
		t.interrupt();
	    try {
			t.join();
		} catch (InterruptedException e) {
		}
	}
	
	private class Watcher implements Runnable {
		@Override
		public void run() {
			WatchKey key;
	        while(processing) { 
	            try {
	                key = watcher.take();
		            for (WatchEvent<?> event: key.pollEvents()) {
		                if (event.kind() == StandardWatchEventKinds.ENTRY_MODIFY || event.kind() == StandardWatchEventKinds.ENTRY_CREATE) {
		                	if (file != null && event.context().toString().equals(file.getName())) fireFileModified();
		                }
		            }
		            key.reset();
	            } catch (InterruptedException x) {
	            }
	        }
		}	
	}
}
