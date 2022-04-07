package com.eteam.dufour.viewmodel;

public class ModelProductForDashboard {

    int surveyLevel;
    int facing;
    String posLin;

    public ModelProductForDashboard(int surveyLevel, int facing, String posLin) {
        this.surveyLevel = surveyLevel;
        this.facing = facing;
        this.posLin = posLin;
    }

    public int getSurveyLevel() {
        return surveyLevel;
    }

    public void setSurveyLevel(int surveyLevel) {
        this.surveyLevel = surveyLevel;
    }

    public int getFacing() {
        return facing;
    }

    public void setFacing(int facing) {
        this.facing = facing;
    }

    public String getPosLin() {
        return posLin;
    }

    public void setPosLin(String posLin) {
        this.posLin = posLin;
    }


}
