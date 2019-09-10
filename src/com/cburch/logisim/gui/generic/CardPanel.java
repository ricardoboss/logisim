/* Copyright (c) 2010, Carl Burch. License information is located in the
 * com.cburch.logisim.Main source code and at www.cburch.com/logisim/. */

package com.cburch.logisim.gui.generic;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.util.ArrayList;

public class CardPanel extends JPanel {
	private ArrayList<ChangeListener> listeners;
	private String current;

	public CardPanel() {
		super(new CardLayout());
		listeners = new ArrayList<ChangeListener>();
		current = "";
	}

	public void addChangeListener(ChangeListener listener) {
		listeners.add(listener);
	}

	public void addView(String name, Component comp) {
		add(comp, name);
	}

	public String getView() {
		return current;
	}

	public void setView(String choice) {
		if (choice == null) choice = "";
		String oldChoice = current;
		if (!oldChoice.equals(choice)) {
			current = choice;
			((CardLayout) getLayout()).show(this, choice);
			ChangeEvent e = new ChangeEvent(this);
			for (ChangeListener listener : listeners) {
				listener.stateChanged(e);
			}
		}
	}

}
