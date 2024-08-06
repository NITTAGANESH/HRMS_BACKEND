package com.SPYDTECH.HRMS.entites;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Data
@Entity
@AllArgsConstructor
@RequiredArgsConstructor
@Table(name ="all_employee")
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;
    private String lastName;

//    @Column(name = "employee_id")
    private String employeeId;

    private String password;

    private String email;

    private String phoneNumber;

    private String joinDate;

    private String confirmPassword;

    private String role;
    @JsonIgnore
    @OneToMany(mappedBy = "orderBy")
    private List<Expense> expenses;


}