/* Copyright (c) 2010, Carl Burch. License information is located in the
 * com.cburch.logisim.Main source code and at www.cburch.com/logisim/. */

package com.cburch.draw.tools;

import com.cburch.draw.model.CanvasObject;
import com.cburch.draw.shapes.DrawAttr;
import com.cburch.draw.shapes.Rectangle;
import com.cburch.logisim.data.Attribute;
import com.cburch.logisim.util.Icons;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class RectangleTool extends RectangularTool {
	private final DrawingAttributeSet attrs;

	public RectangleTool(DrawingAttributeSet attrs) {
		this.attrs = attrs;
	}

	@Override
	public Icon getIcon() {
		return Icons.getIcon("drawrect.gif");
	}

	@Override
	public List<Attribute<?>> getAttributes() {
		return DrawAttr.getFillAttributes(attrs.getValue(DrawAttr.PAINT_TYPE));
	}

	@Override
	public CanvasObject createShape(int x, int y, int w, int h) {
		return attrs.applyTo(new Rectangle(x, y, w, h));
	}

	@Override
	public void drawShape(Graphics g, int x, int y, int w, int h) {
		g.drawRect(x, y, w, h);
	}

	@Override
	public void fillShape(Graphics g, int x, int y, int w, int h) {
		g.fillRect(x, y, w, h);
	}
}
