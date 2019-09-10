/* Copyright (c) 2011, Carl Burch. License information is located in the
 * com.cburch.logisim.Main source code and at www.cburch.com/logisim/. */

package com.cburch.logisim.gui.generic;

import java.awt.*;

public interface AttrTableModelRow {
	public String getLabel();

	public String getValue();

	public void setValue(Object value) throws AttrTableSetException;

	public boolean isValueEditable();

	public Component getEditor(Window parent);
}
