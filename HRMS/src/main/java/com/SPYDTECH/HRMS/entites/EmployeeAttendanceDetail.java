package com.SPYDTECH.HRMS.entites;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class EmployeeAttendanceDetail {

    private String employeeId;
    private String employeeName;
    private String attendanceStatus;

    public EmployeeAttendanceDetail(String employeeId, String employeeName, String attendanceStatus) {
        this.employeeId = employeeId;
        this.employeeName = employeeName;
        this.attendanceStatus = attendanceStatus;
    }
}