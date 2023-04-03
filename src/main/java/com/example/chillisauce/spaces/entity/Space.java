package com.example.chillisauce.spaces.entity;

import com.example.chillisauce.security.UserDetailsImpl;
import com.example.chillisauce.spaces.dto.SpaceRequestDto;
import lombok.Getter;

import lombok.RequiredArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@RequiredArgsConstructor
public class Space {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String spaceName;

    private UserDetailsImpl userDetails;


    @OneToMany(orphanRemoval = true)
    @JoinColumn(name = "space_id")
    private List<Box> boxs = new ArrayList<>();

    @OneToMany(orphanRemoval = true)
    @JoinColumn(name = "space_id")
    private List<Mr> mrs = new ArrayList<>();

    public Space(SpaceRequestDto spaceRequestDto) {
        this.spaceName = spaceRequestDto.getSpaceName();

    }

}
