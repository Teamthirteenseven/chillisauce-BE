package com.example.chillisauce.spaces.entity;

import com.example.chillisauce.spaces.dto.BoxRequestDto;
import lombok.*;


import javax.persistence.*;

@Entity
@NoArgsConstructor
@Getter
public class Box extends Location {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    public Box(BoxRequestDto boxRequestDto) {
        super(boxRequestDto.getLocationName(), boxRequestDto.getX(), boxRequestDto.getY());
    }

    @Builder
    public Box(String locationName, String x, String y) {
        super(locationName, x, y);
    }


    public void updateBox(BoxRequestDto boxRequestDto) {
        this.setLocationName(boxRequestDto.getLocationName());
        this.setX(boxRequestDto.getX());
        this.setY(boxRequestDto.getY());
    }

}

