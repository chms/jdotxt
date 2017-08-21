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

package com.chschmid.jdotxt.gui.controls;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.chschmid.jdotxt.gui.JdotxtGUI;
import com.todotxt.todotxttouch.task.TaskBag;
import com.todotxt.todotxttouch.util.Util;

@SuppressWarnings("serial")
public class JdotxtFilterPanel extends JPanel {
	public static final short  VISIBILITY_NONE     = 0;
	public static final short  VISIBILITY_PROJECTS = 1;
	public static final short  VISIBILITY_CONTEXTS = 2;
	public static final short  VISIBILITY_ALL      = VISIBILITY_PROJECTS + VISIBILITY_CONTEXTS;
	
	public static final int DEFAULT_WIDTH = 350;
	
	private TaskBag taskBag;
	
	private JScrollPane pane1 = new JScrollPane();
	private JScrollPane pane2 = new JScrollPane();
	
	private JList<String> loading1 = new JList<String>();
	private JList<String> loading2 = new JList<String>();
	
	private JList<String> projects = new JList<String>();
	private JList<String> contexts = new JList<String>();
	
	private ArrayList<String> filterContexts = new ArrayList<String>();
	private ArrayList<String> filterProjects = new ArrayList<String>();
	
	private FilterSelectionListener projectsListener, contextsListener;
	
	private ArrayList<FilterChangeListener> filterChangeListenerList = new ArrayList<FilterChangeListener>();
	
	private boolean switchPanels = false;
	private short visibility = VISIBILITY_ALL;
	
	public JdotxtFilterPanel() {
		super(new GridLayout(1, 2));
		initFilterPanel();
	}
	
	private void initFilterPanel() {	
		projects.setFont(JdotxtGUI.fontR);
		projects.setSelectionBackground(JdotxtGUI.COLOR_PRESSED);
		projects.setCellRenderer(new FilterCellRenderer());
		projectsListener = new FilterSelectionListener(projects, filterProjects);
		projects.addListSelectionListener(projectsListener);
		
		contexts.setFont(JdotxtGUI.fontR);
		contexts.setSelectionBackground(JdotxtGUI.COLOR_PRESSED);
		contexts.setCellRenderer(new FilterCellRenderer());
		contextsListener = new FilterSelectionListener(contexts, filterContexts);
		contexts.addListSelectionListener(contextsListener);
		
		String[] loadingString = new String[1];
	    loadingString[0] = JdotxtGUI.lang.getWord("Loading...");
	    loading1.setListData(loadingString);
	    loading1.setFont(JdotxtGUI.fontRI);
	    loading1.setEnabled(false);
	    loading2.setListData(loadingString);
	    loading2.setFont(JdotxtGUI.fontRI);
	    loading2.setEnabled(false);
		
		pane1.setBorder(BorderFactory.createEmptyBorder());
		pane1.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		pane1.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		pane1.setViewportBorder(null);
		pane1.getVerticalScrollBar().setBackground(Color.WHITE);
		pane1.getVerticalScrollBar().setOpaque(true);
		
		pane2.setBorder(BorderFactory.createEmptyBorder());
		pane2.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		pane2.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		pane2.setViewportBorder(null);
		pane2.getVerticalScrollBar().setBackground(Color.WHITE);
		pane2.getVerticalScrollBar().setOpaque(true);
		
		initPaneLayout();
		reset();
	}
	
	public void setTaskBag(TaskBag taskBag) {
		this.taskBag = taskBag;
	}
	
	public void reset() {
		pane1.setViewportView(loading1);
		pane2.setViewportView(loading2);
		projects.clearSelection();
		contexts.clearSelection();
		projects.setListData(new String[0]);
		contexts.setListData(new String[0]);
	}
	
	public void setVisible(short visibility) {
		if (this.visibility == visibility) return;
		this.visibility = visibility;
		initPaneLayout();
	}
	
	private void initPaneLayout() {
		int nWidth = 0;
		this.removeAll();
		
		
		if (visibility == VISIBILITY_NONE) this.setVisible(false);
		else this.setVisible(true);
		
		if (switchPanels) {
			if ((visibility & VISIBILITY_CONTEXTS) != 0) {
				this.add(pane2); nWidth = nWidth + DEFAULT_WIDTH;
			}
			if ((visibility & VISIBILITY_PROJECTS) != 0) {
				this.add(pane1); nWidth = nWidth + DEFAULT_WIDTH;
			}
		} else {
			if ((visibility & VISIBILITY_PROJECTS) != 0) {
				this.add(pane1); nWidth = nWidth + DEFAULT_WIDTH;
			}
			if ((visibility & VISIBILITY_CONTEXTS) != 0) {
				this.add(pane2); nWidth = nWidth + DEFAULT_WIDTH;
			}
		}
		
		setPreferredSize(new Dimension(nWidth,100));
		revalidate();
		repaint();
	}
	
