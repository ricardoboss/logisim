/* Copyright (c) 2010, Carl Burch. License information is located in the
 * com.cburch.logisim.Main source code and at www.cburch.com/logisim/. */

package com.cburch.logisim.gui.generic;

import com.cburch.logisim.prefs.PrefMonitor;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class BasicZoomModel implements ZoomModel {
	private final double[] zoomOptions;

	private final PropertyChangeSupport support;
	private double zoomFactor;
	private boolean showGrid;

	public BasicZoomModel(PrefMonitor<Boolean> gridPref,
						  PrefMonitor<Double> zoomPref, double[] zoomOpts) {
		zoomOptions = zoomOpts;
		support = new PropertyChangeSupport(this);
		zoomFactor = 1.0;
		showGrid = true;

		setZoomFactor(zoomPref.get());
		setShowGrid(gridPref.getBoolean());
	}

	public void addPropertyChangeListener(String prop, PropertyChangeListener l) {
		support.addPropertyChangeListener(prop, l);
	}

	public void removePropertyChangeListener(String prop,
											 PropertyChangeListener l) {
		support.removePropertyChangeListener(prop, l);
	}

	public boolean getShowGrid() {
		return showGrid;
	}

	public void setShowGrid(boolean value) {
		if (value != showGrid) {
			showGrid = value;
			support.firePropertyChange(ZoomModel.SHOW_GRID, !value, value);
		}
	}

	public double getZoomFactor() {
		return zoomFactor;
	}

	public void setZoomFactor(double value) {
		double oldValue = zoomFactor;
		if (value != oldValue) {
			zoomFactor = value;
			support.firePropertyChange(ZoomModel.ZOOM, oldValue,
				value);
		}
	}

	public double[] getZoomOptions() {
		return zoomOptions;
	}
}
