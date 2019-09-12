/* Copyright (c) 2010, Carl Burch. License information is located in the
 * com.cburch.logisim.Main source code and at www.cburch.com/logisim/. */

package com.cburch.logisim.gui.generic;

import javax.swing.*;
import java.awt.*;

public interface CanvasPaneContents extends Scrollable {
	void setCanvasPane(CanvasPane pane);

	void recomputeSize();

	// from Scrollable
	Dimension getPreferredScrollableViewportSize();

	int getScrollableBlockIncrement(Rectangle visibleRect,
									int orientation, int direction);

	boolean getScrollableTracksViewportHeight();

	boolean getScrollableTracksViewportWidth();

	int getScrollableUnitIncrement(Rectangle visibleRect,
								   int orientation, int direction);
}
