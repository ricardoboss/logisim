/* Copyright (c) 2010, Carl Burch. License information is located in the
 * com.cburch.logisim.Main source code and at www.cburch.com/logisim/. */

package com.cburch.logisim.analyze.gui;

import com.cburch.logisim.analyze.model.AnalyzerModel;
import com.cburch.logisim.analyze.model.OutputExpressions;
import com.cburch.logisim.analyze.model.OutputExpressionsEvent;
import com.cburch.logisim.analyze.model.OutputExpressionsListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

class MinimizedTab extends AnalyzerTab {
	private final OutputSelector selector;
	private final KarnaughMapPanel karnaughMap;
	private final JLabel formatLabel = new JLabel();
	private final JComboBox formatChoice = new JComboBox(new FormatModel());
	private final ExpressionView minimizedExpr = new ExpressionView();
	private final JButton setAsExpr = new JButton();
	private final OutputExpressions outputExprs;

	public MinimizedTab(AnalyzerModel model) {
		this.outputExprs = model.getOutputExpressions();
		MyListener myListener = new MyListener();
		outputExprs.addOutputExpressionsListener(myListener);

		selector = new OutputSelector(model);
		selector.addItemListener(myListener);
		karnaughMap = new KarnaughMapPanel(model);
		karnaughMap.addMouseListener(new TruthTableMouseListener());
		setAsExpr.addActionListener(myListener);
		formatChoice.addItemListener(myListener);

		JPanel buttons = new JPanel(new GridLayout(1, 1));
		buttons.add(setAsExpr);

		JPanel formatPanel = new JPanel();
		formatPanel.add(formatLabel);
		formatPanel.add(formatChoice);

		GridBagLayout gb = new GridBagLayout();
		GridBagConstraints gc = new GridBagConstraints();
		setLayout(gb);
		gc.gridx = 0;
		gc.gridy = 0;
		addRow(gb, gc, selector.getLabel(), selector.getComboBox());
		addRow(gb, gc, formatLabel, formatChoice);

		gc.weightx = 0.0;
		gc.gridx = 0;
		gc.gridwidth = 2;
		gc.gridy = GridBagConstraints.RELATIVE;
		gc.fill = GridBagConstraints.BOTH;
		gc.anchor = GridBagConstraints.CENTER;
		gb.setConstraints(karnaughMap, gc);
		add(karnaughMap);
		Insets oldInsets = gc.insets;
		gc.insets = new Insets(20, 0, 0, 0);
		gb.setConstraints(minimizedExpr, gc);
		add(minimizedExpr);
		gc.insets = oldInsets;
		gc.fill = GridBagConstraints.NONE;
		gb.setConstraints(buttons, gc);
		add(buttons);

		String selected = selector.getSelectedOutput();
		setAsExpr.setEnabled(selected != null
			&& outputExprs.isExpressionMinimal(selected));
	}

	private void addRow(GridBagLayout gb, GridBagConstraints gc,
						JLabel label, JComboBox choice) {
		Insets oldInsets = gc.insets;
		gc.weightx = 0.0;
		gc.gridx = 0;
		gc.fill = GridBagConstraints.HORIZONTAL;
		gc.anchor = GridBagConstraints.LINE_START;
		gc.insets = new Insets(5, 5, 5, 5);
		gb.setConstraints(label, gc);
		add(label);
		gc.gridx = 1;
		gc.fill = GridBagConstraints.VERTICAL;
		gb.setConstraints(choice, gc);
		add(choice);
		gc.gridy++;
		gc.insets = oldInsets;
	}

	@Override
	void localeChanged() {
		selector.localeChanged();
		karnaughMap.localeChanged();
		minimizedExpr.localeChanged();
		setAsExpr.setText(Strings.get("minimizedSetButton"));
		formatLabel.setText(Strings.get("minimizedFormat"));
		((FormatModel) formatChoice.getModel()).localeChanged();
	}

	@Override
	void updateTab() {
		String output = getCurrentVariable();
		karnaughMap.setOutput(output);
		int format = outputExprs.getMinimizedFormat(output);
		formatChoice.setSelectedIndex(FormatModel.getFormatIndex(format));
		minimizedExpr.setExpression(outputExprs.getMinimalExpression(output));
		setAsExpr.setEnabled(output != null && outputExprs.isExpressionMinimal(output));
	}

	private String getCurrentVariable() {
		return selector.getSelectedOutput();
	}

	private static class FormatModel extends AbstractListModel
		implements ComboBoxModel {
		private final String[] choices;
		private int selected;

		private FormatModel() {
			selected = 0;
			choices = new String[2];
			localeChanged();
		}

		static int getFormatIndex(int choice) {
			return choice == AnalyzerModel.FORMAT_PRODUCT_OF_SUMS ? 1 : 0;
		}

		void localeChanged() {
			choices[0] = Strings.get("minimizedSumOfProducts");
			choices[1] = Strings.get("minimizedProductOfSums");
			fireContentsChanged(this, 0, choices.length);
		}

		int getSelectedFormat() {
			return selected == 1 ? AnalyzerModel.FORMAT_PRODUCT_OF_SUMS : AnalyzerModel.FORMAT_SUM_OF_PRODUCTS;
		}

		public int getSize() {
			return choices.length;
		}

		public Object getElementAt(int index) {
			return choices[index];
		}

		public Object getSelectedItem() {
			return choices[selected];
		}

		public void setSelectedItem(Object value) {
			for (int i = 0; i < choices.length; i++) {
				if (choices[i].equals(value)) {
					selected = i;
				}
			}
		}
	}

	private class MyListener
		implements OutputExpressionsListener, ActionListener, ItemListener {
		public void expressionChanged(OutputExpressionsEvent event) {
			String output = getCurrentVariable();
			if (event.getType() == OutputExpressionsEvent.OUTPUT_MINIMAL
				&& event.getVariable().equals(output)) {
				minimizedExpr.setExpression(outputExprs.getMinimalExpression(output));
				MinimizedTab.this.validate();
			}
			setAsExpr.setEnabled(output != null && outputExprs.isExpressionMinimal(output));
			int format = outputExprs.getMinimizedFormat(output);
			formatChoice.setSelectedIndex(FormatModel.getFormatIndex(format));
		}

		public void actionPerformed(ActionEvent event) {
			String output = getCurrentVariable();
			int format = outputExprs.getMinimizedFormat(output);
			formatChoice.setSelectedIndex(FormatModel.getFormatIndex(format));
			outputExprs.setExpression(output, outputExprs.getMinimalExpression(output));
		}

		public void itemStateChanged(ItemEvent event) {
			if (event.getSource() == formatChoice) {
				String output = getCurrentVariable();
				FormatModel model = (FormatModel) formatChoice.getModel();
				outputExprs.setMinimizedFormat(output, model.getSelectedFormat());
			} else {
				updateTab();
			}
		}
	}
}
