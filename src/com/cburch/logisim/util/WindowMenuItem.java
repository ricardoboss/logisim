/* Copyright (c) 2010, Carl Burch. License information is located in the
 * com.cburch.logisim.Main source code and at www.cburch.com/logisim/. */

package com.cburch.logisim.util;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

class WindowMenuItem extends JRadioButtonMenuItem {
	private WindowMenuItemManager manager;

	WindowMenuItem(WindowMenuItemManager manager) {
		this.manager = manager;
		setText(manager.getText());
		setSelected(WindowMenuManager.getCurrentManager() == manager);
	}

	public JFrame getJFrame() {
		return manager.getJFrame(true);
	}

	public void actionPerformed(ActionEvent event) {
		JFrame frame = getJFrame();
		frame.setExtendedState(Frame.NORMAL);
		frame.setVisible(true);
		frame.toFront();
	}
}
