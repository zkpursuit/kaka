package com.test.numerical.beans;

/**
 * 道具配置表
 *
 * @author zkpursuit
 */
public class ConfItemInfo {
    private int id;
    private String alias;
    private String name;
    private int type; //1、游戏内虚拟货币；2、道具；3、红包；4、实物；1000、现实货币
    private int pileup;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getPileup() {
        return pileup;
    }

    public void setPileup(int pileup) {
        this.pileup = pileup;
    }
}
