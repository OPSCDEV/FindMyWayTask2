package com.example.findmyway;

public class User {
    String F_Name,L_Name,Address,Email,Password;
    public User(){}

    public User(String f_Name, String l_Name, String address, String email, String password) {
        this.F_Name = f_Name;
        this.L_Name = l_Name;
        this.Address = address;
        this.Email = email;
        this.Password = password;
    }

    public String getF_Name() {
        return F_Name;
    }

    public String getL_Name() {
        return L_Name;
    }

    public String getAddress() {
        return Address;
    }

    public String getUsername_Doc() {
        return Email;
    }

    public String getPassword_Doc() {
        return Password;
    }
}
