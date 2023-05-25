package com.example.chillisauce.spaces.entity;

import com.example.chillisauce.spaces.dto.request.SpaceRequestDto;
import com.example.chillisauce.users.entity.Companies;
import lombok.*;


import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@RequiredArgsConstructor
@Builder
@AllArgsConstructor
@Setter
public class Space {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @NotEmpty
    private String spaceName;

    @OneToMany(mappedBy = "space", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<Location> locations = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "companies_id", nullable = false)
    private Companies companies;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "floor_id")
    private Floor floor;

    public Space(SpaceRequestDto spaceRequestDto, Floor floor, Companies companies) {
        this.spaceName = spaceRequestDto.getSpaceName();
        this.floor = floor;
        this.companies = companies;
    }

    public Space(SpaceRequestDto spaceRequestDto, Companies companies) {
        this.spaceName = spaceRequestDto.getSpaceName();
        this.companies = companies;
    }

    public Space(Companies companies) {
        this.companies = companies;
    }

    public void updateSpace(SpaceRequestDto spaceRequestDto, Floor floor) {
        this.spaceName = spaceRequestDto.getSpaceName();
        this.floor = floor;
    }

    public void updateSpace(Floor floor) {
        this.floor = floor;
    }


}