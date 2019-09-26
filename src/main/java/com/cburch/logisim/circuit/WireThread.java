/* Copyright (c) 2010, Carl Burch. License information is located in the
 * com.cburch.logisim.Main source code and at www.cburch.com/logisim/. */

package com.cburch.logisim.circuit;

import com.cburch.logisim.util.SmallSet;

class WireThread {
	private final SmallSet<CircuitWires.ThreadBundle> bundles
		= new SmallSet<>();
	private WireThread parent;

	WireThread() {
		parent = this;
	}

	SmallSet<CircuitWires.ThreadBundle> getBundles() {
		return bundles;
	}

	void unite(WireThread other) {
		WireThread group = this.find();
		WireThread group2 = other.find();
		if (group != group2) group.parent = group2;
	}

	WireThread find() {
		WireThread ret = this;
		if (ret.parent != ret) {
			do ret = ret.parent; while (ret.parent != ret);
			this.parent = ret;
		}
		return ret;
	}
}
