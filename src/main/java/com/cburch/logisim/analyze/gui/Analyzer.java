/* Copyright (c) 2010, Carl Burch. License information is located in the
 * com.cburch.logisim.Main source code and at www.cburch.com/logisim/. */

package com.cburch.logisim.analyze.gui;

import com.cburch.logisim.analyze.model.AnalyzerModel;
import com.cburch.logisim.gui.generic.LFrame;
import com.cburch.logisim.gui.menu.LogisimMenuBar;
import com.cburch.logisim.util.LocaleListener;
import com.cburch.logisim.util.LocaleManager;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

public class Analyzer extends LFrame {
	// used by circuit analysis to select the relevant tab automatically.
	public static final int INPUTS_TAB = 0;
	public static final int OUTPUTS_TAB = 1;
	public static final int TABLE_TAB = 2;
	public static final int EXPRESSION_TAB = 3;
	private static final int MINIMIZED_TAB = 4;
	private final AnalyzerModel model = new AnalyzerModel();
	private final VariableTab inputsPanel;
	private final VariableTab outputsPanel;
	private final TableTab truthTablePanel;
	private final ExpressionTab expressionPanel;
	private final MinimizedTab minimizedPanel;
	private final BuildCircuitButton buildCircuit;
	private JTabbedPane tabbedPane = new JTabbedPane();

	Analyzer() {
		inputsPanel = new VariableTab(model.getInputs());
		outputsPanel = new VariableTab(model.getOutputs());
		truthTablePanel = new TableTab(model.getTruthTable());
		expressionPanel = new ExpressionTab(model);
		minimizedPanel = new MinimizedTab(model);
		buildCircuit = new BuildCircuitButton(this, model);

		truthTablePanel.addMouseListener(new TruthTableMouseListener());

		tabbedPane = new JTabbedPane();
		addTab(INPUTS_TAB, inputsPanel);
		addTab(OUTPUTS_TAB, outputsPanel);
		addTab(TABLE_TAB, truthTablePanel);
		addTab(EXPRESSION_TAB, expressionPanel);
		addTab(MINIMIZED_TAB, minimizedPanel);

		Container contents = getContentPane();
		JPanel vertStrut = new JPanel(null);
		vertStrut.setPreferredSize(new Dimension(0, 300));
		JPanel horzStrut = new JPanel(null);
		horzStrut.setPreferredSize(new Dimension(450, 0));
		JPanel buttonPanel = new JPanel();
		buttonPanel.add(buildCircuit);
		contents.add(vertStrut, BorderLayout.WEST);
		contents.add(horzStrut, BorderLayout.NORTH);
		contents.add(tabbedPane, BorderLayout.CENTER);
		contents.add(buttonPanel, BorderLayout.SOUTH);

		DefaultRegistry registry = new DefaultRegistry(getRootPane());
		inputsPanel.registerDefaultButtons(registry);
		outputsPanel.registerDefaultButtons(registry);
		expressionPanel.registerDefaultButtons(registry);

		MyListener myListener = new MyListener();
		LocaleManager.addLocaleListener(myListener);
		myListener.localeChanged();

		LogisimMenuBar menubar = new LogisimMenuBar(this, null);
		setJMenuBar(menubar);
		EditListener editListener = new EditListener();
		editListener.register(menubar);
	}

