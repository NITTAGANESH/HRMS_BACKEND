package com.SPYDTECH.HRMS.service;

import com.SPYDTECH.HRMS.entites.Employee;
import com.SPYDTECH.HRMS.repository.EmployeeRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CustomEmployeeDetails implements UserDetailsService {
    private final EmployeeRepository employeeRepository;

    public CustomEmployeeDetails(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String firstname) throws UsernameNotFoundException {
        Employee employee = employeeRepository.findByEmail(firstname);

        if(employee == null){
            throw new UsernameNotFoundException("Employee not found with email: "+ firstname);
        }
        List<GrantedAuthority> authorities = new ArrayList<>();
        return new User(employee.getEmail(), employee.getPassword(),authorities);
    }
}
