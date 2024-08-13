package com.SPYDTECH.HRMS.service;

import com.SPYDTECH.HRMS.entites.Employee;
import com.SPYDTECH.HRMS.entites.HolidaysList;
import com.SPYDTECH.HRMS.repository.HolidayRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class HolidayServiceImpl implements HolidayService {

    @Autowired
    private HolidayRepository holidayRepository;

    public HolidayServiceImpl(HolidayRepository holidayRepository){
        this.holidayRepository = holidayRepository;
    }

    public boolean deleteHoliday(String holidayName) {
        Optional<HolidaysList> holidaysListOptional = holidayRepository.findByHolidayName(holidayName);

        if (!holidaysListOptional.isPresent()) {
            return false;
        }

        holidayRepository.delete(holidaysListOptional.get());
        return true;
    }

    public List<HolidaysList> getAllHolidays() {
        return holidayRepository.findAll();
    }


    public HolidaysList save(HolidaysList holiday) {
        return holidayRepository.save(holiday);
    }

    public HolidaysList updateHoliday(String holidayName, HolidaysList holidaysListDetails) {
        Optional<HolidaysList> HolidaysListOptional = holidayRepository.findByHolidayName(holidayName);

        if (!HolidaysListOptional.isPresent()) {
            return null;
        }

        HolidaysList holidaysList = HolidaysListOptional.get();
        holidaysList.setDate(holidaysListDetails.getDate());
        holidaysList.setDay(holidaysListDetails.getDay());
        holidaysList.setHolidayName(holidaysListDetails.getHolidayName());

        return holidayRepository.save(holidaysList);
    }

}