package com.example.doctorai.data;

import lombok.Data;
@Data
public class loginData{
    private String phonenumber;
    private int age = 18;
    private String name;
    private String sex = "男";
    private String other;
    private String openid;
    private String avatarUrl;
}
