package com.ansorgit.plugins.bash.editor.highlighting;

public class MinMaxValue {
    private long min;
    private long max;
    private long sum;
    private double avg;
    private long count;

    public MinMaxValue() {
    }

    public double average() {
        return (double) sum / count;
    }

    public void add(long value) {
        this.min = count == 0 ? value : Math.min(value, this.min);
        this.max = count == 0 ? value : Math.max(value, this.max);
        this.sum += value;
        this.count++;
    }

    public long min() {
        return min;
    }

    public long max() {
        return max;
    }

    public long getSum() {
        return sum;
    }
}
