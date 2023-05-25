package com.example.chillisauce.spaces.repository;

import com.example.chillisauce.config.TestConfig;
import com.example.chillisauce.reservations.entity.Reservation;
import com.example.chillisauce.reservations.repository.ReservationRepository;
import com.example.chillisauce.spaces.dto.response.*;
import com.example.chillisauce.spaces.entity.*;
import com.example.chillisauce.spaces.service.SpaceService;
import com.example.chillisauce.users.entity.Companies;
import com.example.chillisauce.users.entity.User;
import com.example.chillisauce.users.repository.CompanyRepository;
import com.example.chillisauce.users.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.*;
import java.util.stream.Collectors;

import static com.example.chillisauce.fixture.FixtureFactory.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@Import(TestConfig.class)
public class SpaceRepositoryImplTest {
    @Autowired
    SpaceRepositoryImpl spaceRepositoryImpl;
    @MockBean
    SpaceService spaceService;
    @Autowired
    ReservationRepository reservationRepository;
    @Autowired
    MrRepository mrRepository;
    @Autowired
    SpaceRepository spaceRepository;
    @Autowired
    CompanyRepository companyRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    LocationRepository locationRepository;
    @Autowired
    BoxRepository boxRepository;
    @Autowired
    MultiBoxRepository multiBoxRepository;
    @Autowired
    FloorRepository floorRepository;


    @Nested
    @DisplayName("성공 케이스")
    class SuccessCases {

        private Companies companies;
        private Space space;
        private Mr mr;
        private User user;

        @BeforeEach
        void setup() {
            companies = Company_생성_이름_지정("7jo");
            space = Space_생성_아이디_지정_회사_지정(1L, companies);
            mr = Mr_생성();
            user = User_USER권한_생성(companies);
        }

        @DisplayName("getSpaceList")
        @Test
        void Space_선택_조회() {
            //given
            companyRepository.save(companies);
            userRepository.save(user);
            spaceRepository.save(space);

            Location location = new Mr("testlocation", "150", "200", space);
            location.setSpace(space);
            locationRepository.save(location);


            //when
            List<SpaceResponseDto> spaceList = spaceRepositoryImpl.getSpacesList(space.getId());
            for (SpaceResponseDto spaceResponseDto : spaceList) {
                System.out.println("spacelist = "+ spaceResponseDto.getSpaceName());
            }
            //then
            assertEquals(1, spaceList.size());
            assertEquals(space.getId(), spaceList.get(0).getSpaceId());
        }


        @DisplayName("getSpaceAllList")
        @Test
        void Space_전체_조회() {
            // given
            Companies company1 = Companies.builder()
                    .companyName("testCompany1")
                    .certification("zzang")
                    .build();

            companyRepository.save(company1);

            Floor floor1 = Floor.builder()
                    .companies(company1)
                    .floorName("testFloor1")
                    .build();
            floorRepository.save(floor1);

            Space space1 = Space.builder()
                    .companies(company1)
                    .floor(floor1)
                    .spaceName("testSpace1")
                    .build();

            spaceRepository.save(space1);

            // when
            List<SpaceListResponseDto> result = spaceRepository.getSpaceAllList(company1.getCompanyName());
            for (SpaceListResponseDto spaceListResponseDto : result) {
                System.out.println("result = " + spaceListResponseDto.getSpaceName());
            }

            // then
            assertThat(result).isNotNull();
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getSpaceName()).isEqualTo(space1.getSpaceName());
            assertThat(result.get(0).getFloorName()).isEqualTo(floor1.getFloorName());
        }


        @DisplayName("clearAllReservationsForSpace")
        @Test
        void 공간에_속한_회의실_NULL() {
            // given
            Long givenSpaceId = space.getId();
            // when
            spaceRepositoryImpl.clearAllReservationsForSpace(givenSpaceId);
            // then
            List<Reservation> reservations = reservationRepository.findAllByMeetingRoomId(mr.getId());
            for (Reservation res : reservations) {
                if (res.getMeetingRoom().getSpace().getId().equals(givenSpaceId)) {
                    assertNull(res.getMeetingRoom());
                }

            }
        }
    }

    @Nested
    @DisplayName("createSpaceResponseDto 메서드 성공 케이스")
    class methodSuccessCases {
        private Floor floor;
        private Space space;
        private Reservation reservation;
        private UserLocation userLocation;

        @BeforeEach
        void setup() {
            floor = Floor_생성();
            space = Space_생성();
            reservation = Reservation_생성_빈값();
            userLocation = new UserLocation();
        }

        @Test
        void Space_Response_Dto_생성한다() {
            //given
            Box box = Box_생성();
            box.setUserLocations(List.of(userLocation));

            Mr meetingRoom = MeetingRoom_생성_예약_내역(Reservation_생성_빈값());

            MultiBox multiBox = MultiBox_생성();
            multiBox.setUserLocations(List.of(userLocation));

            space.setFloor(floor);
            space.getLocations().add(box);
            space.getLocations().add(meetingRoom);
            space.getLocations().add(multiBox);

            //when
            SpaceResponseDto result = spaceRepositoryImpl.createSpaceResponseDto(space);

            //then
            assertThat(result).isNotNull();
            assertThat(result.getFloorName()).isEqualTo("testFloor");
            assertThat(result.getBoxList().size()).isEqualTo(1);
            assertThat(result.getMrList().size()).isEqualTo(1);
            assertThat(result.getMultiBoxList().size()).isEqualTo(1);
        }
    }
}







