package com.test.numerical.beans;

import com.kaka.numerical.annotation.NumericField;
import com.test.numerical.converter.ToItems;

import java.util.List;

/**
 * 七日签到配置
 *
 * @author zkpursuit
 */
public class ConfDailySignInfo {
    private int id;
    private String name;

    @NumericField(elements = {"day1"}, converter = ToItems.class)
    private List<Item> day1;

    @NumericField(elements = {"day2"}, converter = ToItems.class)
    private List<Item> day2;

    @NumericField(elements = {"day3"}, converter = ToItems.class)
    private List<Item> day3;

    @NumericField(elements = {"day4"}, converter = ToItems.class)
    private List<Item> day4;

    @NumericField(elements = {"day5"}, converter = ToItems.class)
    private List<Item> day5;

    @NumericField(elements = {"day6"}, converter = ToItems.class)
    private List<Item> day6;

    @NumericField(elements = {"day7"}, converter = ToItems.class)
    private List<Item> day7;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Item> getDay1() {
        return day1;
    }

    public void setDay1(List<Item> day1) {
        this.day1 = day1;
    }

    public List<Item> getDay2() {
        return day2;
    }

    public void setDay2(List<Item> day2) {
        this.day2 = day2;
    }

    public List<Item> getDay3() {
        return day3;
    }

    public void setDay3(List<Item> day3) {
        this.day3 = day3;
    }

    public List<Item> getDay4() {
        return day4;
    }

    public void setDay4(List<Item> day4) {
        this.day4 = day4;
    }

    public List<Item> getDay5() {
        return day5;
    }

    public void setDay5(List<Item> day5) {
        this.day5 = day5;
    }

    public List<Item> getDay6() {
        return day6;
    }

    public void setDay6(List<Item> day6) {
        this.day6 = day6;
    }

    public List<Item> getDay7() {
        return day7;
    }

    public void setDay7(List<Item> day7) {
        this.day7 = day7;
    }
}
