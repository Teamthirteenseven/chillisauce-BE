package com.example.chillisauce.reservations.entity;

import com.example.chillisauce.spaces.entity.Mr;
import com.example.chillisauce.users.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(nullable = false)
    LocalDateTime startTime;

    @Column(nullable = false)
    LocalDateTime endTime;

    @ManyToOne(fetch = FetchType.LAZY)
    User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="meeting_room_id")
    Mr meetingRoom;

    @OneToMany(mappedBy = "reservation")
    List<ReservationUser> reservationUser;

    public void update(LocalDateTime startTime, LocalDateTime endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public void update(Mr meetingRoom){
        this.meetingRoom=meetingRoom;
    }
}
