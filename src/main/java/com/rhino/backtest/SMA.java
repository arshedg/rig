package com.rhino.backtest;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.http.cookie.SM;

public class SMA {
    private DescriptiveStatistics stats = new DescriptiveStatistics();
    private int window;
    public SMA(int window){
        this.window = window;
        stats.setWindowSize(window);
    }

    public void addValue(double value){
        stats.addValue(value);
    }

    public double getMovingAverage(){
        return stats.getMean();
    }
}
