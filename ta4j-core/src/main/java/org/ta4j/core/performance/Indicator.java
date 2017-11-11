package org.ta4j.core.performance;

import org.ta4j.core.TimeSeries;

import java.io.Serializable;

/**
 * Indicator over a {@link TimeSeries time series}.
 * <p></p>
 * For each index of the time series, returns a value of type <b>T</b>.
 * @param <T> the type of returned value (Double, Boolean, etc.)
 */
public interface Indicator<T> extends Serializable {

    /**
     * @param index the tick index
     * @return the value of the indicator
     */
    T getValue(int index);

    /**
     * @return the related time series
     */
    BarTimeSeries getTimeSeries();
}
