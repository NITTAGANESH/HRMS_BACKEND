package com.SPYDTECH.HRMS.entites;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Data
@RequiredArgsConstructor
public class DailyAttendance {

    private String date;
    private List<EmployeeAttendanceDetail> employeeAttendanceDetails;

    public DailyAttendance(String date, List<EmployeeAttendanceDetail> employeeAttendanceDetails) {
        this.date = date;
        this.employeeAttendanceDetails = employeeAttendanceDetails;
    }
}

