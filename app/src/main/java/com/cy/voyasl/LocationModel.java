package com.cy.voyasl;

public class LocationModel {
    public String ID;
    public String userID;
    public String Title;
    public String Description;
    public String Location;
    public String Approval;
    public String ImgUrl_1;
    public String ImgUrl_2;
    public String ImgUrl_3;
    public String XLatLog;



    public LocationModel() {
        // Default constructor required for calls to DataSnapshot.getValue(Product.class)
    }

    // Constructor, getters, and setters
    public LocationModel(String ID, String userID, String Title, String Description, String Location, String Approval, String ImgUrl_1, String ImgUrl_2, String ImgUrl_3, String XLatLog) {
        this.ID = ID;
        this.userID =userID;
        this.Title = Title;
        this.Description = Description;
        this.Location = Location;
        this.Approval = Approval;
        this.ImgUrl_1 = ImgUrl_1;
        this.ImgUrl_2 = ImgUrl_2;
        this.ImgUrl_3 = ImgUrl_3;
        this.XLatLog = XLatLog;

    }



    public String getTitlee() {
        return Title;
    }

    public String getDescriptionn() {
        return Description;
    }



    public String getImgUrl_11() {
        return ImgUrl_1;
    }

    public String getImgUrl_22() {
        return ImgUrl_2;
    }

    public String getImgUrl_33() {
        return ImgUrl_3;
    }
}
