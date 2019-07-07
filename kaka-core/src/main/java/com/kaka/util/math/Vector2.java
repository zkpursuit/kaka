package com.kaka.util.math;

import com.kaka.util.MathUtils;
import java.io.Serializable;

/**
 * 二维向量，坐标点
 *
 * @author zkpursuit
 */
public class Vector2 implements Serializable {

    public final static Vector2 Zero = new Vector2(0, 0);

    public float x;
    public float y;

    public Vector2() {
    }

    public Vector2(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public Vector2(Vector2 v) {
        set(v);
    }

    public final Vector2 set(Vector2 v) {
        x = v.x;
        y = v.y;
        return this;
    }

    public final Vector2 set(float x, float y) {
        this.x = x;
        this.y = y;
        return this;
    }

    public final Vector2 copy() {
        return new Vector2(this);
    }

    public final boolean isZero() {
        return x == 0 && y == 0;
    }

    public final Vector2 setZero() {
        this.x = 0;
        this.y = 0;
        return this;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    /**
     * 减去向量
     *
     * @param x
     * @param y
     * @return
     */
    public Vector2 sub(float x, float y) {
        this.x -= x;
        this.y -= y;
        return this;
    }

    /**
     * 减去向量
     *
     * @param v
     * @return
     */
    public Vector2 sub(Vector2 v) {
        this.x -= v.x;
        this.y -= v.y;
        return this;
    }

    /**
     * 增加向量
     *
     * @param x
     * @param y
     * @return
     */
    public Vector2 add(float x, float y) {
        this.x += x;
        this.y += y;
        return this;
    }

    /**
     * 增加向量
     *
     * @param v
     * @return
     */
    public Vector2 add(Vector2 v) {
        this.x += v.x;
        this.y += v.y;
        return this;
    }

    /**
     * 本向量乘以某个数值
     *
     * @param v
     * @return 乘以数值后新的向量
     */
    public Vector2 mult(float v) {
        return new Vector2(this.x * v, this.y * v);
    }

    /**
     * 本向量除以某数值
     *
     * @param v
     * @return 除以数值后新的向量
     */
    public Vector2 divi(float v) {
        return new Vector2(this.x / v, this.y / v);
    }

    /**
     * 两点距离
     *
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @return
     */
    public static float dist(float x1, float y1, float x2, float y2) {
        final float x_d = x2 - x1;
        final float y_d = y2 - y1;
        return (float) Math.sqrt(x_d * x_d + y_d * y_d);
    }

    /**
     * 与另一点的距离
     *
     * @param v
     * @return
     */
    public float dist(Vector2 v) {
        final float x_d = v.x - x;
        final float y_d = v.y - y;
        return (float) Math.sqrt(x_d * x_d + y_d * y_d);
    }

    /**
     * 与另一点的距离
     *
     * @param x
     * @param y
     * @return
     */
    public float dist(float x, float y) {
        final float x_d = x - this.x;
        final float y_d = y - this.y;
        return (float) Math.sqrt(x_d * x_d + y_d * y_d);
    }

    /**
     * 点积
     *
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @return
     */
    public static float dot(float x1, float y1, float x2, float y2) {
        return x1 * x2 + y1 * y2;
    }

    /**
     * 点积
     *
     * @param v
     * @return
     */
    public float dot(Vector2 v) {
        return x * v.x + y * v.y;
    }

    /**
     * 点积
     *
     * @param ox
     * @param oy
     * @return
     */
    public float dot(float ox, float oy) {
        return x * ox + y * oy;
    }

    /**
     * 叉积
     *
     * @param v
     * @return
     */
    public float crs(Vector2 v) {
        return this.x * v.y - this.y * v.x;
    }

    /**
     * 叉积
     *
     * @param x
     * @param y
     * @return
     */
    public float crs(float x, float y) {
        return this.x * y - this.y * x;
    }

    /**
     * 与x轴的夹角
     * <p>
     * 角度朝向正y轴（通常为逆时针方向），介于0和360之间。
     *
     * @return 角度，非弧度
     */
    public float angle() {
        float angle = (float) (Math.atan2(y, x) * MathUtils.radiansToDegrees);
        if (angle < 0) {
            angle += 360;
        }
        return angle;
    }

    /**
     * 两向量夹角
     * <p>
     * 角度朝向正y轴（通常为逆时针方向），介于-180和+180之间。
     *
     * @param reference
     * @return 角度，非弧度
     */
    public float angle(Vector2 reference) {
        return (float) (Math.atan2(crs(reference), dot(reference)) * MathUtils.radiansToDegrees);
    }

    /**
     * 向量长度平方
     *
     * @return 向量长度平方
     */
    public double lenSQ() {
        return x * x + y * y;
    }

    /**
     * 向量长度
     *
     * @return 向量长度
     */
    public float len() {
        return (float) Math.sqrt(lenSQ());
    }

    /**
     * 设置向量长度并改变坐标
     *
     * @param v 新的向量长度
     */
    public void len(float v) {
        double angle = Math.atan2(y, x);
        x = (float) Math.cos(angle) * v;
        y = (float) Math.sin(angle) * v;
    }

    /**
     * 使本向量变为长度为1的单位向量
     *
     * @return 本向量
     */
    public Vector2 normalize() {
        float len = len();
        if (len == 0) {
            x = 1;
            return this;
        }
        x /= len;
        y /= len;
        return this;
    }

    /**
     * 是否为单位向量
     *
     * @return true为单位向量
     */
    public boolean isNormalized() {
        return this.len() == 1;
    }

    /**
     * 向量反转
     *
     * @return 反转后的本向量
     */
    public Vector2 reverse() {
        this.x = -this.x;
        this.y = -this.y;
        return this;
    }

    /**
     * 以角度旋转向量
     *
     * @param degrees 角度
     * @return 旋转后的本向量
     */
    public Vector2 rotate(float degrees) {
        return rotateRradians((float) (degrees * MathUtils.degreesToRadians));
    }

    /**
     * 以弧度旋转向量
     *
     * @param radians 弧度
     * @return 旋转后的本向量
     */
    public Vector2 rotateRradians(float radians) {
        float cos = (float) Math.cos(radians);
        float sin = (float) Math.sin(radians);
        float newX = this.x * cos - this.y * sin;
        float newY = this.x * sin + this.y * cos;
        this.x = newX;
        this.y = newY;
        return this;
    }

    /**
     * 以X轴为中轴垂直于本向量的向量点
     *
     * @return 垂直于本向量的向量点
     */
    public Vector2 perp() {
        return new Vector2(-y, x);
    }

    /**
     * 判断给定的向量在本向量的左边还是右边
     *
     * @param v 给定的向量
     * @return -1左边，1右边
     */
    public int sign(Vector2 v) {
        return perp().dot(v) < 0 ? -1 : 1;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + Float.floatToIntBits(this.x);
        hash = 59 * hash + Float.floatToIntBits(this.y);
        return hash;
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
        final Vector2 other = (Vector2) obj;
        if (Float.floatToIntBits(this.x) != Float.floatToIntBits(other.x)) {
            return false;
        }
        if (Float.floatToIntBits(this.y) != Float.floatToIntBits(other.y)) {
            return false;
        }
        return true;
    }
}
