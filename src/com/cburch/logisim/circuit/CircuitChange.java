/* Copyright (c) 2010, Carl Burch. License information is located in the
 * com.cburch.logisim.Main source code and at www.cburch.com/logisim/. */

package com.cburch.logisim.circuit;

import com.cburch.logisim.comp.Component;
import com.cburch.logisim.data.Attribute;
import com.cburch.logisim.instance.StdAttr;
import com.cburch.logisim.std.wiring.Pin;

import java.util.Collection;

class CircuitChange {
	private static final int CLEAR = 0;
	private static final int ADD = 1;
	private static final int ADD_ALL = 2;
	private static final int REMOVE = 3;
	private static final int REMOVE_ALL = 4;
	private static final int REPLACE = 5;
	private static final int SET = 6;
	private static final int SET_FOR_CIRCUIT = 7;
	private final Circuit circuit;
	private final int type;
	private final Component comp;
	private final Attribute<?> attr;
	private final Object oldValue;
	private final Object newValue;
	private Collection<? extends Component> comps;

	private CircuitChange(Circuit circuit, int type, Component comp) {
		this(circuit, type, comp, null, null, null);
	}

	private CircuitChange(Circuit circuit, int type,
						  Collection<? extends Component> comps) {
		this(circuit, type, null, null, null, null);
		this.comps = comps;
	}

	private CircuitChange(Circuit circuit, int type, Component comp,
						  Attribute<?> attr, Object oldValue, Object newValue) {
		this.circuit = circuit;
		this.type = type;
		this.comp = comp;
		this.attr = attr;
		this.oldValue = oldValue;
		this.newValue = newValue;
	}

	public static CircuitChange clear(Circuit circuit,
									  Collection<Component> oldComponents) {
		return new CircuitChange(circuit, CLEAR, oldComponents);
	}

	public static CircuitChange add(Circuit circuit, Component comp) {
		return new CircuitChange(circuit, ADD, comp);
	}

	public static CircuitChange addAll(Circuit circuit,
									   Collection<? extends Component> comps) {
		return new CircuitChange(circuit, ADD_ALL, comps);
	}

	public static CircuitChange remove(Circuit circuit, Component comp) {
		return new CircuitChange(circuit, REMOVE, comp);
	}

	public static CircuitChange removeAll(Circuit circuit,
										  Collection<? extends Component> comps) {
		return new CircuitChange(circuit, REMOVE_ALL, comps);
	}

	public static CircuitChange replace(Circuit circuit,
										ReplacementMap replMap) {
		return new CircuitChange(circuit, REPLACE, null, null, null, replMap);
	}

	public static CircuitChange set(Circuit circuit, Component comp,
									Attribute<?> attr, Object value) {
		return new CircuitChange(circuit, SET, comp, attr, null, value);
	}

	public static CircuitChange set(Circuit circuit, Component comp,
									Attribute<?> attr, Object oldValue, Object newValue) {
		return new CircuitChange(circuit, SET, comp, attr, oldValue, newValue);
	}

	public static CircuitChange setForCircuit(Circuit circuit,
											  Attribute<?> attr, Object v) {
		return new CircuitChange(circuit, SET_FOR_CIRCUIT, null, attr, null, v);
	}

	public static CircuitChange setForCircuit(Circuit circuit,
											  Attribute<?> attr, Object oldValue, Object newValue) {
		return new CircuitChange(circuit, SET_FOR_CIRCUIT, null, attr, oldValue,
			newValue);
	}

	public Circuit getCircuit() {
		return circuit;
	}

	public int getType() {
		return type;
	}

	public Component getComponent() {
		return comp;
	}

	public Attribute<?> getAttribute() {
		return attr;
	}

	public Object getOldValue() {
		return oldValue;
	}

	public Object getNewValue() {
		return newValue;
	}

	CircuitChange getReverseChange() {
		switch (type) {
			case CLEAR:
			case REMOVE_ALL:
				return CircuitChange.addAll(circuit, comps);
			case ADD:
				return CircuitChange.remove(circuit, comp);
			case ADD_ALL:
				return CircuitChange.removeAll(circuit, comps);
			case REMOVE:
				return CircuitChange.add(circuit, comp);
			case SET:
				return CircuitChange.set(circuit, comp, attr, newValue, oldValue);
			case SET_FOR_CIRCUIT:
				return CircuitChange.setForCircuit(circuit, attr, newValue, oldValue);
			case REPLACE:
				return CircuitChange.replace(circuit,
					((ReplacementMap) newValue).getInverseMap());
			default:
				throw new IllegalArgumentException("unknown change type " + type);
		}
	}

	void execute(CircuitMutator mutator, ReplacementMap prevReplacements) {
		switch (type) {
			case CLEAR:
				mutator.clear(circuit);
				prevReplacements.reset();
				break;
			case ADD:
				prevReplacements.add(comp);
				break;
			case ADD_ALL:
				for (Component comp : comps) prevReplacements.add(comp);
				break;
			case REMOVE:
				prevReplacements.remove(comp);
				break;
			case REMOVE_ALL:
				for (Component comp : comps) prevReplacements.remove(comp);
				break;
			case REPLACE:
				prevReplacements.append((ReplacementMap) newValue);
				break;
			case SET:
				mutator.replace(circuit, prevReplacements);
				prevReplacements.reset();
				mutator.set(circuit, comp, attr, newValue);
				break;
			case SET_FOR_CIRCUIT:
				mutator.replace(circuit, prevReplacements);
				prevReplacements.reset();
				mutator.setForCircuit(circuit, attr, newValue);
				break;
			default:
				throw new IllegalArgumentException("unknown change type " + type);
		}
	}

	boolean concernsSupercircuit() {
		switch (type) {
			case CLEAR:
				return true;
			case ADD:
			case REMOVE:
				return comp.getFactory() instanceof Pin;
			case ADD_ALL:
			case REMOVE_ALL:
				for (Component comp : comps) {
					if (comp.getFactory() instanceof Pin) return true;
				}
				return false;
			case REPLACE:
				ReplacementMap repl = (ReplacementMap) newValue;
				for (Component comp : repl.getRemovals()) {
					if (comp.getFactory() instanceof Pin) return true;
				}
				for (Component comp : repl.getAdditions()) {
					if (comp.getFactory() instanceof Pin) return true;
				}
				return false;
			case SET:
				return comp.getFactory() instanceof Pin
					&& (attr == StdAttr.WIDTH || attr == Pin.ATTR_TYPE);
			default:
				return false;
		}
	}
}
