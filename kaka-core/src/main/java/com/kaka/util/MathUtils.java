package com.kaka.util;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 数学计算工具类
 *
 * @author zhoukai
 */
public final class MathUtils {

    /**
     * {@link java.util.concurrent.ThreadLocalRandom}生成的随机对象
     */
    public static final Random random = ThreadLocalRandom.current();

    /**
     * 弧度系数，用于角度转弧度
     */
    public static final double degreesToRadians = Math.PI / 180d;

    /**
     * 弧度转角度的系数
     */
    public static final double radiansToDegrees = 180d / Math.PI;

    /**
     * 获取{@link java.util.concurrent.ThreadLocalRandom}
     *
     * @return {@link java.util.concurrent.ThreadLocalRandom}
     */
    public static final Random getRandom() {
        return ThreadLocalRandom.current();
    }

    /**
     * 返回一个[0, range]之间的随机数
     *
     * @param range 闭区间结束范围值
     * @return 随机数
     */
    static public int random(int range) {
        return getRandom().nextInt(range + 1);
    }

    /**
     * 通过{@link Math}生成[start, end]的随机数
     *
     * @param start 最小值
     * @param end 最大值
     * @return [start, end]的随机数
     */
    static final public int getRandom(int start, int end) {
        int number = (int) (Math.random() * (end - start + 1)) + start;
        return number;
    }

    /**
     * 返回一个[start, end]之间的随机数
     *
     * @param start 闭区间起始范围值
     * @param end 闭区间结束范围值
     * @return 随机数
     */
    static final public int random(int start, int end) {
        return start + getRandom().nextInt(end - start + 1);
    }

    /**
     * 返回一个[0, range]之间的随机数
     *
     * @param range 闭区间结束范围值
     * @return 随机数
     */
    static final public long random(long range) {
        return (long) (getRandom().nextDouble() * range);
    }

    /**
     * 返回一个[start, end]之间的随机数
     *
     * @param start 闭区间起始范围值
     * @param end 闭区间结束范围值
     * @return 随机数
     */
    static final public long random(long start, long end) {
        return start + (long) (getRandom().nextDouble() * (end - start));
    }

    /**
     * 获取一个随机布尔值
     *
     * @return 随机布尔值
     */
    static final public boolean randomBoolean() {
        return getRandom().nextBoolean();
    }

    /**
     * 获取一个随机布尔值
     *
     * @param chance [0,1]之间的小数表示的概率
     * @return 随机布尔值
     */
    static final public boolean randomBoolean(float chance) {
        return random() < chance;
    }

    /**
     * 获取一个随机浮点数
     *
     * @return 随机浮点数
     */
    static final public float random() {
        return getRandom().nextFloat();
    }

    /**
     * 返回一个[0, 1)之间的随机数
     *
     * @param range 前闭后开区间结束范围值
     * @return 随机数
     */
    static final public float random(float range) {
        return getRandom().nextFloat() * range;
    }

    /**
     * 返回一个[start, end)之间的随机数
     *
     * @param start 区间起始范围值（包括）
     * @param end 区间结束范围值（不包括）
     * @return 随机数
     */
    static final public float random(float start, float end) {
        return start + getRandom().nextFloat() * (end - start);
    }

    /**
     * 获取比value值大的2次幂数
     *
     * @param value
     * @return
     */
    public final static int nextPowerOfTwo(int value) {
        if (value == 0) {
            return 1;
        }
        value--;
        value |= value >> 1;
        value |= value >> 2;
        value |= value >> 4;
        value |= value >> 8;
        value |= value >> 16;
        return value + 1;
    }
    
    /**
     * 判断一个数是否为2的次幂数
     *
     * @param value
     * @return
     */
    public final static boolean isPowerOfTwo(int value) {
        return value != 0 && (value & value - 1) == 0;
    }
    
    public static long saturatedAdd(long a, long b) {
        long naiveSum = a + b;
        return (a ^ b) < 0L | (a ^ naiveSum) >= 0L ? naiveSum : 9223372036854775807L + (naiveSum >>> 63 ^ 1L);
    }

    public static long saturatedSubtract(long a, long b) {
        long naiveDifference = a - b;
        return (a ^ b) >= 0L | (a ^ naiveDifference) >= 0L ? naiveDifference : 9223372036854775807L + (naiveDifference >>> 63 ^ 1L);
    }

    public static long saturatedMultiply(long a, long b) {
        int leadingZeros = Long.numberOfLeadingZeros(a) + Long.numberOfLeadingZeros(~a) + Long.numberOfLeadingZeros(b) + Long.numberOfLeadingZeros(~b);
        if (leadingZeros > 65) {
            return a * b;
        } else {
            long limit = 9223372036854775807L + ((a ^ b) >>> 63);
            if (leadingZeros < 64 | a < 0L & b == -9223372036854775808L) {
                return limit;
            } else {
                long result = a * b;
                return a != 0L && result / a != b ? limit : result;
            }
        }
    }

    public static long saturatedPow(long b, int k) {
        if(k < 0) {
            throw new IllegalArgumentException(" 指数(" + k + ") 必须 >= 0");
        }
        if (b >= -2L & b <= 2L) {
            switch((int)b) {
                case -2:
                    if (k >= 64) {
                        return 9223372036854775807L + (long)(k & 1);
                    }

                    return (k & 1) == 0 ? 1L << k : -1L << k;
                case -1:
                    return (k & 1) == 0 ? 1L : -1L;
                case 0:
                    return k == 0 ? 1L : 0L;
                case 1:
                    return 1L;
                case 2:
                    if (k >= 63) {
                        return 9223372036854775807L;
                    }

                    return 1L << k;
                default:
                    throw new AssertionError();
            }
        } else {
            long accum = 1L;
            long limit = 9223372036854775807L + (b >>> 63 & (long)(k & 1));

            while(true) {
                switch(k) {
                    case 0:
                        return accum;
                    case 1:
                        return saturatedMultiply(accum, b);
                }

                if ((k & 1) != 0) {
                    accum = saturatedMultiply(accum, b);
                }

                k >>= 1;
                if (k > 0) {
                    if (-3037000499L > b | b > 3037000499L) {
                        return limit;
                    }

                    b *= b;
                }
            }
        }
    }

}
