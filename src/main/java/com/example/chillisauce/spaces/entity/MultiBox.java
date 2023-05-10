package com.example.chillisauce.spaces.entity;

import com.example.chillisauce.spaces.dto.request.MultiBoxRequestDto;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@RequiredArgsConstructor
public class MultiBox extends Location {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    public MultiBox(MultiBoxRequestDto multiBoxRequestDto) {
        super(multiBoxRequestDto.getMultiBoxName(), multiBoxRequestDto.getX(), multiBoxRequestDto.getY());
    }


    public MultiBox(String locationName, String x, String y) {
        super(locationName, x, y);
    }

    @Builder
    public MultiBox(Long id,String locationName, String x, String y) {
        super(id, locationName, x, y);
    }

    public MultiBox(String multiBoxName, String x, String y, Space space) {
        this.setLocationName(multiBoxName);
        this.setX(x);
        this.setY(y);
        this.setSpace(space);
    }


    public void updateMultiBox(MultiBoxRequestDto multiBoxRequestDto) {
        this.setLocationName(multiBoxRequestDto.getMultiBoxName());
        this.setX(multiBoxRequestDto.getX());
        this.setY(multiBoxRequestDto.getY());
    }

    @Override
    public boolean isBox() {
        return false;
    }

    @Override
    public boolean isMultiBox() {
        return true;
    }

    @Override
    public boolean isMr() {
        return false;
    }
}
