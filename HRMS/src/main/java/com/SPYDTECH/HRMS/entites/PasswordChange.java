package com.SPYDTECH.HRMS.entites;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PasswordChange {
    private String oldPassword;
    private String newPassword;
    private String confirmPassword;

}
