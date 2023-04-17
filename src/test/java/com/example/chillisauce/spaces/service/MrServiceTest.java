package com.example.chillisauce.spaces.service;

import com.example.chillisauce.security.UserDetailsImpl;
import com.example.chillisauce.spaces.dto.MrRequestDto;
import com.example.chillisauce.spaces.dto.MrResponseDto;
import com.example.chillisauce.spaces.entity.Mr;
import com.example.chillisauce.spaces.entity.Space;
import com.example.chillisauce.spaces.exception.SpaceErrorCode;
import com.example.chillisauce.spaces.exception.SpaceException;
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
public class MrServiceTest {

    @Mock
    private CompanyRepository companyRepository;
    @Mock
    private MrRepository mrRepository;
    @InjectMocks
    private MrService mrService;
    @Mock
    private SpaceService spaceService;

    private Companies companies;

    private UserDetailsImpl details;

    private Space space;
    private Mr mr;


    @BeforeEach
    void setup() {
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
        mr = Mr.builder() //삭제시 내용 필요 ,생성 및 수정에는 request 를 명시하기 때문에 .build 로 바로 닫아도 됨
                .mrName("MrTest")
                .x("200")
                .y("300")
                .build();
    }

    @Nested
    @DisplayName("Mr 성공 케이스")
    class SuccessCase {

        @Test
        void Mr_생성() {
            //given
            String companyName = "testCompany";
            Long spaceId = 1L;

            MrRequestDto requestDto = new MrRequestDto("MrTest", "200","300");
            when(spaceService.findCompanyNameAndSpaceId(companyName,spaceId)).thenReturn(space);
            when(mrRepository.save(any(Mr.class))).thenReturn(mr);

            //when
            MrResponseDto mrResponseDto = mrService.createMr(companyName,spaceId,requestDto,details);

            //then
            assertNotNull(mrResponseDto);
            assertEquals("MrTest",mrResponseDto.getMrName());
            assertEquals("200",mrResponseDto.getX());
            assertEquals("300",mrResponseDto.getY());

        }

        @Test
        void Mr_수정() {
            //given
            String companyName = "testCompany";
            Long mrId = 1L;

            when(companyRepository.findByCompanyName(companyName)).thenReturn(Optional.of(companies));
            when(mrRepository.findByIdAndSpaceCompanies(mrId,companies)).thenReturn(Optional.of(mr));
            MrRequestDto requestDto = new MrRequestDto("MrTest", "200","300");

            //when
            MrResponseDto mrResponseDto = mrService.updateMr(companyName,mrId,requestDto,details);

            //Then
            assertNotNull(mrResponseDto);
            assertEquals("MrTest",mrResponseDto.getMrName());
            assertEquals("200",mrResponseDto.getX());
            assertEquals("300",mrResponseDto.getY());
        }

        @Test
        void Mr_삭제() {
            //given
            String companyName = "testCompany";
            Long mrId = 1L;

            when(companyRepository.findByCompanyName(companyName)).thenReturn(Optional.of(companies));
            when(mrRepository.findByIdAndSpaceCompanies(mrId,companies)).thenReturn(Optional.of(mr));
            doNothing().when(mrRepository).deleteById(mrId);

            //when
            MrResponseDto mrResponseDto = mrService.deleteMr(companyName,mrId,details);

            //Then
            assertNotNull(mrResponseDto);
            assertEquals("MrTest",mrResponseDto.getMrName());
            assertEquals("200",mrResponseDto.getX());
            assertEquals("300",mrResponseDto.getY());
        }
    }

    @Nested
    @DisplayName("Mr 권한 없음 예외 케이스")
    class NotPermissionExceptionCase {
        // given
        String companyName = "testCompany";
        Long spaceId = 1L;
        UserDetailsImpl details = new UserDetailsImpl(User.builder().role(UserRoleEnum.USER).build(), "test");
        MrRequestDto requestDto = new MrRequestDto("MrTest", "200", "300");

        public static void NOT_HAVE_PERMISSION_EXCEPTION(SpaceErrorCode expectedErrorCode, Executable executable) {
            SpaceException exception = assertThrows(SpaceException.class, executable);
            assertEquals(expectedErrorCode, exception.getErrorCode());
        }

        @Test
        void Mr_생성_권한_예외_테스트() {
            // when & then
            NotPermissionExceptionCase.NOT_HAVE_PERMISSION_EXCEPTION(SpaceErrorCode.NOT_HAVE_PERMISSION, () -> {
                mrService.createMr(companyName, spaceId, requestDto, details);
            });
        }

        @Test
        void Mr_수정_권한_예외_테스트() {
            // when & then
            NotPermissionExceptionCase.NOT_HAVE_PERMISSION_EXCEPTION(SpaceErrorCode.NOT_HAVE_PERMISSION, () -> {
                mrService.updateMr(companyName, spaceId, requestDto, details);
            });
        }

        @Test
        void Mr_삭제_권한_예외_테스트() {
            // when & then
            NotPermissionExceptionCase.NOT_HAVE_PERMISSION_EXCEPTION(SpaceErrorCode.NOT_HAVE_PERMISSION, () -> {
                mrService.deleteMr(companyName, spaceId, details);
            });
        }
    }
    @Nested
    @DisplayName("Mr 메서드 예외 케이스")
    class MethodExceptionCase {
        @Test
        void 해당_회사_없음() {
            //given
            String companyName = "testCompany";
            Long mrId = 1L;
            when(companyRepository.findByCompanyName(companyName)).thenReturn(Optional.empty());

            //When,Then
            SpaceException exception = assertThrows(SpaceException.class, () -> {
                mrService.findCompanyNameAndMrId(companyName, mrId);
            });
            assertEquals(SpaceErrorCode.COMPANIES_NOT_FOUND, exception.getErrorCode());
        }

        @Test
        void 해당_회사_아이디_없음() {
            //given
            String companyName = "testCompany";
            Long mrId = 1L;
            Companies companies = Companies.builder()
                    .companyName("testCompany")
                    .build();

            when(companyRepository.findByCompanyName(companyName)).thenReturn(Optional.of(companies));
            when(mrRepository.findByIdAndSpaceCompanies(mrId, companies)).thenReturn(Optional.empty());

            //when,Then
            SpaceException exception = assertThrows(SpaceException.class, () -> {
                mrService.findCompanyNameAndMrId(companyName, mrId);
            });
            assertEquals(SpaceErrorCode.MR_NOT_FOUND, exception.getErrorCode());
        }
    }
}

