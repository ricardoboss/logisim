/* Copyright (c) 2010, Carl Burch. License information is located in the
 * com.cburch.logisim.Main source code and at www.cburch.com/logisim/. */

package com.cburch.logisim.analyze.gui;

import com.cburch.logisim.analyze.model.AnalyzerModel;
import com.cburch.logisim.analyze.model.Expression;
import com.cburch.logisim.analyze.model.VariableList;
import com.cburch.logisim.circuit.Circuit;
import com.cburch.logisim.circuit.CircuitMutation;
import com.cburch.logisim.file.LogisimFileActions;
import com.cburch.logisim.proj.Project;
import com.cburch.logisim.proj.Projects;
import com.cburch.logisim.std.gates.CircuitBuilder;
import com.cburch.logisim.util.StringUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

class BuildCircuitButton extends JButton {
	private final JFrame parent;
	private final AnalyzerModel model;

	BuildCircuitButton(JFrame parent, AnalyzerModel model) {
		this.parent = parent;
		this.model = model;
		MyListener myListener = new MyListener();
		addActionListener(myListener);
	}

	void localeChanged() {
		setText(Strings.get("buildCircuitButton"));
	}

	private void performAction(Project dest, String name, boolean replace,
							   final boolean twoInputs, final boolean useNands) {
		if (replace) {
			final Circuit circuit = dest.getLogisimFile().getCircuit(name);
			if (circuit == null) {
				JOptionPane.showMessageDialog(parent,
					"Internal error prevents replacing circuit.",
					"Internal Error", JOptionPane.ERROR_MESSAGE);
				return;
			}

			CircuitMutation xn = CircuitBuilder.build(circuit, model, twoInputs,
				useNands);
			dest.doAction(xn.toAction(Strings.getter("replaceCircuitAction")));
		} else {
			// add the circuit
			Circuit circuit = new Circuit(name);
			CircuitMutation xn = CircuitBuilder.build(circuit, model, twoInputs,
				useNands);
			xn.execute();
			dest.doAction(LogisimFileActions.addCircuit(circuit));
			dest.setCurrentCircuit(circuit);
		}
	}

	private static class ProjectItem {
		final Project project;

		ProjectItem(Project project) {
			this.project = project;
		}

		@Override
		public String toString() {
			return project.getLogisimFile().getDisplayName();
		}
	}

	private class DialogPanel extends JPanel {
		private final JComboBox project;
		private final JTextField name = new JTextField(10);
		private final JCheckBox twoInputs = new JCheckBox();
		private final JCheckBox nands = new JCheckBox();

		DialogPanel() {
			List<Project> projects = Projects.getOpenProjects();
			Object[] options = new Object[projects.size()];
			Object initialSelection = null;
			for (int i = 0; i < options.length; i++) {
				Project proj = projects.get(i);
				options[i] = new ProjectItem(proj);
				if (proj == model.getCurrentProject()) {
					initialSelection = options[i];
				}
			}
			project = new JComboBox(options);
			if (options.length == 1) {
				project.setSelectedItem(options[0]);
				project.setEnabled(false);
			} else if (initialSelection != null) {
				project.setSelectedItem(initialSelection);
			}

			Circuit defaultCircuit = model.getCurrentCircuit();
			if (defaultCircuit != null) {
				name.setText(defaultCircuit.getName());
				name.selectAll();
			}

			VariableList outputs = model.getOutputs();
			boolean enableNands = true;
			for (int i = 0; i < outputs.size(); i++) {
				String output = outputs.get(i);
				Expression expr = model.getOutputExpressions().getExpression(output);
				if (expr != null && expr.containsXor()) {
					enableNands = false;
					break;
				}
			}
			nands.setEnabled(enableNands);

			GridBagLayout gb = new GridBagLayout();
			GridBagConstraints gc = new GridBagConstraints();
			setLayout(gb);
			gc.anchor = GridBagConstraints.LINE_START;
			gc.fill = GridBagConstraints.NONE;

			gc.gridx = 0;
			gc.gridy = 0;
			JLabel projectLabel = new JLabel();
			gb.setConstraints(projectLabel, gc);
			add(projectLabel);
			gc.gridx = 1;
			gb.setConstraints(project, gc);
			add(project);
			gc.gridy++;
			gc.gridx = 0;
			JLabel nameLabel = new JLabel();
			gb.setConstraints(nameLabel, gc);
			add(nameLabel);
			gc.gridx = 1;
			gb.setConstraints(name, gc);
			add(name);
			gc.gridy++;
			gb.setConstraints(twoInputs, gc);
			add(twoInputs);
			gc.gridy++;
			gb.setConstraints(nands, gc);
			add(nands);

			projectLabel.setText(Strings.get("buildProjectLabel"));
			nameLabel.setText(Strings.get("buildNameLabel"));
			twoInputs.setText(Strings.get("buildTwoInputsLabel"));
			nands.setText(Strings.get("buildNandsLabel"));
		}
	}

	private class MyListener implements ActionListener {
		public void actionPerformed(ActionEvent event) {
			Project dest = null;
			String name = null;
			boolean twoInputs = false;
			boolean useNands = false;
			boolean replace = false;

			boolean ok = false;
			while (!ok) {
				DialogPanel dlog = new DialogPanel();
				int action = JOptionPane.showConfirmDialog(parent,
					dlog, Strings.get("buildDialogTitle"), JOptionPane.OK_CANCEL_OPTION,
					JOptionPane.QUESTION_MESSAGE);
				if (action != JOptionPane.OK_OPTION) return;

				ProjectItem projectItem = (ProjectItem) dlog.project.getSelectedItem();
				if (projectItem == null) {
					JOptionPane.showMessageDialog(parent, Strings.get("buildNeedProjectError"),
						Strings.get("buildDialogErrorTitle"), JOptionPane.ERROR_MESSAGE);
					continue;
				}
				dest = projectItem.project;

				name = dlog.name.getText().trim();
				if (name.equals("")) {
					JOptionPane.showMessageDialog(parent, Strings.get("buildNeedCircuitError"),
						Strings.get("buildDialogErrorTitle"), JOptionPane.ERROR_MESSAGE);
					continue;
				}

				if (dest.getLogisimFile().getCircuit(name) != null) {
					int choice = JOptionPane.showConfirmDialog(parent,
						StringUtil.format(Strings.get("buildConfirmReplaceMessage"), name),
						Strings.get("buildConfirmReplaceTitle"), JOptionPane.YES_NO_OPTION);
					if (choice != JOptionPane.YES_OPTION) {
						continue;
					}
					replace = true;
				}

				twoInputs = dlog.twoInputs.isSelected();
				useNands = dlog.nands.isSelected();
				ok = true;
			}

			performAction(dest, name, replace, twoInputs, useNands);
		}
	}
}
