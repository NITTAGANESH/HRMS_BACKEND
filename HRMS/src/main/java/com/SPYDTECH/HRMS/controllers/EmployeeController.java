package com.SPYDTECH.HRMS.controllers;

import com.SPYDTECH.HRMS.configuration.JwtTokenProvider;
import com.SPYDTECH.HRMS.entites.Employee;
import com.SPYDTECH.HRMS.entites.OtpVerificationRequest;
import com.SPYDTECH.HRMS.entites.PasswordChange;
import com.SPYDTECH.HRMS.exceptions.UserException;
import com.SPYDTECH.HRMS.repository.EmployeeRepository;
import com.SPYDTECH.HRMS.request.LoginRequest;
import com.SPYDTECH.HRMS.response.AuthResponse;
import com.SPYDTECH.HRMS.service.*;
import jakarta.mail.MessagingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/employee")
public class EmployeeController {
    private static final Logger logger = LoggerFactory.getLogger(EmployeeController.class);

    @Autowired
    private EmployeeService employeeService;

    private String generatedOtp;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private CustomEmployeeDetails customEmployeeDetails;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private EmployeeActivityService employeeActivityService;

    @Autowired
    private OtpService otpService;

    @Autowired
    EmployeeRepository employeeRepository;

    @Autowired
    EmailService emailService;

    @PostMapping("/register")
    public ResponseEntity<?> createLoginCredentials(@RequestBody Employee employee) {
        try {
            String response = employeeService.createUserId(employee);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (MessagingException e) {
            logger.error("Error sending email for Employee registration: {}", e.getMessage());
            return new ResponseEntity<>("Error sending email for Employee registration.", HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            logger.error("Registration error: {}", e.getMessage());
            return new ResponseEntity<>("An error occurred during registration.", HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/signin")
    public ResponseEntity<AuthResponse> signin(@RequestBody LoginRequest loginRequest) {
        String username = loginRequest.getEmail();
        String password = loginRequest.getPassword();

        System.out.println(username +" ----- "+password);

        Authentication authentication = authenticate(username, password);
        SecurityContextHolder.getContext().setAuthentication(authentication);


        String token = jwtTokenProvider.generateToken(authentication);
        AuthResponse authResponse= new AuthResponse();

        authResponse.setStatus(true);
        authResponse.setJwt(token);

        return new ResponseEntity<AuthResponse>(authResponse,HttpStatus.OK);
    }

    private Authentication authenticate(String username, String password) {
        UserDetails userDetails = customEmployeeDetails.loadUserByUsername(username);

        System.out.println("sign in userDetails - "+userDetails);

        if (userDetails == null) {
            System.out.println("sign in userDetails - null " + userDetails);
            throw new BadCredentialsException("Invalid username or password");
        }
        if (!passwordEncoder.matches(password, userDetails.getPassword())) {
            System.out.println("sign in userDetails - password not match " + userDetails);
            throw new BadCredentialsException("Invalid username or password");
        }
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }

    @GetMapping("/profile")
    public ResponseEntity<Employee> getEmployeeProfileHandler(@RequestHeader("Authorization") String jwt) throws Exception {
        Employee employee = employeeService.findEmployeeProfileByJwt(jwt);
        return new ResponseEntity<>(employee, HttpStatus.ACCEPTED);
    }

    @GetMapping("/getAllEmployee")
    public ResponseEntity<List<Employee>> getAllEmployees(){
        return new ResponseEntity<>(employeeService.getAllEmployees(),HttpStatus.OK);
    }

    // Update employee by employeeId
    @PutMapping("/update/{employeeId}")
    public ResponseEntity<Employee> updateEmployee(@PathVariable String employeeId, @RequestBody Employee employeeDetails) {
        Employee updatedEmployee = employeeService.updateEmployee(employeeId, employeeDetails);

        if (updatedEmployee == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(updatedEmployee);
    }

    // Delete employee by employeeId
    @DeleteMapping("/delete/{employeeId}")
    public ResponseEntity<Void> deleteEmployee(@PathVariable String employeeId) {
        boolean isDeleted = employeeService.deleteEmployee(employeeId);

        if (!isDeleted) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok().build();
    }

    @PutMapping("/password/{email}")
    public ResponseEntity<String> updatePassword(@PathVariable String email,@RequestBody PasswordChange passwordChange){
        return new ResponseEntity<>(employeeService.updatePassword(email,passwordChange),HttpStatus.CREATED);
    }

    @PostMapping("/forget")
    public ResponseEntity<String> forgetPassword(@RequestBody Employee employee) throws MessagingException, jakarta.mail.MessagingException, UserException {
        // Check if the email is already registered
        Employee email = employeeRepository.findByEmail(employee.getEmail());
        if (email != null) {
            // Generate OTP
            generatedOtp = otpService.generateOtp();

            // Send OTP via email
            emailService.sendOtpEmail(employee.getEmail(), generatedOtp);


            return ResponseEntity.ok("OTP sent successfully.");

        } else {
            return ResponseEntity.badRequest().body("You entered invalid email.");
        }
    }

    @PostMapping("/validating-otp")
    public ResponseEntity<String> validatingOtp(@RequestBody OtpVerificationRequest request) throws UserException {
        if (generatedOtp != null && request.getOtp().equals(generatedOtp)) {


            return new ResponseEntity<String>("otp verified.", HttpStatus.OK);

        } else {
            // OTP verification failed
            return new ResponseEntity<String>("Invalid otp.", HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/confirmpwd/{email}")
    public ResponseEntity<String> confirmPassword(@PathVariable String email, @RequestBody Employee employee) {
        String password = employee.getPassword();
        System.out.println(password);
        String confirmPassword = employee.getConfirmPassword();
        System.out.println(confirmPassword);

        if (!password.equals(confirmPassword)) {
            return new ResponseEntity<>("Passwords do not match", HttpStatus.BAD_REQUEST);
        }
        // Find the existing user by email
        Employee existingEmployee = employeeRepository.findByEmail(email);
        if (existingEmployee == null) {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }
        // Update password
        existingEmployee.setPassword(passwordEncoder.encode(password));
        existingEmployee.setConfirmPassword(passwordEncoder.encode(confirmPassword));
        employeeRepository.save(existingEmployee);

        return new ResponseEntity<>("Password updated successfully", HttpStatus.OK);
    }

}
