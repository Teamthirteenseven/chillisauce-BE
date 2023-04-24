package com.example.chillisauce.spaces.service;

import com.example.chillisauce.security.UserDetailsImpl;
import com.example.chillisauce.spaces.dto.MrRequestDto;
import com.example.chillisauce.spaces.dto.MrResponseDto;
import com.example.chillisauce.spaces.dto.MultiBoxRequestDto;
import com.example.chillisauce.spaces.dto.MultiBoxResponseDto;
import com.example.chillisauce.spaces.entity.Floor;
import com.example.chillisauce.spaces.entity.Mr;
import com.example.chillisauce.spaces.entity.MultiBox;
import com.example.chillisauce.spaces.entity.Space;
import com.example.chillisauce.spaces.exception.SpaceErrorCode;
import com.example.chillisauce.spaces.exception.SpaceException;
import com.example.chillisauce.spaces.repository.MrRepository;
import com.example.chillisauce.spaces.repository.MultiBoxRepository;
import com.example.chillisauce.users.entity.Companies;
import com.example.chillisauce.users.entity.User;
import com.example.chillisauce.users.entity.UserRoleEnum;
import com.example.chillisauce.users.repository.CompanyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MultiBoxServiceTest {
    @Mock
    private CompanyRepository companyRepository;
    @Mock
    private MultiBoxRepository multiBoxRepository;
    @InjectMocks
    private MultiBoxService multiBoxService;
    @Mock
    private SpaceService spaceService;

    private Companies companies;

    private UserDetailsImpl details;

    private Space space;
    private MultiBox multiBox;

    @BeforeEach
    void setup() {
        Floor floor = Floor.builder()
                .floorName("testFloor")
                .build();
        companies = Companies.builder()
                .build();
        space = Space.builder()
                .spaceName("testSpace")
                .companies(companies)
                .build();
        User user = User.builder()
                .role(UserRoleEnum.ADMIN)
                .companies(companies)
                .build();
        details = new UserDetailsImpl(user, null);
        multiBox = MultiBox.builder() //삭제시 내용 필요 ,생성 및 수정에는 request 를 명시하기 때문에 .build 로 바로 닫아도 됨
                .locationName("MultiBoxTest")
                .x("777")
                .y("888")
                .build();
    }

    @Nested
    @DisplayName("성공케이스")
    class SuccessCase {

        @Test
        void MultiBox_생성() {
            //given
            String companyName = "testCompany";
            Long spaceId = 1L;

            MultiBoxRequestDto multiBoxRequestDto = new MultiBoxRequestDto("MultiBoxTest", "777", "888");
            when(spaceService.findCompanyNameAndSpaceId(companyName, spaceId)).thenReturn(space);
            when(multiBoxRepository.save(any(MultiBox.class))).thenReturn(multiBox);

            //when
            MultiBoxResponseDto multiBoxResponseDto = multiBoxService.createMultiBox(companyName, spaceId, multiBoxRequestDto, details);

            //then
            assertNotNull(multiBoxResponseDto);
            assertEquals("MultiBoxTest", multiBoxResponseDto.getMultiBoxName());
            assertEquals("777", multiBoxResponseDto.getX());
            assertEquals("888", multiBoxResponseDto.getY());

        }
        @Test
        void MultiBox_수정() {
            //given
            String companyName = "testCompany";
            Long multiBoxId = 1L;

            when(companyRepository.findByCompanyName(companyName)).thenReturn(Optional.of(companies));
            when(multiBoxRepository.findByIdAndSpaceCompanies(multiBoxId,companies)).thenReturn(Optional.of(multiBox));
            MultiBoxRequestDto multiBoxRequestDto = new MultiBoxRequestDto("MultiBoxTest", "777", "888");
            //when
            MultiBoxResponseDto multiBoxResponseDto = multiBoxService.updateMultiBox(companyName,multiBoxId,multiBoxRequestDto,details);

            //Then
            assertNotNull(multiBoxResponseDto);
            assertEquals("MultiBoxTest",multiBoxResponseDto.getMultiBoxName());
            assertEquals("777",multiBoxResponseDto.getX());
            assertEquals("888",multiBoxResponseDto.getY());
        }

        @Test
        void MultiBox_삭제() {
            //given
            String companyName = "testCompany";
            Long multiBoxId = 1L;

            when(companyRepository.findByCompanyName(companyName)).thenReturn(Optional.of(companies));
            when(multiBoxRepository.findByIdAndSpaceCompanies(multiBoxId,companies)).thenReturn(Optional.of(multiBox));
            doNothing().when(multiBoxRepository).deleteById(multiBoxId);

            //when
            MultiBoxResponseDto multiBoxResponseDto = multiBoxService.deleteMultiBox(companyName,multiBoxId,details);

            //Then
            assertNotNull(multiBoxResponseDto);
            assertEquals("MultiBoxTest",multiBoxResponseDto.getMultiBoxName());
            assertEquals("777",multiBoxResponseDto.getX());
            assertEquals("888",multiBoxResponseDto.getY());
        }
    }
    @Nested
    @DisplayName("권한 없음 예외 케이스")
    class MultiBox_NotPermissionExceptionCase {
        // given
        String companyName = "testCompany";
        Long spaceId = 1L;
        Long multiBoxId = 1L;
        UserDetailsImpl details = new UserDetailsImpl(User.builder().role(UserRoleEnum.USER).build(), "test");
        MultiBoxRequestDto multiBoxRequestDto = new MultiBoxRequestDto("MultiBoxTest", "777", "888");

        public static void NOT_HAVE_PERMISSION_EXCEPTION(SpaceErrorCode expectedErrorCode, Executable executable) {
            SpaceException exception = assertThrows(SpaceException.class, executable);
            assertEquals(expectedErrorCode, exception.getErrorCode());
        }
        @Test
        void MultiBox_생성_권한_예외_테스트() {
            // when & then
            MultiBox_NotPermissionExceptionCase.NOT_HAVE_PERMISSION_EXCEPTION(SpaceErrorCode.NOT_HAVE_PERMISSION, () -> {
                multiBoxService.createMultiBox(companyName, spaceId, multiBoxRequestDto, details);
            });
        }

        @Test
        void MultiBox_수정_권한_예외_테스트() {
            // when & then
            MultiBox_NotPermissionExceptionCase.NOT_HAVE_PERMISSION_EXCEPTION(SpaceErrorCode.NOT_HAVE_PERMISSION, () -> {
                multiBoxService.updateMultiBox(companyName, multiBoxId, multiBoxRequestDto, details);
            });
        }

        @Test
        void MultiBox_삭제_권한_예외_테스트() {
            // when & then
            MultiBox_NotPermissionExceptionCase.NOT_HAVE_PERMISSION_EXCEPTION(SpaceErrorCode.NOT_HAVE_PERMISSION, () -> {
                multiBoxService.deleteMultiBox(companyName, multiBoxId, details);
            });
        }
    }
    @Nested
    @DisplayName("메서드 예외 케이스")
    class MethodExceptionCase {
        @Test
        void 해당_회사_없음() {
            //given
            String companyName = "testCompany";
            Long multiBoxId = 1L;
            when(companyRepository.findByCompanyName(companyName)).thenReturn(Optional.empty());

            //When,Then
            SpaceException exception = assertThrows(SpaceException.class, () -> {
                multiBoxService.findCompanyNameAndMultiBoxId(companyName, multiBoxId);
            });
            assertEquals(SpaceErrorCode.COMPANIES_NOT_FOUND, exception.getErrorCode());
        }

        @Test
        void 해당_회사_아이디_없음() {
            //given
            String companyName = "testCompany";
            Long multiBoxId = 1L;
            Companies companies = Companies.builder()
                    .companyName("testCompany")
                    .build();

            when(companyRepository.findByCompanyName(companyName)).thenReturn(Optional.of(companies));
            when(multiBoxRepository.findByIdAndSpaceCompanies(multiBoxId, companies)).thenReturn(Optional.empty());

            //when,Then
            SpaceException exception = assertThrows(SpaceException.class, () -> {
                multiBoxService.findCompanyNameAndMultiBoxId(companyName, multiBoxId);
            });
            assertEquals(SpaceErrorCode.SPACE_NOT_FOUND, exception.getErrorCode());
        }
    }
}
