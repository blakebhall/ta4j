package org.ta4j.core.performance;

import org.junit.Before;
import org.junit.Test;

import org.ta4j.core.Decimal;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * First implementation of BarTimeSeries (Tick renamed to Bar) following a columnar store approach
 * See https://medium.com/@entzik/a-time-series-columnar-store-exercise-4e2d77fe6c24
 * TODO: extract interface
 */
public class BarTimeSeries implements Iterable<Bar> {

    String name;
    Decimal[] openPrice;
    Decimal[] minPrice;
    Decimal[] maxPrice;
    Decimal[] closePrice;
    Decimal[] volume;
    int[] trades;
    Decimal[] amount;
    Duration[] timePeriond; // remove this by enum of period for the whole time series?
    ZonedDateTime[] beginTime; // work with long as timestamp?
    ZonedDateTime[] endTime;


    private final int capacity;
    private int position;

    public BarTimeSeries(int capacity){
        this.capacity = capacity;
        position = -1;
        openPrice = new Decimal[capacity];
        minPrice = new Decimal[capacity];
        maxPrice = new Decimal[capacity];
        closePrice = new Decimal[capacity];
        volume = new Decimal[capacity];
        Arrays.fill(volume,Decimal.ZERO); // must be zero to add something
        trades = new int[capacity];
        Arrays.fill(trades, 0); // must be zero to add something
        amount = new Decimal[capacity];
        Arrays.fill(amount, Decimal.ZERO); // must be zero to add something
        beginTime = new ZonedDateTime[capacity];
        endTime = new ZonedDateTime[capacity];
    }

    /**
     * Add a bar to this time series
     * @param time
     * @param open
     * @param min
     * @param max
     * @param close
     * @param vol
     * //TODO: overload for other parameter combinations
     */
    public void emplace(ZonedDateTime time, Decimal open, Decimal min, Decimal max, Decimal close, Decimal vol){
        position++; // TODO: resize if position >= capacity
        endTime[position] = time;
        openPrice[position] = open;
        minPrice[position] = min;
        maxPrice[position] = max;
        closePrice[position] = close;
        volume[position] = vol;
    }

    /**
     * Add a bar to this time series
     * @param time
     * @param open
     * @param min
     * @param max
     * @param close
     * @param vol
     * //TODO: overload for other parameter combinations
     */
    public void emplaceAt(int pos, ZonedDateTime time, Decimal open, Decimal min, Decimal max, Decimal close, Decimal vol){
        endTime[pos] = time;
        openPrice[pos] = open;
        minPrice[pos] = min;
        maxPrice[pos] = max;
        closePrice[pos] = close;
        volume[pos] = vol;
    }

    public int[] extractTimeSliceForEndTime(ZonedDateTime from, ZonedDateTime to) {
        int[] ret = new int[]{
                findLowerTimestampBoundEnd(from),
                findUpperTimestampBoundEnd(to)
        };
        return ret;
    }

    public int[] extractTimeSliceForBeginTime(ZonedDateTime from, ZonedDateTime to) {
        int[] ret = new int[]{
                findLowerTimestampBoundBegin(from),
                findUpperTimestampBoundBegin(to)
        };
        return ret;
    }

    public int getCapacity() {
        return capacity;
    }

    public Stream<Decimal> getOpenPrices(){
        return Arrays.stream(openPrice);
    }

    public Stream<Decimal> getMinPrices(){
        return Arrays.stream(minPrice);
    }

    public Stream<Decimal> getMaxPrices(){
        return Arrays.stream(maxPrice);
    }

    public Stream<Decimal> getClosePrices(){
        return Arrays.stream(closePrice);
    }

    public Stream<Decimal> getVolumes(){
        return Arrays.stream(volume);
    }

    public Stream<Decimal> getAmounts(){
        return Arrays.stream(amount);
    }

    public IntStream getTrades(){
        return Arrays.stream(trades);
    }

