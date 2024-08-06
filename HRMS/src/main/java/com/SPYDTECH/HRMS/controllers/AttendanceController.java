package com.SPYDTECH.HRMS.controllers;


import com.SPYDTECH.HRMS.dto.AttendanceReport;
import com.SPYDTECH.HRMS.dto.DailyAttendanceDTO;
import com.SPYDTECH.HRMS.dto.EmployeeAttendanceDTO;
import com.SPYDTECH.HRMS.service.AttendanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class AttendanceController {

    @Autowired
    private AttendanceService attendanceService;

    @PostMapping("/punch-in")
    public ResponseEntity punchIn(@RequestParam String email) {
        return attendanceService.punchIn(email);
    }

    @PostMapping("/punch-out")
    public ResponseEntity punchOut(@RequestParam String email) {

        return attendanceService.punchOut(email);

    }

    @GetMapping("/create/monthly-attendance")
    public List<DailyAttendanceDTO> getMonthlyAttendance(
            @RequestParam int month,
            @RequestParam int year) {
        return attendanceService.getMonthlyAttendance(month, year);
    }

    @GetMapping("/employeeAttendanceReport")
    public List<AttendanceReport> getEmployeeAttendanceReport(@RequestParam int year, @RequestParam int month) {
        return attendanceService.getAllEmployeeAttendanceReport(year, month);
    }

    @GetMapping("/employee/{employeeId}")
    public EmployeeAttendanceDTO getEmployeeAttendanceDetail(
            @PathVariable String employeeId,
            @RequestParam int year,
            @RequestParam int month) {
        return attendanceService.getEmployeeAttendanceDetail(employeeId, year, month);
    }
}
