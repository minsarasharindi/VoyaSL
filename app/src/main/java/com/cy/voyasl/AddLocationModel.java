package com.cy.voyasl;

public class AddLocationModel {

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


    public AddLocationModel(String ID, String userID, String Title, String Description, String Location, String Approval, String ImgUrl_1, String ImgUrl_2, String ImgUrl_3, String XLatLog) {
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



}
