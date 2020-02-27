package com.kaka.util;

import com.kaka.util.math.Rect;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;

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
     * @param end   最大值
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
     * @param end   闭区间结束范围值
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
     * @param end   闭区间结束范围值
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
     * @param end   区间结束范围值（不包括）
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

    /**
     * 获取两矩形相交的矩形
     *
     * @param r1
     * @param r2
     * @return 相交的矩形
     */
    public static final Rect getOverlapsRect(Rect r1, Rect r2) {
        if (!r1.overlaps(r2)) {
            return null;
        }
        Rect rect = new Rect();
        rect.x = (r1.x > r2.x) ? r1.x : r2.x;
        rect.y = (r1.y > r2.y) ? r1.y : r2.y;
        rect.width = ((r1.x + r1.width) < (r2.x + r2.width) ? (r1.x + r1.width) : (r2.x + r2.width)) - rect.x;
        rect.height = ((r1.y + r1.height) < (r2.y + r2.height) ? (r1.y + r1.height) : (r2.y + r2.height)) - rect.y;
        if (rect.width < 0 || rect.height < 0) {
            rect.width = 0;
            rect.height = 0;
        }
        return rect;
    }

    /**
     * 圆周上等分点
     *
     * @param circleCenterX  圆心X坐标
     * @param circleCenterY  圆心Y坐标
     * @param radius         半径
     * @param num            等分点数量
     * @param deviationAngle 起始角度，偏移角度,影响起始坐标，360/等分数量/4为正位置
     * @return 等分点数组集合
     */
    public static final float[][] equalCircle(int circleCenterX, int circleCenterY, int radius, int num, float deviationAngle) {
        float f1 = 360 / num;
        float f2 = f1 + deviationAngle;
        float[][] array = new float[num][];
        for (int i = 0; i < num; i++) {
            double radian = degreesToRadians * f2;// 角度转弧度
            double x = circleCenterX + radius * Math.cos(radian);
            double y = circleCenterY + radius * Math.sin(radian);
            f2 += f1;
            array[i] = new float[]{(float) x, (float) y};
        }
        return array;
    }

    /**
     * <p>
     * 判断线段是否与矩形相交
     * <p>
     * 先看线段所在直线是否与矩形相交， 如果不相交则返回false， 如果相交，
     * 则看线段的两个点是否在矩形的同一边（即两点的x(y)坐标都比矩形的小x(y)坐标小，或者大）, 若在同一边则返回false， 否则就是相交的情况。
     * </p>
     *
     * @param linePointX1           线段起始点x坐标
     * @param linePointY1           线段起始点y坐标
     * @param linePointX2           线段结束点x坐标
     * @param linePointY2           线段结束点y坐标
     * @param rectangleLeftTopX     矩形左上点x坐标
     * @param rectangleLeftTopY     矩形左上点y坐标
     * @param rectangleRightBottomX 矩形右下点x坐标
     * @param rectangleRightBottomY 矩形右下点y坐标
     * @return 是否相交，true相交
     */
    public final static boolean isLineIntersectRect(float linePointX1, float linePointY1,
                                                    float linePointX2, float linePointY2, float rectangleLeftTopX,
                                                    float rectangleLeftTopY, float rectangleRightBottomX,
                                                    float rectangleRightBottomY) {

        float lineHeight = linePointY1 - linePointY2;
        float lineWidth = linePointX2 - linePointX1;
        // 计算叉乘
        float c = linePointX1 * linePointY2 - linePointX2 * linePointY1;
        if ((lineHeight * rectangleLeftTopX + lineWidth * rectangleLeftTopY + c >= 0 && lineHeight
                * rectangleRightBottomX + lineWidth * rectangleRightBottomY + c <= 0)
                || (lineHeight * rectangleLeftTopX + lineWidth
                * rectangleLeftTopY + c <= 0 && lineHeight
                * rectangleRightBottomX + lineWidth
                * rectangleRightBottomY + c >= 0)
                || (lineHeight * rectangleLeftTopX + lineWidth
                * rectangleRightBottomY + c >= 0 && lineHeight
                * rectangleRightBottomX + lineWidth * rectangleLeftTopY
                + c <= 0)
                || (lineHeight * rectangleLeftTopX + lineWidth
                * rectangleRightBottomY + c <= 0 && lineHeight
                * rectangleRightBottomX + lineWidth * rectangleLeftTopY
                + c >= 0)) {
            if (rectangleLeftTopX > rectangleRightBottomX) {
                float temp = rectangleLeftTopX;
                rectangleLeftTopX = rectangleRightBottomX;
                rectangleRightBottomX = temp;
            }
            if (rectangleLeftTopY < rectangleRightBottomY) {
                float temp = rectangleLeftTopY;
                rectangleLeftTopY = rectangleRightBottomY;
                rectangleRightBottomY = temp;
            }
            if ((linePointX1 < rectangleLeftTopX && linePointX2 < rectangleLeftTopX)
                    || (linePointX1 > rectangleRightBottomX && linePointX2 > rectangleRightBottomX)
                    || (linePointY1 > rectangleLeftTopY && linePointY2 > rectangleLeftTopY)
                    || (linePointY1 < rectangleRightBottomY && linePointY2 < rectangleRightBottomY)) {
                return false;
            } else {
                return true;
            }
        } else {
            return false;
        }
    }

    /**
     * 判断平面两直线是否相交
     *
     * @param p1x 线段1端点X坐标
     * @param p1y 线段1端点Y坐标
     * @param p2x 线段1端点X坐标
     * @param p2y 线段1端点Y坐标
     * @param p3x 线段2端点X坐标
     * @param p3y 线段2端点Y坐标
     * @param p4x 线段2端点X坐标
     * @param p4y 线段2端点Y坐标
     * @return true相交
     */
    public static final boolean isLineSegmentCross(
            float p1x, float p1y, float p2x, float p2y,
            float p3x, float p3y, float p4x, float p4y) {
        //每个线段的两点都在另一个线段的左右不同侧，则能断定线段相交
        //公式对于向量(x1,y1)->(x2,y2),判断点(x3,y3)在向量的左边,右边,还是线上.
        //p=x1(y3-y2)+x2(y1-y3)+x3(y2-y1). p<0 左侧,    p=0 线上, p>0 右侧
        int linep1 = (int) (p1x * (p3y - p2y) + p2x * (p1y - p3y) + p3x * (p2y - p1y));
        int linep2 = (int) (p1x * (p4y - p2y) + p2x * (p1y - p4y) + p4x * (p2y - p1y));
        if (((linep1 ^ linep2) >= 0) && !(linep1 == 0 && linep2 == 0)) {
            //符号位异或为0:pSecond1和pSecond2在pFirst1->pFirst2同侧
            return false;
        }
        linep1 = (int) (p3x * (p1y - p4y) + p4x * (p3y - p1y) + p1x * (p4y - p3y));
        linep2 = (int) (p3x * (p2y - p4y) + p4x * (p3y - p2y) + p2x * (p4y - p3y));
        //否则判为相交，符号位异或为0:pFirst1和pFirst2在pSecond1->pSecond2同侧
        return !(((linep1 ^ linep2) >= 0) && !(linep1 == 0 && linep2 == 0));
    }

    /**
     * 平面两线段交点坐标
     *
     * @param p1x 线段1端点X坐标
     * @param p1y 线段1端点Y坐标
     * @param p2x 线段1端点X坐标
     * @param p2y 线段1端点Y坐标
     * @param p3x 线段2端点X坐标
     * @param p3y 线段2端点Y坐标
     * @param p4x 线段2端点X坐标
     * @param p4y 线段2端点Y坐标
     * @return 平面两线交点(x, y)坐标
     */
    public static final float[] getCrossPoint(
            float p1x, float p1y, float p2x, float p2y,
            float p3x, float p3y, float p4x, float p4y) {
        //必须相交求出的才是线段的交点，但是下面的程序段是通用的
        if (isLineSegmentCross(p1x, p1y, p2x, p2y, p3x, p3y, p4x, p4y) == false) {
            return null;
        }
        //根据两点式化为标准式，进而求线性方程组
        float crossPoint[] = new float[]{0f, 0f};
        float tempLeft;
        float tempRight;
        //求x坐标
        tempLeft = (p4x - p3x) * (p1y - p2y) - (p2x - p1x) * (p3y - p4y);
        tempRight = (p1y - p3y) * (p2x - p1x) * (p4x - p3x) + p3x * (p4y - p3y) * (p2x - p1x) - p1x * (p2y - p1y) * (p4x - p3x);
        crossPoint[0] = tempRight / tempLeft;
        //求y坐标
        tempLeft = (p1x - p2x) * (p4y - p3y) - (p2y - p1y) * (p3x - p4x);
        tempRight = p2y * (p1x - p2x) * (p4y - p3y) + (p4x - p2x) * (p4y - p3y) * (p1y - p2y) - p4y * (p3x - p4x) * (p2y - p1y);
        crossPoint[1] = tempRight / tempLeft;
        return crossPoint;
    }

    /**
     * 点线位置关系
     *
     * @param px            点X坐标
     * @param py            点Y坐标
     * @param linePotStartX 线段1端点X坐标
     * @param linePotStartY 线段1端点Y坐标
     * @param linePotEndX   线段1端点X坐标
     * @param linePotEndY   线段1端点Y坐标
     * @return -1: 点在线段左侧; 0: 点在线段上; 1: 点在线段右侧
     */
    public final static int pointLinePositionRelation(float px, float py, float linePotStartX, float linePotStartY, float linePotEndX, float linePotEndY) {
        linePotStartX -= px;
        linePotStartY -= py;
        linePotEndX -= px;
        linePotEndY -= py;
        float nRet = linePotStartX * linePotEndY - linePotStartY * linePotEndX;
        if (nRet == 0) {
            return 0;
        }
        if (nRet > 0) {
            return 1;
        }
        if (nRet < 0) {
            return -1;
        }
        return 0;
    }

    private static void swap(int[] src, int a, int b) {
        int m = src[a];
        src[a] = src[b];
        src[b] = m;
    }

    /**
     * 数据排列算法
     *
     * @param src    原始数据
     * @param k      一般为src的0号下标，即为0
     * @param m      一般为src的最大下标，即为src.length - 1
     * @param action 排列后的数据访问器
     */
    private static void permutate(int[] src, int k, int m, Consumer<int[]> action) {
        int i;
        if (k > m) {
            int[] perm = new int[src.length];
            for (i = 0; i <= m; i++) {
                perm[i] = src[i];
            }
            action.accept(perm);
        } else {
            for (i = k; i <= m; i++) {
                swap(src, k, i);
                permutate(src, k + 1, m, action);
                swap(src, k, i);
            }
        }
    }

    /**
     * 数据排列算法
     *
     * @param src    原始数据
     * @param action 排列后的数据访问器
     */
    public static void permutate(int[] src, Consumer<int[]> action) {
        permutate(src, 0, src.length - 1, action);
    }

    /**
     * 组合，递归实现
     *
     * @param src       数组对象
     * @param begin     从源数组中的此索引处取得一个数据并存入result中
     * @param number    组合数量
     * @param result    一组组合数据
     * @param resultIdx result中索引变更，即每组组合数据共用result对象，仅改变此索引
     * @param action    一组组合数据的访问器
     */
    private static void combine(int[] src, int begin, int number, int[] result, int resultIdx, Consumer<int[]> action) {
        if (number == 0) {
            action.accept(result);
            return;
        }
        if (begin == src.length) {
            return;
        }
        result[resultIdx] = src[begin];
        resultIdx++;
        combine(src, begin + 1, number - 1, result, resultIdx, action);
        resultIdx--;
        combine(src, begin + 1, number, result, resultIdx, action);
    }

    /**
     * 组合的递归实现，在src数组元素比较多的时候极易内存溢出，数据量少的情况比非递归实现快80%左右<br>
     * 小数据优于combinate1，弱于combinate2，大数据内存溢出
     *
     * @param src         数组对象
     * @param num         最小数量的组合
     * @param surplus_num 是否允许大于最小数量的组合
     * @param action      组合访问器，每个组合共用一个int[]实例
     */
    public final static void combinate3(int[] src, int num, boolean surplus_num, Consumer<int[]> action) {
        if (!surplus_num) {
            combine(src, 0, num, new int[num], 0, action);
            return;
        }
        for (int n = num; n <= src.length; n++) {
            combine(src, 0, n, new int[n], 0, action);
        }
    }

    /**
     * 组合位运算实现 ，abc共有（2^3）-1中可能 即001-111 ；<br>
     * 可以用0代表不存在，1代表存在 。001 即（a*0）(b*0)(c*1) 也就是c ,010为b 以此类推 111为abc<br>
     * 此方法求得的结果散漫，小数据优于combinate3，弱于combinate1，大数据最优
     *
     * @param src         数组对象
     * @param num         最小数量的组合
     * @param surplus_num 是否允许大于最小数量的组合
     * @param action      组合访问器
     */
    public static final void combinate2(int[] src, int num, boolean surplus_num, Consumer<int[]> action) {
        final int len = src.length;
        final int temp = 1 << len;
        IntArray list = new IntArray(len);
        // 共有temp-1种可能，即001~111
        for (int i = 1; i < temp; i++) {
            int t;
            for (int j = 0; j < len; j++) {
                // 与运算,把001~111这七个数与1，2，4即（001，010，100）与运算，若不为0 说明对应该位数存在
                t = 1 << j;
                if ((t & i) != 0) {
                    list.add(src[j]);
                }
            }
            if (!surplus_num) {
                if (list.size() == num) {
                    action.accept(list.toArray());
                }
            } else {
                if (list.size >= num) {
                    action.accept(list.toArray());
                }
            }
            list.clear();
        }
    }

    /**
     * 组合<br>
     * 模拟num层for循环
     *
     * @param src    数组对象
     * @param num    组合的数量
     * @param action 组合访问器
     */
    private static void combinate(int[] src, int num, Consumer<int[]> action) {
        int[][] idx_max = new int[num][2]; //每层循环的循环变量和最大值
        for (int i = 0; i < idx_max.length; i++) {
            idx_max[i][0] = i; //初始索引
            idx_max[i][1] = src.length - (num - i); //索引最大值
        }
        int lastLoop = num - 1; //最后一层循环
        while (idx_max[0][0] <= idx_max[0][1]) {
            int[] group = new int[num];
            for (int i = 0; i < idx_max.length; i++) {
                group[i] = src[idx_max[i][0]];//写入每层循环的值
            }
            action.accept(group);
            idx_max[lastLoop][0]++;
            if (idx_max[lastLoop][0] > idx_max[lastLoop][1]) {
                int jinIdx = -1; //需要进1的索引位（需要执行的上层循环）
                for (int j = lastLoop; j >= 0; j--) {
                    if (idx_max[j][0] > idx_max[j][1]) {
                        int prev = j - 1;
                        if (prev >= 0) {
                            idx_max[prev][0]++;
                            jinIdx = prev;
                        }
                    }
                }
                if (jinIdx >= 0) {
                    //如有上层循环需要执行，则其内的每层内层循环的初始值将被重置
                    for (int j = jinIdx + 1; j < idx_max.length; j++) {
                        int prev = j - 1;
                        int prevTempIdx = -1;
                        if (prev >= 0) {
                            prevTempIdx = idx_max[prev][0];
                        }
                        if (prevTempIdx >= 0) {
                            idx_max[j][0] = prevTempIdx + 1;
                        } else {
                            idx_max[j][0] = j;
                        }
                    }
                }
            }
        }
    }

    /**
     * 组合的非递归实现，数据量小的情况请使用递归实现，数据量大运行效率也不是很高，但不会内存溢出
     *
     * @param src         数组对象
     * @param num         最小数量的组合
     * @param surplus_num 是否允许大于最小数量的组合
     * @param action      组合访问器，每个组合为独立的int[]实例
     */
    public final static void combinate1(int[] src, int num, boolean surplus_num, Consumer<int[]> action) {
        if (!surplus_num) {
            combinate(src, num, action);
            return;
        }
        for (int n = num; n <= src.length; n++) {
            combinate(src, n, action);
        }
    }

    /**
     * 类型聚合的排列组合，递归实现
     *
     * @param src       数组对象
     * @param begin     从源数组中的此索引处取得一个数据并存入result中
     * @param number    组合数量
     * @param result    一组组合数据
     * @param resultIdx result中索引变更，即每组组合数据共用result对象，仅改变此索引
     * @param action    一组组合数据的访问器
     */
    private static void combine(KeyValue[] src, int begin, int number, KeyValue[] result, int resultIdx, Consumer<KeyValue[]> action) {
        if (number == 0) {
            action.accept(result);
            return;
        }
        if (begin >= src.length) {
            return;
        }
        boolean flag = true;
        if (resultIdx > 0) {
            KeyValue prevResult = result[resultIdx - 1];
            KeyValue currSrc = src[begin];
            if (prevResult.getKey().equals(currSrc.getKey())) {
                flag = false;
            }
        }
        result[resultIdx] = src[begin];
        if (flag) {
            resultIdx++;
            combine(src, begin + 1, number - 1, result, resultIdx, action);
            resultIdx--;
            combine(src, begin + 1, number, result, resultIdx, action);
        } else {
            combine(src, begin + 1, number, result, resultIdx, action);
        }
    }

    /**
     * 类型聚合的排列组合
     *
     * @param src    其中的KeyValue对象，key表示类型，value表示类型所指向的内容
     * @param action 组合访问器
     */
    public final static void combinate(KeyValue[] src, Consumer<KeyValue[]> action) {
        //相同类型数量
        int same_key_size = 0;
        //临时保存类型
        Object tempKey = null;
        //计算相同类型的数量
        for (int i = 0; i < src.length; i++) {
            if (tempKey == null || !src[i].getKey().equals(tempKey)) {
                tempKey = src[i].getKey();
                same_key_size++;
            }
        }
        //排列组合
        combine(src, 0, same_key_size, new KeyValue[same_key_size], 0, action);
    }

    /**
     * 组合，笛卡尔积，递归实现
     *
     * @param <T>      数据限定类型
     * @param dimvalue 原始数据
     * @param layer    原始数据递归中的索引
     * @param curList  当前组合
     * @param action   当前组合访问器
     */
    private static <T> void descartes(List<List<T>> dimvalue, int layer, List<T> curList, Consumer<List<T>> action) {
        if (layer < dimvalue.size() - 1) {
            if (dimvalue.get(layer).isEmpty()) {
                MathUtils.descartes(dimvalue, layer + 1, curList, action);
            } else {
                for (int i = 0; i < dimvalue.get(layer).size(); i++) {
                    List<T> list = new ArrayList<>(dimvalue.size());
                    list.addAll(curList);
                    list.add(dimvalue.get(layer).get(i));
                    MathUtils.descartes(dimvalue, layer + 1, list, action);
                }
            }
        } else if (layer == dimvalue.size() - 1) {
            if (dimvalue.get(layer).isEmpty()) {
                action.accept(curList);
            } else {
                for (int i = 0; i < dimvalue.get(layer).size(); i++) {
                    List<T> list = new ArrayList<>(dimvalue.size());
                    list.addAll(curList);
                    list.add(dimvalue.get(layer).get(i));
                    action.accept(list);
                }
            }
        }
    }

    /**
     * 组合，笛卡尔积，递归实现
     *
     * @param <T>      数据限定类型
     * @param dimvalue 原始数据
     * @param action   当前组合访问器
     */
    public final static <T> void descartes(List<List<T>> dimvalue, Consumer<List<T>> action) {
        MathUtils.descartes(dimvalue, 0, new ArrayList<>(dimvalue.size()), action);
    }

    /**
     * 组合，笛卡尔积，递归实现
     *
     * @param <T>
     * @param dimvalue 原始数据
     * @return 所有组合的集合
     */
    public final static <T> List<List<T>> descartes(List<List<T>> dimvalue) {
        final List<List<T>> list = new ArrayList<>();
        MathUtils.descartes(dimvalue, 0, new ArrayList<>(dimvalue.size()), (List<T> group) -> {
            list.add(group);
        });
        return list;
    }

    /**
     * 按src下标索引有序的排列组合
     *
     * @param <T>
     * @param src    列表对象
     * @param num    最小数量的组合
     * @param action 组合访问器
     */
    public final static <T> void delineate(List<T> src, int num, Consumer<List<T>> action) {
        if (num == src.size()) {
            action.accept(src);
            return;
        }
        int loop = src.size() - num;
        for (int i = 0; i <= loop; i++) {
            List<T> group = new ArrayList<>(num);
            int max = i + num;
            for (int j = i; j < max; j++) {
                group.add(src.get(j));
            }
            action.accept(group);
        }
    }

    /**
     * 按src下标索引有序的排列组合
     *
     * @param <T>
     * @param src             列表对象
     * @param min_num         最小数量的组合
     * @param surplus_min_num 是否允许大于最小数量的组合
     * @param action          组合访问器
     */
    public final static <T> void delineate(List<T> src, int min_num, boolean surplus_min_num, Consumer<List<T>> action) {
        if (!surplus_min_num) {
            delineate(src, min_num, action);
            return;
        }
        for (int n = min_num; n <= src.size(); n++) {
            delineate(src, n, action);
        }
    }

    /**
     * 按src下标索引有序的排列组合
     *
     * @param src    数组对象
     * @param num    圈定数量
     * @param action 组合访问器
     */
    public final static void delineate(int[] src, int num, Consumer<int[]> action) {
        if (num == src.length) {
            action.accept(src);
            return;
        }
        int loop = src.length - num;
        for (int i = 0; i <= loop; i++) {
            int[] values = new int[num];
            System.arraycopy(src, i, values, 0, num);
            action.accept(values);
        }
    }

    /**
     * 按src下标索引有序的排列组合
     *
     * @param src             数组对象
     * @param min_num         最小数量的组合
     * @param surplus_min_num 是否允许大于最小数量的组合
     * @param action          组合访问器
     */
    public final static void delineate(int[] src, int min_num, boolean surplus_min_num, Consumer<int[]> action) {
        if (!surplus_min_num) {
            delineate(src, min_num, action);
            return;
        }
        for (int n = min_num; n <= src.length; n++) {
            delineate(src, n, action);
        }
    }

    /**
     * 概率命中
     *
     * @param rate  概率值（大于0)
     * @param total 总值
     * @return true命中
     */
    public final static boolean isHitProbability(float rate, float total) {
        if (rate >= total) {
            return true;
        }
        float hitValue = MathUtils.random(total);
        return hitValue <= rate;
    }

    /**
     * 概率命中算法
     *
     * @param rates 各种概率值
     * @return 命中的索引位置
     */
    public final static int hitProbability(int[] rates) {
        int totals = 0;
        for (int rate : rates) {
            totals += rate;
        }
        //取[1, totals]闭区间中的一个随机数
        double randomPoint = MathUtils.random(1, totals);
        for (int i = 0; i < rates.length; i++) {
            if (randomPoint < rates[i]) {
                return i;
            } else {
                randomPoint -= rates[i];
            }
        }
        return rates.length - 1;
    }

    /**
     * 概率命中算法
     *
     * @param rates 各种概率值
     * @return 命中的索引位置
     */
    public final static int hitProbability(double[] rates) {
        double totals = 0d;
        for (double rate : rates) {
            totals += rate;
        }
        //取[0, totals]之间的随机数
        double randomPoint = getRandom().nextDouble() * totals;
        for (int i = 0; i < rates.length; i++) {
            if (randomPoint < rates[i]) {
                return i;
            } else {
                randomPoint -= rates[i];
            }
        }
        return rates.length - 1;
    }

    /**
     * 二维矩阵旋转
     *
     * @param matrix 二维矩阵（二维数组）
     * @param angle  旋转角度，仅限于0度或者90的倍数
     * @param txy    平移量（依据此点旋转）
     * @return 旋转后的二维矩阵
     */
    private static int[][] rotate(int[][] matrix, int angle, float[] txy) {
        int maxX = -1, maxY = matrix.length;
        int[][] copy = new int[matrix.length][];
        for (int i = 0; i < matrix.length; i++) {
            copy[i] = new int[matrix[i].length];
            System.arraycopy(matrix[i], 0, copy[i], 0, copy[i].length);
            maxX = copy[i].length;
        }
        double tx, ty;
        if (txy == null) {
            tx = ((float) maxX - 1) / 2;
            ty = ((float) maxY - 1) / 2;
        } else {
            tx = txy[0];
            ty = txy[1];
        }
        double radian = degreesToRadians * Math.abs(angle);
        double cos = Math.cos(radian);
        double sin = Math.sin(radian);
        int _maxX = Math.abs((int) Math.round(maxX * cos + maxY * sin));
        int _maxY = Math.abs((int) Math.round(maxY * cos + maxX * sin));
        if (_maxX == 0) {
            _maxX = maxY;
        }
        if (_maxY == 0) {
            _maxY = maxX;
        }
        int[][] newMatrix = copy;
        if (_maxX != maxX || _maxY != maxY) {
            newMatrix = new int[_maxY][_maxX];
        }
        radian = degreesToRadians * angle;
        cos = Math.cos(radian);
        sin = Math.sin(radian);
        for (int y = 0; y < matrix.length; y++) {
            for (int x = 0; x < matrix[y].length; x++) {
                double x0 = x - tx;                 //先做一次平移
                double y0 = y - ty;
                double rx = x0 * cos - y0 * sin;    //再做旋转
                double ry = x0 * sin + y0 * cos;
                double rtx = tx * cos - ty * sin;   //旋转围绕点
                double rty = tx * sin + ty * cos;
                rx += Math.abs(rtx);                //再反向平移
                ry += Math.abs(rty);

                int _x = (int) Math.round(rx);
                int _y = (int) Math.round(ry);
                _x = Math.abs(_x);
                _y = Math.abs(_y);
                newMatrix[_y][_x] = matrix[y][x];
            }
        }
        return newMatrix;
    }

    /**
     * 二维矩阵旋转
     *
     * @param matrix 二维矩阵（二维数组）
     * @param angle  旋转角度，仅限于0度或者90的倍数
     * @return 旋转后的二维矩阵
     */
    public static final int[][] rotate(int[][] matrix, int angle) {
        if (angle % 90 != 0) {
            throw new IllegalArgumentException("angle角度参数必须能整除90");
        }
        return rotate(matrix, angle, null);
    }

    /**
     * 动态规划01背包算法，求解最大价值时的最优组合
     *
     * @param val 价值数组，必须与权重数组数量相同，一个价值和权重构成一个物品
     * @param wt  权重数组，必须与价值数组数量相同，一个价值和权重构成一个物品
     * @param W   总权重
     * @return 最优项集合，索引从1开始，0号索引为最优时的最大价值；集合中的每项0号下标为价值，1号下标为权重
     */
    public static List<int[]> knapsack(int val[], int wt[], int W) {
        //物品数量总和
        int N = wt.length;
        //创建一个二维数组
        //行最多存储N个物品，列最多为总权重W，下边N+1和W+1是保证从1开始
        int[][] F = new int[N + 1][W + 1];
        //将行为 0或者列为0的值，都设置为0
        for (int col = 0; col <= W; col++) {
            F[0][col] = 0;
        }
        for (int row = 0; row <= N; row++) {
            F[row][0] = 0;
        }
        //从1开始遍历N个物品
        for (int item = 1; item <= N; item++) {
            //一行一行的填充数据
            for (int weight = 1; weight <= W; weight++) {
                if (wt[item - 1] <= weight) {
                    //选取（当前项值+之前项去掉当前项权重的值）与不取当前项的值得最大者
                    F[item][weight] = Math.max(val[item - 1] + F[item - 1][weight - wt[item - 1]], F[item - 1][weight]);
                } else {//不选取当前项，以之前项代替
                    F[item][weight] = F[item - 1][weight];
                }
            }

        }
//        //打印最终矩阵
//        for (int[] rows : V) {
//            for (int col : rows) {
//                System.out.format("%5d", col);
//            }
//            System.out.println();
//        }

//        int N = wt.length;
//        int[][] F = new int[N + 1][W + 1];
//        for (int i = 0; i <= N; i++) {
//            for (int v = 0; v <= W; v++) {
//                if (i < 1 || v == 0) {
//                    F[i][v] = 0;
//                } else {
//                    int w = wt[i - 1];
//                    if (w > v || F[i - 1][v] > F[i - 1][v - w] + w) {
//                        F[i][v] = F[i - 1][v];
//                    } else {
//                        F[i][v] = F[i - 1][v - w] + w;
//                    }
//                }
//            }
//        }

        // 回溯算法，算出选择的商品
        List<int[]> selected = new ArrayList<>();
        int k = W;
        int sumWt = 0;
        for (int i = wt.length; i > 0; i--) {
            int n = i - 1;
            if (F[i][k] > F[n][k]) {
                selected.add(new int[]{val[n], wt[n]});
                sumWt += wt[n];
                k -= wt[n];
            }
        }
        selected.add(new int[]{F[N][W], sumWt});
        Collections.reverse(selected);
        //返回结果
        return selected;
    }

