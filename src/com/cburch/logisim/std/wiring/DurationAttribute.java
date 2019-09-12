/* Copyright (c) 2010, Carl Burch. License information is located in the
 * com.cburch.logisim.Main source code and at www.cburch.com/logisim/. */

package com.cburch.logisim.std.wiring;

import com.cburch.logisim.data.Attribute;
import com.cburch.logisim.util.StringGetter;
import com.cburch.logisim.util.StringUtil;

import javax.swing.*;

public class DurationAttribute extends Attribute<Integer> {
	private final int min;
	private final int max;

	public DurationAttribute(String name, StringGetter disp, int min, int max) {
		super(name, disp);
		this.min = min;
		this.max = max;
	}

	@Override
	public Integer parse(String value) {
		try {
			Integer ret = Integer.valueOf(value);
			if (ret < min) {
				throw new NumberFormatException(StringUtil.format(Strings.get("durationSmallMessage"), "" + min));
			} else if (ret > max) {
				throw new NumberFormatException(StringUtil.format(Strings.get("durationLargeMessage"), "" + max));
			}
			return ret;
		} catch (NumberFormatException e) {
			throw new NumberFormatException(Strings.get("freqInvalidMessage"));
		}
	}

	@Override
	public String toDisplayString(Integer value) {
		if (value.equals(1)) {
			return Strings.get("clockDurationOneValue");
		} else {
			return StringUtil.format(Strings.get("clockDurationValue"),
				value.toString());
		}
	}

	@Override
	public java.awt.Component getCellEditor(Integer value) {
		JTextField field = new JTextField();
		field.setText(value.toString());
		return field;
	}

}
