package com.eteam.model;

public class ItemsModel {
    private int _id;
   private String No_ ;
   private String AssortmentiStatus ;
   private String AssortmentiCustCodes ;
   private String Marchio ;
   private String Linea ;
   private String MacroFamiglia ;
   private String Description ;
   private String Base_Unit_of_Measure ;


    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String getNo_() {
        return No_;
    }

    public void setNo_(String no_) {
        No_ = no_;
    }

    public String getAssortmentiStatus() {
        return AssortmentiStatus;
    }

    public void setAssortmentiStatus(String assortmentiStatus) {
        AssortmentiStatus = assortmentiStatus;
    }

    public String getAssortmentiCustCodes() {
        return AssortmentiCustCodes;
    }

    public void setAssortmentiCustCodes(String assortmentiCustCodes) {
        AssortmentiCustCodes = assortmentiCustCodes;
    }

    public String getMarchio() {
        return Marchio;
    }

    public void setMarchio(String marchio) {
        Marchio = marchio;
    }

    public String getLinea() {
        return Linea;
    }

    public void setLinea(String linea) {
        Linea = linea;
    }

    public String getMacroFamiglia() {
        return MacroFamiglia;
    }

    public void setMacroFamiglia(String macroFamiglia) {
        MacroFamiglia = macroFamiglia;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getBase_Unit_of_Measure() {
        return Base_Unit_of_Measure;
    }

    public void setBase_Unit_of_Measure(String base_Unit_of_Measure) {
        Base_Unit_of_Measure = base_Unit_of_Measure;
    }
}
