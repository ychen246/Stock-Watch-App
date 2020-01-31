package com.yujunchen.stockwatch;

public class Stock implements Comparable<Stock> {
    private String symbol;
    private String name;
    private double latestPrice;
    private double change;
    private double changePercentage;

    public Stock(String sym, String na){
        symbol = sym;
        name = na;
        latestPrice = 0.0;
        change = 0.0;
        changePercentage = 0.0;
    }

    String getSymbol(){
        return symbol;
    }

    String getName(){
        return name;
    }

    double getLatestPrice() {
        return latestPrice;
    }

    double getChange() {
        return change;
    }

    double getChangePercentage() {
        return changePercentage;
    }

    void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    void setName(String name) {
        this.name = name;
    }

    void setLatestPrice(double latestPrice) {
        this.latestPrice = latestPrice;
    }

    void setChange(double change) {
        this.change = change;
    }

    void setChangePercentage(double changePercentage) {
        this.changePercentage = changePercentage;
    }

    @Override
    public int compareTo(Stock o) {
        return getSymbol().compareTo(o.getSymbol());
    }
}
