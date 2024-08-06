package com.SPYDTECH.HRMS.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;
@Data
@RequiredArgsConstructor
public class EmployeeAttendanceDTO {
    private String employeeId;
    private String employeeName;
    private List<AttendanceDetail> attendanceDetails; // Contains details for each day
    private long attendedDays;
    private long weekOffs;
    private long holidays;
    private long timeOff;
    private double lossOfPay; // Assuming this is in hours
    private double lateHours; // Changed from long to double
    private double earlyHours; // Changed from long to double
    private double overtimeHours; // Changed from long to double
}