//    private static class BagItem {
//        public int weight;
//        public int value;
//
//        public BagItem(int weight, int value) {
//            this.weight = weight;
//            this.value = value;
//        }
//
//        @Override
//        public String toString() {
//            return "BagItem{" +
//                    "weight=" + weight +
//                    ", value=" + value +
//                    '}';
//        }
//    }
//    private static List<BagItem> knapsack(BagItem[] arr, int W) {
//        int[][] F = new int[arr.length + 1][W + 1];
//        for (int i = 0; i <= arr.length; i++) {
//            for (int v = 0; v <= W; v++) {
//                if (i < 1 || v == 0) {
//                    F[i][v] = 0;
//                } else {
//                    int w = arr[i - 1].weight;
//                    if (w > v || F[i - 1][v] > F[i - 1][v - w] + w) {
//                        F[i][v] = F[i - 1][v];
//                    } else {
//                        F[i][v] = F[i - 1][v - w] + w;
//                    }
//                }
//            }
//        }
//        List selected = new ArrayList();
//        int k = W;
//        for (int i = arr.length; i > 0; i--) {
//            if (F[i][k] > F[i - 1][k]) {
//                selected.add(0, arr[i - 1]);
//                k -= arr[i - 1].weight;
//            }
//        }
//        return selected;
//    }

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
        if (k < 0) {
            throw new IllegalArgumentException(" 指数(" + k + ") 必须 >= 0");
        }
        if (b >= -2L & b <= 2L) {
            switch ((int) b) {
                case -2:
                    if (k >= 64) {
                        return 9223372036854775807L + (long) (k & 1);
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
            long limit = 9223372036854775807L + (b >>> 63 & (long) (k & 1));

            while (true) {
                switch (k) {
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

    public static void main(String[] args) {
        List<int[]> result = knapsack(new int[]{4, 5, 10, 11, 13}, new int[]{3, 6, 7, 8, 9}, 16);
        System.out.println(Arrays.deepToString(result.toArray()));

        result = knapsack(new int[]{100, 200, 300, 500, 900, 1005, 999}, new int[]{100, 200, 300, 500, 900, 1005, 999}, 1000);
        System.out.println(Arrays.deepToString(result.toArray()));

        result = knapsack(new int[]{1, 5, 8, 9, 10, 17, 17, 20, 24, 30}, new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10}, 30);
        System.out.println(Arrays.deepToString(result.toArray()));

//        BagItem[] arr = new BagItem[10];
//        arr[0] = new BagItem(100, 100);
//        arr[1] = new BagItem(200, 200);
//        arr[2] = new BagItem(300, 300);
//        arr[3] = new BagItem(500, 500);
//        arr[4] = new BagItem(900, 900);
//        arr[5] = new BagItem(1005, 1005);
//        arr[6] = new BagItem(999, 999);
//
//        List<BagItem> list = knapsack(arr, 30);
//        System.out.println(Arrays.toString(list.toArray()));
//
//        int t = 0;
//        for (BagItem item : list) {
//            t += item.value;
//        }
//        System.out.println(t);
    }

}
