package com.example.chillisauce.spaces.entity;

import com.example.chillisauce.spaces.dto.BoxRequestDto;
import com.example.chillisauce.users.entity.User;
import lombok.*;


import javax.persistence.*;

@Entity
@NoArgsConstructor
@Getter
@Setter
public class Box extends Location {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;



    public Box(BoxRequestDto boxRequestDto) {
        super(boxRequestDto.getBoxName(), boxRequestDto.getX(), boxRequestDto.getY());
    }

    @Builder
    public Box(String locationName, String x, String y) {
        super(locationName, x, y);
    }




    public void updateBox(BoxRequestDto boxRequestDto) {
        this.setLocationName(boxRequestDto.getBoxName());
        this.setX(boxRequestDto.getX());
        this.setY(boxRequestDto.getY());


    }


}

