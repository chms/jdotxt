package com.chschmid.jdotxt.gui.controls;

import com.chschmid.jdotxt.gui.JdotxtGUI;
import com.todotxt.todotxttouch.task.sorter.Sorters;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class JdotxtSortDialog extends JDialog {

    Map<Sorters, Boolean> sort;
    private Vector<Vector> sortDisplay, addDisplay;
    private Vector<Map.Entry<Sorters, Boolean>> sortList = new Vector<>();
    private Vector<Sorters> addList = new Vector<>();

    private JTable current, add;
    private JButton saveButton;
    private JTextField name;
    private String nameStr;
    private JButton ok;
    private boolean edit;

    public JdotxtSortDialog(Map<Sorters, Boolean> sort, boolean edit, String name) {
        super();
        init(sort, edit, name);
    }

    public JdotxtSortDialog(Map<Sorters, Boolean> sort, boolean edit) {
        super();
        init(sort, edit, "");
    }

    private void init(Map<Sorters, Boolean> sort, boolean edit, String name) {
        this.sort = sort;
        this.edit = edit;
        this.nameStr = name;
        prepareData();
        initGUI();
    }

    private void prepareData() {
        sortDisplay = new Vector<>();
        for (Map.Entry<Sorters, Boolean> entry : sort.entrySet()) {
            Vector<Object> s = new Vector<>();
            s.add(entry.getKey().getName());
            s.add(entry.getValue() ? "▲" : "▼");
            s.add("↑");
            s.add("↓");
            s.add("✕");
            sortList.add(entry);
            sortDisplay.add(s);
        }

        addDisplay = new Vector<>();
        for (Sorters s : Sorters.values()) {
            Vector<Object> a = new Vector<>();
            if (sort.containsKey(s) || s == Sorters.ID)
                continue;
            a.add(s.getName());
            a.add("+");
            addList.add(s);
            addDisplay.add(a);
        }
    }

    private void initGUI() {
        this.setModal(true);
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.setTitle(JdotxtGUI.lang.getWord("jdotxt") + " " + JdotxtGUI.lang.getWord("Sort"));
        this.setResizable(false);

        Vector<String> cols = new Vector<>();
        cols.setSize(5);
        Collections.fill(cols, "");
        Box tables = new Box(BoxLayout.Y_AXIS);
        current = new JTable(new EditDisabledTableModel(sortDisplay, cols));
        current.setRowSelectionAllowed(false);
        current.setCellSelectionEnabled(false);
        current.setIntercellSpacing(new Dimension(0,0));
        current.getColumnModel().getColumn(1).setMaxWidth(17);
        current.getColumnModel().getColumn(2).setMaxWidth(17);
        current.getColumnModel().getColumn(3).setMaxWidth(17);
        current.getColumnModel().getColumn(4).setMaxWidth(17);
        current.setFont(new Font("Serif", Font.PLAIN, 14));
        current.setShowGrid(false);
        current.addMouseListener(createMouseListener(current, new ClickHandler() {
            @Override
            public void click(int row, int col) {
                if (col == 1) {
                    Boolean res = !sortList.get(row).getValue();
                    sortList.get(row).setValue(res);
                    sortDisplay.get(row).set(1, res ? "▲" : "▼");
                } else if (col == 2) {
                    if (row != 0) {
                        Vector sub = sortDisplay.remove(row);
                        sortDisplay.insertElementAt(sub, row - 1);
                        Map.Entry subE = sortList.remove(row);
                        sortList.insertElementAt(subE, row - 1);
                    }
                } else if (col == 3) {
                    if (row != sortDisplay.size() - 1) {
                        Vector sub = sortDisplay.remove(row);
                        sortDisplay.insertElementAt(sub, row + 1);
                        Map.Entry subE = sortList.remove(row);
                        sortList.insertElementAt(subE, row + 1);
                    }
                } else if (col == 4) {
                    sortDisplay.remove(row);
                    Map.Entry<Sorters, Boolean> e = sortList.remove(row);
                    addDisplay.add(new Vector(Arrays.asList(e.getKey().getName(), "+")));
                    addList.add(e.getKey());
                }
                current.updateUI();
                add.updateUI();
            }
        }));

        tables.add(Box.createRigidArea(new Dimension(0,15)));
        tables.add(new JLabel("Current sort"));
        tables.add(current);

        tables.add(Box.createRigidArea(new Dimension(0,15)));

        Box save = new Box(BoxLayout.X_AXIS);
        name = new JTextField();
        name.setText(nameStr);
        save.add(name);
        if (!edit) {
            saveButton = new JButton();
            saveButton.setText("Save");
            save.add(saveButton);
        }

        cols = new Vector<>();
        cols.setSize(2);
        Collections.fill(cols, "");
        add = new JTable(new EditDisabledTableModel(addDisplay, cols));
        add.setRowSelectionAllowed(false);
        add.setCellSelectionEnabled(false);
        add.setIntercellSpacing(new Dimension(0,0));
        add.getColumnModel().getColumn(1).setMaxWidth(17);
        add.setShowGrid(false);
        add.setFont(new Font("Serif", Font.PLAIN, 14));
        add.addMouseListener(createMouseListener(add, new ClickHandler() {
            @Override
            public void click(int row, int col) {
                if (col == 1) {
                    addDisplay.remove(row);
                    Sorters s = addList.remove(row);
                    sortDisplay.add(new Vector(Arrays.asList(s.getName(), "▲", "↑", "↓", "✕")));
                    sortList.add(new AbstractMap.SimpleEntry<>(s, true));
                }
                current.updateUI();
                add.updateUI();
            }
        }));
        tables.add(new JLabel("Available fields"));
        tables.add(add);
        tables.add(Box.createRigidArea(new Dimension(0,15)));
        tables.add(save);
        tables.add(Box.createRigidArea(new Dimension(0,15)));

        Box buttons = new Box(BoxLayout.X_AXIS);
        buttons.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 3));
        ok = new JButton(JdotxtGUI.lang.getWord("OK"));
        JButton cancel = new JButton(JdotxtGUI.lang.getWord("Cancel"));

        ok.setFont(JdotxtGUI.fontR);
        cancel.setFont(JdotxtGUI.fontR);
        ok.setPreferredSize(cancel.getPreferredSize());

        cancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                JdotxtSortDialog.this.dispose();
            }
        });

        ok.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                JdotxtSortDialog.this.dispose();
            }
        });

        buttons.add(Box.createHorizontalGlue());
        buttons.add(ok);
        buttons.add(cancel);
        buttons.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        buttons.setBackground(JdotxtGUI.COLOR_GRAY_PANEL);
        buttons.setOpaque(true);

        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (int) ((dimension.getWidth() - this.getWidth()) / 2);
        int y = (int) ((dimension.getHeight() - this.getHeight()) / 2);
        this.setLocation(x, y);

        this.add(tables, BorderLayout.CENTER);
        this.add(buttons, BorderLayout.PAGE_END);
        this.add(Box.createRigidArea(new Dimension(15,0)), BorderLayout.WEST);
        this.add(Box.createRigidArea(new Dimension(15,0)), BorderLayout.EAST);
        pack();
    }

    public JButton getOk() {
        return ok;
    }
    static class EditDisabledTableModel extends DefaultTableModel {


        EditDisabledTableModel(Vector vector, Vector vector1) {
            super(vector, vector1);
        }

        @Override
        public boolean isCellEditable(int i, int i1) {
            return false;
        }

    }

    public static MouseListener createMouseListener(final JTable table, final ClickHandler ch) {
        return new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
                int row = table.rowAtPoint(mouseEvent.getPoint());
                int col = table.columnAtPoint(mouseEvent.getPoint());
                ch.click(row, col);
            }
        };
    }


    public JButton getSaveButton() {
        return saveButton;
    }

    public String getSortName() {
        return name.getText();
    }

    public Map<Sorters, Boolean> getSort() {
        Map<Sorters, Boolean> res = new HashMap<>();

        for (Map.Entry<Sorters, Boolean> s: sortList) {
            res.put(s.getKey(), s.getValue());
        }

        return res;
    }

    interface ClickHandler {
        void click(int row, int col);
    }

}
