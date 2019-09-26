/* Copyright (c) 2010, Carl Burch. License information is located in the
 * com.cburch.logisim.Main source code and at www.cburch.com/logisim/. */

package com.cburch.logisim.gui.generic;

import com.cburch.logisim.util.WindowClosable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class LFrame extends JFrame implements WindowClosable {
	private static final String PATH = "logisim/img/logisim-icon-";
	private static final int[] SIZES = {16, 20, 24, 48, 64, 128};
	private static final int DEFAULT_SIZE = 48;
	private static List<Image> ICONS = null;
	private static Image DEFAULT_ICON = null;

	public LFrame() {
		LFrame.attachIcon(this);
	}

	public static void attachIcon(Window frame) {
		if (ICONS == null) {
			List<Image> loadedIcons = new ArrayList<>();
			ClassLoader loader = LFrame.class.getClassLoader();
			for (int size : SIZES) {
				URL url = loader.getResource(PATH + size + ".png");
				if (url != null) {
					ImageIcon icon = new ImageIcon(url);
					loadedIcons.add(icon.getImage());
					if (size == DEFAULT_SIZE) {
						DEFAULT_ICON = icon.getImage();
					}
				}
			}
			ICONS = loadedIcons;
		}

		boolean success = false;
		try {
			if (!ICONS.isEmpty()) {
				Method set = frame.getClass().getMethod("setIconImages", List.class);
				set.invoke(frame, ICONS);
				success = true;
			}
		} catch (Exception ignored) {
		}

		if (!success && frame instanceof JFrame && DEFAULT_ICON != null) {
			frame.setIconImage(DEFAULT_ICON);
		}
	}

	public void requestClose() {
		WindowEvent closing = new WindowEvent(this, WindowEvent.WINDOW_CLOSING);
		processWindowEvent(closing);
	}
}