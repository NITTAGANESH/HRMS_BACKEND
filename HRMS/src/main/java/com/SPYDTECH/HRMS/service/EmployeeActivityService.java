package com.SPYDTECH.HRMS.service;

import com.SPYDTECH.HRMS.entites.Employee;
import com.SPYDTECH.HRMS.repository.EmployeeRepository;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class EmployeeActivityService {
    @Getter
    private final Map<String, Employee> activeEmployees = new ConcurrentHashMap<>();

    @Autowired
    private EmployeeRepository employeeRepository;

    public void employeeLoggedIn(Employee employee){
        activeEmployees.put(employee.getEmployeeId(),employee);
    }

    public void employeeLoggedOut(String email){
        activeEmployees.remove(email);
    }

    public int getActiveEmployeeCount(){
        return activeEmployees.size();
    }

    public int getInactiveEmployeeCount(){
        long totalEmployeeCount = employeeRepository.count();
        return (int) (totalEmployeeCount-activeEmployees.size());
    }
}
