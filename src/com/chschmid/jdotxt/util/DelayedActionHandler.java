package com.chschmid.jdotxt.util;

import java.awt.event.ActionListener;

public class DelayedActionHandler {
	private ActionListener action;
	private Thread t;
	private int delayMillis;
	private long triggerTime;
	
	private boolean isRunning;
	
	public DelayedActionHandler(int delayMillis, ActionListener action) {
		this.delayMillis = delayMillis;
		this.action = action;
		triggerTime = 0;
		isRunning   = true;
		initThread();
	}
	
	private void initThread() {
		t = new Thread(new DelayRunnable());
		t.start();
	}
	
	public void triggerAction() {
		synchronized (t) {
			triggerTime = System.currentTimeMillis() + delayMillis;
			t.notify();
		}
	}
	
	public void close() {
		synchronized (t) {
			isRunning = false;
			t.notify();
		}
	}
	
	private class DelayRunnable implements Runnable {
		@Override
		public void run() {
			while (isRunning) {
				synchronized (t) {
					long now = System.currentTimeMillis();
					if (now >= triggerTime && triggerTime != 0) {
						action.actionPerformed(null);
						triggerTime = 0;
					} else if (triggerTime == 0) {
						try {
							t.wait();
						} catch (InterruptedException e) { }
					} else {
						try {
							t.wait(triggerTime - now);
						} catch (InterruptedException e) { }
					}
				}
			}
		}
	}
}
