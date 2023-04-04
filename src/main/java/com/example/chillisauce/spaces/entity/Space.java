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

    //CascadeType.ALL은 모든 Cascade Type을 적용하는 것으로, 부모 엔티티의 변경사항이 자식 엔티티에도 전파되어 모든 변경사항을 함께 저장하고 관리하게 됩니다.
    // 하지만 CascadeType.ALL은 필요 이상으로 많은 작업을 수행할 수 있기 때문에 주의해서 사용해야 합니다.
    @OneToMany(mappedBy = "space", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<Box> boxes = new ArrayList<>();


    @OneToMany(mappedBy = "space", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<Mr> mrs = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "companies_id", nullable = false)
    private Companies companies;



    public Space(SpaceRequestDto spaceRequestDto, Companies companies) {
        this.spaceName = spaceRequestDto.getSpaceName();
        this.companies = companies;
    }

    public void addBox(Box box) {
        this.boxes.add(box);
        box.linkSpace(this);
    }

    public void addMr(Mr mr) {
        this.mrs.add(mr);
        mr.linkSpace(this);
    }
    public void updateSpaceName(String newSpaceName) {
        this.spaceName = newSpaceName;
    }


}
