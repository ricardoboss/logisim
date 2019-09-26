/* Copyright (c) 2010, Carl Burch. License information is located in the
 * com.cburch.logisim.Main source code and at www.cburch.com/logisim/. */

package com.cburch.logisim.gui.appear;

import com.cburch.draw.model.CanvasModel;
import com.cburch.draw.model.CanvasObject;
import com.cburch.draw.util.ZOrder;
import com.cburch.logisim.circuit.appear.AppearanceAnchor;
import com.cburch.logisim.data.Direction;
import com.cburch.logisim.data.Location;
import com.cburch.logisim.proj.Action;
import com.cburch.logisim.proj.Project;

import java.util.ArrayList;
import java.util.Map;

public class ClipboardActions extends Action {

	private final boolean remove;
	private final AppearanceCanvas canvas;
	private final CanvasModel canvasModel;
	private final Map<CanvasObject, Integer> affected;
	private final ClipboardContents newClipboard;
	private ClipboardContents oldClipboard;

	private ClipboardActions(boolean remove, AppearanceCanvas canvas) {
		this.remove = remove;
		this.canvas = canvas;
		this.canvasModel = canvas.getModel();

		ArrayList<CanvasObject> contents = new ArrayList<>();
		Direction anchorFacing = null;
		Location anchorLocation = null;
		ArrayList<CanvasObject> aff = new ArrayList<>();
		for (CanvasObject o : canvas.getSelection().getSelected()) {
			if (o.canRemove()) {
				aff.add(o);
				contents.add(o.clone());
			} else if (o instanceof AppearanceAnchor) {
				AppearanceAnchor anch = (AppearanceAnchor) o;
				anchorFacing = anch.getFacing();
				anchorLocation = anch.getLocation();
			}
		}
		contents.trimToSize();
		affected = ZOrder.getZIndex(aff, canvasModel);
		newClipboard = new ClipboardContents(contents, anchorLocation, anchorFacing);
	}

	public static Action cut(AppearanceCanvas canvas) {
		return new ClipboardActions(true, canvas);
	}

	public static Action copy(AppearanceCanvas canvas) {
		return new ClipboardActions(false, canvas);
	}

	@Override
	public String getName() {
		if (remove) {
			return Strings.get("cutSelectionAction");
		} else {
			return Strings.get("copySelectionAction");
		}
	}

	@Override
	public void doIt(Project proj) {
		oldClipboard = Clipboard.get();
		Clipboard.set(newClipboard);
		if (remove) {
			canvasModel.removeObjects(affected.keySet());
		}
	}

	@Override
	public void undo(Project proj) {
		if (remove) {
			canvasModel.addObjects(affected);
			canvas.getSelection().clearSelected();
			canvas.getSelection().setSelected(affected.keySet(), true);
		}
		Clipboard.set(oldClipboard);
	}

}
