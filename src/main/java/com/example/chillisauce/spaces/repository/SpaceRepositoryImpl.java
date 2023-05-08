package com.example.chillisauce.spaces.repository;

import com.example.chillisauce.reservations.entity.QReservation;
import com.example.chillisauce.spaces.dto.response.*;
import com.example.chillisauce.spaces.entity.*;
import com.example.chillisauce.users.entity.QCompanies;
import com.example.chillisauce.users.entity.User;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.example.chillisauce.reservations.entity.QReservation.reservation;
import static com.example.chillisauce.spaces.entity.QFloor.floor;
import static com.example.chillisauce.spaces.entity.QMr.mr;
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
    public SpaceResponseDto getSpacesWithLocations(Long spaceId) {
        QLocation location = QLocation.location;
        QUserLocation userLocation = QUserLocation.userLocation;
        QSpace qSpace = space;
        Space space = from(qSpace)
                .leftJoin(qSpace.floor, floor)
                .leftJoin(qSpace.locations, location)
                .leftJoin(location.userLocations, userLocation)
                .where(qSpace.id.eq(spaceId))
                .distinct()
                .fetchOne();

        List<BoxResponseDto> boxList = new ArrayList<>();
        List<MrResponseDto> mrList = new ArrayList<>();
        List<MultiBoxResponseDto> multiBoxList = new ArrayList<>();

        space.getLocations().forEach(l -> {
            if (l instanceof Box) {
                UserLocation locationUser = l.getUserLocations().stream()
                        .findFirst().orElse(null);
                boxList.add(new BoxResponseDto((Box) l, locationUser));
            } else if (l instanceof Mr) {
                mrList.add(new MrResponseDto((Mr) l));
            } else if (l instanceof MultiBox) {
                multiBoxList.add(new MultiBoxResponseDto((MultiBox) l, l.getUserLocations()));
            }
        });

        return new SpaceResponseDto(space,
                space.getFloor() != null ? space.getFloor().getId() : null,
                space.getFloor() != null ? space.getFloor().getFloorName() : null,
                boxList, mrList, multiBoxList);
    }

    /*
    space 전체 조회
    */
    public List<SpaceListResponseDto> getSpaceAllList(String companyName) {
        QCompanies company = QCompanies.companies;
            return from(space)
                .leftJoin(space.floor, floor)
                .leftJoin(space.companies, company)
                .where(companyNameEquals(companyName))
                .select(Projections.constructor(
                        SpaceListResponseDto.class,
                        space.id,
                        space.spaceName,
                        floor.id,
                        floor.floorName
                ))
                .fetch();
    }


    /**
     * 해당하는 space 전체 삭제 2차 개선
     */
    public void clearAllReservationsForSpace(Long spaceId) {
        List<Long> meetingRoomList = queryFactory
                .select(mr.id)
                .from(mr)
                .where(mr.space.id.eq(spaceId))
                .fetch();
        queryFactory
                .update(reservation)
                .set(reservation.meetingRoom, (Mr) null)
                .where(reservation.meetingRoom.id.in(meetingRoomList))
                .execute();
    }
    /**
     * 해당하는 floor 전체 삭제 2차 개선
     */
    public void clearAllReservationsForFloor(Long floorId) {
        List<Long> meetingRoomList = queryFactory
                .select(mr.id)
                .from(mr)
                .where(mr.space.floor.id.eq(floorId))
                .fetch();
        queryFactory
                .update(reservation)
                .set(reservation.meetingRoom, (Mr) null)
                .where(reservation.meetingRoom.id.in(meetingRoomList))
                .execute();
    }


    private BooleanExpression companyNameEquals(String companyName) {
        return space.companies.companyName.eq(companyName);
    }

}