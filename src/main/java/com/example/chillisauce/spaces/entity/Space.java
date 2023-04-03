package com.example.chillisauce.spaces.entity;

import com.example.chillisauce.security.UserDetailsImpl;
import com.example.chillisauce.spaces.dto.SpaceRequestDto;
import com.example.chillisauce.users.entity.Companies;
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
    @OneToMany(orphanRemoval = true)
    @JoinColumn(name = "space_id")
    private List<Box> boxs = new ArrayList<>();

    @OneToMany(orphanRemoval = true)
    @JoinColumn(name = "space_id")
    private List<Mr> mrs = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "companies_id", nullable = false)
    private Companies companies;



    public Space(SpaceRequestDto spaceRequestDto, Companies companies) {
        this.spaceName = spaceRequestDto.getSpaceName();
        this.companies = companies;
    }
}
