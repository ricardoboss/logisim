/* Copyright (c) 2010, Carl Burch. License information is located in the
 * com.cburch.logisim.Main source code and at www.cburch.com/logisim/. */

package com.cburch.logisim.gui.opts;

import com.cburch.logisim.data.Attribute;
import com.cburch.logisim.data.AttributeEvent;
import com.cburch.logisim.data.AttributeListener;
import com.cburch.logisim.data.AttributeSet;
import com.cburch.logisim.file.Options;
import com.cburch.logisim.util.TableLayout;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class SimulateOptions extends OptionsPanel {
	private final JLabel simLimitLabel = new JLabel();
	private final JComboBox simLimit = new JComboBox(new Integer[]{
		200,
		500,
		1000,
		2000,
		5000,
		10000,
		20000,
		50000,
	});
	private final JCheckBox simRandomness = new JCheckBox();
	private final JLabel gateUndefinedLabel = new JLabel();
	private final JComboBox gateUndefined = new JComboBox(new Object[]{
		new ComboOption(Options.GATE_UNDEFINED_IGNORE),
		new ComboOption(Options.GATE_UNDEFINED_ERROR)
	});

	public SimulateOptions(OptionsFrame window) {
		super(window);

		JPanel simLimitPanel = new JPanel();
		simLimitPanel.add(simLimitLabel);
		simLimitPanel.add(simLimit);
		MyListener myListener = new MyListener();
		simLimit.addActionListener(myListener);

		JPanel gateUndefinedPanel = new JPanel();
		gateUndefinedPanel.add(gateUndefinedLabel);
		gateUndefinedPanel.add(gateUndefined);
		gateUndefined.addActionListener(myListener);

		simRandomness.addActionListener(myListener);

		setLayout(new TableLayout(1));
		add(simLimitPanel);
		add(gateUndefinedPanel);
		add(simRandomness);

		window.getOptions().getAttributeSet().addAttributeListener(myListener);
		AttributeSet attrs = getOptions().getAttributeSet();
		myListener.loadSimLimit(attrs.getValue(Options.sim_limit_attr));
		myListener.loadGateUndefined(attrs.getValue(Options.ATTR_GATE_UNDEFINED));
		myListener.loadSimRandomness(attrs.getValue(Options.sim_rand_attr));
	}

	@Override
	public String getTitle() {
		return Strings.get("simulateTitle");
	}

	@Override
	public String getHelpText() {
		return Strings.get("simulateHelp");
	}

	@Override
	public void localeChanged() {
		simLimitLabel.setText(Strings.get("simulateLimit"));
		gateUndefinedLabel.setText(Strings.get("gateUndefined"));
		simRandomness.setText(Strings.get("simulateRandomness"));
	}

	private class MyListener implements ActionListener, AttributeListener {
		public void actionPerformed(ActionEvent event) {
			Object source = event.getSource();
			if (source == simLimit) {
				Integer opt = (Integer) simLimit.getSelectedItem();
				if (opt != null) {
					AttributeSet attrs = getOptions().getAttributeSet();
					getProject().doAction(OptionsActions.setAttribute(attrs,
						Options.sim_limit_attr, opt));
				}
			} else if (source == simRandomness) {
				AttributeSet attrs = getOptions().getAttributeSet();
				Object val = simRandomness.isSelected() ? Options.sim_rand_dflt
					: Integer.valueOf(0);
				getProject().doAction(OptionsActions.setAttribute(attrs,
					Options.sim_rand_attr, val));
			} else if (source == gateUndefined) {
				ComboOption opt = (ComboOption) gateUndefined.getSelectedItem();
				if (opt != null) {
					AttributeSet attrs = getOptions().getAttributeSet();
					getProject().doAction(OptionsActions.setAttribute(attrs,
						Options.ATTR_GATE_UNDEFINED, opt.getValue()));
				}
			}
		}

		public void attributeListChanged(AttributeEvent e) {
		}

		public void attributeValueChanged(AttributeEvent e) {
			Attribute<?> attr = e.getAttribute();
			Object val = e.getValue();
			if (attr == Options.sim_limit_attr) {
				loadSimLimit((Integer) val);
			} else if (attr == Options.sim_rand_attr) {
				loadSimRandomness((Integer) val);
			}
		}

		private void loadSimLimit(Integer val) {
			int value = val;
			ComboBoxModel model = simLimit.getModel();
			for (int i = 0; i < model.getSize(); i++) {
				Integer opt = (Integer) model.getElementAt(i);
				if (opt == value) {
					simLimit.setSelectedItem(opt);
				}
			}
		}

		private void loadGateUndefined(Object val) {
			ComboOption.setSelected(gateUndefined, val);
		}

		private void loadSimRandomness(Integer val) {
			simRandomness.setSelected(val > 0);
		}
	}
}
