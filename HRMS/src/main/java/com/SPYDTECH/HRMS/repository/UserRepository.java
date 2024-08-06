package com.SPYDTECH.HRMS.repository;

import com.SPYDTECH.HRMS.entites.User;
import org.springframework.data.jpa.repository.JpaRepository;


public interface UserRepository extends JpaRepository<User,Long> {

    User findByEmail(String email);
    boolean existsByEmail(String email);
    User findUserByEmail(String email);
//    User findByEmployeeid(String employeeid);


}