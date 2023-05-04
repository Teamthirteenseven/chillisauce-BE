package com.example.chillisauce.spaces.service;

import com.example.chillisauce.security.UserDetailsImpl;
import com.example.chillisauce.spaces.dto.MultiBoxRequestDto;
import com.example.chillisauce.spaces.dto.MultiBoxResponseDto;
import com.example.chillisauce.spaces.entity.MultiBox;
import com.example.chillisauce.spaces.entity.Space;
import com.example.chillisauce.spaces.exception.SpaceErrorCode;
import com.example.chillisauce.spaces.exception.SpaceException;
import com.example.chillisauce.spaces.repository.MultiBoxRepository;
import com.example.chillisauce.users.entity.Companies;
import com.example.chillisauce.users.entity.User;
import com.example.chillisauce.users.entity.UserRoleEnum;
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
import static com.example.chillisauce.fixture.SpaceFixtureFactory.*;
import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
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


    @Nested
    @DisplayName("성공케이스")
    class SuccessCase {
        Companies companies = Company_생성();
        Space space = Space_생성_아이디_지정(1L);
        UserDetailsImpl details = details_권한_ADMIN_유저_네임_NULL(companies);
        MultiBox multiBox = MultiBox_생성();
        @Test
        void 멀티박스_생성() {
            //given
            MultiBoxRequestDto multiBoxRequestDto = new MultiBoxRequestDto("testMultiBox", "222" , "200");
            when(spaceService.findCompanyNameAndSpaceId(companies.getCompanyName(), space.getId())).thenReturn(space);
            when(multiBoxRepository.save(any(MultiBox.class))).thenReturn(multiBox);

            //when
            MultiBoxResponseDto multiBoxResponseDto = multiBoxService.createMultiBox(companies.getCompanyName(), space.getId(), multiBoxRequestDto, details);

            //then
            assertNotNull(multiBoxResponseDto);
            assertEquals("testMultiBox", multiBoxResponseDto.getMultiBoxName());
            assertEquals("222", multiBoxResponseDto.getX());
            assertEquals("200", multiBoxResponseDto.getY());

        }
        @Test
        void MultiBox_수정() {
            //given

            when(companyRepository.findByCompanyName(companies.getCompanyName())).thenReturn(Optional.of(companies));
            when(multiBoxRepository.findByIdAndSpaceCompanies(multiBox.getId(), companies)).thenReturn(Optional.of(multiBox));
            MultiBoxRequestDto multiBoxRequestDto = new MultiBoxRequestDto("MultiBoxTest", "777", "888");
            //when
            MultiBoxResponseDto multiBoxResponseDto = multiBoxService.updateMultiBox(companies.getCompanyName(),multiBox.getId(),multiBoxRequestDto,details);

            //Then
            assertNotNull(multiBoxResponseDto);
            assertEquals("MultiBoxTest",multiBoxResponseDto.getMultiBoxName());
            assertEquals("777",multiBoxResponseDto.getX());
            assertEquals("888",multiBoxResponseDto.getY());
        }

        @Test
        void MultiBox_삭제() {
            //given

            when(companyRepository.findByCompanyName(companies.getCompanyName())).thenReturn(Optional.of(companies));
            when(multiBoxRepository.findByIdAndSpaceCompanies(multiBox.getId(), companies)).thenReturn(Optional.of(multiBox));
            doNothing().when(multiBoxRepository).deleteById(multiBox.getId());

            //when
            MultiBoxResponseDto multiBoxResponseDto = multiBoxService.deleteMultiBox(companies.getCompanyName(), multiBox.getId(), details);

            //Then
            assertNotNull(multiBoxResponseDto);
            assertEquals("testMultiBox",multiBoxResponseDto.getMultiBoxName());
            assertEquals("222",multiBoxResponseDto.getX());
            assertEquals("200",multiBoxResponseDto.getY());
        }
    }
    @Nested
    @DisplayName("권한 없음 예외 케이스")
    class MultiBox_NotPermissionExceptionCase {
        // given
        Companies companies = Company_생성();
        Space space = Space_생성_아이디_지정(1L);
        MultiBox multiBox = MultiBox_생성();
        UserDetailsImpl details = details_권한_USER(companies);
        MultiBoxRequestDto multiBoxRequestDto = new MultiBoxRequestDto("MultiBoxTest", "777", "888");

        public static void NOT_HAVE_PERMISSION_EXCEPTION(SpaceErrorCode expectedErrorCode, Executable executable) {
            SpaceException exception = assertThrows(SpaceException.class, executable);
            assertEquals(expectedErrorCode, exception.getErrorCode());
        }
        @Test
        void MultiBox_생성_권한_예외_테스트() {
            // when & then
            MultiBox_NotPermissionExceptionCase.NOT_HAVE_PERMISSION_EXCEPTION(SpaceErrorCode.NOT_HAVE_PERMISSION, () -> {
                multiBoxService.createMultiBox(companies.getCompanyName(), space.getId(), multiBoxRequestDto, details);
            });
        }

        @Test
        void MultiBox_수정_권한_예외_테스트() {
            // when & then
            MultiBox_NotPermissionExceptionCase.NOT_HAVE_PERMISSION_EXCEPTION(SpaceErrorCode.NOT_HAVE_PERMISSION, () -> {
                multiBoxService.updateMultiBox(companies.getCompanyName(), multiBox.getId(), multiBoxRequestDto, details);
            });
        }

        @Test
        void MultiBox_삭제_권한_예외_테스트() {
            // when & then
            MultiBox_NotPermissionExceptionCase.NOT_HAVE_PERMISSION_EXCEPTION(SpaceErrorCode.NOT_HAVE_PERMISSION, () -> {
                multiBoxService.deleteMultiBox(companies.getCompanyName(), multiBox.getId(), details);
            });
        }
    }
    @Nested
    @DisplayName("메서드 예외 케이스")
    class MethodExceptionCase {
        Companies companies = Company_생성();
        MultiBox multiBox = MultiBox_생성();
        @Test
        void 해당_회사_없음() {
            //given
            when(companyRepository.findByCompanyName(companies.getCompanyName())).thenReturn(Optional.empty());

            //When,Then
            SpaceException exception = assertThrows(SpaceException.class, () -> {
                multiBoxService.findCompanyNameAndMultiBoxId(companies.getCompanyName(), multiBox.getId());
            });
            assertEquals(SpaceErrorCode.COMPANIES_NOT_FOUND, exception.getErrorCode());
        }

        @Test
        void 해당_회사_아이디_없음() {
            //given
            when(companyRepository.findByCompanyName(companies.getCompanyName())).thenReturn(Optional.of(companies));
            when(multiBoxRepository.findByIdAndSpaceCompanies(multiBox.getId(), companies)).thenReturn(Optional.empty());

            //when,Then
            SpaceException exception = assertThrows(SpaceException.class, () -> {
                multiBoxService.findCompanyNameAndMultiBoxId(companies.getCompanyName(), multiBox.getId());
            });
            assertEquals(SpaceErrorCode.SPACE_NOT_FOUND, exception.getErrorCode());
        }
    }
}