    public Stream<Duration> getTimePeriods(){
        return Arrays.stream(timePeriond);
    }

    public Stream<ZonedDateTime> getBeginTimes(){
        return Arrays.stream(beginTime);
    }

    public Stream<ZonedDateTime> getEndTimes(){
        return Arrays.stream(endTime);
    }

    public Stream<Bar> stream(int start, int end) {
        final Iterator<Bar> iterator = iterator(start, end);
        final Spliterator<Bar> spliterator
                = Spliterators.spliteratorUnknownSize(iterator, 0);
        return StreamSupport.stream(spliterator, false);
    }

    public BarAccessCursor get(int index){
        return new BarAccessCursor(index);
    }

    private int findUpperTimestampBoundBegin(ZonedDateTime max) {
        int maxNdx = Arrays.binarySearch(beginTime, max);
        if (maxNdx < 0)
            maxNdx = (-maxNdx) - 1;
        while (maxNdx < (beginTime.length - 1) && beginTime[maxNdx] == beginTime[maxNdx + 1])
            maxNdx++;
        return maxNdx;
    }

    private int findLowerTimestampBoundBegin(ZonedDateTime min) {
        int minNdx = Arrays.binarySearch(beginTime, min);
        if (minNdx < 0)
            minNdx = (-minNdx) - 1;
        while (minNdx > 0 && beginTime[minNdx] == beginTime[minNdx - 1])
            minNdx--;
        return minNdx;
    }

    private int findUpperTimestampBoundEnd(ZonedDateTime max) {
        int maxNdx = Arrays.binarySearch(endTime, max);
        if (maxNdx < 0)
            maxNdx = (-maxNdx) - 1;
        while (maxNdx < (endTime.length - 1) && endTime[maxNdx] == endTime[maxNdx + 1])
            maxNdx++;
        return maxNdx;
    }

    private int findLowerTimestampBoundEnd(ZonedDateTime min) {
        int minNdx = Arrays.binarySearch(endTime, min);
        if (minNdx < 0)
            minNdx = (-minNdx) - 1;
        while (minNdx > 0 && endTime[minNdx] == endTime[minNdx - 1])
            minNdx--;
        return minNdx;
    }

    @Override
    public Iterator<Bar> iterator() {
        return new BarIterator();
    }

    public Iterator<Bar> iterator(int start, int end){
        return new BarIterator(start, end);
    }




    public class BarAccessCursor implements Bar{
        private int index;

        protected BarAccessCursor(int index){
            this.index = index;
        }

        public BarAccessCursor at(int index){
            this.index = index;
            return this;
        }


        @Override
        public Decimal getOpenPrice() {
            return BarTimeSeries.this.openPrice[index];
        }

        protected void setOpenPrice(Decimal openPrice){ // i think it is faster not to use setter, but indices
            BarTimeSeries.this.openPrice[index] = openPrice;
        }

        @Override
        public Decimal getMinPrice() {
            return BarTimeSeries.this.minPrice[index];
        }

        protected void setMinPrice(Decimal minPrice){ // i think it is faster not to use setter, but indices
            BarTimeSeries.this.minPrice[index] = minPrice;
        }

        @Override
        public Decimal getMaxPrice() {
            return BarTimeSeries.this.maxPrice[index];
        }

        protected void setMaxPrice(Decimal maxPrice){ // i think it is faster not to use setter, but indices
            BarTimeSeries.this.maxPrice[index] = maxPrice;
        }

        @Override
        public Decimal getClosePrice() {
            return BarTimeSeries.this.closePrice[index];
        }

        protected void setClosePrice(Decimal closePrice){ // i think it is faster not to use setter, but indices
            BarTimeSeries.this.closePrice[index] = closePrice;
        }

        @Override
        public Decimal getVolume() {
            return BarTimeSeries.this.volume[index];
        }

