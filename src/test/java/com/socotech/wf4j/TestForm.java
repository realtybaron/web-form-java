package com.socotech.wf4j;

import java.util.Date;

/**
 * User: marc Date: Jan 22, 2010 Time: 5:18:30 PM
 */
public class TestForm {
    private int integer;
    private Date date;
    private Long longs;
    private double doubl;
    private String string;
    private boolean bool;
    private String[] array;

    public boolean isBool() {
        return bool;
    }

    public void setBool(boolean bool) {
        this.bool = bool;
    }

    public int getInteger() {
        return integer;
    }

    public void setInteger(int integer) {
        this.integer = integer;
    }

    public double getDoubl() {
        return doubl;
    }

    public void setDoubl(double doubl) {
        this.doubl = doubl;
    }

    public Long getLongs() {
        return longs;
    }

    public void setLongs(Long longs) {
        this.longs = longs;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getString() {
        return string;
    }

    public void setString(String string) {
        this.string = string;
    }

    public String[] getArray() {
        return array;
    }

    public void setArray(String[] array) {
        this.array = array;
    }
}
