package com.ttn.comparedemo;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ProfileData implements Serializable{
    @SerializedName("name")
    public String name;

    @SerializedName("email")
    public String email;
    @SerializedName("phone")
    public String phone;
    @SerializedName("profilePicUrl")
    public String profilePicUrl;
    @SerializedName("street")
    public String street;
    @SerializedName("city")
    public String city;
    @SerializedName("pin")
    public String pin;




}
