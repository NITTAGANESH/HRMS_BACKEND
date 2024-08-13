package com.SPYDTECH.HRMS.repository;

import com.SPYDTECH.HRMS.entites.HolidaysList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface HolidayRepository extends JpaRepository<HolidaysList,Long> {
    Optional<HolidaysList> findByHolidayName(String holidayName);
}
