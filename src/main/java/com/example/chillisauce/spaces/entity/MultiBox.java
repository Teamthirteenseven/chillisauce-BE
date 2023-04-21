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
    @Column(name = "multiBox_id")
    private Long id;

    public MultiBox(MultiBoxRequestDto multiBoxRequestDto) {
        super(multiBoxRequestDto.getLocationName(), multiBoxRequestDto.getX(), multiBoxRequestDto.getY());
    }

    public void updateMultiBox(MultiBoxRequestDto multiBoxRequestDto) {
        this.setLocationName(multiBoxRequestDto.getLocationName());
        this.setX(multiBoxRequestDto.getX());
        this.setY(multiBoxRequestDto.getY());

    }


}
