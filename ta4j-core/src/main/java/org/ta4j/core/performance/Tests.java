package org.ta4j.core.performance;

import org.junit.Test;
import org.ta4j.core.Decimal;
import org.ta4j.core.Tick;

import java.util.ArrayList;
import java.util.List;

public class Tests {

    @Test
    public void testNewStructure(){
        double[] input = getInput(54 * 5 * 24 * 60 * 3);
        MockBarTimeSeries series = new MockBarTimeSeries(input);
        long start = System.currentTimeMillis();
        Decimal average = smaCalculations(series);
        long end = System.currentTimeMillis();

        System.out.println(String.format("[Ta4j] time: %s lastValue: %s",(end - start), average));
    }

    private Decimal smaCalculations(BarTimeSeries series){
        ClosePriceIndicator closePriceIndicator = new ClosePriceIndicator(series);
        Decimal average = Decimal.NaN;
        for (int h = 2; h < 201; h++) {
            SMAIndicator sma = new SMAIndicator(closePriceIndicator,h);
            for (int i = 0; i < series.getCapacity(); i++) {
                average = sma.getValue(i);
                //System.out.println(average);
            }
        }
        return average;
    }


    private double[] getInput(int capacity){
        int initialCapacity = capacity;
        double[] input = new double[initialCapacity];
        List<Tick> ticks = new ArrayList<>();

        for (int i = 0; i < input.length; i++) {
            input[i] = i;
        }
        return input;
    }
}
