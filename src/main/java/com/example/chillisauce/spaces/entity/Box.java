package com.example.chillisauce.spaces.entity;

import com.example.chillisauce.spaces.dto.BoxRequestDto;
import com.example.chillisauce.users.entity.User;
import lombok.*;


import javax.persistence.*;

@Entity
@NoArgsConstructor
@Getter
@Setter
@Builder
@AllArgsConstructor
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

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    public User user;

    public Box(BoxRequestDto boxRequestDto) {
        this.boxName = boxRequestDto.getBoxName();
        this.x = boxRequestDto.getX();
        this.y = boxRequestDto.getY();
        this.username = getUsername();
    }

    public void updateBox(BoxRequestDto boxRequestDto, User user) {
        this.boxName = boxRequestDto.getBoxName();
        this.x = boxRequestDto.getX();
        this.y = boxRequestDto.getY();
        this.user = user;
        this.username = user.getUsername();

    }

    public void linkSpace(Space space) {
        this.space = space;
        space.getBoxes().add(this);
    }


    public void updateBox(String boxName, String x, String y, User user) {
        this.boxName = boxName;
        this.x = x;
        this.y = y;
        this.user = user;
        this.username = user.getUsername();
    }
}
