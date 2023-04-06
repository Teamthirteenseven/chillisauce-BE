package com.example.chillisauce.reservations.entity;

import com.example.chillisauce.reservations.dto.ReservationRequestDto;
import com.example.chillisauce.spaces.entity.Mr;
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
@NoArgsConstructor
@AllArgsConstructor
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    LocalDateTime startTime;
    LocalDateTime endTime;
    @ManyToOne(fetch = FetchType.LAZY)
    User user;

    @ManyToOne(fetch = FetchType.LAZY)
    Mr meetingRoom;

    public Reservation(ReservationRequestDto requestDto, User user, Mr meetingRoom) {
        this.startTime = requestDto.getStart();
        this.endTime = requestDto.getEnd();
        this.user = user;
        this.meetingRoom = meetingRoom;
    }

    public void update(ReservationRequestDto requestDto) {
        this.startTime = requestDto.getStart();
        this.endTime = requestDto.getEnd();
    }
}
