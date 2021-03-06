/* Copyright (c) 2010, Carl Burch. License information is located in the
 * com.cburch.logisim.Main source code and at www.cburch.com/logisim/. */

package com.cburch.draw.canvas;

import com.cburch.draw.model.CanvasObject;

import java.util.Collection;
import java.util.EventObject;

public class SelectionEvent extends EventObject {
	public static final int ACTION_ADDED = 0;
	public static final int ACTION_REMOVED = 1;
	public static final int ACTION_HANDLE = 2;

	private final int action;
	private final Collection<CanvasObject> affected;

	public SelectionEvent(Selection source, int action,
						  Collection<CanvasObject> affected) {
		super(source);
		this.action = action;
		this.affected = affected;
	}

	public Selection getSelection() {
		return (Selection) getSource();
	}

	public int getAction() {
		return action;
	}

	public Collection<CanvasObject> getAffected() {
		return affected;
	}
}
