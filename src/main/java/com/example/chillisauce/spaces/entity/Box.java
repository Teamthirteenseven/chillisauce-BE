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
    @Column(name = "box_id")
    private Long id;


    public Box(BoxRequestDto boxRequestDto) {
        super(boxRequestDto.getLocationName(), boxRequestDto.getX(), boxRequestDto.getY());

    }

    public void updateBox(BoxRequestDto boxRequestDto) {
        this.setLocationName(boxRequestDto.getLocationName());
        this.setX(boxRequestDto.getX());
        this.setY(boxRequestDto.getY()); //이렇게 사용하면 객체지향프로그래밍 캡슐화원칙에 어긋날수도있다. @AllArgsConstructor , @Data 타입을 사용해 setter를 자동생성
    }

}

