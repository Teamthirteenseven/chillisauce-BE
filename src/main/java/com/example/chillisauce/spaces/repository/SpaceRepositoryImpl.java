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
    public List<SpaceResponseDto> getSpacesWithLocations(Long spaceId) {
        return from(space)
                .leftJoin(space.floor, floor)
                .leftJoin(space.locations)
                .where(space.id.eq(spaceId))
                .distinct()
                .fetchJoin()
                .stream()
                .map(s -> SpaceResponseDto.builder().space(s).floorId(s.getFloor() != null ? s.getFloor().getId() : null )
                        .floorName(s.getFloor() != null ? s.getFloor().getFloorName() : null).build())
                .collect(Collectors.toList());
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
//        return spaces.stream()
//                .map(s -> new SpaceResponseDto
//                        (s, s.getFloor() != null ? s.getFloor().getId() : null, s.getFloor() != null ? s.getFloor().getFloorName() : null))
//                .collect(Collectors.toList());
//                .map(s -> {
//                    List<BoxResponseDto> boxList = s.getLocations().stream()
//                            .filter(l -> l instanceof Box)
//                            .map(l -> (Box) l).map(BoxResponseDto::new)
//                            .collect(Collectors.toList());
//
//                    List<MrResponseDto> mrList = s.getLocations().stream()
//                            .filter(l -> l instanceof Mr)
//                            .map(l -> (Mr) l).map(MrResponseDto::new)
//                            .collect(Collectors.toList());
//
//                    List<MultiBoxResponseDto> multiBoxList = s.getLocations().stream()
//                            .filter(l -> l instanceof MultiBox)
//                            .map(l -> (MultiBox) l).map(MultiBoxResponseDto::new)
//                            .collect(Collectors.toList());
//
//                    return new SpaceResponseDto(s, s.getFloor() != null ? s.getFloor().getId() : null,
//                            s.getFloor() != null ? s.getFloor().getFloorName() : null,
//                            boxList, mrList, multiBoxList);
//                })
//                .collect(Collectors.toList());

    public List<BoxResponseDto> getBoxList() {
        QBox box = QBox.box;
        QUserLocation userLocation = QUserLocation.userLocation;
        return queryFactory.select(Projections.constructor(BoxResponseDto.class, box, userLocation))
                .from(box)
                .leftJoin(box.userLocations, userLocation)
                .fetchJoin()
                .fetch();
    }
    public List<MrResponseDto> getMrList() {
        QMr mr = QMr.mr;
        QReservation reservation = QReservation.reservation;
        return queryFactory.select(Projections.constructor(MrResponseDto.class, mr))
                .from(mr)
                .fetchJoin()
                .fetch();
    }

    public List<MultiBoxResponseDto> getMultiboxList() {
        QMultiBox multiBox = QMultiBox.multiBox;
        QUserLocation userLocation = QUserLocation.userLocation;
        return queryFactory.select(Projections.constructor(MultiBoxResponseDto.class, multiBox, userLocation))
                .from(multiBox)
                .leftJoin(multiBox.userLocations, userLocation)
                .fetchJoin()
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