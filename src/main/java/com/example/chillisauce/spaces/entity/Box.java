package com.example.chillisauce.spaces.entity;

import com.example.chillisauce.spaces.dto.request.BoxRequestDto;
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

    public Box(BoxRequestDto boxRequestDto, Space space) {
        super(boxRequestDto.getBoxName(), boxRequestDto.getX(), boxRequestDto.getY(), space);
    }

    public Box(String locationName, String x, String y) {
        super(locationName, x, y);
    }
    @Builder
    public Box(Long id, String locationName, String x, String y) {
        super(id,locationName, x, y);
    }

    public Box(String boxName, String x, String y, Space space) {
        this.setLocationName(boxName);
        this.setX(x);
        this.setY(y);
        this.setSpace(space);
    }


    public void updateBox(BoxRequestDto boxRequestDto) {
        this.setLocationName(boxRequestDto.getBoxName());
        this.setX(boxRequestDto.getX());
        this.setY(boxRequestDto.getY());
    }

    @Override
    public boolean isBox() {
        return true;
    }

    @Override
    public boolean isMultiBox() {
        return false;
    }

    @Override
    public boolean isMr() {
        return false;
    }
}

