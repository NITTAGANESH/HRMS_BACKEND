package com.SPYDTECH.HRMS.service;

import com.SPYDTECH.HRMS.entites.Employee;
import com.SPYDTECH.HRMS.entites.HolidaysList;

import java.util.List;

public interface HolidayService {
    boolean deleteHoliday(String name);
    public List<HolidaysList> getAllHolidays();
    HolidaysList save(HolidaysList holidaysList);
    HolidaysList updateHoliday(String name, HolidaysList holidaysListDetails);
}
