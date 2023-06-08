package com.example.chillisauce.domain.schedules.entity;

import com.example.chillisauce.domain.reservations.entity.Reservation;
import com.example.chillisauce.domain.schedules.dto.ScheduleRequestDto;
import com.example.chillisauce.domain.users.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Schedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    User user;

    @Column(nullable = false)
    String title;

    @Column(nullable = false)
    LocalDateTime startTime;

    @Column(nullable = false)
    LocalDateTime endTime;

    @Column(nullable = false)
    String comment;

    public void update(ScheduleRequestDto requestDto, LocalDateTime startTime, LocalDateTime endTime) {
        this.title= requestDto.getScTitle();
        this.comment= requestDto.getScComment();
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public Schedule(Reservation reservation, User user){
        this.title="회의";
        this.comment=reservation.getMeetingRoom().getLocationName();
        this.user=user;
        this.startTime=reservation.getStartTime();
        this.endTime=reservation.getEndTime();
    }
}
