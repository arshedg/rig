package com.rhino.backtest;

import org.apache.commons.csv.CSVRecord;

public class EOD {

    CSVRecord csvRecord;

    public EOD(CSVRecord csvRecord){
        this.csvRecord = csvRecord;
    }

    public double getAvergePrice(){
        return Double.parseDouble(csvRecord.get("AVERAGE_PRICE"));
    }

    public double deliveryVolume(){
        return Double.parseDouble(csvRecord.get("Deliverable Qty"));
    }

    public String date(){
        return csvRecord.get("Date");
    }
}
