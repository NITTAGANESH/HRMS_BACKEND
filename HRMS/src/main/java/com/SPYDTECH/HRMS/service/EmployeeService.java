package com.SPYDTECH.HRMS.service;

import com.SPYDTECH.HRMS.entites.Employee;
import com.SPYDTECH.HRMS.entites.PasswordChange;
import jakarta.mail.MessagingException;

import java.util.List;

public interface EmployeeService {
    String createUserId(Employee employees) throws MessagingException;
     void logInEmployee(Employee employees);

     void logOutEmployee(String email);
     public List<Employee> getAllEmployees();
     Employee findEmployeeProfileByJwt(String jwt) throws Exception;

    Employee updateEmployee(String employeeId, Employee employeeDetails);

    boolean deleteEmployee(String employeeId);

    public String updatePassword(String email, PasswordChange passwordChange);

}
