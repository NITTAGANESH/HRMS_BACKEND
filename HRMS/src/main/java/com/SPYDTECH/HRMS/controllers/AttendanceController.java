package com.SPYDTECH.HRMS.controllers;


import com.SPYDTECH.HRMS.dto.AttendanceReport;
import com.SPYDTECH.HRMS.dto.DailyAttendanceDTO;
import com.SPYDTECH.HRMS.dto.EmployeeAttendanceDTO;
import com.SPYDTECH.HRMS.entites.Attendance;
import com.SPYDTECH.HRMS.repository.AttendanceRepository;
import com.SPYDTECH.HRMS.service.AttendanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/attendance")
public class AttendanceController {

    @Autowired
    private AttendanceRepository attendanceRepository;

    @PostMapping
    public Attendance saveAttendance(@RequestBody Attendance attendance) {
        return attendanceRepository.save(attendance);
    }

    @GetMapping
    public List<Attendance> getAllAttendance() {
        return attendanceRepository.findAll();
    }
}
