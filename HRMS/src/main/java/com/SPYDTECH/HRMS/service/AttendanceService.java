package com.SPYDTECH.HRMS.service;


import com.SPYDTECH.HRMS.dto.AttendanceReport;
import com.SPYDTECH.HRMS.dto.DailyAttendanceDTO;
import com.SPYDTECH.HRMS.dto.EmployeeAttendanceDTO;
import com.SPYDTECH.HRMS.entites.Attendance;
import com.SPYDTECH.HRMS.repository.AttendanceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AttendanceService {

    @Autowired
    private AttendanceRepository attendanceRepository;

    public List<Attendance> getAllAttendance() {
        return attendanceRepository.findAll();
    }

    public Attendance getAttendanceById(Long id) {
        return attendanceRepository.findById(id).orElse(null);
    }

    public Attendance saveAttendance(Attendance attendance) {
        return attendanceRepository.save(attendance);
    }

    public void deleteAttendance(Long id) {
        attendanceRepository.deleteById(id);
    }
}
