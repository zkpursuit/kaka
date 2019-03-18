package com.kaka.util.concurrent;

import com.kaka.util.Stopwatch;
import com.kaka.util.StringUtils;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * 移植于Google Guava中的令牌桶算法
 * <br> 移植原因：Guava中很多东西用不到，又不想引入Guava，故在此仅保留源码出处。
 *
 * @author zkpursuit
 */
public abstract class RateLimiter {

    private final RateLimiter.SleepingStopwatch stopwatch;
    private volatile Object mutexDoNotUseDirectly;

    public static RateLimiter create(double permitsPerSecond) {
        return create(permitsPerSecond, RateLimiter.SleepingStopwatch.createFromSystemTimer());
    }

    static RateLimiter create(double permitsPerSecond, RateLimiter.SleepingStopwatch stopwatch) {
        RateLimiter rateLimiter = new SmoothRateLimiter.SmoothBursty(stopwatch, 1.0D);
        rateLimiter.setRate(permitsPerSecond);
        return rateLimiter;
    }

    public static RateLimiter create(double permitsPerSecond, long warmupPeriod, TimeUnit unit) {
        if (warmupPeriod < 0) {
            throw new IllegalArgumentException(StringUtils.replace("warmupPeriod must not be negative: {0}", warmupPeriod));
        }
        return create(permitsPerSecond, warmupPeriod, unit, 3.0D, RateLimiter.SleepingStopwatch.createFromSystemTimer());
    }

    static RateLimiter create(double permitsPerSecond, long warmupPeriod, TimeUnit unit, double coldFactor, RateLimiter.SleepingStopwatch stopwatch) {
        RateLimiter rateLimiter = new SmoothRateLimiter.SmoothWarmingUp(stopwatch, warmupPeriod, unit, coldFactor);
        rateLimiter.setRate(permitsPerSecond);
        return rateLimiter;
    }

    private Object mutex() {
        Object mutex = this.mutexDoNotUseDirectly;
        if (mutex == null) {
            synchronized (this) {
                mutex = this.mutexDoNotUseDirectly;
                if (mutex == null) {
                    this.mutexDoNotUseDirectly = mutex = new Object();
                }
            }
        }

        return mutex;
    }

    RateLimiter(RateLimiter.SleepingStopwatch stopwatch) {
        if (stopwatch == null) {
            throw new NullPointerException("stopwatch");
        }
        this.stopwatch = stopwatch;
    }

    public final void setRate(double permitsPerSecond) {
        if (permitsPerSecond <= 0 || Double.isNaN(permitsPerSecond)) {
            throw new IllegalArgumentException(StringUtils.replace("rate must be positive"));
        }
        synchronized (this.mutex()) {
            this.doSetRate(permitsPerSecond, this.stopwatch.readMicros());
        }
    }

    abstract void doSetRate(double var1, long var3);

    public final double getRate() {
        synchronized (this.mutex()) {
            return this.doGetRate();
        }
    }

    abstract double doGetRate();

    public double acquire() {
        return this.acquire(1);
    }

    public double acquire(int permits) {
        long microsToWait = this.reserve(permits);
        this.stopwatch.sleepMicrosUninterruptibly(microsToWait);
        return 1.0D * (double) microsToWait / (double) TimeUnit.SECONDS.toMicros(1L);
    }

    final long reserve(int permits) {
        if (permits <= 0) {
            throw new IllegalArgumentException(StringUtils.replace("Requested permits ({0}) must be positive", permits));
        }
        synchronized (this.mutex()) {
            return this.reserveAndGetWaitLength(permits, this.stopwatch.readMicros());
        }
    }

    public boolean tryAcquire(long timeout, TimeUnit unit) {
        return this.tryAcquire(1, timeout, unit);
    }

    public boolean tryAcquire(int permits) {
        return this.tryAcquire(permits, 0L, TimeUnit.MICROSECONDS);
    }

    public boolean tryAcquire() {
        return this.tryAcquire(1, 0L, TimeUnit.MICROSECONDS);
    }

    public boolean tryAcquire(int permits, long timeout, TimeUnit unit) {
        if (permits <= 0) {
            throw new IllegalArgumentException(StringUtils.replace("Requested permits ({0}) must be positive", permits));
        }
        long timeoutMicros = Math.max(unit.toMicros(timeout), 0L);
        long microsToWait;
        synchronized (this.mutex()) {
            long nowMicros = this.stopwatch.readMicros();
            if (!this.canAcquire(nowMicros, timeoutMicros)) {
                return false;
            }

            microsToWait = this.reserveAndGetWaitLength(permits, nowMicros);
        }

        this.stopwatch.sleepMicrosUninterruptibly(microsToWait);
        return true;
    }

    private boolean canAcquire(long nowMicros, long timeoutMicros) {
        return this.queryEarliestAvailable(nowMicros) - timeoutMicros <= nowMicros;
    }

    final long reserveAndGetWaitLength(int permits, long nowMicros) {
        long momentAvailable = this.reserveEarliestAvailable(permits, nowMicros);
        return Math.max(momentAvailable - nowMicros, 0L);
    }

    abstract long queryEarliestAvailable(long var1);

    abstract long reserveEarliestAvailable(int var1, long var2);

    @Override
    public String toString() {
        return String.format(Locale.ROOT, "RateLimiter[stableRate=%3.1fqps]", this.getRate());
    }

    abstract static class SleepingStopwatch {

        protected SleepingStopwatch() {
        }

        protected abstract long readMicros();

        protected abstract void sleepMicrosUninterruptibly(long var1);

        public static RateLimiter.SleepingStopwatch createFromSystemTimer() {
            return new RateLimiter.SleepingStopwatch() {
                final Stopwatch stopwatch = Stopwatch.createStarted();

                @Override
                protected long readMicros() {
                    return this.stopwatch.elapsed(TimeUnit.MICROSECONDS);
                }

                @Override
                protected void sleepMicrosUninterruptibly(long micros) {
                    if (micros > 0L) {
                        sleepUninterruptibly(micros, TimeUnit.MICROSECONDS);
                    }

                }
            };
        }

        void sleepUninterruptibly(long sleepFor, TimeUnit unit) {
            boolean interrupted = false;
            try {
                long remainingNanos = unit.toNanos(sleepFor);
                long end = System.nanoTime() + remainingNanos;
                while (true) {
                    try {
                        TimeUnit.NANOSECONDS.sleep(remainingNanos);
                        return;
                    } catch (InterruptedException var12) {
                        interrupted = true;
                        remainingNanos = end - System.nanoTime();
                    }
                }
            } finally {
                if (interrupted) {
                    Thread.currentThread().interrupt();
                }

            }
        }

    }
}
