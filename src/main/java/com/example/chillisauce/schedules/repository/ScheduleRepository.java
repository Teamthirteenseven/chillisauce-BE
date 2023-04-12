package com.example.chillisauce.schedules.repository;

import com.example.chillisauce.reservations.entity.Reservation;
import com.example.chillisauce.schedules.entity.Schedules;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface ScheduleRepository extends JpaRepository<Schedules, Long> {

    @Query("select s from Schedules s where s.startTime >= :startTime and s.startTime <= :endTime")
    List<Schedules> findAllByStartTime(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

    @Query("select s from Schedules s where s.id != :scId " +
            "and s.startTime < :endTime and s.endTime > :startTime")
    List<Schedules> findAllByIdNotAndStartTimeLessThanAndEndTimeGreaterThan(
            @Param("scId") Long scId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime);
}
