package com.chschmid.jdotxt.gui.controls;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class JdotxtToggleImageButton extends JdotxtImageButton {

    private boolean toggle = false;
    private Color untoggledColor;
    private Runnable toggleAction;

    public JdotxtToggleImageButton() {
        super();
    }

    public JdotxtToggleImageButton(boolean toggled) {
        toggle = toggled;
    }

    public JdotxtToggleImageButton(ImageIcon icon) {
        super(icon);
        addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                setToggle(!toggle);
                toggleAction.run();
            }
        });
    }

    public void setToggle(boolean toggle) {
        this.toggle = toggle;
        super.setBackgroundColor(toggle ? getPressedColor() : untoggledColor);
    }

    public boolean isToggle() {
        return toggle;
    }

    @Override
    public void setBackgroundColor(Color color) {
        super.setBackgroundColor(color);
        untoggledColor = color;
    }

    public void setToggleAction(Runnable toggleAction) {
        this.toggleAction = toggleAction;
    }

    public Runnable getToggleAction() {
        return toggleAction;
    }
}
