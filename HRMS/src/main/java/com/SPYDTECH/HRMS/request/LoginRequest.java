package com.SPYDTECH.HRMS.request;

import lombok.Data;

@Data
public class LoginRequest {
    private String email;
    private String password;


    public LoginRequest() {
        // TODO Auto-generated constructor stub
    }


    public LoginRequest(String email, String password) {
        super();
        this.email = email;
        this.password = password;
    }
}
