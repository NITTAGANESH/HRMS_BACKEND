package com.SPYDTECH.HRMS.service;

import com.SPYDTECH.HRMS.entites.DailyAttendance;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface AttendanceService {

    ResponseEntity punchIn(String email);

    ResponseEntity punchOut(String email);

    List<DailyAttendance> getMonthlyAttendance(int month, int year);
}