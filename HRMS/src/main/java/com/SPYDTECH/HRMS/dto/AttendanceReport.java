package com.SPYDTECH.HRMS.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class AttendanceReport {

    private String employee_name;
    private String employee_id;
    private long attended;
    private long week_off;
    private long holidays;
    private double time_off;
    private double over_time;
    private long loss_of_pay;
    private double late_count;
    private double early_count;


}
