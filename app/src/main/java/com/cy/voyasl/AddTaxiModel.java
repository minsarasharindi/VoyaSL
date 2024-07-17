package com.cy.voyasl;

public class AddTaxiModel {

    public String ID;
    public String userID;
    public String Name;
    public String Vehicalnumber;
    public String Vehicaltype;

    public String ImgUrl_1;

    public AddTaxiModel() {
        // Required for data retrieval from Firebase
    }


    public AddTaxiModel(String ID, String userID, String Name, String Vehicalnumber, String Vehicaltype,  String ImgUrl_1) {
        this.ID = ID;
        this.userID =userID;
        this.Name = Name;
        this.Vehicalnumber = Vehicalnumber;
        this.Vehicaltype = Vehicaltype;
        this.ImgUrl_1 = ImgUrl_1;


    }



}
