package com.example.chillisauce.spaces.repository;

import com.example.chillisauce.spaces.dto.response.*;
import com.example.chillisauce.spaces.entity.*;
import com.example.chillisauce.users.entity.QCompanies;
import com.example.chillisauce.users.entity.User;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.stream.Collectors;

import static com.example.chillisauce.reservations.entity.QReservation.reservation;
import static com.example.chillisauce.spaces.entity.QFloor.floor;
import static com.example.chillisauce.spaces.entity.QMr.mr;
import static com.example.chillisauce.spaces.entity.QSpace.space;


@Slf4j
@Repository
public class SpaceRepositoryImpl extends QuerydslRepositorySupport implements SpaceRepositorySupport {

    private final JPAQueryFactory queryFactory;
    private final EntityManager em;
    public SpaceRepositoryImpl(EntityManager em) {
        super(User.class);
        this.em = em;
        this.queryFactory = new JPAQueryFactory(em);
    }

    /**
     * space 선택 조회
     */
    public List<SpaceResponseDto> getSpacesList(Long spaceId) {
        QLocation location = QLocation.location;
        QUserLocation userLocation = QUserLocation.userLocation;
        QSpace qSpace = space;
        List<Space> spaces = from(qSpace)
                .leftJoin(qSpace.floor, floor)
                .leftJoin(qSpace.locations, location).fetchJoin()
                .leftJoin(location.userLocations, userLocation)
                .where(qSpace.id.eq(spaceId))
                .distinct()
                .fetch();

        return spaces.stream().map(space -> createSpaceResponseDto(space)).collect(Collectors.toList());
    }



    /**
     * space 전체 조회
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


    private BooleanExpression companyNameEquals(String companyName) {
        return space.companies.companyName.eq(companyName);
    }

    SpaceResponseDto createSpaceResponseDto(Space space) {
        List<BoxResponseDto> boxList = createBoxList(space);
        List<MrResponseDto> mrList = createMrList(space);
        List<MultiBoxResponseDto> multiBoxList = createMultiBoxList(space);

        Long floorId = space.getFloor() != null ? space.getFloor().getId() : null;
        String floorName = space.getFloor() != null ? space.getFloor().getFloorName() : null;

        return new SpaceResponseDto(space.getId(), space.getSpaceName(), floorId, floorName, boxList, mrList, multiBoxList);
    }

    private List<BoxResponseDto> createBoxList(Space space) {
        return space.getLocations().stream()
                .filter(Location::isBox)
                .map(l -> {
                    UserLocation locationUser = l.getUserLocations().stream()
                            .findFirst().orElse(null);
                    return new BoxResponseDto((Box) l, locationUser);
                })
                .collect(Collectors.toList());
    }

    private List<MrResponseDto> createMrList(Space space) {
        return space.getLocations().stream()
                .filter(Location::isMr)
                .map(l -> new MrResponseDto((Mr) l))
                .collect(Collectors.toList());
    }

    private List<MultiBoxResponseDto> createMultiBoxList(Space space) {
        return space.getLocations().stream()
                .filter(Location::isMultiBox)
                .map(l -> new MultiBoxResponseDto((MultiBox) l, l.getUserLocations()))
                .collect(Collectors.toList());
    }

}