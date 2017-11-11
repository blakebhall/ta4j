package org.ta4j.core.performance;

import org.ta4j.core.Decimal;

public class ClosePriceIndicator extends AbstractIndicator<Decimal> {

    /**
     * Constructor.
     *
     * @param series the related time series
     */
    public ClosePriceIndicator(BarTimeSeries series) {
        super(series);
    }

    @Override
    public Decimal getValue(int index) {
        return getTimeSeries().closePrice[index];
    }
}
