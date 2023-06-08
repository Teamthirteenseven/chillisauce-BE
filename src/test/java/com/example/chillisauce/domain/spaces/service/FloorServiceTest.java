package com.example.chillisauce.domain.spaces.service;

import com.example.chillisauce.domain.spaces.service.FloorService;
import com.example.chillisauce.global.security.UserDetailsImpl;
import com.example.chillisauce.domain.spaces.dto.request.FloorRequestDto;
import com.example.chillisauce.domain.spaces.dto.response.FloorResponseDto;
import com.example.chillisauce.domain.spaces.entity.Floor;
import com.example.chillisauce.domain.spaces.entity.Space;
import com.example.chillisauce.domain.spaces.exception.SpaceErrorCode;
import com.example.chillisauce.domain.spaces.exception.SpaceException;
import com.example.chillisauce.domain.spaces.repository.FloorRepository;
import com.example.chillisauce.domain.spaces.repository.SpaceRepository;
import com.example.chillisauce.domain.users.entity.Companies;
import com.example.chillisauce.domain.users.repository.CompanyRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.example.chillisauce.fixture.FixtureFactory.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
@ExtendWith(MockitoExtension.class)
public class FloorServiceTest {

    @Mock
    private CompanyRepository companyRepository;
    @Mock
    private FloorRepository floorRepository;
    @InjectMocks
    private FloorService floorService;
    @Mock
    private SpaceRepository spaceRepository;

    @Nested
    @DisplayName("성공케이스")
    class SuccessCase {

        Floor floor = Floor_생성_아이디_지정(1L);
        Companies companies = Company_생성();
        UserDetailsImpl details = details_권한_ADMIN_유저_네임_NULL(companies);

        @Test
        void Floor_생성() {
            //given
            FloorRequestDto floorRequestDto = new FloorRequestDto("testFloor");
            when(floorRepository.save(any(Floor.class))).thenReturn(floor);
            when(companyRepository.findByCompanyName(companies.getCompanyName())).thenReturn(Optional.of(Companies.builder().build()));

            //When
            FloorResponseDto floorResponseDto = floorService.createFloor(companies.getCompanyName(), floorRequestDto, details);

            //Then
            assertNotNull(floorResponseDto);
            assertEquals("testFloor", floorResponseDto.getFloorName());

        }


        @Test
        void Floor_전체_조회() {
            //given
            List<Floor> floorList = Collections.singletonList(floor);
            List<FloorResponseDto> responseDto = floorList.stream().map(FloorResponseDto::new).toList();
            when(floorRepository.getFloorAllList(companies.getCompanyName())).thenReturn(floorList.stream().map(FloorResponseDto::new).collect(Collectors.toList()));

            //when
            List<FloorResponseDto> result = floorService.getFloor(companies.getCompanyName(), details);

            //Then
            assertNotNull(result);
            assertThat(responseDto).allSatisfy(responseSpace -> {
                assertThat(responseSpace.getFloorName()).isEqualTo("testFloor");
            });
        }

        @Test
        void Floor_수정() {
            //given
            when(companyRepository.findByCompanyName(companies.getCompanyName())).thenReturn(Optional.of(companies));
            when(floorRepository.findByIdAndCompanies(anyLong(), any(Companies.class))).thenReturn(Optional.of(floor));
            FloorRequestDto floorRequestDto = new FloorRequestDto("플로워 수정");

            //when
            FloorResponseDto floorResponseDto = floorService.updateFloor(companies.getCompanyName(), floor.getId(), floorRequestDto, details);

            //Then
            assertNotNull(floorResponseDto);
            assertEquals("플로워 수정", floorResponseDto.getFloorName());
        }

        @Test
        void Floor_삭제() {
            when(companyRepository.findByCompanyName(companies.getCompanyName())).thenReturn(Optional.of(Companies.builder().build()));
            when(floorRepository.findByIdAndCompanies(anyLong(), any(Companies.class))).thenReturn(Optional.of(floor));
            doNothing().when(floorRepository).clearAllReservationsForFloor(floor.getId());
            doNothing().when(floorRepository).delete(floor);
            //when
            FloorResponseDto floorResponseDto = floorService.deleteFloor(companies.getCompanyName(), floor.getId(), details);

            //then
            assertNotNull(floorResponseDto);
            assertEquals("testFloor", floorResponseDto.getFloorName());
        }
    }

    @Nested
    @DisplayName("플로우 권한 없음 예외 케이스")
    class NotPermissionExceptionCase {
        // given
        Companies companies = Company_생성();
        Floor floor = Floor_생성_아이디_지정(1L);
        UserDetailsImpl details = details_권한_USER_유저_네임_NULL(companies);
        FloorRequestDto requestDto = new FloorRequestDto("floorTest");

