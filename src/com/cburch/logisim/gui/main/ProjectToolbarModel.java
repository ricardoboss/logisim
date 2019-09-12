/* Copyright (c) 2010, Carl Burch. License information is located in the
 * com.cburch.logisim.Main source code and at www.cburch.com/logisim/. */

package com.cburch.logisim.gui.main;

import com.cburch.draw.toolbar.AbstractToolbarModel;
import com.cburch.draw.toolbar.ToolbarItem;
import com.cburch.draw.toolbar.ToolbarSeparator;
import com.cburch.logisim.gui.menu.LogisimMenuBar;
import com.cburch.logisim.util.UnmodifiableList;

import java.util.List;

class ProjectToolbarModel extends AbstractToolbarModel
	implements MenuListener.EnabledListener {
	private final Frame frame;
	private final LogisimToolbarItem itemLayout;
	private final LogisimToolbarItem itemAppearance;
	private final List<ToolbarItem> items;

	public ProjectToolbarModel(Frame frame, MenuListener menu) {
		this.frame = frame;

		LogisimToolbarItem itemAdd = new LogisimToolbarItem(menu, "projadd.gif", LogisimMenuBar.ADD_CIRCUIT,
			Strings.getter("projectAddCircuitTip"));
		LogisimToolbarItem itemUp = new LogisimToolbarItem(menu, "projup.gif", LogisimMenuBar.MOVE_CIRCUIT_UP,
			Strings.getter("projectMoveCircuitUpTip"));
		LogisimToolbarItem itemDown = new LogisimToolbarItem(menu, "projdown.gif", LogisimMenuBar.MOVE_CIRCUIT_DOWN,
			Strings.getter("projectMoveCircuitDownTip"));
		LogisimToolbarItem itemDelete = new LogisimToolbarItem(menu, "projdel.gif", LogisimMenuBar.REMOVE_CIRCUIT,
			Strings.getter("projectRemoveCircuitTip"));
		itemLayout = new LogisimToolbarItem(menu, "projlayo.gif", LogisimMenuBar.EDIT_LAYOUT,
			Strings.getter("projectEditLayoutTip"));
		itemAppearance = new LogisimToolbarItem(menu, "projapp.gif", LogisimMenuBar.EDIT_APPEARANCE,
			Strings.getter("projectEditAppearanceTip"));

		items = UnmodifiableList.create(new ToolbarItem[]{
			itemAdd,
			itemUp,
			itemDown,
			itemDelete,
			new ToolbarSeparator(4),
			itemLayout,
			itemAppearance,
		});

		menu.addEnabledListener(this);
	}

	@Override
	public List<ToolbarItem> getItems() {
		return items;
	}

	@Override
	public boolean isSelected(ToolbarItem item) {
		String view = frame.getEditorView();
		if (item == itemLayout) {
			return view.equals(Frame.EDIT_LAYOUT);
		} else if (item == itemAppearance) {
			return view.equals(Frame.EDIT_APPEARANCE);
		} else {
			return false;
		}
	}

	@Override
	public void itemSelected(ToolbarItem item) {
		if (item instanceof LogisimToolbarItem) {
			((LogisimToolbarItem) item).doAction();
		}
	}

	//
	// EnabledListener methods
	//
	public void menuEnableChanged(MenuListener source) {
		fireToolbarAppearanceChanged();
	}
}
