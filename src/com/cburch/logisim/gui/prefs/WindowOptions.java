/* Copyright (c) 2010, Carl Burch. License information is located in the
 * com.cburch.logisim.Main source code and at www.cburch.com/logisim/. */

package com.cburch.logisim.gui.prefs;

import com.cburch.logisim.data.Direction;
import com.cburch.logisim.prefs.AppPreferences;
import com.cburch.logisim.util.TableLayout;

import javax.swing.*;

class WindowOptions extends OptionsPanel {
	private final PrefBoolean[] checks;
	private final PrefOptionList toolbarPlacement;

	public WindowOptions(PreferencesFrame window) {
		super(window);

		checks = new PrefBoolean[]{
			new PrefBoolean(AppPreferences.SHOW_TICK_RATE,
				Strings.getter("windowTickRate")),
		};

		toolbarPlacement = new PrefOptionList(AppPreferences.TOOLBAR_PLACEMENT,
			Strings.getter("windowToolbarLocation"),
			new PrefOption[]{
				new PrefOption(Direction.NORTH.toString(),
					Direction.NORTH.getDisplayGetter()),
				new PrefOption(Direction.SOUTH.toString(),
					Direction.SOUTH.getDisplayGetter()),
				new PrefOption(Direction.EAST.toString(),
					Direction.EAST.getDisplayGetter()),
				new PrefOption(Direction.WEST.toString(),
					Direction.WEST.getDisplayGetter()),
				new PrefOption(AppPreferences.TOOLBAR_DOWN_MIDDLE,
					Strings.getter("windowToolbarDownMiddle")),
				new PrefOption(AppPreferences.TOOLBAR_HIDDEN,
					Strings.getter("windowToolbarHidden"))});

		JPanel panel = new JPanel(new TableLayout(2));
		panel.add(toolbarPlacement.getJLabel());
		panel.add(toolbarPlacement.getJComboBox());

		setLayout(new TableLayout(1));
		for (PrefBoolean check : checks) {
			add(check);
		}
		add(panel);
	}

	@Override
	public String getTitle() {
		return Strings.get("windowTitle");
	}

	@Override
	public String getHelpText() {
		return Strings.get("windowHelp");
	}

	@Override
	public void localeChanged() {
		for (PrefBoolean check : checks) {
			check.localeChanged();
		}
		toolbarPlacement.localeChanged();
	}
}
