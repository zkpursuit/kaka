package com.kaka.util;

import java.time.Duration;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public final class Stopwatch {
    private final Ticker ticker;
    private boolean isRunning;
    private long elapsedNanos;
    private long startTick;

    public static Stopwatch createUnstarted() {
        return new Stopwatch();
    }

    public static Stopwatch createUnstarted(Ticker ticker) {
        return new Stopwatch(ticker);
    }

    public static Stopwatch createStarted() {
        return (new Stopwatch()).start();
    }

    public static Stopwatch createStarted(Ticker ticker) {
        return (new Stopwatch(ticker)).start();
    }

    Stopwatch() {
        this.ticker = Ticker.systemTicker();
    }

    Stopwatch(Ticker ticker) {
        if (ticker == null) {
            throw new NullPointerException("ticker");
        }
        this.ticker = ticker;
    }

    public boolean isRunning() {
        return this.isRunning;
    }

    public Stopwatch start() {
        if (this.isRunning) {
            new IllegalStateException(String.valueOf("This stopwatch is already running."));
        }
        this.isRunning = true;
        this.startTick = this.ticker.read();
        return this;
    }

    public Stopwatch stop() {
        long tick = this.ticker.read();
        if (!this.isRunning) {
            new IllegalStateException(String.valueOf("This stopwatch is already stopped."));
        }
        this.isRunning = false;
        this.elapsedNanos += tick - this.startTick;
        return this;
    }

    public Stopwatch reset() {
        this.elapsedNanos = 0L;
        this.isRunning = false;
        return this;
    }

    private long elapsedNanos() {
        return this.isRunning ? this.ticker.read() - this.startTick + this.elapsedNanos : this.elapsedNanos;
    }

    public long elapsed(TimeUnit desiredUnit) {
        return desiredUnit.convert(this.elapsedNanos(), TimeUnit.NANOSECONDS);
    }

    public Duration elapsed() {
        return Duration.ofNanos(this.elapsedNanos());
    }

    public String toString() {
        long nanos = this.elapsedNanos();
        TimeUnit unit = chooseUnit(nanos);
        double value = (double) nanos / (double) TimeUnit.NANOSECONDS.convert(1L, unit);
        return String.format(Locale.ROOT, "%.4g", value) + " " + abbreviate(unit);
    }

    private static TimeUnit chooseUnit(long nanos) {
        if (TimeUnit.DAYS.convert(nanos, TimeUnit.NANOSECONDS) > 0L) {
            return TimeUnit.DAYS;
        } else if (TimeUnit.HOURS.convert(nanos, TimeUnit.NANOSECONDS) > 0L) {
            return TimeUnit.HOURS;
        } else if (TimeUnit.MINUTES.convert(nanos, TimeUnit.NANOSECONDS) > 0L) {
            return TimeUnit.MINUTES;
        } else if (TimeUnit.SECONDS.convert(nanos, TimeUnit.NANOSECONDS) > 0L) {
            return TimeUnit.SECONDS;
        } else if (TimeUnit.MILLISECONDS.convert(nanos, TimeUnit.NANOSECONDS) > 0L) {
            return TimeUnit.MILLISECONDS;
        } else {
            return TimeUnit.MICROSECONDS.convert(nanos, TimeUnit.NANOSECONDS) > 0L ? TimeUnit.MICROSECONDS : TimeUnit.NANOSECONDS;
        }
    }

    private static String abbreviate(TimeUnit unit) {
        switch (unit) {
            case NANOSECONDS:
                return "ns";
            case MICROSECONDS:
                return "μs";
            case MILLISECONDS:
                return "ms";
            case SECONDS:
                return "s";
            case MINUTES:
                return "min";
            case HOURS:
                return "h";
            case DAYS:
                return "d";
            default:
                throw new AssertionError();
        }
    }
}
