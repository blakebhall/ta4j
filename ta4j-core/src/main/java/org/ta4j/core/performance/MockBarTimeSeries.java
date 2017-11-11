package org.ta4j.core.performance;

import org.ta4j.core.Decimal;

import java.time.ZonedDateTime;

public class MockBarTimeSeries extends BarTimeSeries{



    public MockBarTimeSeries(int capazity) {
        super(capazity);
    }

    public MockBarTimeSeries(double[] onePrices){
        super(onePrices.length);
        emplace(onePrices);
    }

    public void emplace(double onePrice){
        super.emplace(ZonedDateTime.now(), Decimal.valueOf(onePrice),Decimal.valueOf(onePrice),
                Decimal.valueOf(onePrice),Decimal.valueOf(onePrice), Decimal.valueOf(onePrice));
    }

    public void emplace(double[] onePrices){
        for(double onePrice: onePrices) {
            super.emplace(ZonedDateTime.now(), Decimal.valueOf(onePrice), Decimal.valueOf(onePrice),
                    Decimal.valueOf(onePrice), Decimal.valueOf(onePrice), Decimal.valueOf(onePrice));
        }
    }
}
