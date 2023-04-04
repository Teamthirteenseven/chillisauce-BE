package com.example.chillisauce.spaces.entity;

import com.example.chillisauce.spaces.dto.BoxRequestDto;
import com.example.chillisauce.users.entity.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


import javax.persistence.*;

@Entity
@NoArgsConstructor
@Getter
public class Box {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String boxName;

    private String x;

    private String y;

    private String username;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "space_id")
    private Space space;


    public Box(BoxRequestDto boxRequestDto, User user) {
        this.boxName = boxRequestDto.getBoxName();
        this.x = boxRequestDto.getX();
        this.y = boxRequestDto.getY();
        this.username = user.getUsername();
    }

    public void updateBox(BoxRequestDto boxRequestDto) {
        this.boxName = boxRequestDto.getBoxName();
        this.x = boxRequestDto.getX();
        this.y = boxRequestDto.getY();

    }
    public void linkSpace(Space space) {
        this.space = space;
        space.getBoxes().add(this);
    }

}
