package com.rhino.backtest;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Paths;

import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class EODConverter {

    public static final int DATE = 2;
    public static final int PREVS_CLOSE = 3;
    public static final int OPEN_PRICE = 4;
    public static final int HIGH_PRICE = 5;
    public static final int LOW_PRICE = 6;
    public static final int LAST_PRICE = 7;
    public static final int CLOSE_PRICE = 8;
    public static final int AVERAGE_PRICE = 9;
    public static final int VOLUME = 10;
    public static final int TRADES = 12;
    public static final int DELIVERY = 13;
    public static final int DELIVERY_PERCENTAGE = 14;
    public static final int SERIES = 1;

    public static void main(String[] args) throws IOException {
        String folder = "/Users/arshed/stocks";
        String output = "/Users/arshed/stock_out";
        File file = new File(folder);
        for (File stock : file.listFiles()) {
            Document document = Jsoup.parse(stock, Charset.defaultCharset().name());
            String text = document.body().getElementById("csvContentDiv").text();
            String lines[] = text.split(":");
            StringBuilder csvContent = new StringBuilder();
            for (String line : lines) {
                if (line.contains("Symbol")) {
                    csvContent.append(line).append("\n");
                    continue;
                }
                String words[] = line.split(",");
                StringBuilder csv = new StringBuilder(words[0]).append(",").append(words[1]).append(",").append(words[2]);
                for (int i = 3; i < words.length; i++) {
                    String word = words[i];
                    word = word.replaceAll("\"", "");
                    csv.append(",").append(Double.valueOf(word));
                }
                csvContent.append(csv).append("\n");
            }

            File newFile = Paths.get(output, stock.getName() + ".csv").toFile();
            newFile.createNewFile();
            FileUtils.write(newFile, csvContent.toString(), Charset.defaultCharset());
            System.out.println("writing " + stock.getName());
        }
    }

    private static String csvLine(Elements tds) {
        StringBuilder builder = new StringBuilder();
        return builder
                .append(tds.get(DATE).text()).append(",")
                .append(tds.get(PREVS_CLOSE).text()).append(",")
                .append(tds.get(OPEN_PRICE).text()).append(",")
                .append(tds.get(HIGH_PRICE).text()).append(",")
                .append(tds.get(LOW_PRICE).text()).append(",")
                .append(tds.get(LAST_PRICE).text()).append(",")
                .append(tds.get(CLOSE_PRICE).text()).append(",")
                .append(tds.get(AVERAGE_PRICE).text()).append(",")
                .append(tds.get(VOLUME).text()).append(",")
                .append(tds.get(TRADES).text()).append(",")
                .append(tds.get(DELIVERY).text()).append(",")
                .append(tds.get(DELIVERY_PERCENTAGE).text()).append("\n").toString();
    }
}
