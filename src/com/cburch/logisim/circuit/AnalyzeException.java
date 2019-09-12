/* Copyright (c) 2010, Carl Burch. License information is located in the
 * com.cburch.logisim.Main source code and at www.cburch.com/logisim/. */

package com.cburch.logisim.circuit;

import com.cburch.logisim.util.StringUtil;

class AnalyzeException extends Exception {
	AnalyzeException() {
	}

	AnalyzeException(String message) {
		super(message);
	}

	public static class Circular extends AnalyzeException {
		public Circular() {
			super(Strings.get("analyzeCircularError"));
		}
	}

	public static class Conflict extends AnalyzeException {
		public Conflict() {
			super(Strings.get("analyzeConflictError"));
		}
	}

	public static class CannotHandle extends AnalyzeException {
		public CannotHandle(String reason) {
			super(StringUtil.format(Strings.get("analyzeCannotHandleError"), reason));
		}
	}
}
