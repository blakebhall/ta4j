package org.ta4j.core.performance;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ta4j.core.Indicator;
import org.ta4j.core.TimeSeries;

/**
 * Abstract {@link Indicator indicator}.
 * <p/>
 * TODO: implement caching algorithm
 */
public abstract class AbstractIndicator<T> implements org.ta4j.core.performance.Indicator<T>{

    /** The logger */
    protected final Logger log = LoggerFactory.getLogger(getClass());

    private BarTimeSeries series;

    /**
     * Constructor.
     * @param series the related time series
     */
    public AbstractIndicator(BarTimeSeries series) {
        this.series = series;
    }

    @Override
    public BarTimeSeries getTimeSeries() {
        return series;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }
}
