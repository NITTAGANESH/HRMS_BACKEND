package com.SPYDTECH.HRMS.service;



import com.SPYDTECH.HRMS.dto.*;
import com.SPYDTECH.HRMS.entites.Attendance;
import com.SPYDTECH.HRMS.entites.Employee;
import com.SPYDTECH.HRMS.entites.HolidaysList;
import com.SPYDTECH.HRMS.entites.User;
import com.SPYDTECH.HRMS.exceptions.ErrorResponse;
import com.SPYDTECH.HRMS.repository.AttendanceRepository;
import com.SPYDTECH.HRMS.repository.EmployeeRepository;
import com.SPYDTECH.HRMS.repository.HolidayRepository;
import com.SPYDTECH.HRMS.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AttendanceServiceImpl implements AttendanceService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AttendanceRepository attendanceRepository;

    @Autowired
    private HolidayRepository holidayRepository;

    @Autowired
            private EmployeeRepository employeeRepository;

    Attendance existingAttendance;

    Attendance attendance;

    String employeeId;

    String emailId;

    long weekendCount;

    LocalDateTime now = LocalDateTime.now();
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    @Override
    public ResponseEntity punchIn(String email){

       // emailId = getUsername();

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

        //emailId = getUsername();

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

    public List<DailyAttendanceDTO> getMonthlyAttendance(int month, int year) {
        List<Attendance> attendances = attendanceRepository.findAllByMonthAndYear(month, year);
        DateTimeFormatter formatterTwo = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        // Group attendances by date
        Map<String, List<Attendance>> groupedByDate = attendances.stream()
                .collect(Collectors.groupingBy(attendance -> attendance.getPunchIn().toLocalDate().format(formatterTwo)));

        List<DailyAttendanceDTO> dailyAttendanceList = new ArrayList<>();

        for (Map.Entry<String, List<Attendance>> entry : groupedByDate.entrySet()) {
            String date = entry.getKey();
            List<Attendance> dailyAttendances = entry.getValue();

            List<EmployeeAttendanceDetailDTO> employeeAttendanceDetails = new ArrayList<>();

            for (Attendance attendance : dailyAttendances) {
                String employeeId = attendance.getEmployeeId();
                Optional<Employee> employeeOptional = employeeRepository.findByEmployeeId(employeeId);
                Employee employee=employeeOptional.get();
                String employeeName = employee != null ? employee.getFirstName() +employee.getLastName(): "Unknown";
                String status = attendance.getAttendanceStatus();

                EmployeeAttendanceDetailDTO detailDTO = new EmployeeAttendanceDetailDTO(employeeId, employeeName, status);
                employeeAttendanceDetails.add(detailDTO);
            }

            DailyAttendanceDTO dailyDTO = new DailyAttendanceDTO(date, employeeAttendanceDetails);
            dailyAttendanceList.add(dailyDTO);
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

        Employee empData ;
        String id;

        if(employeeRepository.existsByEmail(email)){
            empData = employeeRepository.findByEmail(email);
            id = empData.getEmployeeId();
        }
        else{
            id = null;
        }
        return id;
    }


    public String getUsername() {

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username;
        if (principal instanceof UserDetails) {
            username = ((UserDetails) principal).getUsername();
        } else {
            username = " ";
        }
        return username;

    }

    public EmployeeAttendanceDTO getEmployeeAttendanceDetail(String employeeId, int year, int month) {
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();

        List<Attendance> attendances = attendanceRepository.findByEmployeeIdAndPunchInBetween(
                employeeId,
                startDate.atStartOfDay(),
                endDate.atTime(LocalTime.MAX)
        );

        List<HolidaysList> holidays = holidayRepository.findByDayBetween(startDate, endDate);

        // Generate list of all dates in the month
        List<LocalDate> allDates = startDate.datesUntil(endDate.plusDays(1)).collect(Collectors.toList());

        // Create map for easy lookup
        Set<LocalDate> holidayDates = holidays.stream().map(HolidaysList::getDay).collect(Collectors.toSet());
        Set<LocalDate> attendedDates = attendances.stream().map(att -> att.getPunchIn().toLocalDate()).collect(Collectors.toSet());

        EmployeeAttendanceDTO dto = new EmployeeAttendanceDTO();
        dto.setEmployeeId(employeeId);
        Optional<Employee> OptionalEmployee = employeeRepository.findByEmployeeId(employeeId);
        Employee employee=OptionalEmployee.get();
        String employeeName = (employee != null) ? (employee.getFirstName() + " " + employee.getLastName()) : "Unknown";
        dto.setEmployeeName(employeeName);

        List<AttendanceDetail> details = new ArrayList<>();
        long attendedDays = 0;

        for (LocalDate date : allDates) {
            boolean isPresent = attendedDates.contains(date);
            boolean isWeekend = date.getDayOfWeek() == DayOfWeek.SATURDAY || date.getDayOfWeek() == DayOfWeek.SUNDAY;
            boolean isHoliday = holidayDates.contains(date);

            if (isPresent) {
                attendedDays++;
            }

            details.add(new AttendanceDetail(date, isPresent, isWeekend, isHoliday));
        }

        dto.setAttendanceDetails(details);
        dto.setAttendedDays(attendedDays);
        dto.setWeekOffs(countWeekends(year, month));
        dto.setHolidays(countHolidays(year, month));
        dto.setTimeOff(0); // Placeholder - Implement as needed
        dto.setLossOfPay(getTotalWorkingDays(year, month) - attendedDays - dto.getHolidays()); // Use method for total working days
        dto.setLateHours(countLatePunchInMinutes(employeeId, year, month));
        dto.setEarlyHours(countEarlyPunchInMinutes(employeeId, year, month));
        dto.setOvertimeHours(countOvertimeMinutes(employeeId, year, month));

        return dto;
    }

    @Override
    public List<AttendanceReport> getAllEmployeeAttendanceReport (int year, int month){
        List<Employee> employeeList = employeeRepository.findAll();
        List<HolidaysList> holidays = holidayRepository.findByDayBetween(
                YearMonth.of(year, month).atDay(1),
                YearMonth.of(year, month).atEndOfMonth()
        );

        long totalWorkingDays = getTotalWorkingDays(year, month);
        long weekOffs = countWeekends(year, month);

        List<AttendanceReport> reports = new ArrayList<>();

        for (Employee emp : employeeList) {
            String employeeId = emp.getEmployeeId();
            List<Attendance> employeeAttendances = attendanceRepository.findByEmployeeIdAndPunchInBetween(
                    employeeId,
                    YearMonth.of(year, month).atDay(1).atStartOfDay(),
                    YearMonth.of(year, month).atEndOfMonth().atTime(LocalTime.MAX)
            );

            AttendanceReport report = new AttendanceReport();
            report.setEmployee_id(employeeId);
            report.setEmployee_name(emp.getFirstName() + " " + emp.getLastName()); // Assuming User has a getName() method

            report.setAttended(countPresentDays(employeeId, year, month));
            report.setWeek_off(weekOffs);
            report.setHolidays(holidays.size());
            report.setTime_off(0);
            report.setLoss_of_pay(totalWorkingDays - countPresentDays(employeeId, year, month) - holidays.size()); // Placeholder - Implement as needed
            report.setLate_count(countLatePunchInMinutes(employeeId, year, month));
            report.setEarly_count(countEarlyPunchInMinutes(employeeId, year, month));
            report.setOver_time(countOvertimeMinutes(employeeId, year, month));

            reports.add(report);
        }

        return reports;
    }

    private long getTotalWorkingDays(int year, int month) {
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();
        long totalDays = yearMonth.lengthOfMonth();

        long weekendDays = countWeekends(year, month);
        return totalDays - weekendDays;
    }

    public long countWeekends(int year ,int month){
        YearMonth yearMonth = YearMonth.of(year, month);
        int totalDays = yearMonth.lengthOfMonth();
        weekendCount = 0;

        for (int day = 1; day <= totalDays; day++) {
            LocalDate date = LocalDate.of(year, month, day);
            DayOfWeek dayOfWeek = date.getDayOfWeek();
            if (dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY) {
                weekendCount++;
            }
        }

        return weekendCount;
    }

    public long countHolidays(int year, int month) {
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();

        List<HolidaysList> holidays = holidayRepository.findByDayBetween(startDate, endDate);
        return holidays.size();
    }

    public long countPresentDays(String employeeId, int year, int month) {
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDateTime startDateTime = yearMonth.atDay(1).atStartOfDay();
        LocalDateTime endDateTime = yearMonth.atEndOfMonth().atTime(LocalTime.MAX);

        List<Attendance> attendances = attendanceRepository.findByEmployeeIdAndPunchInBetween(employeeId, startDateTime, endDateTime);
        return attendances.stream()
                .map(attendance -> attendance.getPunchIn().toLocalDate())
                .distinct()
                .count();
    }

    public double countEarlyPunchInMinutes(String employeeId, int year, int month) {
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDateTime startDateTime = yearMonth.atDay(1).atStartOfDay();
        LocalDateTime endDateTime = yearMonth.atEndOfMonth().atTime(LocalTime.MAX);

        LocalTime startTime = LocalTime.of(9, 0); // 9 AM

        List<Attendance> attendances = attendanceRepository.findByEmployeeIdAndPunchInBetween(employeeId, startDateTime, endDateTime);

        long totalMinutes = attendances.stream()
                .filter(attendance -> attendance.getPunchIn().toLocalTime().isBefore(startTime))
                .mapToLong(attendance -> {
                    LocalTime punchInTime = attendance.getPunchIn().toLocalTime();
                    Duration duration = Duration.between(punchInTime, startTime);
                    return duration.toMinutes();
                })
                .sum();
        double totalHours = totalMinutes / 60.0;

        return totalHours;
    }

    public double countLatePunchInMinutes(String employeeId, int year, int month) {
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDateTime startDateTime = yearMonth.atDay(1).atStartOfDay();
        LocalDateTime endDateTime = yearMonth.atEndOfMonth().atTime(LocalTime.MAX);

        LocalTime startTime = LocalTime.of(9, 0); // 9 AM

        List<Attendance> attendances = attendanceRepository.findByEmployeeIdAndPunchInBetween(employeeId, startDateTime, endDateTime);

        long totalMinutes = attendances.stream()
                .filter(attendance -> attendance.getPunchIn().toLocalTime().isAfter(startTime))
                .mapToLong(attendance -> {
                    LocalTime punchInTime = attendance.getPunchIn().toLocalTime();
                    Duration duration = Duration.between(startTime, punchInTime);
                    return duration.toMinutes();
                })
                .sum();

        double totalHours = totalMinutes / 60.0;

        return totalHours;
    }

    public double countEarlyPunchOutMinutes(String employeeId, int year, int month) {
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDateTime startDateTime = yearMonth.atDay(1).atStartOfDay();
        LocalDateTime endDateTime = yearMonth.atEndOfMonth().atTime(LocalTime.MAX);

        LocalTime endTime = LocalTime.of(9, 0); // 6 PM

        List<Attendance> attendances = attendanceRepository.findByEmployeeIdAndPunchInBetween(employeeId, startDateTime, endDateTime);

        long totalMinutes = attendances.stream()
                .filter(attendance -> attendance.getPunchOut() != null && attendance.getPunchOut().toLocalTime().isBefore(endTime))
                .mapToLong(attendance -> {
                    LocalTime punchOutTime = attendance.getPunchOut().toLocalTime();
                    Duration duration = Duration.between(punchOutTime, endTime);
                    return duration.toMinutes();
                })
                .sum();

        double totalHours = totalMinutes / 60.0;

        return totalHours;
    }

    public double countOvertimeMinutes(String employeeId, int year, int month) {
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDateTime startDateTime = yearMonth.atDay(1).atStartOfDay();
        LocalDateTime endDateTime = yearMonth.atEndOfMonth().atTime(LocalTime.MAX);

        LocalTime endTime = LocalTime.of(18, 0); // 6 PM

        List<Attendance> attendances = attendanceRepository.findByEmployeeIdAndPunchInBetween(employeeId, startDateTime, endDateTime);

        long totalMinutes = attendances.stream()
                .filter(attendance -> attendance.getPunchOut() != null && attendance.getPunchOut().toLocalTime().isAfter(endTime))
                .mapToLong(attendance -> {
                    LocalTime punchOutTime = attendance.getPunchOut().toLocalTime();
                    Duration duration = Duration.between(endTime, punchOutTime);
                    return duration.toMinutes();
                })
                .sum();

        double totalHours = totalMinutes / 60.0;

        return totalHours;
    }


}
