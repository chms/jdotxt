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

import com.chschmid.jdotxt.gui.JdotxtGUI;
import com.todotxt.todotxttouch.task.TaskBag;
import com.todotxt.todotxttouch.util.Util;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class JdotxtFilterPanel extends JPanel {
	public static final short  VISIBILITY_NONE     = 0;
	public static final short  VISIBILITY_PROJECTS = 1;
	public static final short  VISIBILITY_CONTEXTS = 2;
	public static final short  VISIBILITY_ALL      = VISIBILITY_PROJECTS + VISIBILITY_CONTEXTS;
	
	public static final int DEFAULT_WIDTH = 200;
	
	private TaskBag taskBag;
	
	private JScrollPane pane1 = new JScrollPane();
	private JScrollPane pane2 = new JScrollPane();
	
	private JList<String> loading1 = new JList<String>();
	private JList<String> loading2 = new JList<String>();
	
	private JList<String> projects = new JList<String>();
	private JList<String> contexts = new JList<String>();
	private String matchProjects = "";
	private String matchContexts = "";
	
	private ArrayList<String> filterContexts = new ArrayList<String>();
	private ArrayList<String> filterProjects = new ArrayList<String>();
	
	private FilterSelectionListener projectsListener, contextsListener;
	
	private ArrayList<FilterChangeListener> filterChangeListenerList = new ArrayList<FilterChangeListener>();
	private ArrayList<TaskBagUpdatedListener> taskBagUpdatedListenerList = new ArrayList<TaskBagUpdatedListener>();
	
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
		projects.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent keyEvent) {
				if (!isPrintableChar(keyEvent.getKeyChar()))
					return;
				final FilterField f = new FilterField(keyEvent.getKeyChar()) {
					@Override
					public void onType(String typed) {
						matchProjects = typed;
						forceUpdateFilterPanes();
						if (projects.getModel().getSize() >= 3)
							projects.setSelectedIndex(2);
					}

					@Override
					public void onFinish() {
						String itemToSelect = projects.getSelectedValue();
						matchProjects = "";
						forceUpdateFilterPanes();
						if (itemToSelect != null)
							projects.setSelectedValue(itemToSelect, true);
						dispose();
					}

					@Override
					public void selectNext() {
						int i = projects.getSelectedIndex();
						if (i + 1 < projects.getModel().getSize())
							projects.setSelectedIndex(i + 1);
					}

					@Override
					public void selectPrev() {
						int i = projects.getSelectedIndex();
						if (i > 0)
							projects.setSelectedIndex(i-1);
					}
				};
				f.setLocation(projects.getLocationOnScreen());
				f.setVisible(true);
			}
		});
		projects.addMouseListener(new ListMouseListener(this, ListMouseListener.PROJECT));

		contexts.setFont(JdotxtGUI.fontR);
		contexts.setSelectionBackground(JdotxtGUI.COLOR_PRESSED);
		contexts.setCellRenderer(new FilterCellRenderer());
		contextsListener = new FilterSelectionListener(contexts, filterContexts);
		contexts.addListSelectionListener(contextsListener);
		contexts.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent keyEvent) {
				if (!isPrintableChar(keyEvent.getKeyChar()))
					return;
				final FilterField f = new FilterField(keyEvent.getKeyChar()) {
					@Override
					public void onType(String typed) {
						matchContexts = typed;
						forceUpdateFilterPanes();
						if (contexts.getModel().getSize() >= 3) {
							contexts.setSelectedIndex(2);
						}
					}

					@Override
					public void onFinish() {
						String itemToSelect = contexts.getSelectedValue();
						matchContexts = "";
						forceUpdateFilterPanes();
						if (itemToSelect != null)
							contexts.setSelectedValue(itemToSelect, true);
						dispose();
					}

					@Override
					public void selectNext() {
						int i = contexts.getSelectedIndex();
						if (i + 1 < contexts.getModel().getSize())
							contexts.setSelectedIndex(i + 1);
					}

					@Override
					public void selectPrev() {
						int i = contexts.getSelectedIndex();
						if (i > 0)
							contexts.setSelectedIndex(i-1);
					}
				};
				f.setLocation(contexts.getLocationOnScreen());
				f.setVisible(true);
			}
		});
		contexts.addMouseListener(new ListMouseListener(this, ListMouseListener.CONTEXT));

		String[] loadingString = new String[1];
	    loadingString[0] = JdotxtGUI.lang.getWord("Loading...");
	    loading1.setListData(loadingString);
	    loading1.setFont(JdotxtGUI.fontRI);
	    loading1.setEnabled(false);
	    loading2.setListData(loadingString);
	    loading2.setFont(JdotxtGUI.fontRI);
	    loading2.setEnabled(false);
		
		pane1.setBorder(BorderFactory.createEmptyBorder());
		pane1.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		pane1.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		pane1.setViewportBorder(null);
		pane1.getVerticalScrollBar().setBackground(Color.WHITE);
		pane1.getVerticalScrollBar().setOpaque(true);
		
		pane2.setBorder(BorderFactory.createEmptyBorder());
		pane2.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		pane2.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		pane2.setViewportBorder(null);
		pane2.getVerticalScrollBar().setBackground(Color.WHITE);
		pane2.getVerticalScrollBar().setOpaque(true);
		
		initPaneLayout();
		reset();
	}

	public boolean isPrintableChar( char c ) {
		Character.UnicodeBlock block = Character.UnicodeBlock.of( c );
		return (!Character.isISOControl(c)) &&
				c != KeyEvent.CHAR_UNDEFINED &&
				block != null &&
				block != Character.UnicodeBlock.SPECIALS;
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
		
		ArrayList<String> myProjects = new ArrayList<>();

		for (String e : taskBag.getProjects(false)) {
			if (e.toLowerCase().contains(matchProjects.toLowerCase())) {
				myProjects.add(e);
			}
		}

		ArrayList<String> myContexts = new ArrayList<>();

		for (String e : taskBag.getContexts(false)) {
			if (e.toLowerCase().contains(matchContexts.toLowerCase())) {
				myContexts.add(e);
			}
		}
		
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
	
	public interface FilterChangeListener {
		public void filterChanged(ArrayList<String> filterContexts,
		                          ArrayList<String> filterProjects);
	}
	public void addFilterChangeListener(FilterChangeListener filterChangeListener) {
		filterChangeListenerList.add(filterChangeListener);
	}
	public void removeFilterChangeListener(FilterChangeListener filterChangeListener) {
		filterChangeListenerList.remove(filterChangeListener);
	}
	private void fireFilterChange() {
		for (int i = filterChangeListenerList.size()-1; i >= 0; i--) {
			filterChangeListenerList.get(i).filterChanged(filterContexts, filterProjects);
		}
	}

	public interface TaskBagUpdatedListener {
		public void taskBagUpdated();
	}
	public void addTaskBagUpdatedListener(TaskBagUpdatedListener taskBagUpdatedListener) {
		taskBagUpdatedListenerList.add(taskBagUpdatedListener);
	}
	public void removeTaskBagUpdatedListener(TaskBagUpdatedListener taskBagUpdatedListener) {
		taskBagUpdatedListenerList.remove(taskBagUpdatedListener);
	}
	private void fireTaskBagUpdated() {
		for (int i = taskBagUpdatedListenerList.size()-1; i >= 0; i--) {
			taskBagUpdatedListenerList.get(i).taskBagUpdated();
		}
	}

	abstract class FilterField extends JDialog {
		JTextField jtf;

		public FilterField(char first) {
			initGUI();
			jtf.setText(String.valueOf(first));
			refresh();
			onType(jtf.getText());
		}

		protected void initGUI() {
			setUndecorated(true);
			jtf = new JTextField();
			jtf.addKeyListener(new KeyAdapter() {
				@Override
				public void keyReleased(KeyEvent keyEvent) {
					if (keyEvent.getKeyCode() == KeyEvent.VK_ESCAPE || keyEvent.getKeyCode() == KeyEvent.VK_ENTER) {
						onFinish();
					} else if (keyEvent.getKeyCode() == KeyEvent.VK_UP) {
						selectPrev();
					} else if (keyEvent.getKeyCode() == KeyEvent.VK_DOWN) {
						selectNext();
					} else {
						onType(jtf.getText());
						refresh();
					}
				}
			});
			jtf.setMinimumSize(new Dimension(100, 20));
			add(jtf, BorderLayout.CENTER);
			jtf.addFocusListener(new FocusAdapter() {
				@Override
				public void focusLost(FocusEvent focusEvent) {
					onFinish();
				}
			});
		}

		private void refresh() {
			jtf.updateUI();
			pack();
			jtf.setCaretPosition(jtf.getText().length());
		}

		public abstract void onType(String typed);
		public abstract void onFinish();
		public abstract void selectNext();
		public abstract void selectPrev();
	}

	class ListMouseListener extends MouseAdapter {

		public final static int PROJECT = 1;
		public final static int CONTEXT = 2;

		private JList list;
		private int kind;
		private JdotxtFilterPanel filterPanel;
		private String prefixCharacter;

		public ListMouseListener(JdotxtFilterPanel filterPanel, int kind) {
			this.filterPanel = filterPanel;
			this.kind = kind;
			if (kind == PROJECT) {
				this.list = filterPanel.projects;
				this.prefixCharacter = "+";
			} else if (kind == CONTEXT) {
				this.list = filterPanel.contexts;
				this.prefixCharacter = "@";
			}
		}

		@Override
		public void mousePressed(MouseEvent e) {
			showPopup(e);
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			showPopup(e);
		}

		private void showPopup(MouseEvent e) {
			if (!e.isPopupTrigger()) {
				return;
			}

			int clicked = list.locationToIndex(e.getPoint());
			if(clicked != -1 && list.getCellBounds(clicked, clicked).contains(e.getPoint())) {
				list.setSelectedIndex(clicked);

				JPopupMenu menu = new JPopupMenu();

				menu.add(new AbstractAction("Rename"){
					public void actionPerformed(ActionEvent e) {
						String projectName = (String)list.getModel().getElementAt(clicked);
						String newName = JOptionPane.showInputDialog(
								filterPanel,
								"Rename " + projectName + " to:");
						if (newName == null) {
							return;
						}
						if (newName.equals("")) {
							int r = JOptionPane.showConfirmDialog(
									filterPanel,
									"Are you sure? This will delete tag from all entries!");
							if (r != JOptionPane.OK_OPTION) {
								return;
							}
						} else {
							newName = prefixCharacter + newName;
						}
						filterPanel.taskBag.renameProject(projectName, newName);
						filterPanel.forceUpdateFilterPanes();
						filterPanel.fireTaskBagUpdated();
					}
				});
				menu.show(list, e.getX(), e.getY());
			}
		}
	}
}
