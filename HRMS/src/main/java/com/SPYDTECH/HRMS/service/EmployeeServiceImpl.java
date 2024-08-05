package com.SPYDTECH.HRMS.service;

import com.SPYDTECH.HRMS.configuration.JwtTokenProvider;
import com.SPYDTECH.HRMS.entites.Employee;
import com.SPYDTECH.HRMS.entites.PasswordChange;
import com.SPYDTECH.HRMS.exceptions.UserException;
import com.SPYDTECH.HRMS.repository.EmployeeRepository;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EmployeeServiceImpl implements EmployeeService{

    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    @Autowired
    private EmployeeActivityService employeeActivityService;
    @Autowired
    private EmailService emailService;
    @Autowired
    private final JwtTokenProvider jwtTokenProvider;

    public EmployeeServiceImpl(EmployeeRepository employeeRepository, BCryptPasswordEncoder passwordEncoder, EmployeeActivityService employeeActivityService, JwtTokenProvider jwtTokenProvider) {
        this.employeeRepository = employeeRepository;
        this.passwordEncoder = passwordEncoder;
        this.employeeActivityService = employeeActivityService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public String createUserId(Employee employees) throws MessagingException {
        if(employeeRepository.existsByEmail(employees.getEmail())){
            return "Email is already registered.";
        }

        String password = employees.getPassword();
        employees.setPassword(passwordEncoder.encode(password));

        employeeRepository.save(employees);
        emailService.sendEmployeeIdAndPassword(employees.getEmail(),employees.getEmployeeId(),password);

        return "EmployeeId and password are sent successfully";
    }

    @Override
    public void logInEmployee(Employee employees) {
        employeeActivityService.employeeLoggedIn(employees);

    }

    @Override
    public void logOutEmployee(String email) {
        employeeActivityService.employeeLoggedOut(email);
    }

    @Override
    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    @Override
    public Employee findEmployeeProfileByJwt(String jwt) throws Exception {
        String email = jwtTokenProvider.getEmailFromJwtToken(jwt);
        Employee employee = employeeRepository.findByEmail(email);
        if(employee == null){
            throw new UserException("Employee not exist with email" + email);
        }
        return employee;
    }

    public Employee updateEmployee(String employeeId, Employee employeeDetails) {
        Optional<Employee> employeeOptional = employeeRepository.findByEmployeeId(employeeId);

        if (!employeeOptional.isPresent()) {
            return null;
        }

        Employee employee = employeeOptional.get();
        employee.setFirstName(employeeDetails.getFirstName());
        employee.setLastName(employeeDetails.getLastName());
        employee.setEmail(employeeDetails.getEmail());
        employee.setPhoneNumber(employeeDetails.getPhoneNumber());
        employee.setRole(employeeDetails.getRole());
        employee.setJoinDate(employeeDetails.getJoinDate());

        return employeeRepository.save(employee);
    }

    public boolean deleteEmployee(String employeeId) {
        Optional<Employee> employeeOptional = employeeRepository.findByEmployeeId(employeeId);

        if (!employeeOptional.isPresent()) {
            return false;
        }

        employeeRepository.delete(employeeOptional.get());
        return true;
    }


    public String updatePassword(String email, PasswordChange passwordChange){
        Optional<Employee> employee= Optional.ofNullable(employeeRepository.findByEmail(email));
        if(employee.isPresent()){
            Employee employee1=employee.get();
            if(passwordChange.getNewPassword().equals(passwordChange.getConfirmPassword())){
                if(passwordEncoder.matches(passwordChange.getOldPassword(), employee1.getPassword())){
                    employee1.setPassword(passwordEncoder.encode(passwordChange.getNewPassword()));
                    employeeRepository.save(employee1);
                    return "password updated successfully";
                }else{
                    return "password not matches with the old password";
                }

            }else{
                return "new Password and confirm Password are not equal";
            }

        }else{
            return "user is not found";
        }


    }
}
