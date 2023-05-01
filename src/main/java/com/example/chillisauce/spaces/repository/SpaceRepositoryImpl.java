package com.example.chillisauce.spaces.repository;

import com.example.chillisauce.spaces.dto.SpaceResponseDto;
import com.example.chillisauce.spaces.entity.Space;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.stream.Collectors;

import static com.example.chillisauce.spaces.entity.QFloor.floor;
import static com.example.chillisauce.spaces.entity.QSpace.space;


public class SpaceRepositoryImpl extends QuerydslRepositorySupport implements SpaceRepositorySupport {

    private final EntityManager em;

    public SpaceRepositoryImpl(JPAQueryFactory queryFactory, EntityManager em) {
        super(Space.class);
        this.em = em;
    }

    public List<SpaceResponseDto> getSpacesWithLocations(Long spaceId) {

        List<Space> spaces = from(space)
                .leftJoin(space.floor, floor)
                .leftJoin(space.locations)
                .where(space.id.eq(spaceId))
//                        eqFloorName(String.valueOf(floor.floorName)),
//                        eqFloorId(floor.id))
                .distinct()
                .fetch();

        return spaces.stream()
                .map(s -> new SpaceResponseDto(s, s.getFloor().getId(), s.getFloor().getFloorName()))
                .collect(Collectors.toList());
    }

//    private BooleanExpression eqFloorName(String floorName) {
//        return floorName == null ? null : floor.floorName.eq(floorName);
//    }
//
//    private BooleanExpression eqFloorId(NumberPath<Long> floorId) {
//        return floorId == null ? null : floor.id.eq(floorId);
//    }

}