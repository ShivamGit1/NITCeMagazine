package com.example.nitcemagazine.LoginAndSignUp;

public class UserDetails {
    public String name,role,email;

    public UserDetails() {}

    public UserDetails(String name, String email) {
        this.name = name;
        this.email = email;
        this.role = "student";
    }
}
