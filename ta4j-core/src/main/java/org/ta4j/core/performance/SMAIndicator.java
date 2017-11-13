package org.ta4j.core.performance;

import org.ta4j.core.Decimal;

public class SMAIndicator extends AbstractIndicator<Decimal> {

    private final Indicator<Decimal> indicator;
    private final int timeFrame;

    public SMAIndicator(Indicator<Decimal> indicator, int timeFrame){
        super(indicator.getTimeSeries());
        this.indicator = indicator;
        this.timeFrame = timeFrame;
    }

    @Override
    public Decimal getValue(int index) {
        Decimal sum = Decimal.ZERO;
        if (index == getTimeSeries().getCapacity()-1){
            System.out.print("");
        }
        for (int i = Math.max(0, index - timeFrame + 1); i <= index; i++) {
            sum = sum.plus(indicator.getValue(i));
        }

        final int realTimeFrame = Math.min(timeFrame, index + 1);
        return sum.dividedBy(Decimal.valueOf(realTimeFrame));
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " timeFrame: " + timeFrame;
    }
}
