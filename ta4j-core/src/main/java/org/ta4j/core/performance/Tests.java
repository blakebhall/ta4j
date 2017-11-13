package org.ta4j.core.performance;

import org.junit.Test;
import org.ta4j.core.Decimal;
import org.ta4j.core.Tick;
import org.ta4j.core.TimeSeries;

import java.util.ArrayList;
import java.util.List;

public class Tests {

    @Test
    public void dataInArraysVSdataInObjects(){
        int capacity = 54 * 5 * 24 * 60 * 3;
        int upto = 30; // timeFrame SMA from 2 to 30
        double[] input = getInput(capacity);

        MockBarTimeSeries columnSeries = new MockBarTimeSeries(input);
        TimeSeries series = new AnotherMockTimeSeries(input);


        for (int i= 3; i<=upto; i++){ // for each SMA(2)-SMA(i) calculate every value and print time
            System.out.println("--------------Test (time frame 2-"+i+")--------------");
            long time = testNewStructure(columnSeries, i);
            long time2 = testOldStructure(series, i);
            String fastest = "[Classic     ]";
            if (time < time2){
                fastest = "[Column based]";
            }
            System.out.println(String.format("Fastest: %s diff: %d %n", fastest, Math.abs(time-time2)));


        }
    }

    public long testNewStructure(BarTimeSeries series, int upto){
        long start = System.currentTimeMillis();
        Decimal average = smaCalculations(series,4);
        long time = System.currentTimeMillis()-start;
        System.out.println(String.format("[Column based] time: %s lastValue: %s", time, average));
        return time;
    }


    public long testOldStructure(TimeSeries series, int upto){
        long start = System.currentTimeMillis();
        Decimal average = smaCalculations(series, upto);
        long time = System.currentTimeMillis()-start;
        System.out.println(String.format("[Classic     ] time: %s lastValue: %s", time, average));
        return time;
    }

    /**
     * Run calculations for new TimeSeries structure
     * @param series
     * @return
     */
    private Decimal smaCalculations(BarTimeSeries series, int upTo){
        ClosePriceIndicator closePriceIndicator = new ClosePriceIndicator(series);
        Decimal average = Decimal.NaN;
        for (int h = 2; h < upTo; h++) {
            SMAIndicator sma = new SMAIndicator(closePriceIndicator,h);
            for (int i = 0; i < series.getCapacity(); i++) {
                average = sma.getValue(i);
                //System.out.println(average);
            }
        }
        return average;
    }

    /**
     * Run calculations for old TimeSeries structure
     * @param series
     * @return
     */
    private Decimal smaCalculations(TimeSeries series, int upTo){
        org.ta4j.core.indicators.helpers.ClosePriceIndicator closePriceIndicator = new org.ta4j.core.indicators.helpers.ClosePriceIndicator(series);
        Decimal average = Decimal.NaN;
        for (int h = 2; h < upTo; h++) {
            org.ta4j.core.indicators.SMAIndicator sma = new org.ta4j.core.indicators.SMAIndicator(closePriceIndicator,h);
            for (int i = 0; i < series.getEndIndex(); i++) {
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
