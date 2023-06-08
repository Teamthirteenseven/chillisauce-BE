package com.example.chillisauce.domain.spaces.repository;

import com.example.chillisauce.global.config.TestConfig;
import com.example.chillisauce.domain.reservations.entity.Reservation;
import com.example.chillisauce.domain.reservations.repository.ReservationRepository;
import com.example.chillisauce.domain.spaces.dto.response.FloorResponseDto;
import com.example.chillisauce.domain.spaces.entity.Floor;
import com.example.chillisauce.domain.spaces.entity.Space;
import com.example.chillisauce.domain.spaces.repository.FloorRepository;
import com.example.chillisauce.domain.spaces.repository.FloorRepositoryImpl;
import com.example.chillisauce.domain.spaces.repository.SpaceRepository;
import com.example.chillisauce.domain.users.entity.Companies;
import com.example.chillisauce.domain.users.repository.CompanyRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import static com.example.chillisauce.fixture.FixtureFactory.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@Import(TestConfig.class)
public class FloorRepositoryImplTest {
    @Autowired
    CompanyRepository companyRepository;
    @Autowired
    SpaceRepository spaceRepository;
    @Autowired
    FloorRepository floorRepository;
    @Autowired
    FloorRepositoryImpl floorRepositoryImpl;
    @Autowired
    ReservationRepository reservationRepository;


    @Nested
    @DisplayName("성공 케이스")
    class SuccessCases {
        @DisplayName("getFloorAllList")
        @Test
        void Floor_전체_조회() {
            // given
            Companies companies = Company_생성_이름_지정("7jo");
            companyRepository.save(companies);

            Floor floor = Floor.builder()
                    .companies(companies)
                    .floorName("testFloor2")
                    .build();

            Space space1 = Space.builder()
                    .spaceName("testSpace1")
                    .companies(companies)
                    .floor(floor)
                    .build();
            spaceRepository.save(space1);

            Space space2 = Space.builder()
                    .spaceName("testSpace2")
                    .companies(companies)
                    .floor(floor)
                    .build();
            spaceRepository.save(space2);

            floor.updateFloor(List.of(space1,space2));
            floorRepository.save(floor);

            // when
            List<FloorResponseDto> result = floorRepositoryImpl.getFloorAllList(companies.getCompanyName());
            for (FloorResponseDto floorResponseDto : result) {
                System.out.println(floorResponseDto.getFloorName() + floor.getSpaces().toString());
            }
            // then
            assertThat(result).isNotNull();
            assertThat(result.size()).isEqualTo(1);
            assertThat(result.get(0).getFloorName()).isEqualTo(floor.getFloorName());
            assertThat(result.get(0).getSpaceList().size()).isEqualTo(2);
            assertThat(result.get(0).getSpaceList().get(1).getSpaceName()).isEqualTo(space2.getSpaceName());
            assertThat(result.get(0).getSpaceList().get(0).getFloorId()).isEqualTo(floor.getId());
            assertThat(result.get(0).getSpaceList().get(1).getFloorId()).isEqualTo(floor.getId());
            assertThat(result.get(0).getSpaceList().get(0).getFloorName()).isEqualTo(floor.getFloorName());
            assertThat(result.get(0).getSpaceList().get(1).getFloorName()).isEqualTo(floor.getFloorName());
        }
    }
        @DisplayName("clearAllReservationsForFloor")
        @Test
        void floor에_속한_모든_예약_삭제() {
            // given
            Floor floor = Floor_생성_아이디_지정(1L);

            // when
            floorRepositoryImpl.clearAllReservationsForFloor(floor.getId());

            // then
            List<Reservation> reservations = reservationRepository.findAll();
            for (Reservation res : reservations) {
                assertNull(res.getMeetingRoom());
            }
        }
    }





