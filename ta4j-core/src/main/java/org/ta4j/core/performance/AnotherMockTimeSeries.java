package org.ta4j.core.performance;

import org.ta4j.core.BaseTimeSeries;
import org.ta4j.core.Tick;

import java.util.ArrayList;
import java.util.List;

public class AnotherMockTimeSeries extends BaseTimeSeries {

    public AnotherMockTimeSeries(double... data) {
        super(doublesToTicks(data));
    }

    private static List<Tick> doublesToTicks(double... data) {
        ArrayList<Tick> ticks = new ArrayList<>();
        for (int i = 0; i < data.length; i++) {
            ticks.add(new MockTick(data[i]));
            //ticks.add(new MockTick(ZonedDateTime.now().with(ChronoField.MILLI_OF_SECOND, i), data[i]));        }

        }
        return ticks;
    }
}