        protected void setVolumen(Decimal volume){
            BarTimeSeries.this.volume[index] = volume;
        }

        @Override
        public int getTrades() {
            return BarTimeSeries.this.trades[index];
        }

        protected void setTrades(int trades){
            BarTimeSeries.this.trades[index] = trades;
        }

        @Override
        public Decimal getAmount() {
            return BarTimeSeries.this.amount[index];
        }

        protected void setAmount(Decimal amount){
            BarTimeSeries.this.amount[index] = amount;
        }

        @Override
        public Duration getTimePeriod() {
            return BarTimeSeries.this.timePeriond[index];
        }

        @Override
        public ZonedDateTime getBeginTime() {
            return BarTimeSeries.this.beginTime[index];
        }

        @Override
        public ZonedDateTime getEndTime() {
            return BarTimeSeries.this.endTime[index];
        }

        @Override
        public void addTrade(Decimal tradeVolume, Decimal tradePrice) {

            if (openPrice == null) {
                BarTimeSeries.this.openPrice[index] = tradePrice;
            }
            setClosePrice(tradePrice);

            if (maxPrice == null) {
                BarTimeSeries.this.maxPrice[index] = tradePrice;
            } else {
                if(getMaxPrice().isLessThan(tradePrice)){
                    BarTimeSeries.this.maxPrice[index] = tradePrice;;
                }
            }
            if (minPrice == null) {
                BarTimeSeries.this.minPrice[index] = tradePrice;
            } else {
                if(getMinPrice().isGreaterThan(tradePrice)){
                    BarTimeSeries.this.minPrice[index] = tradePrice;
                }
            }
            BarTimeSeries.this.volume[index] = BarTimeSeries.this.volume[index].plus(tradeVolume);
            BarTimeSeries.this.amount[index] = BarTimeSeries.this.amount[index].plus(tradePrice);
            BarTimeSeries.this.trades[index] += 1;
        }
    }




    public class BarIterator implements Bar, Iterator<Bar>{

        private int currentIndex;
        private final int end;


        public BarIterator(){
            this.currentIndex = 1; // why not 0?
            this.end = BarTimeSeries.this.position;
        }

        public BarIterator(int currentIndex, int end){
            this.currentIndex = currentIndex-1;
            this.end = end;
        }

        @Override
        public Decimal getOpenPrice() {
            return BarTimeSeries.this.openPrice[currentIndex];
        }

        @Override
        public Decimal getMinPrice() {
            return BarTimeSeries.this.minPrice[currentIndex];
        }

        @Override
        public Decimal getMaxPrice() {
            return BarTimeSeries.this.maxPrice[currentIndex];
        }

        @Override
        public Decimal getClosePrice() {
            return BarTimeSeries.this.closePrice[currentIndex];
        }

        @Override
        public Decimal getVolume() {
            return BarTimeSeries.this.volume[currentIndex];
        }

        @Override
        public int getTrades() {
            return BarTimeSeries.this.trades[currentIndex];
        }

        @Override
        public Decimal getAmount() {
            return BarTimeSeries.this.amount[currentIndex];
        }

        @Override
        public Duration getTimePeriod() {
            return BarTimeSeries.this.timePeriond[currentIndex];
        }

        @Override
        public ZonedDateTime getBeginTime() {
            return BarTimeSeries.this.beginTime[currentIndex];
        }

        @Override
        public ZonedDateTime getEndTime() {
            return BarTimeSeries.this.endTime[currentIndex];
        }

        @Override
        public void addTrade(Decimal tradeVolume, Decimal tradePrice) {

        }

        @Override
        public boolean hasNext() {
            return currentIndex < end;
        }

        @Override
        public BarIterator next() {
            currentIndex++;
            return this;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Cannot remove from array base");
        }

        @Override
        public void forEachRemaining(Consumer<? super Bar> action) {
            while (hasNext())
                action.accept(next());
        }

    }
}
