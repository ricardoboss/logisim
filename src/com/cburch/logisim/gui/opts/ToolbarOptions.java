/* Copyright (c) 2010, Carl Burch. License information is located in the
 * com.cburch.logisim.Main source code and at www.cburch.com/logisim/. */

package com.cburch.logisim.gui.opts;

import com.cburch.logisim.file.ToolbarData;
import com.cburch.logisim.gui.generic.ProjectExplorer;
import com.cburch.logisim.gui.generic.ProjectExplorerEvent;
import com.cburch.logisim.gui.generic.ProjectExplorerListener;
import com.cburch.logisim.gui.generic.ProjectExplorerToolNode;
import com.cburch.logisim.tools.AddTool;
import com.cburch.logisim.tools.Tool;
import com.cburch.logisim.util.TableLayout;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class ToolbarOptions extends OptionsPanel {
	private final ProjectExplorer explorer;
	private final JButton addTool;
	private final JButton addSeparator;
	private final JButton moveUp;
	private final JButton moveDown;
	private final JButton remove;
	private final ToolbarList list;

	public ToolbarOptions(OptionsFrame window) {
		super(window);
		explorer = new ProjectExplorer(getProject());
		addTool = new JButton();
		addSeparator = new JButton();
		moveUp = new JButton();
		moveDown = new JButton();
		remove = new JButton();

		list = new ToolbarList(getOptions().getToolbarData());

		TableLayout middleLayout = new TableLayout(1);
		JPanel middle = new JPanel(middleLayout);
		middle.add(addTool);
		middle.add(addSeparator);
		middle.add(moveUp);
		middle.add(moveDown);
		middle.add(remove);
		middleLayout.setRowWeight(4, 1.0);

		Listener listener = new Listener();
		explorer.setListener(listener);
		addTool.addActionListener(listener);
		addSeparator.addActionListener(listener);
		moveUp.addActionListener(listener);
		moveDown.addActionListener(listener);
		remove.addActionListener(listener);
		list.addListSelectionListener(listener);
		listener.computeEnabled();

		GridBagLayout gridbag = new GridBagLayout();
		GridBagConstraints gbc = new GridBagConstraints();
		setLayout(gridbag);
		JScrollPane explorerPane = new JScrollPane(explorer,
			ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
			ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		JScrollPane listPane = new JScrollPane(list,
			ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
			ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		gbc.fill = GridBagConstraints.BOTH;
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		gridbag.setConstraints(explorerPane, gbc);
		add(explorerPane);
		gbc.fill = GridBagConstraints.VERTICAL;
		gbc.anchor = GridBagConstraints.NORTH;
		gbc.weightx = 0.0;
		gridbag.setConstraints(middle, gbc);
		add(middle);
		gbc.fill = GridBagConstraints.BOTH;
		gbc.weightx = 1.0;
		gridbag.setConstraints(listPane, gbc);
		add(listPane);
	}

	@Override
	public String getTitle() {
		return Strings.get("toolbarTitle");
	}

	@Override
	public String getHelpText() {
		return Strings.get("toolbarHelp");
	}

	@Override
	public void localeChanged() {
		addTool.setText(Strings.get("toolbarAddTool"));
		addSeparator.setText(Strings.get("toolbarAddSeparator"));
		moveUp.setText(Strings.get("toolbarMoveUp"));
		moveDown.setText(Strings.get("toolbarMoveDown"));
		remove.setText(Strings.get("toolbarRemove"));
		list.localeChanged();
	}

	private class Listener
		implements ProjectExplorerListener, ActionListener, ListSelectionListener {
		public void selectionChanged(ProjectExplorerEvent event) {
			computeEnabled();
		}

		public void doubleClicked(ProjectExplorerEvent event) {
			Object target = event.getTarget();
			if (target instanceof ProjectExplorerToolNode) {
				Tool tool = ((ProjectExplorerToolNode) target).getValue();
				doAddTool(tool);
			}
		}

		public void moveRequested(ProjectExplorerEvent event, AddTool dragged, AddTool target) {
		}

		public void deleteRequested(ProjectExplorerEvent event) {
		}

		public JPopupMenu menuRequested(ProjectExplorerEvent event) {
			return null;
		}

		public void actionPerformed(ActionEvent event) {
			Object src = event.getSource();
			if (src == addTool) {
				doAddTool(explorer.getSelectedTool().cloneTool());
			} else if (src == addSeparator) {
				getOptions().getToolbarData().addSeparator();
			} else if (src == moveUp) {
				doMove(-1);
			} else if (src == moveDown) {
				doMove(1);
			} else if (src == remove) {
				int index = list.getSelectedIndex();
				if (index >= 0) {
					getProject().doAction(ToolbarActions.removeTool(getOptions().getToolbarData(), index));
					list.clearSelection();
				}
			}
		}

		public void valueChanged(ListSelectionEvent event) {
			computeEnabled();
		}

		private void computeEnabled() {
			int index = list.getSelectedIndex();
			addTool.setEnabled(explorer.getSelectedTool() != null);
			moveUp.setEnabled(index > 0);
			moveDown.setEnabled(index >= 0 && index < list.getModel().getSize() - 1);
			remove.setEnabled(index >= 0);
		}

		private void doAddTool(Tool tool) {
			if (tool != null) {
				getProject().doAction(ToolbarActions.addTool(getOptions().getToolbarData(), tool));
			}
		}

		private void doMove(int delta) {
			int oldIndex = list.getSelectedIndex();
			int newIndex = oldIndex + delta;
			ToolbarData data = getOptions().getToolbarData();
			if (oldIndex >= 0 && newIndex >= 0 && newIndex < data.size()) {
				getProject().doAction(ToolbarActions.moveTool(data,
					oldIndex, newIndex));
				list.setSelectedIndex(newIndex);
			}
		}
	}
}
