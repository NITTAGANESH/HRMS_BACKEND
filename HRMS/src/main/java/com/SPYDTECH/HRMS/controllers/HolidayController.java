package com.SPYDTECH.HRMS.controllers;

import com.SPYDTECH.HRMS.entites.HolidaysList;
import com.SPYDTECH.HRMS.service.HolidayService;
import com.SPYDTECH.HRMS.service.HolidayServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/holidays")
public class HolidayController {

    @Autowired
    private HolidayService holidayService;

    @GetMapping
    public List<HolidaysList> getAllHolidays() {
        return holidayService.getAllHolidays();
    }


    @PostMapping
    public ResponseEntity<HolidaysList> createHoliday(@RequestBody HolidaysList holiday) {
        HolidaysList savedHoliday = holidayService.save(holiday);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedHoliday);
    }

    @PutMapping("/{holidayName}")
    public ResponseEntity<HolidaysList> updateHoliday(@PathVariable String holidayName, @RequestBody HolidaysList holiday) {
        HolidaysList updatedHoliday = holidayService.updateHoliday(holidayName, holiday);
        return ResponseEntity.ok(updatedHoliday);
    }

    @DeleteMapping("/delete/{holidayName}")
    public ResponseEntity<Void> deleteHoliday(@PathVariable String holidayName) {
        boolean isDeleted = holidayService.deleteHoliday(holidayName);

        if (!isDeleted) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok().build();
    }

}