/* Copyright (c) 2010, Carl Burch. License information is located in the
 * com.cburch.logisim.Main source code and at www.cburch.com/logisim/. */

package com.cburch.draw.toolbar;

import javax.swing.*;
import java.awt.*;

public class Toolbar extends JPanel {
	public static final Object VERTICAL = new Object();
	public static final Object HORIZONTAL = new Object();
	private final JPanel subpanel;
	private final MyListener myListener;
	private ToolbarModel model;
	private Object orientation;
	private ToolbarButton curPressed;

	public Toolbar(ToolbarModel model) {
		super(new BorderLayout());
		this.subpanel = new JPanel();
		this.model = model;
		this.orientation = HORIZONTAL;
		this.myListener = new MyListener();
		this.curPressed = null;

		this.add(new JPanel(), BorderLayout.CENTER);
		setOrientation(HORIZONTAL);

		computeContents();
		if (model != null) model.addToolbarModelListener(myListener);
	}

	public ToolbarModel getToolbarModel() {
		return model;
	}

	public void setToolbarModel(ToolbarModel value) {
		ToolbarModel oldValue = model;
		if (value != oldValue) {
			if (oldValue != null) oldValue.removeToolbarModelListener(myListener);
			if (value != null) value.addToolbarModelListener(myListener);
			model = value;
			computeContents();
		}
	}

	private void computeContents() {
		subpanel.removeAll();
		ToolbarModel m = model;
		if (m != null) {
			for (ToolbarItem item : m.getItems()) {
				subpanel.add(new ToolbarButton(this, item));
			}
			subpanel.add(Box.createGlue());
		}
		revalidate();
	}

	ToolbarButton getPressed() {
		return curPressed;
	}

	void setPressed(ToolbarButton value) {
		ToolbarButton oldValue = curPressed;
		if (oldValue != value) {
			curPressed = value;
			if (oldValue != null) oldValue.repaint();
			if (value != null) value.repaint();
		}
	}

	Object getOrientation() {
		return orientation;
	}

	public void setOrientation(Object value) {
		int axis;
		String position;
		if (value == HORIZONTAL) {
			axis = BoxLayout.X_AXIS;
			position = BorderLayout.LINE_START;
		} else if (value == VERTICAL) {
			axis = BoxLayout.Y_AXIS;
			position = BorderLayout.NORTH;
		} else {
			throw new IllegalArgumentException();
		}
		this.remove(subpanel);
		subpanel.setLayout(new BoxLayout(subpanel, axis));
		this.add(subpanel, position);
		this.orientation = value;
	}

	private class MyListener implements ToolbarModelListener {
		public void toolbarAppearanceChanged(ToolbarModelEvent event) {
			repaint();
		}

		public void toolbarContentsChanged(ToolbarModelEvent event) {
			computeContents();
		}
	}
}
