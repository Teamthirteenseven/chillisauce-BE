package com.example.chillisauce.spaces.entity;

import com.example.chillisauce.reservations.entity.Reservation;
import com.example.chillisauce.spaces.dto.MrRequestDto;
import lombok.*;


import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@Getter
@Entity
@RequiredArgsConstructor
public class Mr extends Location{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mr_id")
    private Long id;

    @OneToMany(mappedBy = "meetingRoom")
    List<Reservation> reservations;


    public Mr(MrRequestDto mrRequestDto) {
        super(mrRequestDto.getLocationName(), mrRequestDto.getX(), mrRequestDto.getY());
    }

    public void updateMr(MrRequestDto mrRequestDto) {
        this.setLocationName(mrRequestDto.getLocationName());
        this.setX(mrRequestDto.getX());
        this.setY(mrRequestDto.getY());
    }

}
