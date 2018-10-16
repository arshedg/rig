package com.rhino.backtest;

import java.io.IOException;
import java.sql.Date;
import java.util.Optional;
import java.util.Scanner;

import com.zerodhatech.kiteconnect.KiteConnect;
import com.zerodhatech.kiteconnect.kitehttp.exceptions.KiteException;
import com.zerodhatech.models.HistoricalData;
import com.zerodhatech.models.Instrument;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class HistoricData {

    @Autowired
    private KiteConnect kiteConnect;

    // @EventListener(ApplicationReadyEvent.class)
    public void getInstrumentToken() throws KiteException, IOException {
        kiteConnect.getInstruments("NSE").stream().filter(i -> i.name != null &&
                i.name.toLowerCase().contains("ongc") || i.tradingsymbol.toLowerCase().contains("ongc"))
                .forEach(i -> {
                    System.out.println(i.name);
                    System.out.println(i.instrument_token);
                });
    }

    private String instrument;
    private String instrumentName;

   @EventListener(ApplicationReadyEvent.class)
    public void executor() throws KiteException, IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("enter the name");
        instrumentName = scanner.next();

        Optional<Instrument> hasValue = kiteConnect.getInstruments("NSE").stream().filter(i -> {

            return i.tradingsymbol != null &&
                    i.tradingsymbol.toLowerCase().equals(instrumentName.toLowerCase());
        }).findFirst();
        if (hasValue.isPresent()) {
            instrument = hasValue.get().instrument_token + "";
            System.out.println("stock name " + hasValue.get().name);
        } else {
            System.out.println("cannot find symbol. printing similar ones");
            kiteConnect.getInstruments("NSE").stream().filter(i -> i.name != null &&
                    i.tradingsymbol.toLowerCase().startsWith(instrumentName.charAt(0) + "")).forEach(i -> System.out.println(i.tradingsymbol));
            executor();
        }


    }

    @EventListener(ApplicationReadyEvent.class)
    public void details() throws KiteException, IOException {
        Date from = Date.valueOf("2018-1-24");
        Date to = Date.valueOf("2018-10-30");
        HistoricalData index = kiteConnect.getHistoricalData(from, to, "256265", "day", false);
        HistoricalData stock = kiteConnect.getHistoricalData(from, to, instrument, "day", false);

       for(int i=0;i<stock.dataArrayList.size();i++){
           HistoricalData stockToday = stock.dataArrayList.get(i);
           HistoricalData indexToday = index.dataArrayList.get(i);
       }



        executor();

    }

    private double profit(HistoricalData stock, int boughtIndex, int currentIndex) {
        HistoricalData today = stock.dataArrayList.get(currentIndex);
        HistoricalData old = stock.dataArrayList.get(boughtIndex);
        return change(old.close,today.close);
    }

    private boolean shouldBuy(HistoricalData data, HistoricalData indexData, int currentIndex,int observeIndex){
        HistoricalData today = data.dataArrayList.get(currentIndex);
        HistoricalData yesterDay = data.dataArrayList.get(currentIndex-1);
        HistoricalData indexToday = indexData.dataArrayList.get(currentIndex);
        HistoricalData indexYester = indexData.dataArrayList.get(currentIndex-1);


        double positiveChange = change(yesterDay.close,today.close);
        double indexChange = change(indexYester.close,indexToday.close);

        if(positiveChange<indexChange){
            return false;
        }
        if(observeIndex>0) {
            HistoricalData observed = data.dataArrayList.get(observeIndex);

            double changeFromWatch = change(observed.close, today.close);
            if (changeFromWatch < 2) {
                return false;
            }
        }
        double volumeChange = change(yesterDay.volume,today.volume);
        if(volumeChange>40){
            return true;
        }
        return false;
    }

    private double change(double old, double latest) {
        return (latest - old) / (old) * 100;
    }
}
