package com.SPYDTECH.HRMS.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
@Data
@RequiredArgsConstructor
public class AttendanceDetail {

    private LocalDate date;
    private boolean isPresent;
    private boolean isWeekend;
    private boolean isHoliday;

    public AttendanceDetail(LocalDate date, boolean isPresent, boolean isWeekend, boolean isHoliday) {
        this.date = date;
        this.isPresent = isPresent;
        this.isWeekend = isWeekend;
        this.isHoliday = isHoliday;
    }
}
