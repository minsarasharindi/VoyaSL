package com.cy.voyasl;

public class AddTaxiModelview {

    public String ID;
    public String userID;
    public String Name;
    public String Vehicalnumber;
    public String Vehicaltype;

    public String ImgUrl_1;

    public AddTaxiModelview() {
        // Required for data retrieval from Firebase
    }



    public AddTaxiModelview(String ID, String userID, String Name, String Vehicalnumber, String Vehicaltype, String ImgUrl_1) {
        this.ID = ID;
        this.userID =userID;
        this.Name = Name;
        this.Vehicalnumber = Vehicalnumber;
        this.Vehicaltype = Vehicaltype;
        this.ImgUrl_1 = ImgUrl_1;


    }


    public String getIDd() {
        return ID;
    }

    public String getUserIDd() {
        return userID;
    }

    public String getNamed() {
        return Name;
    }

    public String getVehicalnumberd() {
        return Vehicalnumber;
    }

    public String getVehicaltyped() {
        return Vehicaltype;
    }

    public String getImgUrl_1d() {
        return ImgUrl_1;
    }

}
