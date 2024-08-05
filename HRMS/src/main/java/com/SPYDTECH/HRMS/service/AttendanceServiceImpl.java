package com.SPYDTECH.HRMS.service;

import com.SPYDTECH.HRMS.entites.*;
import com.SPYDTECH.HRMS.repository.AttendanceRepository;
import com.SPYDTECH.HRMS.repository.EmployeeRepository;
import com.SPYDTECH.HRMS.exceptions.ErrorResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AttendanceServiceImpl implements AttendanceService {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private AttendanceRepository attendanceRepository;

    Attendance existingAttendance;

    Attendance attendance;

    String employeeId;

    LocalDateTime now = LocalDateTime.now();
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    public AttendanceServiceImpl(EmployeeRepository employeeRepository, AttendanceRepository attendanceRepository) {
        this.employeeRepository = employeeRepository;
        this.attendanceRepository = attendanceRepository;
    }

    @Override
    public ResponseEntity punchIn(String email){

        employeeId = getEmployeeId(email);
        if(employeeId != null){
            existingAttendance = attendanceRepository.findFirstByEmployeeIdOrderByIdDesc(employeeId);
        }
        else{
            ErrorResponse errorResponse = new ErrorResponse("Employee Id Is NULL.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }

        try{
            if (existingAttendance != null && existingAttendance.getPunchOut() == null) {
                throw new IllegalStateException("Employee has already punched in and has not punched out yet.");
            }
            Attendance attendance = new Attendance();
            attendance.setEmployeeId(employeeId);
            attendance.setPunchIn(LocalDateTime.now());
            attendance.setStatus("PUNCHED_IN");
            attendanceRepository.save(attendance);

        }
        catch(Exception e)
        {
            ErrorResponse errorResponse = new ErrorResponse("ERROR: "+e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);

        }
        return ResponseEntity.ok( "Successfully PUNCHED_IN");
    }

    @Override
    public ResponseEntity punchOut(String email){

        employeeId = getEmployeeId(email);

        if(employeeId != null){
            attendance = attendanceRepository.findFirstByEmployeeIdOrderByIdDesc(employeeId);
        }
        else{
            ErrorResponse errorResponse = new ErrorResponse("Employee Id Is NULL.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
        try{
            if (attendance == null || attendance.getPunchOut() != null) {
                throw new IllegalStateException("No active punch-in record found for the employee.");
            }
            LocalDateTime punchOutTime = LocalDateTime.now();
            attendance.setPunchOut(punchOutTime.now());
            long workingHours = Duration.between(attendance.getPunchIn(), punchOutTime).toMinutes();
            attendance.setWorkingMinutes(workingHours);
            attendance.setStatus("PUNCHED_OUT");
            attendance.setAttendanceStatus("PRESENT");
            attendanceRepository.save(attendance);
        }
        catch(Exception e)
        {
            ErrorResponse errorResponse = new ErrorResponse("ERROR: "+e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
        return ResponseEntity.ok( "Successfully PUNCHED_OUT");
    }

    @Override
    public List<DailyAttendance> getMonthlyAttendance(int month, int year) {
        List<Attendance> attendances = attendanceRepository.findAllByMonthAndYear(month, year);
        DateTimeFormatter formatterTwo = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        // Group attendances by date
        Map<String, List<Attendance>> groupedByDate = attendances.stream()
                .collect(Collectors.groupingBy(attendance -> attendance.getPunchIn().toLocalDate().format(formatterTwo)));

        List<DailyAttendance> dailyAttendanceList = new ArrayList<>();

        for (Map.Entry<String, List<Attendance>> entry : groupedByDate.entrySet()) {
            String date = entry.getKey();
            List<Attendance> dailyAttendances = entry.getValue();

            List<EmployeeAttendanceDetail> employeeAttendanceDetails = new ArrayList<>();

            for (Attendance attendance : dailyAttendances) {
                String employeeId = attendance.getEmployeeId();
                Optional<Employee> employeeOptional = employeeRepository.findByEmployeeId(employeeId);
                Employee employee = employeeOptional.get();
                String employeeName = employee != null ? employee.getFirstName() +employee.getLastName() : "Unknown";
                String status = attendance.getAttendanceStatus();

                EmployeeAttendanceDetail details= new EmployeeAttendanceDetail(employeeId, employeeName, status);
                employeeAttendanceDetails.add(details);
            }

            DailyAttendance daily = new DailyAttendance(date, employeeAttendanceDetails);
            dailyAttendanceList.add(daily);
        }

        return dailyAttendanceList;
    }


    @Scheduled(cron = "0 0 0 * * ?")
    public void markAbsentEmployees() {
        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(1);

        List<String> allEmployeeIds = getAllEmployeeIds();
        List<String> presentEmployeeIds = attendanceRepository.findEmployeeIdsWithRecordsForDate(yesterday);

        allEmployeeIds.removeAll(presentEmployeeIds);

        for (String absentEmployeeId : allEmployeeIds) {
            // Handle marking the employee as absent
            Attendance absentAttendance = new Attendance();
            absentAttendance.setEmployeeId(absentEmployeeId);
            absentAttendance.setStatus("ABSENT");
            absentAttendance.setAttendanceStatus("ABSENT");
            absentAttendance.setPunchIn(LocalDateTime.of(yesterday, LocalTime.MIN));
            absentAttendance.setPunchOut(LocalDateTime.of(yesterday, LocalTime.MIN));
            absentAttendance.setWorkingMinutes(0L);
            attendanceRepository.save(absentAttendance);
        }
    }

    @Scheduled(cron = "0 59 23 * * ?") // Runs every day at 11:59 PM
    public void automaticPunchOut() {
        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.atTime(LocalTime.of(23, 59));

        List<Attendance> activeAttendances = attendanceRepository.findActiveAttendancesForToday(startOfDay, endOfDay);
        for (Attendance attendance : activeAttendances) {
            if (attendance.getPunchOut() == null) {
                attendance.setPunchOut(endOfDay);
                attendance.setWorkingMinutes(Duration.between(attendance.getPunchIn(), endOfDay).toMinutes());
                attendance.setStatus("AUTOMATIC_PUNCH_OUT");
                attendance.setAttendanceStatus("PRESENT");
                attendanceRepository.save(attendance);
            }
        }
    }

    private List<String> getAllEmployeeIds() {

        List<Employee> allEmployees = employeeRepository.findAll();

        List<String> allEmployeeIds = allEmployees.stream().map(Employee:: getEmployeeId).collect(Collectors.toList());
        return allEmployeeIds;
    }

    public String getEmployeeId (String email){

        Employee employeeData ;
        String id;

        if(employeeRepository.existsByEmail(email)){
            employeeData = employeeRepository.findByEmail(email);
            id = employeeData.getEmployeeId();
        }
        else{
            id = null;
        }
        return id;
    }
}
