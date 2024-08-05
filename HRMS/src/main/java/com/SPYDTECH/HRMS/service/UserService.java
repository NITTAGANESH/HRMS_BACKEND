package com.SPYDTECH.HRMS.service;

import com.SPYDTECH.HRMS.entites.Employee;
import com.SPYDTECH.HRMS.entites.User;


public interface UserService {
    User findUserProfileByJwt(String jwt) throws Exception;

}
