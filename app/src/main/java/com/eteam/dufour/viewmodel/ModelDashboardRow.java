package com.eteam.dufour.viewmodel;

public class ModelDashboardRow {
    ModelDashboardItem val1,val2,val3,val4,val5;

    public ModelDashboardRow(ModelDashboardItem val1, ModelDashboardItem val2, ModelDashboardItem val3, ModelDashboardItem val4, ModelDashboardItem val5) {
        this.val1 = val1;
        this.val2 = val2;
        this.val3 = val3;
        this.val4 = val4;
        this.val5 = val5;
    }

    public ModelDashboardRow() {
    }

    public ModelDashboardItem getVal1() {
        return val1;
    }

    public void setVal1(ModelDashboardItem val1) {
        this.val1 = val1;
    }

    public ModelDashboardItem getVal2() {
        return val2;
    }

    public void setVal2(ModelDashboardItem val2) {
        this.val2 = val2;
    }

    public ModelDashboardItem getVal3() {
        return val3;
    }

    public void setVal3(ModelDashboardItem val3) {
        this.val3 = val3;
    }

    public ModelDashboardItem getVal4() {
        return val4;
    }

    public void setVal4(ModelDashboardItem val4) {
        this.val4 = val4;
    }

    public ModelDashboardItem getVal5() {
        return val5;
    }

    public void setVal5(ModelDashboardItem val5) {
        this.val5 = val5;
    }
}
