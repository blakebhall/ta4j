package eu.verdelhan.ta4j.indicators.simple;

import eu.verdelhan.ta4j.Indicator;

public class ConstantIndicator<T extends Number> implements Indicator<T> {

	private T value;

	public ConstantIndicator(T t) {
		this.value = t;
	}

	@Override
	public T getValue(int index) {
		return value;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + " Value: " + value;
	}
}