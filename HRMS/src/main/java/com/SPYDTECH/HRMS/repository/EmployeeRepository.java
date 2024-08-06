package com.SPYDTECH.HRMS.repository;

import com.SPYDTECH.HRMS.entites.Employee;
import com.SPYDTECH.HRMS.entites.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee,Long> {

    boolean existsByEmail(String email);
    Employee findByEmail(String email);
    Optional<Employee> findByEmployeeId(String employeeId);


    User findUserByEmail(String email);
}
