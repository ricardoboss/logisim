/* Copyright (c) 2010, Carl Burch. License information is located in the
 * com.cburch.logisim.Main source code and at www.cburch.com/logisim/. */

package com.cburch.logisim.gui.main;

import com.cburch.draw.toolbar.Toolbar;
import com.cburch.draw.toolbar.ToolbarItem;
import com.cburch.draw.toolbar.ToolbarModel;

import javax.swing.*;
import java.awt.event.ActionEvent;

class KeyboardToolSelection extends AbstractAction {
	private final Toolbar toolbar;
	private final int index;

	private KeyboardToolSelection(Toolbar toolbar, int index) {
		this.toolbar = toolbar;
		this.index = index;
	}

	public static void register(Toolbar toolbar) {
		ActionMap amap = toolbar.getActionMap();
		InputMap imap = toolbar.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
		int mask = toolbar.getToolkit().getMenuShortcutKeyMaskEx();
		for (int i = 0; i < 10; i++) {
			KeyStroke keyStroke = KeyStroke.getKeyStroke((char) ('0' + i), mask);
			int j = (i == 0 ? 10 : i - 1);
			KeyboardToolSelection action = new KeyboardToolSelection(toolbar, j);
			String key = "ToolSelect" + i;
			amap.put(key, action);
			imap.put(keyStroke, key);
		}
	}

	public void actionPerformed(ActionEvent event) {
		ToolbarModel model = toolbar.getToolbarModel();
		int i = -1;
		for (ToolbarItem item : model.getItems()) {
			if (item.isSelectable()) {
				i++;
				if (i == index) {
					model.itemSelected(item);
				}
			}
		}
	}
}
