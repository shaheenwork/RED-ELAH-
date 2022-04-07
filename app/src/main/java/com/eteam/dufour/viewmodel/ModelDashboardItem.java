package com.eteam.dufour.viewmodel;

public class ModelDashboardItem {

    int type;
    String value;
    int circleColor;
    int plusOrMinus;

    public ModelDashboardItem(int type, String value, int circleColor, int plusOrMinus) {
        this.type = type;
        this.value = value;
        this.circleColor = circleColor;
        this.plusOrMinus = plusOrMinus;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public int getCircleColor() {
        return circleColor;
    }

    public void setCircleColor(int circleColor) {
        this.circleColor = circleColor;
    }

    public int getPlusOrMinus() {
        return plusOrMinus;
    }

    public void setPlusOrMinus(int plusOrMinus) {
        this.plusOrMinus = plusOrMinus;
    }
}