	public void setSwitchPanels(boolean switchPanels) {
		if (this.switchPanels == switchPanels) return;
		this.switchPanels = switchPanels;
		initPaneLayout();
	}
	
	public void addFilterChangeListener(FilterChangeListener filterChangeListener) { filterChangeListenerList.add(filterChangeListener); }
	public void removeFilterChangeListener(FilterChangeListener filterChangeListener) { filterChangeListenerList.remove(filterChangeListener); }
	
	public boolean isFilterPaneUpToDate() {
		ArrayList<String> myProjects = taskBag.getProjects(false);
		ArrayList<String> myContexts = taskBag.getContexts(false);
		
		if ((projects.getModel().getSize() != myProjects.size() + 2) || (contexts.getModel().getSize() != myContexts.size() + 2)) return false;

		for (int k1 = 0; k1 < myProjects.size(); k1++) if (!projects.getModel().getElementAt(k1+2).substring(1).equals(myProjects.get(k1))) return false;
		for (int k1 = 0; k1 < myContexts.size(); k1++) if (!contexts.getModel().getElementAt(k1+2).substring(1).equals(myContexts.get(k1))) return false;
		
		return true;
	}
	
	public void updateFilterPanes() { if (!isFilterPaneUpToDate()) forceUpdateFilterPanes(); }
	
	private void forceUpdateFilterPanes() {
		projects.removeListSelectionListener(projectsListener);
		contexts.removeListSelectionListener(contextsListener);
		
		List<String> selectedProjects = (List<String>) projects.getSelectedValuesList();
		List<String> selectedContexts = (List<String>) contexts.getSelectedValuesList();
		
		ArrayList<String> myProjects = taskBag.getProjects(false);
		ArrayList<String> myContexts = taskBag.getContexts(false);
		
		Util.prependString(myProjects, "+");
		Util.prependString(myContexts, "@");
		
		myProjects.add(0, JdotxtGUI.lang.getWord("Uncategorized"));
		myProjects.add(0, JdotxtGUI.lang.getWord("All_projects"));
		
		myContexts.add(0, JdotxtGUI.lang.getWord("Uncategorized"));
		myContexts.add(0, JdotxtGUI.lang.getWord("All_contexts"));
		
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
		
		if(pane1.getViewport().getView() != projects) {
			pane1.setViewportView(projects);
			pane2.setViewportView(contexts);
		}
		
		if (selectedProjectsIndices.size() == 0) projectsListener.forceValueChanged();
		else if (selectedContextsIndices.size() == 0) contextsListener.forceValueChanged();
		
		projects.addListSelectionListener(projectsListener);
		contexts.addListSelectionListener(contextsListener);
	}
	
	private class FilterSelectionListener implements ListSelectionListener {
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
			
			if (selection.contains(JdotxtGUI.lang.getWord("All"))
				|| selection.contains(JdotxtGUI.lang.getWord("All_contexts"))
				|| selection.contains(JdotxtGUI.lang.getWord("All_projects"))) {
				if (selection.size() > 1) {
					ListModel<String> all = list.getModel();
					for (int k1 = 2; k1 < all.getSize(); k1++) {
						if (!selection.contains(all.getElementAt(k1))) filter.add(all.getElementAt(k1).substring(1));
					}
					if (filter.size() == 0) {
						if (!selection.contains(JdotxtGUI.lang.getWord("Uncategorized"))) filter.add("-");
						else filter.add("98jLpz+LfAH4JQ_lQJb0x"); // Make sure that no item is found when all items + "All" is selected
					}
				}
			}
			else {
				if (selection.contains(JdotxtGUI.lang.getWord("Uncategorized"))) {
					filter.add("-");
					selection.remove(JdotxtGUI.lang.getWord("Uncategorized"));
				}
				for (String s: selection) filter.add(s.substring(1));
			}
			fireFilterChange();
		}
	}
	
	private class FilterCellRenderer extends DefaultListCellRenderer {
		private static final long serialVersionUID = -7795431329110866824L;
		
		@Override
		public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
	        JLabel c = new JLabel(value.toString());
	        c.setBorder(new EmptyBorder(4, 4, 4, 4));
	        c.setOpaque(true);
	        
	        if (JdotxtGUI.lang.getWord("All").equals(value)
	        	|| JdotxtGUI.lang.getWord("All_contexts").equals(value)
	        	|| JdotxtGUI.lang.getWord("All_projects").equals(value)
	        	|| JdotxtGUI.lang.getWord("Uncategorized").equals(value)) {
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
	
	private void fireFilterChange() {
		for (int i = filterChangeListenerList.size()-1; i >= 0; i--) filterChangeListenerList.get(i).filterChanged(filterContexts, filterProjects);
	}
	
	public interface FilterChangeListener {
		public void filterChanged(ArrayList<String> filterContexts, ArrayList<String> filterProjects);
	}
}
