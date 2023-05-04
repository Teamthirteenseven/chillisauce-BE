package com.example.chillisauce.spaces.service;

import com.example.chillisauce.security.UserDetailsImpl;
import com.example.chillisauce.spaces.dto.BoxRequestDto;
import com.example.chillisauce.spaces.dto.BoxResponseDto;
import com.example.chillisauce.spaces.entity.Box;
import com.example.chillisauce.spaces.entity.Space;
import com.example.chillisauce.spaces.exception.SpaceErrorCode;
import com.example.chillisauce.spaces.exception.SpaceException;
import com.example.chillisauce.spaces.repository.BoxRepository;
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

import static com.example.chillisauce.fixture.SpaceFixtureFactory.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class BoxServiceTest {

    @Mock
    private CompanyRepository companyRepository;
    @Mock
    private BoxRepository boxRepository;
    @Mock
    private SpaceService spaceService;
    @InjectMocks
    private BoxService boxService;

    @Nested
    @DisplayName("성공케이스")
    class SuccessCase {
        Companies companies = Company_생성();
        Space space = Space_생성_아이디_지정(1L);
        UserDetailsImpl details = details_권한_ADMIN_유저_네임_NULL(companies);
        Box box = Box_생성();
        @Test
        void 박스_생성() {
            //given
            BoxRequestDto boxRequestDto = new BoxRequestDto("이민재자리", "777", "777");
            when(spaceService.findCompanyNameAndSpaceId(companies.getCompanyName(), space.getId())).thenReturn(space);
            when(boxRepository.save(any(Box.class))).thenReturn(box);


            BoxResponseDto boxResponseDto = boxService.createBox(companies.getCompanyName(), space.getId(), boxRequestDto, details);

            //then
            assertNotNull(boxResponseDto);
            assertEquals("이민재자리", boxResponseDto.getBoxName());
            assertEquals("777", boxResponseDto.getX());
            assertEquals("777", boxResponseDto.getY());
        }

        @Test
        void Box_수정() {
            //given
            when(companyRepository.findByCompanyName(companies.getCompanyName())).thenReturn(Optional.of(companies));
            when(boxRepository.findByIdAndSpaceCompanies(box.getId(), companies)).thenReturn(Optional.of(box));
            BoxRequestDto boxRequestDto = new BoxRequestDto("testBox", "500", "999");

            //when
            BoxResponseDto boxResponseDto = boxService.updateBox(companies.getCompanyName(), box.getId(), boxRequestDto, details);

            //then
            assertNotNull(boxResponseDto);
            assertEquals("testBox", boxResponseDto.getBoxName());
            assertEquals("500", boxResponseDto.getX());
            assertEquals("999", boxResponseDto.getY());
        }

        @Test
        void Box_삭제() {
            //given

            when(companyRepository.findByCompanyName(companies.getCompanyName())).thenReturn(Optional.of(companies));
            when(boxRepository.findByIdAndSpaceCompanies(box.getId(), companies)).thenReturn(Optional.of(box));
            doNothing().when(boxRepository).deleteById(box.getId());

            //when
            BoxResponseDto boxResponseDto = boxService.deleteBox(companies.getCompanyName(), box.getId(), details);
            //then
            assertNotNull(boxResponseDto);
            assertEquals("testBox", boxResponseDto.getBoxName());
            assertEquals("777", boxResponseDto.getX());
            assertEquals("777", boxResponseDto.getY());
        }



    @Nested
    @DisplayName("Box 권한 없음 예외 케이스")
    class NotPermissionExceptionCase {
        // given
        UserDetailsImpl details = details_권한_USER(companies);
        BoxRequestDto requestDto = new BoxRequestDto("BoxTest", "200", "300");
        public static void NOT_HAVE_PERMISSION_EXCEPTION(SpaceErrorCode expectedErrorCode, Executable executable) {
            SpaceException exception = assertThrows(SpaceException.class, executable);
            assertEquals(expectedErrorCode, exception.getErrorCode());
        }

        @Test
        void Box_생성_권한_예외_테스트() {
            // when & then
            NotPermissionExceptionCase.NOT_HAVE_PERMISSION_EXCEPTION(SpaceErrorCode.NOT_HAVE_PERMISSION, () -> {
                boxService.createBox(companies.getCompanyName(), space.getId(), requestDto, details);
            });
        }

        @Test
        void Box_수정_권한_예외_테스트() {
            // when & then
            NotPermissionExceptionCase.NOT_HAVE_PERMISSION_EXCEPTION(SpaceErrorCode.NOT_HAVE_PERMISSION, () -> {
                boxService.updateBox(companies.getCompanyName(), box.getId(), requestDto, details);
            });
        }

        @Test
        void Box_삭제_권한_예외_테스트() {
            // when & then
            NotPermissionExceptionCase.NOT_HAVE_PERMISSION_EXCEPTION(SpaceErrorCode.NOT_HAVE_PERMISSION, () -> {
                boxService.deleteBox(companies.getCompanyName(), box.getId(), details);
            });
        }
    }

        @Nested
        @DisplayName("Box 메서드 예외 케이스")
        class MethodExceptionCase {
            @Test
            void 해당_회사_없음() {
                //given
                when(companyRepository.findByCompanyName(companies.getCompanyName())).thenReturn(Optional.empty());

                //When,Then
                SpaceException exception = assertThrows(SpaceException.class, () -> {
                    boxService.findCompanyNameAndBoxId(companies.getCompanyName(), box.getId());
                });
                assertEquals(SpaceErrorCode.COMPANIES_NOT_FOUND, exception.getErrorCode());
            }

            @Test
            void 해당_회사_아이디_없음() {
                //given

                when(companyRepository.findByCompanyName(companies.getCompanyName())).thenReturn(Optional.of(companies));
                when(boxRepository.findByIdAndSpaceCompanies(box.getId(), companies)).thenReturn(Optional.empty());

                //when,Then
                SpaceException exception = assertThrows(SpaceException.class, () -> {
                    boxService.findCompanyNameAndBoxId(companies.getCompanyName(), box.getId());
                });
                assertEquals(SpaceErrorCode.BOX_NOT_FOUND, exception.getErrorCode());
            }
        }
    }
}






