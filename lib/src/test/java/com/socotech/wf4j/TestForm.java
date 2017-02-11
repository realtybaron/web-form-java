package com.socotech.wf4j;

import java.util.Date;

/**
 * User: marc Date: Jan 22, 2010 Time: 5:18:30 PM
 */
public final class TestForm {
    private int integer;
    private double doubl;
    private boolean bool;
    private Date date;
    private Long longs;
    private String string;
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
