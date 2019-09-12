/* Copyright (c) 2010, Carl Burch. License information is located in the
 * com.cburch.logisim.Main source code and at www.cburch.com/logisim/. */

package com.cburch.logisim.prefs;

import com.cburch.logisim.Main;
import com.cburch.logisim.circuit.RadixOption;
import com.cburch.logisim.data.Direction;
import com.cburch.logisim.gui.start.Startup;
import com.cburch.logisim.util.LocaleListener;
import com.cburch.logisim.util.LocaleManager;
import com.cburch.logisim.util.PropertyChangeWeakSupport;

import javax.swing.*;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.prefs.BackingStoreException;
import java.util.prefs.PreferenceChangeEvent;
import java.util.prefs.PreferenceChangeListener;
import java.util.prefs.Preferences;

public class AppPreferences {
	// Template preferences
	public static final int TEMPLATE_UNKNOWN = -1;
	public static final int TEMPLATE_EMPTY = 0;
	public static final int TEMPLATE_PLAIN = 1;
	public static final int TEMPLATE_CUSTOM = 2;
	public static final String TEMPLATE_TYPE = "templateType";
	public static final String TEMPLATE_FILE = "templateFile";
	// International preferences
	public static final String SHAPE_SHAPED = "shaped";
	public static final String SHAPE_RECTANGULAR = "rectangular";
	public static final String SHAPE_DIN40700 = "din40700";
	public static final PrefMonitor<String> GATE_SHAPE
		= create(new PrefMonitorStringOpts("gateShape",
		new String[]{SHAPE_SHAPED, SHAPE_RECTANGULAR, SHAPE_DIN40700},
		SHAPE_SHAPED));
	public static final PrefMonitor<String> LOCALE
		= create(new LocalePreference());
	public static final PrefMonitor<Boolean> ACCENTS_REPLACE
		= create(new PrefMonitorBoolean("accentsReplace", false));
	// Window preferences
	public static final String TOOLBAR_HIDDEN = "hidden";
	public static final String TOOLBAR_DOWN_MIDDLE = "downMiddle";
	public static final PrefMonitor<Boolean> SHOW_TICK_RATE
		= create(new PrefMonitorBoolean("showTickRate", false));
	public static final PrefMonitor<String> TOOLBAR_PLACEMENT
		= create(new PrefMonitorStringOpts("toolbarPlacement", new String[]{
		Direction.NORTH.toString(), Direction.SOUTH.toString(),
		Direction.EAST.toString(), Direction.WEST.toString(),
		TOOLBAR_DOWN_MIDDLE, TOOLBAR_HIDDEN},
		Direction.NORTH.toString()));
	// Layout preferences
	public static final String ADD_AFTER_UNCHANGED = "unchanged";
	public static final String ADD_AFTER_EDIT = "edit";
	public static final PrefMonitor<Boolean> PRINTER_VIEW
		= create(new PrefMonitorBoolean("printerView", false));
	public static final PrefMonitor<Boolean> ATTRIBUTE_HALO
		= create(new PrefMonitorBoolean("attributeHalo", true));
	public static final PrefMonitor<Boolean> COMPONENT_TIPS
		= create(new PrefMonitorBoolean("componentTips", true));
	public static final PrefMonitor<Boolean> MOVE_KEEP_CONNECT
		= create(new PrefMonitorBoolean("keepConnected", true));
	public static final PrefMonitor<Boolean> ADD_SHOW_GHOSTS
		= create(new PrefMonitorBoolean("showGhosts", true));
	public static final PrefMonitor<String> ADD_AFTER
		= create(new PrefMonitorStringOpts("afterAdd",
		new String[]{ADD_AFTER_EDIT, ADD_AFTER_UNCHANGED}, ADD_AFTER_EDIT));
	// Experimental preferences
	public static final String ACCEL_DEFAULT = "default";
	public static final String ACCEL_NONE = "none";
	public static final String ACCEL_OPENGL = "opengl";
	public static final String ACCEL_D3D = "d3d";
	public static final PrefMonitor<String> GRAPHICS_ACCELERATION
		= create(new PrefMonitorStringOpts("graphicsAcceleration",
		new String[]{ACCEL_DEFAULT, ACCEL_NONE, ACCEL_OPENGL, ACCEL_D3D},
		ACCEL_DEFAULT));
	// hidden window preferences - not part of the preferences dialog, changes
	// to preference does not affect current windows, and the values are not
	// saved until the application is closed
	public static final String RECENT_PROJECTS = "recentProjects";
	public static final PrefMonitor<Double> TICK_FREQUENCY
		= create(new PrefMonitorDouble("tickFrequency", 1.0));
	public static final PrefMonitor<Boolean> LAYOUT_SHOW_GRID
		= create(new PrefMonitorBoolean("layoutGrid", true));
	public static final PrefMonitor<Double> LAYOUT_ZOOM
		= create(new PrefMonitorDouble("layoutZoom", 1.0));
	public static final PrefMonitor<Boolean> APPEARANCE_SHOW_GRID
		= create(new PrefMonitorBoolean("appearanceGrid", true));
	public static final PrefMonitor<Double> APPEARANCE_ZOOM
		= create(new PrefMonitorDouble("appearanceZoom", 1.0));
	public static final PrefMonitor<Integer> WINDOW_STATE
		= create(new PrefMonitorInt("windowState", JFrame.NORMAL));
	public static final PrefMonitor<Integer> WINDOW_WIDTH
		= create(new PrefMonitorInt("windowWidth", 640));
	public static final PrefMonitor<Integer> WINDOW_HEIGHT
		= create(new PrefMonitorInt("windowHeight", 480));
	public static final PrefMonitor<String> WINDOW_LOCATION
		= create(new PrefMonitorString("windowLocation", "0,0"));
	public static final PrefMonitor<Double> WINDOW_MAIN_SPLIT
		= create(new PrefMonitorDouble("windowMainSplit", 0.25));
	public static final PrefMonitor<Double> WINDOW_LEFT_SPLIT
		= create(new PrefMonitorDouble("windowLeftSplit", 0.5));
	public static final PrefMonitor<String> DIALOG_DIRECTORY
		= create(new PrefMonitorString("dialogDirectory", ""));
	public static final PrefMonitor<String> POKE_WIRE_RADIX1;
	public static final PrefMonitor<String> POKE_WIRE_RADIX2;
	private static final String TEMPLATE = "template";
	private static final RecentProjects recentProjects = new RecentProjects();
	private static final PropertyChangeWeakSupport propertySupport
		= new PropertyChangeWeakSupport(AppPreferences.class);
	// class variables for maintaining consistency between properties,
	// internal variables, and other classes
	private static Preferences prefs = null;
	private static MyListener myListener = null;
	private static int templateType = TEMPLATE_PLAIN;
	private static File templateFile = null;
	private static Template plainTemplate = null;
	private static Template emptyTemplate = null;
	private static Template customTemplate = null;
	private static File customTemplateFile = null;

