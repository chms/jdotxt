package com.chschmid.jdotxt.gui.controls;

import javax.swing.*;
import java.util.Collection;
import java.util.Vector;

public class JdotxtCombobox extends JComboBox<String> {
    private String placeholder;
    private Vector<String> sorts = new Vector<>();
    private boolean firstSelect = true;
    private String more = "More...";

    public JdotxtCombobox(String placeholder) {
        super();
        this.placeholder = placeholder;
        initGUI();
    }

    public JdotxtCombobox(String placeholder, String more) {
        super();
        this.placeholder = placeholder;
        this.more = more;
        initGUI();
    }

    private void initGUI() {
        setSelectedItem(placeholder);
        setMaximumSize(getPreferredSize());
    }

    public void setValues(Collection<String> newValues) {
        sorts.clear();
        sorts.addAll(newValues);
        Vector<String> toShow = new Vector<>(newValues);
        toShow.add(more);
        setModel(new PlaceholderComboBoxModel<>(placeholder, toShow));
    }

    public String getMore() {
        return more;
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
