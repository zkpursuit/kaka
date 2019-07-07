package com.kaka.util.math;

import java.io.Serializable;

/**
 * 平面数学中的矩形
 *
 * @author zkpursuit
 */
public class Rect implements Serializable {

    public float x, y;
    public float width, height;

    public Rect() {

    }

    public Rect(float x, float y, float width, float height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public Rect(Rect rect) {
        x = rect.x;
        y = rect.y;
        width = rect.width;
        height = rect.height;
    }

    public Rect set(float x, float y, float width, float height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        return this;
    }

    public float getX() {
        return x;
    }

    public Rect setX(float x) {
        this.x = x;
        return this;
    }

    public float getY() {
        return y;
    }

    public Rect setY(float y) {
        this.y = y;
        return this;
    }

    public float getWidth() {
        return width;
    }

    public Rect setWidth(float width) {
        this.width = width;
        return this;
    }

    public float getHeight() {
        return height;
    }

    public Rect setHeight(float height) {
        this.height = height;
        return this;
    }

    public Rect setPosition(float x, float y) {
        this.x = x;
        this.y = y;
        return this;
    }

    public Rect setSize(float width, float height) {
        this.width = width;
        this.height = height;
        return this;
    }

    public Rect setSize(float sizeXY) {
        this.width = sizeXY;
        this.height = sizeXY;
        return this;
    }

    /**
     * 判断是否包含指定的点
     *
     * @param x
     * @param y
     * @return
     */
    public boolean contains(float x, float y) {
        return this.x <= x && this.x + this.width >= x && this.y <= y && this.y + this.height >= y;
    }

    /**
     * 判断是否包含另一个矩形
     *
     * @param rectangle
     * @return
     */
    public boolean contains(Rect rectangle) {
        float xmin = rectangle.x;
        float xmax = xmin + rectangle.width;

        float ymin = rectangle.y;
        float ymax = ymin + rectangle.height;

        return ((xmin > x && xmin < x + width) && (xmax > x && xmax < x + width))
                && ((ymin > y && ymin < y + height) && (ymax > y && ymax < y + height));
    }

    /**
     * 是否包含指定的圆
     *
     * @param circleX 圆X坐标
     * @param circleY 圆Y坐标
     * @param circleRadius 圆半径
     * @return
     */
    public boolean contains(float circleX, float circleY, float circleRadius) {
        return (circleX - circleRadius >= x) && (circleX + circleRadius <= x + width)
                && (circleY - circleRadius >= y) && (circleY + circleRadius <= y + height);
    }

    /**
     * 判断是否与另一个矩形相交
     *
     * @param r
     * @return
     */
    public boolean overlaps(Rect r) {
        return x < r.x + r.width && x + width > r.x && y < r.y + r.height && y + height > r.y;
    }

    public Rect set(Rect rect) {
        this.x = rect.x;
        this.y = rect.y;
        this.width = rect.width;
        this.height = rect.height;
        return this;
    }

    /**
     * 将一个矩形并入到此矩形中
     *
     * @param rect
     * @return
     */
    public Rect merge(Rect rect) {
        float minX = Math.min(x, rect.x);
        float maxX = Math.max(x + width, rect.x + rect.width);
        x = minX;
        width = maxX - minX;

        float minY = Math.min(y, rect.y);
        float maxY = Math.max(y + height, rect.y + rect.height);
        y = minY;
        height = maxY - minY;

        return this;
    }

    /**
     * 将点并入到矩形区域
     *
     * @param x
     * @param y
     * @return
     */
    public Rect merge(float x, float y) {
        float minX = Math.min(this.x, x);
        float maxX = Math.max(this.x + width, x);
        this.x = minX;
        this.width = maxX - minX;

        float minY = Math.min(this.y, y);
        float maxY = Math.max(this.y + height, y);
        this.y = minY;
        this.height = maxY - minY;

        return this;
    }

    /**
     * 获取宽高比
     *
     * @return 宽高比
     */
    public float getAspectRatio() {
        return (height == 0) ? Float.NaN : width / height;
    }

    public Rect fitOutside(Rect rect) {
        float ratio = getAspectRatio();

        if (ratio > rect.getAspectRatio()) {
            // Wider than tall
            setSize(rect.height * ratio, rect.height);
        } else {
            // Taller than wide
            setSize(rect.width, rect.width / ratio);
        }

        setPosition((rect.x + rect.width / 2) - width / 2, (rect.y + rect.height / 2) - height / 2);
        return this;
    }

    public Rect fitInside(Rect rect) {
        float ratio = getAspectRatio();

        if (ratio < rect.getAspectRatio()) {
            // Taller than wide
            setSize(rect.height * ratio, rect.height);
        } else {
            // Wider than tall
            setSize(rect.width, rect.width / ratio);
        }

        setPosition((rect.x + rect.width / 2) - width / 2, (rect.y + rect.height / 2) - height / 2);
        return this;
    }

    /**
     * 面积
     *
     * @return 面积
     */
    public float area() {
        return this.width * this.height;
    }

    /**
     * 周长
     *
     * @return 周长
     */
    public float perimeter() {
        return 2 * (this.width + this.height);
    }

    /**
     * 复制对象
     *
     * @return 复制的对象
     */
    public Rect copy() {
        return new Rect(this.x, this.y, this.width, this.height);
    }

    @Override
    public String toString() {
        return "[" + x + "," + y + "," + width + "," + height + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Float.floatToRawIntBits(height);
        result = prime * result + Float.floatToRawIntBits(width);
        result = prime * result + Float.floatToRawIntBits(x);
        result = prime * result + Float.floatToRawIntBits(y);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Rect other = (Rect) obj;
        if (Float.floatToRawIntBits(height) != Float.floatToRawIntBits(other.height)) {
            return false;
        }
        if (Float.floatToRawIntBits(width) != Float.floatToRawIntBits(other.width)) {
            return false;
        }
        if (Float.floatToRawIntBits(x) != Float.floatToRawIntBits(other.x)) {
            return false;
        }
        if (Float.floatToRawIntBits(y) != Float.floatToRawIntBits(other.y)) {
            return false;
        }
        return true;
    }

}
