package com.SPYDTECH.HRMS.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class EmployeeAttendanceDetailDTO {

    private String employeeId;
    private String employeeName;
    private String attendanceStatus;

    public EmployeeAttendanceDetailDTO(String employeeId, String employeeName, String attendanceStatus) {
        this.employeeId = employeeId;
        this.employeeName = employeeName;
        this.attendanceStatus = attendanceStatus;
    }
}
