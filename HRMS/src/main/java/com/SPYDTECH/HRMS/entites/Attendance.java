package com.SPYDTECH.HRMS.entites;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.Duration;
import java.time.LocalDateTime;

@Data
@Entity
@AllArgsConstructor
@RequiredArgsConstructor
@Table(name ="attendance")
public class Attendance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String employeeId;
    private String employeeName;

    private LocalDateTime punchIn;

    private LocalDateTime punchOut;

    private int productionHours;
    private int productionMinutes;
    private int productionSeconds;
    private int breakHours;
    private int breakMinutes;
    private int breakSeconds;
    private int overtime;

}
