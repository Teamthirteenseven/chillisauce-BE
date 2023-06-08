package com.example.chillisauce.domain.spaces.repository;

import com.example.chillisauce.domain.spaces.dto.response.FloorResponseDto;
import com.example.chillisauce.domain.spaces.dto.response.SpaceListResponseDto;
import com.example.chillisauce.domain.spaces.entity.Floor;
import com.example.chillisauce.domain.spaces.entity.Mr;
import com.example.chillisauce.domain.users.entity.QCompanies;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.stream.Collectors;

import static com.example.chillisauce.domain.reservations.entity.QReservation.reservation;
import static com.example.chillisauce.domain.spaces.entity.QFloor.floor;
import static com.example.chillisauce.domain.spaces.entity.QMr.mr;
import static com.example.chillisauce.domain.spaces.entity.QSpace.space;

@Repository
public class FloorRepositoryImpl extends QuerydslRepositorySupport implements FloorRepositorySupport {

    private final JPAQueryFactory queryFactory;
    private final EntityManager em;
    public FloorRepositoryImpl(EntityManager em) {
        super(Floor.class);
        this.em = em;
        this.queryFactory = new JPAQueryFactory(em);
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
    /**
     * 해당하는 floor 전체 조회 쿼리
     */
    public List<FloorResponseDto> getFloorAllList(String companyName) {
        QCompanies company = QCompanies.companies;
        List<Floor> floors = from(floor)
                .leftJoin(floor.companies, company)
                .leftJoin(floor.spaces,space).fetchJoin()
                .where(company.companyName.eq(companyName))
                .distinct()
                .fetch();
        return floors.stream().map(f -> {
            List<SpaceListResponseDto> spaceList = f.getSpaces().stream()
                    .map(SpaceListResponseDto::new)
                    .collect(Collectors.toList());

            return new FloorResponseDto(f.getId(), f.getFloorName(), spaceList);
        }).collect(Collectors.toList());
    }

}
