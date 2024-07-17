package com.cy.voyasl;

public class User {
    public String firstName, lastName, email, mobile;

    public User() {
        // Default constructor required for Firebase's DataSnapshot.getValue(User.class)
    }

    public User(String firstName, String lastName, String email, String mobile) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.mobile = mobile;

    }
}

