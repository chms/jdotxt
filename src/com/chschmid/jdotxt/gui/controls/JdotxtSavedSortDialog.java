package com.chschmid.jdotxt.gui.controls;


import com.chschmid.jdotxt.gui.JdotxtGUI;
import com.todotxt.todotxttouch.task.sorter.Sorters;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;

public class JdotxtSavedSortDialog extends JDialog {
    private Map<String, Map<Sorters, Boolean>> sorts;
    private Vector<Vector> sortDisplay = new Vector<>();
    private JButton ok;
    public JdotxtSavedSortDialog(Map<String, Map<Sorters, Boolean>> savedSorts) {
        sorts = new LinkedHashMap<>();
        for (Map.Entry<String, Map<Sorters, Boolean>> e : savedSorts.entrySet())
            sorts.put(e.getKey(), new LinkedHashMap<>(e.getValue()));
        buildData();
        initGUI();
    }

    private void buildData() {
        sortDisplay.clear();
        for (String s : sorts.keySet()) {
            sortDisplay.add(new Vector<>(Arrays.asList(s, "⚙", "✕")));
        }
    }

    public Map<String, Map<Sorters, Boolean>> getSorts() {
        return sorts;
    }

    private void initGUI() {

        setModal(true);
        Vector<String> cols = new Vector<>();
        cols.setSize(3);
        Collections.fill(cols, "");
        setTitle("Saved sorts");
        Box main = new Box(BoxLayout.Y_AXIS);
        final JTable sorts = new JTable(new JdotxtSortDialog.EditDisabledTableModel(sortDisplay, cols));
        sorts.setRowSelectionAllowed(false);
        sorts.setCellSelectionEnabled(false);
        sorts.setIntercellSpacing(new Dimension(0,0));
        sorts.getColumnModel().getColumn(1).setMaxWidth(17);
        sorts.getColumnModel().getColumn(2).setMaxWidth(17);
        sorts.setFont(new Font("Serif", Font.PLAIN, 14));
        sorts.setShowGrid(false);

        sorts.addMouseListener(JdotxtSortDialog.createMouseListener(sorts, new JdotxtSortDialog.ClickHandler() {
            @Override
            public void click(int row, int col) {
                if (col == 1) {
                    final String name = sortDisplay.get(row).get(0).toString();
                    Map<Sorters, Boolean> sort = JdotxtSavedSortDialog.this.sorts.get(name);
                    final JdotxtSortDialog edit = new JdotxtSortDialog(sort, true, name);
                    edit.getOk().addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent actionEvent) {
                            Map<Sorters, Boolean> newSort = edit.getSort();
                            String newName = edit.getSortName();
                            JdotxtSavedSortDialog.this.sorts.remove(name);
                            JdotxtSavedSortDialog.this.sorts.put(newName, newSort);
                            buildData();
                            sorts.updateUI();
                            pack();
                        }
                    });
                    edit.setVisible(true);
                } else if (col == 2) {
                    final String name = sortDisplay.get(row).get(0).toString();
                    sortDisplay.remove(row);
                    JdotxtSavedSortDialog.this.sorts.remove(name);
                }
                buildData();
                sorts.updateUI();
                pack();
            }
        }));

        main.add(sorts);
        main.add(Box.createRigidArea(new Dimension(0,10)));

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
                JdotxtSavedSortDialog.this.dispose();
            }
        });

        ok.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                JdotxtSavedSortDialog.this.dispose();
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

        add(Box.createRigidArea(new Dimension(0,10)), BorderLayout.NORTH);
        add(Box.createRigidArea(new Dimension(15,0)), BorderLayout.WEST);
        add(Box.createRigidArea(new Dimension(15,0)), BorderLayout.EAST);
        add(main, BorderLayout.CENTER);
        add(buttons, BorderLayout.PAGE_END);

        pack();
    }

    public JButton getOk() {
        return ok;
    }
}