	static {
		RadixOption[] radixOptions = RadixOption.OPTIONS;
		String[] radixStrings = new String[radixOptions.length];
		for (int i = 0; i < radixOptions.length; i++) {
			radixStrings[i] = radixOptions[i].getSaveString();
		}
		POKE_WIRE_RADIX1 = create(new PrefMonitorStringOpts("pokeRadix1",
			radixStrings, RadixOption.RADIX_2.getSaveString()));
		POKE_WIRE_RADIX2 = create(new PrefMonitorStringOpts("pokeRadix2",
			radixStrings, RadixOption.RADIX_10_SIGNED.getSaveString()));
	}

	private static <E> PrefMonitor<E> create(PrefMonitor<E> monitor) {
		return monitor;
	}

	public static void clear() {
		Preferences p = getPrefs(true);
		try {
			p.clear();
		} catch (BackingStoreException ignored) {
		}
	}

	static Preferences getPrefs() {
		return getPrefs(false);
	}

	private static Preferences getPrefs(boolean shouldClear) {
		if (prefs == null) {
			synchronized (AppPreferences.class) {
				if (prefs == null) {
					Preferences p = Preferences.userNodeForPackage(Main.class);
					if (shouldClear) {
						try {
							p.clear();
						} catch (BackingStoreException ignored) {
						}
					}
					myListener = new MyListener();
					p.addPreferenceChangeListener(myListener);
					prefs = p;

					setTemplateFile(convertFile(p.get(TEMPLATE_FILE, null)));
					setTemplateType(p.getInt(TEMPLATE_TYPE, TEMPLATE_PLAIN));
				}
			}
		}
		return prefs;
	}

