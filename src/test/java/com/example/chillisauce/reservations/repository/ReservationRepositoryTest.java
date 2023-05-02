package com.example.chillisauce.reservations.repository;

import com.example.chillisauce.config.TestConfig;
import com.example.chillisauce.reservations.dto.request.ReservationRequestDto;
import com.example.chillisauce.reservations.dto.request.ReservationTime;
import com.example.chillisauce.reservations.dto.response.ReservationResponseDto;
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
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Import(TestConfig.class)
@DisplayName("ReservationRepository 클래스")
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

    @Nested
    @DisplayName("save 메서드를 호출할 때")
    class SaveTestCase {
        @Test
        @Transactional
        public void 예약을_저장한다() {
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
                    .reservation(new ArrayList<>())
                    .locationName("회의실 1")
                    .x("100")
                    .y("100")
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

    @Nested
    @DisplayName("시간 중복 예약을 찾는 메서드를 호출할 때")
    class FindDuplicatedTestCase {
        @Test
        public void 여러_스레드에서_접근() throws InterruptedException {
            // given
            Companies company = companyRepository.save(Companies.builder()
                    .companyName("testCompany")
                    .certification("cert")
                    .build());

            User user = userRepository.save(User.builder()
                    .companies(company)
                    .email("test@test.com")
                    .username("tester")
                    .password("12345678")
                    .role(UserRoleEnum.USER)
                    .build());

            Mr mr = meetingRoomRepository.save(Mr.builder()
                    .x("100").y("150").locationName("testMeetingRoom")
                    .build());

            Reservation reservation = Reservation.builder()
                    .user(user)
                    .meetingRoom(mr)
                    .startTime(LocalDateTime.of(2023, 4, 5, 10, 0))
                    .endTime(LocalDateTime.of(2023, 4, 5, 11, 0))
                    .build();

            Reservation saved = reservationRepository.save(reservation);

            int threadCount = 5;
            ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
            CountDownLatch countDownLatch = new CountDownLatch(threadCount);
            // when
            IntStream.range(0, threadCount).forEach(e ->
                    executorService.submit(() -> {
                        try {
                            Optional<Reservation> r = reservationRepository
                                    .findFirstByMeetingRoomIdAndStartTimeLessThanAndEndTimeGreaterThan(mr.getId(),
                                            saved.getStartTime(), saved.getEndTime());
                        } finally {
                            countDownLatch.countDown();
                        }
                    }));

            countDownLatch.await();

            // then
            //TODO: 동시성 이슈 해결 후 수정 필요
            assertThat(reservationRepository.findAll().size()).isEqualTo(1);
        }
    }
}