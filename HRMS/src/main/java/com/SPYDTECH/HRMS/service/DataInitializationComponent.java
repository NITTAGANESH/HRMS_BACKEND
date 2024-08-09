package com.SPYDTECH.HRMS.service;

import com.SPYDTECH.HRMS.entites.Employee;
import com.SPYDTECH.HRMS.entites.User;
import com.SPYDTECH.HRMS.repository.EmployeeRepository;
import com.SPYDTECH.HRMS.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.dao.DataAccessException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializationComponent implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializationComponent.class);

    private final UserRepository userRepository;
    @Autowired
     EmployeeRepository employeeRepository;

    //    private final CartService cartService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public DataInitializationComponent(UserRepository userRepository,
                                       PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;

    }

    @Override
    public void run(String... args) {
        initializeAdminUser();
    }

    private void initializeAdminUser() {
        String adminUsername = "mounika@spydtech.com";

        try {
            if (userRepository.findByEmail(adminUsername) == null) {
                User adminUser= new User();
                adminUser.setFirstName("Spyd");
                adminUser.setLastName("tech");
                adminUser.setEmail(adminUsername);
                adminUser.setPassword(passwordEncoder.encode("Spyd@1234"));
                adminUser.setRole("ADMIN");

                User  admin = userRepository.save(adminUser);

//                cartService.createCart(admin);

                logger.info("Admin user initialized successfully.");
            } else {
                logger.warn("Admin user already exists. Skipping initialization.");
            }
        } catch (DataAccessException e) {
            logger.error("Data access error while initializing admin user: " + e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Error initializing admin user: " + e.getMessage(), e);
        }
    }
}
