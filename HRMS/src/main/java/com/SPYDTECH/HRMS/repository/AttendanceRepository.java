package com.SPYDTECH.HRMS.repository;

import com.SPYDTECH.HRMS.entites.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance,Long> {

    Attendance findFirstByEmployeeIdOrderByIdDesc(String employeeId);


    @Query(value = "SELECT DISTINCT employee_id FROM attendance WHERE DATE(punch_in) = :date", nativeQuery = true)
    List<String> findEmployeeIdsWithRecordsForDate(@Param("date") LocalDate date);

    @Query("SELECT a FROM Attendance a WHERE a.punchIn >= :startOfDay AND a.punchIn < :endOfDay AND a.punchOut IS NULL")
    List<Attendance> findActiveAttendancesForToday(@Param("startOfDay") LocalDateTime startOfDay, @Param("endOfDay") LocalDateTime endOfDay);

    @Query("SELECT a FROM Attendance a WHERE MONTH(a.punchIn) = :month AND YEAR(a.punchIn) = :year")
    List<Attendance> findAllByMonthAndYear(@Param("month") int month, @Param("year") int year);
}