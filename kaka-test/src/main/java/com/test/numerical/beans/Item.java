package com.test.numerical.beans;

/**
 * @author zkpursuit
 */
public class Item {

    private int cid;
    //数量
    private int num;

    public Item(int cid, int num) {
        this.cid = cid;
        this.num = num;
    }

    public int getCid() {
        return cid;
    }

    public void setCid(int cid) {
        this.cid = cid;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    @Override
    public String toString() {
        return cid + "#" + num;
    }

    @Override
    public Item clone() {
        return new Item(this.cid, this.num);
    }

}