	public static void main(String[] args) {
		Analyzer frame = new Analyzer();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);
	}

	private void addTab(int index, final JComponent comp) {
		final JScrollPane pane = new JScrollPane(comp,
			ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
			ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		if (comp instanceof TableTab) {
			pane.setVerticalScrollBar(((TableTab) comp).getVerticalScrollBar());
		}
		pane.addComponentListener(new ComponentListener() {
			public void componentResized(ComponentEvent event) {
				int width = pane.getViewport().getWidth();
				comp.setSize(new Dimension(width, comp.getHeight()));
			}

			public void componentMoved(ComponentEvent arg0) {
			}

			public void componentShown(ComponentEvent arg0) {
			}

			public void componentHidden(ComponentEvent arg0) {
			}
		});
		tabbedPane.insertTab("Untitled", null, pane, null, index);
	}

	public AnalyzerModel getModel() {
		return model;
	}

	public void setSelectedTab(int index) {
		Object found = tabbedPane.getComponentAt(index);
		if (found instanceof AnalyzerTab) {
			((AnalyzerTab) found).updateTab();
		}
		tabbedPane.setSelectedIndex(index);
	}

	private class MyListener implements LocaleListener {
		public void localeChanged() {
			Analyzer.this.setTitle(Strings.get("analyzerWindowTitle"));
			tabbedPane.setTitleAt(INPUTS_TAB, Strings.get("inputsTab"));
			tabbedPane.setTitleAt(OUTPUTS_TAB, Strings.get("outputsTab"));
			tabbedPane.setTitleAt(TABLE_TAB, Strings.get("tableTab"));
			tabbedPane.setTitleAt(EXPRESSION_TAB, Strings.get("expressionTab"));
			tabbedPane.setTitleAt(MINIMIZED_TAB, Strings.get("minimizedTab"));
			tabbedPane.setToolTipTextAt(INPUTS_TAB, Strings.get("inputsTabTip"));
			tabbedPane.setToolTipTextAt(OUTPUTS_TAB, Strings.get("outputsTabTip"));
			tabbedPane.setToolTipTextAt(TABLE_TAB, Strings.get("tableTabTip"));
			tabbedPane.setToolTipTextAt(EXPRESSION_TAB, Strings.get("expressionTabTip"));
			tabbedPane.setToolTipTextAt(MINIMIZED_TAB, Strings.get("minimizedTabTip"));
			buildCircuit.setText(Strings.get("buildCircuitButton"));
			inputsPanel.localeChanged();
			outputsPanel.localeChanged();
			truthTablePanel.localeChanged();
			expressionPanel.localeChanged();
			minimizedPanel.localeChanged();
			buildCircuit.localeChanged();
		}
	}

	private class EditListener implements ActionListener, ChangeListener {
		private void register(LogisimMenuBar menubar) {
			menubar.addActionListener(LogisimMenuBar.CUT, this);
			menubar.addActionListener(LogisimMenuBar.COPY, this);
			menubar.addActionListener(LogisimMenuBar.PASTE, this);
			menubar.addActionListener(LogisimMenuBar.DELETE, this);
			menubar.addActionListener(LogisimMenuBar.SELECT_ALL, this);
			tabbedPane.addChangeListener(this);
			enableItems(menubar);
		}

		public void actionPerformed(ActionEvent e) {
			Object src = e.getSource();
			Component c = tabbedPane.getSelectedComponent();
			if (c instanceof JScrollPane) {
				c = ((JScrollPane) c).getViewport().getView();
			}
			if (!(c instanceof TabInterface)) return;
			TabInterface tab = (TabInterface) c;
			if (src == LogisimMenuBar.CUT) {
				tab.copy();
				tab.delete();
			} else if (src == LogisimMenuBar.COPY) {
				tab.copy();
			} else if (src == LogisimMenuBar.PASTE) {
				tab.paste();
			} else if (src == LogisimMenuBar.DELETE) {
				tab.delete();
			} else if (src == LogisimMenuBar.SELECT_ALL) {
				tab.selectAll();
			}
		}

		private void enableItems(LogisimMenuBar menubar) {
			Component c = tabbedPane.getSelectedComponent();
			if (c instanceof JScrollPane) {
				c = ((JScrollPane) c).getViewport().getView();
			}
			boolean support = c instanceof TabInterface;
			menubar.setEnabled(LogisimMenuBar.CUT, support);
			menubar.setEnabled(LogisimMenuBar.COPY, support);
			menubar.setEnabled(LogisimMenuBar.PASTE, support);
			menubar.setEnabled(LogisimMenuBar.DELETE, support);
			menubar.setEnabled(LogisimMenuBar.SELECT_ALL, support);
		}

		public void stateChanged(ChangeEvent e) {
			enableItems((LogisimMenuBar) getJMenuBar());

			Object selected = tabbedPane.getSelectedComponent();
			if (selected instanceof JScrollPane) {
				selected = ((JScrollPane) selected).getViewport().getView();
			}
			if (selected instanceof AnalyzerTab) {
				((AnalyzerTab) selected).updateTab();
			}
		}
	}
}