	private static File convertFile(String fileName) {
		if (fileName == null || fileName.equals("")) {
			return null;
		} else {
			File file = new File(fileName);
			return file.canRead() ? file : null;
		}
	}

	//
	// PropertyChangeSource methods
	//
	public static void addPropertyChangeListener(PropertyChangeListener listener) {
		propertySupport.addPropertyChangeListener(listener);
	}

	public static void removePropertyChangeListener(PropertyChangeListener listener) {
		propertySupport.removePropertyChangeListener(listener);
	}

	public static void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
		propertySupport.addPropertyChangeListener(propertyName, listener);
	}

	public static void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
		propertySupport.removePropertyChangeListener(propertyName, listener);
	}

	static void firePropertyChange(String property, boolean oldVal, boolean newVal) {
		propertySupport.firePropertyChange(property, oldVal, newVal);
	}

	static void firePropertyChange(String property, Object oldVal, Object newVal) {
		propertySupport.firePropertyChange(property, oldVal, newVal);
	}

	//
	// accessor methods
	//
	public static int getTemplateType() {
		getPrefs();
		int ret = templateType;
		if (ret == TEMPLATE_CUSTOM && templateFile == null) {
			ret = TEMPLATE_UNKNOWN;
		}
		return ret;
	}

	public static void setTemplateType(int value) {
		getPrefs();
		if (value != TEMPLATE_PLAIN && value != TEMPLATE_EMPTY && value != TEMPLATE_CUSTOM) {
			value = TEMPLATE_UNKNOWN;
		}
		if (value != TEMPLATE_UNKNOWN && templateType != value) {
			getPrefs().putInt(TEMPLATE_TYPE, value);
		}
	}

	public static File getTemplateFile() {
		getPrefs();
		return templateFile;
	}

	public static void setTemplateFile(File value) {
		getPrefs();
		setTemplateFile(value, null);
	}

	public static void setTemplateFile(File value, Template template) {
		getPrefs();
		if (value != null && !value.canRead()) value = null;
		if (!Objects.equals(value, templateFile)) {
			try {
				customTemplateFile = template == null ? null : value;
				customTemplate = template;
				getPrefs().put(TEMPLATE_FILE, value == null ? "" : value.getCanonicalPath());
			} catch (IOException ignored) {
			}
		}
	}

	public static void handleGraphicsAcceleration() {
		String accel = GRAPHICS_ACCELERATION.get();
		try {
			switch (accel) {
				case ACCEL_NONE:
					System.setProperty("sun.java2d.opengl", "False");
					System.setProperty("sun.java2d.d3d", "False");
					break;
				case ACCEL_OPENGL:
					System.setProperty("sun.java2d.opengl", "True");
					System.setProperty("sun.java2d.d3d", "False");
					break;
				case ACCEL_D3D:
					System.setProperty("sun.java2d.opengl", "False");
					System.setProperty("sun.java2d.d3d", "True");
					break;
			}
		} catch (Throwable ignored) {
		}
	}

	//
	// template methods
	//
	public static Template getTemplate() {
		getPrefs();
		switch (templateType) {
			case TEMPLATE_EMPTY:
				return getEmptyTemplate();
			case TEMPLATE_CUSTOM:
				return getCustomTemplate();
			case TEMPLATE_PLAIN:
			default:
				return getPlainTemplate();
		}
	}

	public static Template getEmptyTemplate() {
		if (emptyTemplate == null) emptyTemplate = Template.createEmpty();
		return emptyTemplate;
	}

	private static Template getPlainTemplate() {
		if (plainTemplate == null) {
			ClassLoader ld = Startup.class.getClassLoader();
			InputStream in = ld.getResourceAsStream("resources/logisim/default.templ");
			if (in == null) {
				plainTemplate = getEmptyTemplate();
			} else {
				try {
					try (in) {
						plainTemplate = Template.create(in);
					}
				} catch (Throwable e) {
					plainTemplate = getEmptyTemplate();
				}
			}
		}
		return plainTemplate;
	}

	private static Template getCustomTemplate() {
		File toRead = templateFile;
		if (customTemplateFile == null || !(customTemplateFile.equals(toRead))) {
			if (toRead == null) {
				customTemplate = null;
				customTemplateFile = null;
			} else {
				try (FileInputStream reader = new FileInputStream(toRead)) {
					customTemplate = Template.create(reader);
					customTemplateFile = templateFile;
				} catch (Throwable t) {
					setTemplateFile(null);
					customTemplate = null;
					customTemplateFile = null;
				}
			}
		}
		return customTemplate == null ? getPlainTemplate() : customTemplate;
	}

	//
	// recent projects
	//
	public static List<File> getRecentFiles() {
		return recentProjects.getRecentFiles();
	}

	public static void updateRecentFile(File file) {
		recentProjects.updateRecent(file);
	}

	//
	// methods for accessing preferences
	//
	private static class MyListener implements PreferenceChangeListener,
		LocaleListener {
		public void preferenceChange(PreferenceChangeEvent event) {
			Preferences prefs = event.getNode();
			String prop = event.getKey();
			if (ACCENTS_REPLACE.getIdentifier().equals(prop)) {
				getPrefs();
				LocaleManager.setReplaceAccents(ACCENTS_REPLACE.getBoolean());
			} else if (prop.equals(TEMPLATE_TYPE)) {
				int oldValue = templateType;
				int value = prefs.getInt(TEMPLATE_TYPE, TEMPLATE_UNKNOWN);
				if (value != oldValue) {
					templateType = value;
					propertySupport.firePropertyChange(TEMPLATE, oldValue, value);
					propertySupport.firePropertyChange(TEMPLATE_TYPE, oldValue, value);
				}
			} else if (prop.equals(TEMPLATE_FILE)) {
				File oldValue = templateFile;
				File value = convertFile(prefs.get(TEMPLATE_FILE, null));
				if (!Objects.equals(value, oldValue)) {
					templateFile = value;
					if (templateType == TEMPLATE_CUSTOM) {
						customTemplate = null;
						propertySupport.firePropertyChange(TEMPLATE, oldValue, value);
					}
					propertySupport.firePropertyChange(TEMPLATE_FILE, oldValue, value);
				}
			}
		}

		public void localeChanged() {
			Locale loc = LocaleManager.getLocale();
			String lang = loc.getLanguage();
			if (LOCALE != null) {
				LOCALE.set(lang);
			}
		}
	}

	//
	// LocalePreference
	//
	private static class LocalePreference extends PrefMonitorString {
		LocalePreference() {
			super("locale", "");

			String localeStr = this.get();
			if (localeStr != null && !localeStr.equals("")) {
				LocaleManager.setLocale(new Locale(localeStr));
			}
			LocaleManager.addLocaleListener(myListener);
			myListener.localeChanged();
		}

		private static Locale findLocale(String lang) {
			Locale[] check;
			for (int set = 0; set < 2; set++) {
				if (set == 0) check = new Locale[]{Locale.getDefault(), Locale.ENGLISH};
				else check = Locale.getAvailableLocales();
				for (Locale loc : check) {
					if (loc != null && loc.getLanguage().equals(lang)) {
						return loc;
					}
				}
			}
			return null;
		}

		@Override
		public void set(String value) {
			if (findLocale(value) != null) {
				super.set(value);
			}
		}
	}
}
