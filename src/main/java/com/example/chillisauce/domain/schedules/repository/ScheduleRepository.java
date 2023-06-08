package com.example.chillisauce.domain.schedules.repository;

import com.example.chillisauce.domain.schedules.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    List<Schedule> findAllByUserId(Long userId);

    @Query("select s from Schedule s " +
            "where s.user.id = :userId and s.startTime between :startTime and :endTime")
    List<Schedule> findAllByUserIdAndStartTimeBetween(@Param("userId") Long userId,
                                               @Param("startTime") LocalDateTime startTime,
                                               @Param("endTime") LocalDateTime endTime);

    @Query("select s from Schedule s " +
            "where s.user.id= :userId and s.startTime < :endTime and s.endTime > :startTime")
    List<Schedule> findFirstByUserIdAndStartTimeLessThanAndEndTimeGreaterThan(
            @Param("userId") Long userId,
            @Param("startTime") LocalDateTime start,
            @Param("endTime") LocalDateTime end);

    @Query("select s from Schedule s " +
            "where s.user.id = :userId and s.id != :scId " +
            "and s.startTime < :endTime and s.endTime > :startTime")
    List<Schedule> findAllByUserIdAndIdNotAndStartTimeLessThanAndEndTimeGreaterThan(
            @Param("userId") Long userId,
            @Param("scId") Long scId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime);
}
