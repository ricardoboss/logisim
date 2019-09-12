/* Copyright (c) 2010, Carl Burch. License information is located in the
 * com.cburch.logisim.Main source code and at www.cburch.com/logisim/. */

package com.cburch.logisim.gui.opts;

import com.cburch.logisim.file.*;
import com.cburch.logisim.gui.generic.LFrame;
import com.cburch.logisim.gui.menu.LogisimMenuBar;
import com.cburch.logisim.proj.Project;
import com.cburch.logisim.util.LocaleListener;
import com.cburch.logisim.util.LocaleManager;
import com.cburch.logisim.util.StringUtil;
import com.cburch.logisim.util.WindowMenuItemManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;

public class OptionsFrame extends LFrame {
	private final Project project;
	private final LogisimFile file;
	private final WindowMenuManager windowManager = new WindowMenuManager();
	private final OptionsPanel[] panels;
	private final JTabbedPane tabbedPane;
	private final JButton revert = new JButton();
	private final JButton close = new JButton();

	public OptionsFrame(Project project) {
		this.project = project;
		this.file = project.getLogisimFile();
		MyListener myListener = new MyListener();
		file.addLibraryListener(myListener);
		setDefaultCloseOperation(HIDE_ON_CLOSE);
		setJMenuBar(new LogisimMenuBar(this, project));

		panels = new OptionsPanel[]{
			new SimulateOptions(this),
			new ToolbarOptions(this),
			new MouseOptions(this),
		};
		tabbedPane = new JTabbedPane();
		for (OptionsPanel panel : panels) {
			tabbedPane.addTab(panel.getTitle(), null, panel, panel.getToolTipText());
		}

		JPanel buttonPanel = new JPanel();
		buttonPanel.add(revert);
		buttonPanel.add(close);
		revert.addActionListener(myListener);
		close.addActionListener(myListener);

		Container contents = getContentPane();
		tabbedPane.setPreferredSize(new Dimension(450, 300));
		contents.add(tabbedPane, BorderLayout.CENTER);
		contents.add(buttonPanel, BorderLayout.SOUTH);

		LocaleManager.addLocaleListener(myListener);
		myListener.localeChanged();
		pack();
	}

	private static String computeTitle(LogisimFile file) {
		String name = file == null ? "???" : file.getName();
		return StringUtil.format(Strings.get("optionsFrameTitle"), name);
	}

	public Project getProject() {
		return project;
	}

	public LogisimFile getLogisimFile() {
		return file;
	}

	public Options getOptions() {
		return file.getOptions();
	}

	@Override
	public void setVisible(boolean value) {
		if (value) {
			windowManager.frameOpened(this);
		}
		super.setVisible(value);
	}

	OptionsPanel[] getPrefPanels() {
		return panels;
	}

	private class WindowMenuManager extends WindowMenuItemManager
		implements LocaleListener {
		WindowMenuManager() {
			super(Strings.get("optionsFrameMenuItem"), false);
		}

		@Override
		public JFrame getJFrame(boolean create) {
			return OptionsFrame.this;
		}

		public void localeChanged() {
			String title = project.getLogisimFile().getDisplayName();
			setText(StringUtil.format(Strings.get("optionsFrameMenuItem"), title));
		}
	}

	private class MyListener
		implements ActionListener, LibraryListener, LocaleListener {
		public void actionPerformed(ActionEvent event) {
			Object src = event.getSource();
			if (src == revert) {
				getProject().doAction(LogisimFileActions.revertDefaults());
			} else if (src == close) {
				WindowEvent e = new WindowEvent(OptionsFrame.this,
					WindowEvent.WINDOW_CLOSING);
				OptionsFrame.this.processWindowEvent(e);
			}
		}

		public void libraryChanged(LibraryEvent event) {
			if (event.getAction() == LibraryEvent.SET_NAME) {
				setTitle(computeTitle(file));
				windowManager.localeChanged();
			}
		}

		public void localeChanged() {
			setTitle(computeTitle(file));
			for (int i = 0; i < panels.length; i++) {
				tabbedPane.setTitleAt(i, panels[i].getTitle());
				tabbedPane.setToolTipTextAt(i, panels[i].getToolTipText());
				panels[i].localeChanged();
			}
			revert.setText(Strings.get("revertButton"));
			close.setText(Strings.get("closeButton"));
			windowManager.localeChanged();
		}
	}
}
