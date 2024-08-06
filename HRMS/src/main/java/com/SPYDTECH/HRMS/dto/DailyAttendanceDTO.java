package com.SPYDTECH.HRMS.dto;


import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Data
@RequiredArgsConstructor
public class DailyAttendanceDTO {

    private String date;
    private List<EmployeeAttendanceDetailDTO> employeeAttendanceDetails;

    public DailyAttendanceDTO(String date, List<EmployeeAttendanceDetailDTO> employeeAttendanceDetails) {
        this.date = date;
        this.employeeAttendanceDetails = employeeAttendanceDetails;
    }
}
