package com.jxavier.almanax;


public class Almanax {
    private String date;
    private String name;
    private String offering;
    private String bonusTitle;
    private String bonusDescription;
    private String objectID;

    public Almanax(){

    }
    public Almanax(String date,
                   String name,
                   String offering,
                   String bonusTitle,
                   String bonusDescription,
                   String objectID) {
        this.date = date;
        this.name = name;
        this.offering = offering;
        this.bonusTitle = bonusTitle;
        this.bonusDescription = bonusDescription;
        this.objectID = objectID;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOffering() {
        return offering;
    }

    public void setOffering(String offering) {
        this.offering = offering;
    }

    public String getBonusTitle() {
        return bonusTitle;
    }

    public void setBonusTitle(String bonusTitle) {
        this.bonusTitle = bonusTitle;
    }

    public String getBonusDescription() {
        return bonusDescription;
    }

    public void setBonusDescription(String bonusDescription) {
        this.bonusDescription = bonusDescription;
    }

    public String getObjectID() {
        return objectID;
    }

    public void setObjectID(String objectID) {
        this.objectID = objectID;
    }
}
