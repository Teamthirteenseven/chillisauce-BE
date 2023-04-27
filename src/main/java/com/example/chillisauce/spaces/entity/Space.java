package com.example.chillisauce.spaces.entity;

import com.example.chillisauce.security.UserDetailsImpl;
import com.example.chillisauce.spaces.dto.SpaceRequestDto;
import com.example.chillisauce.users.entity.Companies;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import lombok.RequiredArgsConstructor;


import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@RequiredArgsConstructor
@Builder
@AllArgsConstructor
public class Space {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    @NotEmpty
    private String spaceName;

    @OneToMany(mappedBy = "space", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Location> locations = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "companies_id", nullable = false)
    private Companies companies;

    @ManyToOne
    @JoinColumn(name = "floor_id")
    private Floor floor;



    public Space(SpaceRequestDto spaceRequestDto, Companies companies, Floor floor) {
        this.spaceName = spaceRequestDto.getSpaceName();
        this.companies = companies;
        this.floor = floor;
    }

    public Space(SpaceRequestDto spaceRequestDto, Companies companies) {
        this.spaceName = spaceRequestDto.getSpaceName();
        this.companies = companies;
    }

    public Space(Companies companies) {
        this.companies = companies;
    }


    public void addLocation(Box box) {
        this.locations.add(box);
        box.linkSpace(this);
    }

    public void addLocation(Mr mr) {
        this.locations.add(mr);
        mr.linkSpace(this);
    }

    public void addLocation(MultiBox multiBox) {
        this.locations.add(multiBox);
        multiBox.linkSpace(this);
    }


    public void updateSpace(SpaceRequestDto spaceRequestDto, Floor floor) {
        this.spaceName = spaceRequestDto.getSpaceName();
        this.floor = floor;
    }

}
