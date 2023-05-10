package com.example.chillisauce.spaces.service;

import com.example.chillisauce.reservations.entity.Reservation;
import com.example.chillisauce.reservations.service.ReservationService;
import com.example.chillisauce.security.UserDetailsImpl;
import com.example.chillisauce.spaces.dto.request.MrRequestDto;
import com.example.chillisauce.spaces.dto.response.MrResponseDto;
import com.example.chillisauce.spaces.entity.Mr;
import com.example.chillisauce.spaces.entity.Space;
import com.example.chillisauce.spaces.exception.SpaceErrorCode;
import com.example.chillisauce.spaces.exception.SpaceException;
import com.example.chillisauce.spaces.repository.MrRepository;
import com.example.chillisauce.users.entity.Companies;
import com.example.chillisauce.users.repository.CompanyRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static com.example.chillisauce.fixture.FixtureFactory.*;
import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MrServiceTest {

    @Mock
    private CompanyRepository companyRepository;
    @Mock
    private MrRepository mrRepository;
    @InjectMocks
    private MrService mrService;
    @Mock
    private SpaceService spaceService;
    @Mock
    private ReservationService reservationService;



    @Nested
    @DisplayName("Mr 성공 케이스")
    class SuccessCase {
        Companies companies = Company_생성();
        Space space = Space_생성_아이디_지정(1L);
        UserDetailsImpl details = details_권한_ADMIN_유저_네임_NULL(companies);
        Reservation reservation = Reservation_생성_빈값();
        Mr mr = MeetingRoom_생성_아이디_지정(1L);
        @Test
        void 미팅룸_생성() {
            //given
            MrRequestDto requestDto = new MrRequestDto("MrTest", "200","300");
            when(spaceService.findCompanyNameAndSpaceId(companies.getCompanyName(),space.getId())).thenReturn(space);
            when(mrRepository.save(any(Mr.class))).thenReturn(mr);

            //when
            MrResponseDto mrResponseDto = mrService.createMr(companies.getCompanyName(),space.getId(),requestDto,details);

            //then
            assertNotNull(mrResponseDto);
            assertEquals("MrTest",mrResponseDto.getMrName());
            assertEquals("200",mrResponseDto.getX());
            assertEquals("300",mrResponseDto.getY());

        }

        @Test
        void Mr_수정() {
            //given
            Mr mr = Mr_생성_예약_추가(reservation);
            when(companyRepository.findByCompanyName(companies.getCompanyName())).thenReturn(Optional.of(companies));
            when(mrRepository.findByIdAndSpaceCompanies(mr.getId(),companies)).thenReturn(Optional.of(mr));
            MrRequestDto requestDto = new MrRequestDto("MrTest", "200","300");

            //when
            MrResponseDto mrResponseDto = mrService.updateMr(companies.getCompanyName(),mr.getId(),requestDto,details);

            //Then
            assertNotNull(mrResponseDto);
            assertEquals("MrTest",mrResponseDto.getMrName());
            assertEquals("200",mrResponseDto.getX());
            assertEquals("300",mrResponseDto.getY());
        }

        @Test
        void Mr_삭제() {
            //given
            Mr mr = Mr_생성_예약_추가(reservation);
            when(companyRepository.findByCompanyName(companies.getCompanyName())).thenReturn(Optional.of(companies));
            when(mrRepository.findByIdAndSpaceCompanies(mr.getId(),companies)).thenReturn(Optional.of(mr));
            doNothing().when(mrRepository).deleteById(mr.getId());
            //when
            when(reservationService.deleteMeetingRoomInReservations(mr.getId(), null)).thenReturn(String.valueOf(reservation));
            MrResponseDto mrResponseDto = mrService.deleteMr(companies.getCompanyName(),mr.getId(),details);

            //Then
            assertNotNull(mrResponseDto);
            assertEquals("testMr",mrResponseDto.getMrName());
            assertEquals("111",mrResponseDto.getX());
            assertEquals("222",mrResponseDto.getY());
        }
    }

    @Nested
    @DisplayName("Mr 권한 없음 예외 케이스")
    class NotPermissionExceptionCase {
        // given
        Companies companies = Company_생성();
        UserDetailsImpl details = details_권한_USER_유저_네임_NULL(companies);
        MrRequestDto requestDto = new MrRequestDto("MrTest", "200", "300");
        Space space = Space_생성_아이디_지정(1L);

        public static void NOT_HAVE_PERMISSION_EXCEPTION(SpaceErrorCode expectedErrorCode, Executable executable) {
            SpaceException exception = assertThrows(SpaceException.class, executable);
            assertEquals(expectedErrorCode, exception.getErrorCode());
        }

        @Test
        void Mr_생성_권한_예외_테스트() {
            // when & then
            NotPermissionExceptionCase.NOT_HAVE_PERMISSION_EXCEPTION(SpaceErrorCode.NOT_HAVE_PERMISSION, () -> {
                mrService.createMr(companies.getCompanyName(),space.getId() , requestDto, details);
            });
        }

        @Test
        void Mr_수정_권한_예외_테스트() {
            // when & then
            NotPermissionExceptionCase.NOT_HAVE_PERMISSION_EXCEPTION(SpaceErrorCode.NOT_HAVE_PERMISSION, () -> {
                mrService.updateMr(companies.getCompanyName(), space.getId(), requestDto, details);
            });
        }

        @Test
        void Mr_삭제_권한_예외_테스트() {
            // when & then
            NotPermissionExceptionCase.NOT_HAVE_PERMISSION_EXCEPTION(SpaceErrorCode.NOT_HAVE_PERMISSION, () -> {
                mrService.deleteMr(companies.getCompanyName(), space.getId(), details);
            });
        }
    }
    @Nested
    @DisplayName("Mr 메서드 예외 케이스")
    class MethodExceptionCase {
        @Test
        void 해당_회사_없음() {
            //given
            Mr mr = MeetingRoom_생성_아이디_지정(1L);
            Companies companies = Different_Company_생성();
            when(companyRepository.findByCompanyName(companies.getCompanyName())).thenReturn(Optional.empty());

            //When,Then
            SpaceException exception = assertThrows(SpaceException.class, () -> {
                mrService.findCompanyNameAndMrId(companies.getCompanyName(), mr.getId());
            });
            assertEquals(SpaceErrorCode.COMPANIES_NOT_FOUND, exception.getErrorCode());
        }

        @Test
        void 해당_회사_아이디_없음() {
            //given
            Companies companies = Different_Company_생성();
            Mr mr = MeetingRoom_생성_아이디_지정(1L);

            when(companyRepository.findByCompanyName(companies.getCompanyName())).thenReturn(Optional.of(companies));
            when(mrRepository.findByIdAndSpaceCompanies(mr.getId(), companies)).thenReturn(Optional.empty());

            //when,Then
            SpaceException exception = assertThrows(SpaceException.class, () -> {
                mrService.findCompanyNameAndMrId(companies.getCompanyName(), mr.getId());
            });
            assertEquals(SpaceErrorCode.MR_NOT_FOUND, exception.getErrorCode());
        }
    }
}

