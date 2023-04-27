package com.example.chillisauce.spaces.service;

import com.example.chillisauce.security.UserDetailsImpl;
import com.example.chillisauce.spaces.dto.FloorRequestDto;
import com.example.chillisauce.spaces.dto.FloorResponseDto;
import com.example.chillisauce.spaces.entity.Floor;
import com.example.chillisauce.spaces.entity.Mr;
import com.example.chillisauce.spaces.entity.Space;
import com.example.chillisauce.spaces.exception.SpaceErrorCode;
import com.example.chillisauce.spaces.exception.SpaceException;
import com.example.chillisauce.spaces.repository.FloorRepository;
import com.example.chillisauce.spaces.repository.MrRepository;
import com.example.chillisauce.users.entity.Companies;
import com.example.chillisauce.users.entity.User;
import com.example.chillisauce.users.entity.UserRoleEnum;
import com.example.chillisauce.users.repository.CompanyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.junit.jupiter.api.function.Executable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

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
    private MrRepository mrRepository;
    private Floor floor;

    private Companies companies;

    private UserDetailsImpl details;

    @BeforeEach
    void setup() {
        floor = Floor.builder()
                .floorName("testFloor")
                .build();
        companies = Companies.builder()
                .companyName("testCompany")
                .build();
        User user = User.builder()
                .role(UserRoleEnum.ADMIN)
                .companies(companies)
                .build();
        details = new UserDetailsImpl(user, null);
    }

    @Nested
    @DisplayName("성공케이스")
    class SuccessCase {
        @Test
        void Floor_생성() {
            //given
            String companyName = "testCompany";

            FloorRequestDto floorRequestDto = new FloorRequestDto("testFloor");
            when(floorRepository.save(any(Floor.class))).thenReturn(floor);
            when(companyRepository.findByCompanyName(companyName)).thenReturn(Optional.of(Companies.builder().build()));

            //When
            FloorResponseDto floorResponseDto = floorService.createFloor(companyName, floorRequestDto, details);

            //Then
            assertNotNull(floorResponseDto);
            assertEquals("testFloor", floorResponseDto.getFloorName());

        }

        @Test
        void Floor_선택_조회() {
            //given
            String companyName = "testCompany";
            Long floorId = 1L;

            when(companyRepository.findByCompanyName(companyName)).thenReturn(Optional.of(Companies.builder().build()));
            when(floorRepository.findByIdAndCompanies(anyLong(), any(Companies.class))).thenReturn(Optional.of(floor));
            FloorResponseDto floorResponseDto = new FloorResponseDto(1L, "testFloor");

            //When
            List<FloorResponseDto> result = floorService.getFloorlist(companyName, floorId, details);

            //Then
            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals("testFloor", floorResponseDto.getFloorName());
        }

        @Test
        void Floor_전체_조회() {
            //given
            String companyName = "testCompany";
            List<Floor> floorList = Collections.singletonList(floor);
            List<FloorResponseDto> responseDto = floorList.stream()
                    .map(FloorResponseDto::new)
                    .toList();
            when(companyRepository.findByCompanyName(companyName)).thenReturn(Optional.of(companies));
            when(floorRepository.findAllByCompaniesId(any())).thenReturn(floorList);

            //when
            List<FloorResponseDto> result = floorService.getFloor(companyName, details);

            //Then
            assertNotNull(result);
            assertEquals(result.size(), responseDto.size());
            assertThat(responseDto).allSatisfy(responseSpace -> {
                assertThat(responseSpace.getFloorName()).isEqualTo("testFloor");
            });
        }

        @Test
        void Floor_수정() {
            String companyName = "testFloor";
            Long floorId = 1L;

            when(companyRepository.findByCompanyName(companyName)).thenReturn(Optional.of(companies));
            when(floorRepository.findByIdAndCompanies(anyLong(), any(Companies.class))).thenReturn(Optional.of(floor));
            FloorRequestDto floorRequestDto = new FloorRequestDto("플로워 수정");

            //when
            FloorResponseDto floorResponseDto = floorService.updateFloor(companyName, floorId, floorRequestDto, details);

            //Then
            assertNotNull(floorResponseDto);
            assertEquals("플로워 수정", floorResponseDto.getFloorName());
        }

        @Test
        void Floor_삭제() {
            String companyName = "testFloor";
            Long floorId = 1L;
            List<Mr> mrList = new ArrayList<>();

            when(companyRepository.findByCompanyName(companyName)).thenReturn(Optional.of(Companies.builder().build()));
            when(floorRepository.findByIdAndCompanies(anyLong(), any(Companies.class))).thenReturn(Optional.of(floor));
            doNothing().when(floorRepository).delete(floor);
            doNothing().when(mrRepository).deleteAll(mrList);
            //when
            FloorResponseDto floorResponseDto = floorService.deleteFloor(companyName, floorId, details);

            //then
            assertNotNull(floorResponseDto);
            assertEquals("testFloor", floorResponseDto.getFloorName());
        }
    }

    @Nested
    @DisplayName("플로우 권한 없음 예외 케이스")
    class NotPermissionExceptionCase {
        // given
        String companyName = "testCompany";
        Long floorId = 1L;
        UserDetailsImpl details = new UserDetailsImpl(User.builder().role(UserRoleEnum.USER).build(), "test");
        FloorRequestDto requestDto = new FloorRequestDto("floorTest");

        public static void NOT_HAVE_PERMISSION_EXCEPTION(SpaceErrorCode expectedErrorCode, Executable executable) {
            SpaceException exception = assertThrows(SpaceException.class, executable);
            assertEquals(expectedErrorCode, exception.getErrorCode());
        }

        @Test
        void Floor_생성_권한_예외_테스트() {
            // when & then
            NotPermissionExceptionCase.NOT_HAVE_PERMISSION_EXCEPTION(SpaceErrorCode.NOT_HAVE_PERMISSION, () -> {
                floorService.createFloor(companyName, requestDto, details);
            });
        }

        @Test
        void Floor_수정_권한_예외_테스트() {
            // when & then
            NotPermissionExceptionCase.NOT_HAVE_PERMISSION_EXCEPTION(SpaceErrorCode.NOT_HAVE_PERMISSION, () -> {
                floorService.updateFloor(companyName, floorId, requestDto, details);
            });
        }

        @Test
        void Floor_삭제_권한_예외_테스트() {
            // when & then
            NotPermissionExceptionCase.NOT_HAVE_PERMISSION_EXCEPTION(SpaceErrorCode.NOT_HAVE_PERMISSION, () -> {
                floorService.deleteFloor(companyName, floorId, details);
            });
        }
    }

    @Nested
    @DisplayName("해당 회사 권한 없음 예외 케이스")
    class CompanyNotPermissionExceptionCase {
        // given
        String companyName = "missingCompany";

        String differentCompanyName = "differentCompany";
        Long floorId = 1L;
        Companies companies = Companies.builder()
                .companyName(differentCompanyName)
                .build();
        User user = User.builder()
                .role(UserRoleEnum.ADMIN)
                .companies(companies)
                .build();

        UserDetailsImpl details = new UserDetailsImpl(user, null);

        public static void COMPANIES_NOT_PERMISSION_EXCEPTION(SpaceErrorCode expectedErrorCode, Executable executable) {
            SpaceException exception = assertThrows(SpaceException.class, executable);
            assertEquals(expectedErrorCode, exception.getErrorCode());
        }
        @Test
        void Floor_선택_조회_해당_회사_권한_없음() {
            // when & then
            CompanyNotPermissionExceptionCase.COMPANIES_NOT_PERMISSION_EXCEPTION(SpaceErrorCode.NOT_HAVE_PERMISSION_COMPANIES, () -> {
                floorService.getFloorlist(companyName, floorId, details);
            });
        }

        @Test
        void Floor_전체_조회_해당_회사_권한_없음() {
            // when & then
            CompanyNotPermissionExceptionCase.COMPANIES_NOT_PERMISSION_EXCEPTION(SpaceErrorCode.NOT_HAVE_PERMISSION_COMPANIES, () -> {
                floorService.getFloor(companyName, details);
            });
        }
    }

    @Nested
    @DisplayName("회사 이름 으로 찾을 수 없는 경우")
    class findByCompanyName {
        // given
        String companyName = "testCompany";

        FloorRequestDto requestDto = new FloorRequestDto("floorTest");


        public static void COMPANIES_NOT_FOUND_EXCEPTION(SpaceErrorCode expectedErrorCode, Executable executable) {
            SpaceException exception = assertThrows(SpaceException.class, executable);
            assertEquals(expectedErrorCode, exception.getErrorCode());
        }

        @Test
        void Floor_생성_해당_회사_없음() {
            //when,then
            findByCompanyName.COMPANIES_NOT_FOUND_EXCEPTION(SpaceErrorCode.COMPANIES_NOT_FOUND, () -> {
                floorService.createFloor(companyName,requestDto, details);
            });
        }


        @Test
        void Floor_전체_조회_해당_회사_없음() {
            //when,then
            findByCompanyName.COMPANIES_NOT_FOUND_EXCEPTION(SpaceErrorCode.COMPANIES_NOT_FOUND, () -> {
                floorService.getFloor(companyName, details);
            });
        }
    }

    @Nested
    @DisplayName("회사 이름과 공간 ID로 층을 찾을 수 없는 경우")
    class findCompanyNameAndFloorId {
        @Test
        void 해당_회사_없음() {
            //given
            String companyName = "testCompany";
            Long spaceId = 1L;
            when(companyRepository.findByCompanyName(companyName)).thenReturn(Optional.empty());

            //When,Then
            SpaceException exception = assertThrows(SpaceException.class, () -> {
                floorService.findCompanyNameAndFloorId(companyName, spaceId);
            });
            assertEquals(SpaceErrorCode.COMPANIES_NOT_FOUND, exception.getErrorCode());
        }

        @Test
        void 해당_회사_아이디_없음() {
            //given
            String companyName = "testCompany";
            Long spaceId = 1L;
            Companies company = Companies.builder()
                    .companyName("testCompany")
                    .build();

            when(companyRepository.findByCompanyName(companyName)).thenReturn(Optional.of(company));
            when(floorRepository.findByIdAndCompanies(spaceId, company)).thenReturn(Optional.empty());

            //when,Then
            SpaceException exception = assertThrows(SpaceException.class, () -> {
                floorService.findCompanyNameAndFloorId(companyName, spaceId);
            });
            assertEquals(SpaceErrorCode.SPACE_NOT_FOUND, exception.getErrorCode());
        }
    }
}




