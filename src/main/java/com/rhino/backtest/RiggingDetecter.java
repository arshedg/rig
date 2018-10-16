package com.rhino.backtest;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.csv.CSVParser;

import static org.apache.commons.csv.CSVFormat.DEFAULT;
import static org.apache.commons.csv.CSVFormat.EXCEL;

public class RiggingDetecter {
    public static void main(String[] args) throws IOException {
        String input = "/Users/arshed/stock_out";
        File file = new File(input);
        for (File stock : file.listFiles()) {
            CSVParser parser = CSVParser.parse(stock, Charset.defaultCharset(), EXCEL.withFirstRecordAsHeader());
            SMA day20 = new SMA(20);
            SMA day5 = new SMA(5);
            AtomicInteger counter = new AtomicInteger(0);
            parser.forEach(record -> {
                EOD eod = new EOD(record);
                double volume = eod.deliveryVolume();
                day20.addValue(volume);
                day5.addValue(volume);
                if (counter.incrementAndGet()>25 && day20.getMovingAverage() * 2 < day5.getMovingAverage()) {
                    System.out.println("stock " + stock.getName());
                    System.out.println("High volume detected on " + eod.date());
                    System.out.println("SMA 20 "+day20.getMovingAverage());
                    System.out.println("SMA 5 "+day5.getMovingAverage());
                }
            });
        }

    }
}
