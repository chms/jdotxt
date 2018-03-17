package com.chschmid.jdotxt.gui.controls;

import javax.swing.*;
import java.util.Collection;
import java.util.Vector;

public class JdotxtSavedSortCombobox extends JComboBox<String> {
    private String placeholder = "Select a predefined sort";
    private Vector<String> sorts = new Vector<>();
    private boolean firstSelect = true;
    public JdotxtSavedSortCombobox() {
        super();
        initGUI();
    }

    private void initGUI() {
        setSelectedItem(placeholder);
        setMaximumSize(getPreferredSize());
    }

    public void setSorts(Collection<String> newSorts) {
        sorts.clear();
        sorts.addAll(newSorts);
        Vector<String> toShow = new Vector<>(newSorts);
        toShow.add("Manage...");
        setModel(new PlaceholderComboBoxModel<>(placeholder, toShow));
    }

    class PlaceholderComboBoxModel<E> extends DefaultComboBoxModel<E> {

        public PlaceholderComboBoxModel(String placeholder, Vector<E> list) {
            super(list);
            this.placeholder = placeholder;
        }

        private String placeholder;

        @Override
        public void setSelectedItem(Object o) {
            if (o == null) {
                super.setSelectedItem(placeholder);
            } else if (!placeholder.equals(o)) {
                super.setSelectedItem(o);
            } else if (firstSelect) {
                firstSelect = false;
                super.setSelectedItem(o);
            }
        }
    }
}