        public static void NOT_HAVE_PERMISSION_EXCEPTION(SpaceErrorCode expectedErrorCode, Executable executable) {
            SpaceException exception = assertThrows(SpaceException.class, executable);
            assertEquals(expectedErrorCode, exception.getErrorCode());
        }

        @Test
        void Floor_생성_권한_예외_테스트() {
            // when & then
            NotPermissionExceptionCase.NOT_HAVE_PERMISSION_EXCEPTION(SpaceErrorCode.NOT_HAVE_PERMISSION, () -> {
                floorService.createFloor(companies.getCompanyName(), requestDto, details);
            });
        }

        @Test
        void Floor_수정_권한_예외_테스트() {
            // when & then
            NotPermissionExceptionCase.NOT_HAVE_PERMISSION_EXCEPTION(SpaceErrorCode.NOT_HAVE_PERMISSION, () -> {
                floorService.updateFloor(companies.getCompanyName(), floor.getId(), requestDto, details);
            });
        }

        @Test
        void Floor_삭제_권한_예외_테스트() {
            // when & then
            NotPermissionExceptionCase.NOT_HAVE_PERMISSION_EXCEPTION(SpaceErrorCode.NOT_HAVE_PERMISSION, () -> {
                floorService.deleteFloor(companies.getCompanyName(), floor.getId(), details);
            });
        }
    }

    @Nested
    @DisplayName("해당 회사 권한 없음 예외 케이스")
    class CompanyNotPermissionExceptionCase {
        // given
        Companies companies = Company_생성();
        Companies different = Different_Company_생성();
        Floor floor = Floor_생성_아이디_지정(1L);
        UserDetailsImpl details = details_권한_ADMIN_유저_네임_NULL(companies);

        public static void COMPANIES_NOT_PERMISSION_EXCEPTION(SpaceErrorCode expectedErrorCode, Executable executable) {
            SpaceException exception = assertThrows(SpaceException.class, executable);
            assertEquals(expectedErrorCode, exception.getErrorCode());
        }


        @Test
        void Floor_전체_조회_해당_회사_권한_없음() {
            // when & then
            CompanyNotPermissionExceptionCase.COMPANIES_NOT_PERMISSION_EXCEPTION(SpaceErrorCode.NOT_HAVE_PERMISSION_COMPANIES, () -> {
                floorService.getFloor(different.getCompanyName(), details);
            });
        }
    }

    @Nested
    @DisplayName("회사 이름 으로 찾을 수 없는 경우")
    class findByCompanyName {
        // given
        Companies companies = Company_생성();
        FloorRequestDto requestDto = new FloorRequestDto("floorTest");
        UserDetailsImpl details = details_권한_ADMIN_유저_네임_NULL(companies);

        public static void COMPANIES_NOT_FOUND_EXCEPTION(SpaceErrorCode expectedErrorCode, Executable executable) {
            SpaceException exception = assertThrows(SpaceException.class, executable);
            assertEquals(expectedErrorCode, exception.getErrorCode());
        }

        @Test
        void Floor_생성_해당_회사_없음() {
            //when,then
            findByCompanyName.COMPANIES_NOT_FOUND_EXCEPTION(SpaceErrorCode.COMPANIES_NOT_FOUND, () -> {
                floorService.createFloor(companies.getCompanyName(), requestDto, details);
            });
        }


        @Nested
        @DisplayName("회사 이름과 공간 ID로 층을 찾을 수 없는 경우")
        class findCompanyNameAndFloorId {
            Companies companies = Company_생성();
            Space space = Space_생성_아이디_지정(1L);

            @Test
            void 해당_회사_없음() {
                //given
                when(companyRepository.findByCompanyName(companies.getCompanyName())).thenReturn(Optional.empty());

                //When,Then
                SpaceException exception = assertThrows(SpaceException.class, () -> {
                    floorService.findCompanyNameAndFloorId(companies.getCompanyName(), space.getId());
                });
                assertEquals(SpaceErrorCode.COMPANIES_NOT_FOUND, exception.getErrorCode());
            }

            @Test
            void 해당_회사_아이디_없음() {
                //given
                when(companyRepository.findByCompanyName(companies.getCompanyName())).thenReturn(Optional.of(companies));
                when(floorRepository.findByIdAndCompanies(space.getId(), companies)).thenReturn(Optional.empty());

                //when,Then
                SpaceException exception = assertThrows(SpaceException.class, () -> {
                    floorService.findCompanyNameAndFloorId(companies.getCompanyName(), space.getId());
                });
                assertEquals(SpaceErrorCode.SPACE_NOT_FOUND, exception.getErrorCode());
            }
        }
    }
}





