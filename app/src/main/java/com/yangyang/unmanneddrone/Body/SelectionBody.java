package com.yangyang.unmanneddrone.Body;

/**
 * 断面数据
 */
public class SelectionBody {

    private String serialNum;
    /** 横坐标 */
    private String abscissa;
    /** 纵坐标*/
    private String ordinate;

    public String getSerialNum() {
        return serialNum;
    }

    public void setSerialNum(String serialNum) {
        this.serialNum = serialNum;
    }

    public String getAbscissa() {
        return abscissa;
    }

    public void setAbscissa(String abscissa) {
        this.abscissa = abscissa;
    }

    public String getOrdinate() {
        return ordinate;
    }

    public void setOrdinate(String ordinate) {
        this.ordinate = ordinate;
    }

    @Override
    public String toString() {
        return "SelectionBody{" +
                "serialNum='" + serialNum + '\'' +
                ", abscissa='" + abscissa + '\'' +
                ", ordinate='" + ordinate + '\'' +
                '}';
    }
}
