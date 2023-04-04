package com.example.chillisauce.spaces.entity;

import com.example.chillisauce.reservations.entity.Reservation;
import com.example.chillisauce.spaces.dto.MrRequestDto;
import com.example.chillisauce.users.entity.User;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;


import javax.persistence.*;
import java.util.List;

@Getter
@Entity
@RequiredArgsConstructor
public class Mr {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String mrName;

    private String x;

    private String y;

    private String username;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "space_id")
    private Space space;

    @OneToMany(mappedBy = "meetingRoom")
    List<Reservation> reservations;
    public Mr(MrRequestDto mrRequestDto, User user) {
        this.mrName = mrRequestDto.getMrName();
        this.x = mrRequestDto.getX();
        this.y = mrRequestDto.getY();
        this.username = user.getUsername();
    }


    public void updateMr(MrRequestDto mrRequestDto) {
        this.mrName = mrRequestDto.getMrName();
        this.x = mrRequestDto.getX();
        this.y = mrRequestDto.getY();
    }
    public void linkSpace(Space space) {
        this.space = space;
        space.getMrs().add(this);
    }
}
