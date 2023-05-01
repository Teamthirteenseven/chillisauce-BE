package com.example.chillisauce.spaces.repository;

import com.example.chillisauce.spaces.dto.SpaceResponseDto;
import com.example.chillisauce.spaces.entity.Space;
import com.example.chillisauce.users.entity.QCompanies;
import com.example.chillisauce.users.entity.User;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.stream.Collectors;

import static com.example.chillisauce.spaces.entity.QFloor.floor;
import static com.example.chillisauce.spaces.entity.QSpace.space;


public class SpaceRepositoryImpl extends QuerydslRepositorySupport implements SpaceRepositorySupport {

    private final JPAQueryFactory queryFactory;
    private final EntityManager em;
    public SpaceRepositoryImpl(EntityManager em) {
        super(User.class);
        this.em = em;
        this.queryFactory = new JPAQueryFactory(em);
    }
    /*
    space 선택 조회
     */
    public List<SpaceResponseDto> getSpacesWithLocations(Long spaceId) {
        return from(space)
                .leftJoin(space.floor, floor)
                .leftJoin(space.locations)
                .where(space.id.eq(spaceId))
                .distinct()
                .fetchJoin()
                .stream()
                .map(s -> new SpaceResponseDto
                        (s, s.getFloor() != null ? s.getFloor().getId() : null, s.getFloor() != null ? s.getFloor().getFloorName() : null))
                .collect(Collectors.toList());
    }

    /*
    space 전체 조회
    */
    public List<SpaceResponseDto> getSpaceAllList(String companyName) {
        QCompanies company = QCompanies.companies;
        List<Space> spaces = from(space)
                .leftJoin(space.floor, floor)
                .leftJoin(space.companies, company)
                .where(companyNameEquals(companyName))
                .distinct()
                .fetchJoin()
                .fetch();
        return spaces.stream()
                .map(s -> new SpaceResponseDto
                        (s, s.getFloor() != null ? s.getFloor().getId() : null, s.getFloor() != null ? s.getFloor().getFloorName() : null))
                .collect(Collectors.toList());
    }

    private BooleanExpression companyNameEquals(String companyName) {
        return space.companies.companyName.eq(companyName);
    }

}