package com.kaka.util.math;

import java.io.Serializable;

/**
 * 圆
 *
 * @author zkpursuit
 */
public class Circle implements Serializable {

    public float x, y;
    public float radius;

    public Circle(float x, float y, float radius) {
        this.x = x;
        this.y = y;
        this.radius = radius;
    }

    public Circle(Vector2 position, float radius) {
        this.x = position.x;
        this.y = position.y;
        this.radius = radius;
    }

    public Circle(Circle circle) {
        this.x = circle.x;
        this.y = circle.y;
        this.radius = circle.radius;
    }

    public void set(float x, float y, float radius) {
        this.x = x;
        this.y = y;
        this.radius = radius;
    }

    public void set(Vector2 position, float radius) {
        this.x = position.x;
        this.y = position.y;
        this.radius = radius;
    }

    public void set(Circle circle) {
        this.x = circle.x;
        this.y = circle.y;
        this.radius = circle.radius;
    }

    public void setPosition(Vector2 position) {
        this.x = position.x;
        this.y = position.y;
    }

    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void setX(float x) {
        this.x = x;
    }

    public void setY(float y) {
        this.y = y;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }

    /**
     * 是否包含坐标点
     *
     * @param x
     * @param y
     * @return true包含
     */
    public boolean contains(float x, float y) {
        x = this.x - x;
        y = this.y - y;
        return x * x + y * y <= radius * radius;
    }

    /**
     * 是否包含坐标点
     *
     * @param point 坐标点
     * @return true包含
     */
    public boolean contains(Vector2 point) {
        float dx = x - point.x;
        float dy = y - point.y;
        return dx * dx + dy * dy <= radius * radius;
    }

    /**
     * 是否包含另一个圆
     *
     * @param c
     * @return
     */
    public boolean contains(Circle c) {
        final float radiusDiff = radius - c.radius;
        if (radiusDiff < 0f) {
            return false;
        }
        final float dx = x - c.x;
        final float dy = y - c.y;
        final float dst = dx * dx + dy * dy;
        final float radiusSum = radius + c.radius;
        return (!(radiusDiff * radiusDiff < dst) && (dst < radiusSum * radiusSum));
    }

    /**
     * 与另一个圆是否相交
     *
     * @param c
     * @return
     */
    public boolean overlaps(Circle c) {
        float dx = x - c.x;
        float dy = y - c.y;
        float distance = dx * dx + dy * dy;
        float radiusSum = radius + c.radius;
        return distance < radiusSum * radiusSum;
    }

    @Override
    public String toString() {
        return x + "," + y + "," + radius;
    }

    /**
     * 圆周长
     *
     * @return
     */
    public float circumference() {
        return (float) (this.radius * Math.PI * 2);
    }

    /**
     * 圆面积
     *
     * @return
     */
    public float area() {
        return (float) (this.radius * this.radius * Math.PI);
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o == null || o.getClass() != this.getClass()) {
            return false;
        }
        Circle c = (Circle) o;
        return this.x == c.x && this.y == c.y && this.radius == c.radius;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 79 * hash + Float.floatToIntBits(this.x);
        hash = 79 * hash + Float.floatToIntBits(this.y);
        hash = 79 * hash + Float.floatToIntBits(this.radius);
        return hash;
    }

}
