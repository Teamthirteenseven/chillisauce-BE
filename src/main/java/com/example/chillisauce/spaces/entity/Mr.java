package com.example.chillisauce.spaces.entity;

import com.example.chillisauce.reservations.entity.Reservation;
import com.example.chillisauce.spaces.dto.MrRequestDto;
import com.example.chillisauce.users.entity.Companies;
import lombok.*;


import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@RequiredArgsConstructor
@AllArgsConstructor
public class Mr extends Location{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "meetingRoom")
    List<Reservation> reservations = new ArrayList<>();


    public Mr(MrRequestDto mrRequestDto) {
        super(mrRequestDto.getMrName(), mrRequestDto.getX(), mrRequestDto.getY());
    }


    @Builder
    public Mr(Long id, String locationName, String x, String y, List<Reservation> reservation) {
        super(locationName, x, y);
        this.id= id;
        this.reservations = reservation;
    }



    public void updateMr(MrRequestDto mrRequestDto) {
        this.setLocationName(mrRequestDto.getMrName());
        this.setX(mrRequestDto.getX());
        this.setY(mrRequestDto.getY());
    }

}
