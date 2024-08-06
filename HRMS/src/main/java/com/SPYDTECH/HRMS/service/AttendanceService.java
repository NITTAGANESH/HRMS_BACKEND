package com.SPYDTECH.HRMS.service;


import com.SPYDTECH.HRMS.dto.AttendanceReport;
import com.SPYDTECH.HRMS.dto.DailyAttendanceDTO;
import com.SPYDTECH.HRMS.dto.EmployeeAttendanceDTO;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface AttendanceService {

    ResponseEntity punchIn(String email);

    ResponseEntity punchOut(String email);

    List<DailyAttendanceDTO> getMonthlyAttendance(int month, int year);

    List<AttendanceReport> getAllEmployeeAttendanceReport(int year, int month);

    EmployeeAttendanceDTO getEmployeeAttendanceDetail(String employeeId, int year, int month);
}
