/* Copyright (c) 2010, Carl Burch. License information is located in the
 * com.cburch.logisim.Main source code and at www.cburch.com/logisim/. */

package com.cburch.logisim.util;

class Strings {
	static final LocaleManager source
		= new LocaleManager("logisim", "util");

	public static LocaleManager getLocaleManager() {
		return source;
	}

	public static String get(String key) {
		return source.get(key);
	}

	public static StringGetter getter(String key) {
		return source.getter(key);
	}
}
