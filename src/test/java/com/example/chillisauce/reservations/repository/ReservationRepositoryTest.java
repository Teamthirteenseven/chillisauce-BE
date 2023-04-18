package com.example.chillisauce.reservations.repository;

import com.example.chillisauce.reservations.entity.Reservation;
import com.example.chillisauce.spaces.repository.MrRepository;
import com.example.chillisauce.spaces.repository.SpaceRepository;
import com.example.chillisauce.spaces.entity.Mr;
import com.example.chillisauce.spaces.entity.Space;
import com.example.chillisauce.users.entity.Companies;
import com.example.chillisauce.users.entity.User;
import com.example.chillisauce.users.entity.UserRoleEnum;
import com.example.chillisauce.users.repository.CompanyRepository;
import com.example.chillisauce.users.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ReservationRepositoryTest {
    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SpaceRepository spaceRepository;

    @Autowired
    private MrRepository meetingRoomRepository;

    @Test
    public void 예약등록_성공() {
        // given
        Companies company = companyRepository.save(Companies.builder()
                .companyName("삼성전자")
                .certification("admin")
                .build());

        User adminUser = userRepository.save(User.builder()
                .email("test@gmail.com")
                .password("12345678")
                .role(UserRoleEnum.ADMIN)
                .username("홍길동")
                .companies(company)
                .build());

        Space space = spaceRepository.save(Space.builder()
                .spaceName("마케팅부")
                .companies(company)
                .build());

        Mr meetingRoom = meetingRoomRepository.save(Mr.builder()
                .reservations(new ArrayList<>())
                .mrName("회의실 1")
                .x("100")
                .y("100")
                .space(space)
                .build());

        final LocalDateTime start = LocalDateTime.of(2023, 4, 5, 10, 0);
        final LocalDateTime end = LocalDateTime.of(2023, 4, 5, 11, 0);
        final Reservation reservation = Reservation.builder()
                .id(1L)
                .user(adminUser)
                .meetingRoom(meetingRoom)
                .startTime(start)
                .endTime(end)
                .build();

        // when
        final Reservation result = reservationRepository.save(reservation);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getUser()).isNotNull();
        assertThat(result.getMeetingRoom()).isNotNull();
        assertThat(result.getStartTime()).isEqualTo(LocalDateTime.of(2023, 4, 5, 10, 0));
        assertThat(result.getEndTime()).isEqualTo(LocalDateTime.of(2023, 4, 5, 11, 0));
    }
}