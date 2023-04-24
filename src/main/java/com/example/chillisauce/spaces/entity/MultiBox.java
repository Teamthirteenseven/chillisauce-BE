package com.example.chillisauce.spaces.entity;

import com.example.chillisauce.security.UserDetailsImpl;
import com.example.chillisauce.spaces.dto.BoxRequestDto;
import com.example.chillisauce.spaces.dto.MultiBoxRequestDto;
import com.example.chillisauce.users.entity.User;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
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

    @Builder
    public MultiBox(String locationName, String x, String y) {
        super(locationName, x, y);
    }


    public void updateMultiBox(MultiBoxRequestDto multiBoxRequestDto) {
        this.setLocationName(multiBoxRequestDto.getMultiBoxName());
        this.setX(multiBoxRequestDto.getX());
        this.setY(multiBoxRequestDto.getY());

    }


}
