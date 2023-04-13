package com.example.chillisauce.schedules.entity;

import com.example.chillisauce.schedules.dto.ScheduleRequestDto;
import com.example.chillisauce.users.entity.User;
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

    public Schedule(ScheduleRequestDto requestDto, User user) {
        this.user = user;
        this.title = requestDto.getScTitle();
        this.startTime = requestDto.getScStart();
        this.endTime = requestDto.getScEnd();
        this.comment = requestDto.getScComment();
    }

    public void update(ScheduleRequestDto requestDto) {
        this.title= requestDto.getScTitle();
        this.startTime = requestDto.getScStart();
        this.endTime = requestDto.getScEnd();
        this.comment= requestDto.getScComment();
    }
}
