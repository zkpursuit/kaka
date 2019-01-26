package com.kaka.util;

public abstract class Ticker {
    private static final Ticker SYSTEM_TICKER = new Ticker() {
        public long read() {
            return System.nanoTime();
        }
    };

    protected Ticker() {
    }

    public abstract long read();

    public static Ticker systemTicker() {
        return SYSTEM_TICKER;
    }
}
