package com.example.chillisauce.domain.reservations.entity;

import com.example.chillisauce.domain.users.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class ReservationUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    // 회의 참석자
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    User attendee;

    // 회의
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id")
    Reservation reservation;

    public ReservationUser(User attendee, Reservation reservation) {
        this.attendee = attendee;
        this.reservation = reservation;
    }
}
