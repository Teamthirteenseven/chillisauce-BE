package com.example.chillisauce.spaces.service;

import com.example.chillisauce.security.UserDetailsImpl;
import com.example.chillisauce.spaces.entity.MultiBox;
import com.example.chillisauce.spaces.repository.BoxRepository;
import com.example.chillisauce.spaces.dto.BoxRequestDto;
import com.example.chillisauce.spaces.dto.BoxResponseDto;
import com.example.chillisauce.spaces.entity.Box;
import com.example.chillisauce.spaces.entity.Space;
import com.example.chillisauce.spaces.exception.SpaceErrorCode;
import com.example.chillisauce.spaces.exception.SpaceException;
import com.example.chillisauce.spaces.repository.MultiBoxRepository;
import com.example.chillisauce.users.entity.Companies;
import com.example.chillisauce.users.entity.User;
import com.example.chillisauce.users.entity.UserRoleEnum;
import com.example.chillisauce.users.repository.CompanyRepository;
import com.example.chillisauce.users.repository.UserRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;
import org.mockito.Mockito;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class BoxServiceTest {

    @Mock
    private CompanyRepository companyRepository;
    @Mock
    private BoxRepository boxRepository;
    @Mock
    private SpaceService spaceService;
    @Mock
    private MultiBoxRepository multiBoxRepository;
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private BoxService boxService;


    private Companies companies;

    private UserDetailsImpl details;

    private Box box;
    private Space space;
    private MultiBox multiBox;


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
        box = Box.builder()
                .boxName("testBox")
                .x("500")
                .y("999")
                .build();
        multiBox = MultiBox.builder().build();
    }

    @Nested
    @DisplayName("성공케이스")
    class SuccessCase {
        @Test
        void Box_생성() {
            //given
            String companyName = "호랑이";
            Long spaceId = 1L;
            BoxRequestDto boxRequestDto = new BoxRequestDto("이민재자리", "777", "777");
            when(spaceService.findCompanyNameAndSpaceId(companyName, spaceId)).thenReturn(space);
            when(boxRepository.save(any(Box.class))).thenReturn(box);


            BoxResponseDto boxResponseDto = boxService.createBox(companyName, spaceId, boxRequestDto, details);

            //then
            assertNotNull(boxResponseDto);
            assertEquals("이민재자리", boxResponseDto.getBoxName());
            assertEquals("777", boxResponseDto.getX());
            assertEquals("777", boxResponseDto.getY());
        }

        @Test
        void Box_수정() {
            //given
            String companyName = "호랑이";
            Long boxId = 1L;

            when(companyRepository.findByCompanyName(companyName)).thenReturn(Optional.of(companies));
            when(boxRepository.findByIdAndSpaceCompanies(boxId, companies)).thenReturn(Optional.of(box));
            BoxRequestDto boxRequestDto = new BoxRequestDto("testBox", "500", "999");

            //when
            BoxResponseDto boxResponseDto = boxService.updateBox(companyName, boxId, boxRequestDto, details);

            //then
            assertNotNull(boxResponseDto);
            assertEquals("testBox", boxResponseDto.getBoxName());
            assertEquals("500", boxResponseDto.getX());
            assertEquals("999", boxResponseDto.getY());
        }

        @Test
        void Box_삭제() {
            //given
            String companyName = "호랑이";
            Long boxId = 1L;

            when(companyRepository.findByCompanyName(companyName)).thenReturn(Optional.of(companies));
            when(boxRepository.findByIdAndSpaceCompanies(boxId, companies)).thenReturn(Optional.of(box));
            doNothing().when(boxRepository).deleteById(boxId);

            //when
            BoxResponseDto boxResponseDto = boxService.deleteBox(companyName, boxId, details);
            //then
            assertNotNull(boxResponseDto);
            assertEquals("testBox", boxResponseDto.getBoxName());
            assertEquals("500", boxResponseDto.getX());
            assertEquals("999", boxResponseDto.getY());
        }

        @Test
        void Box_isPresent_true_유저_등록_이동() {
            // given
            String companyName = "호랑이";
            Long toBoxId = 1L;
            Box toBox = Box.builder()
                    .id(toBoxId)
                    .boxName("to box")
                    .x("2")
                    .y("2")
                    .space(space)
                    .user(null)
                    .build();
            details = new UserDetailsImpl(User.builder().role(UserRoleEnum.USER).build(), "test");


            BoxRequestDto boxRequestDto = new BoxRequestDto("박스", "3", "3");
            when(userRepository.findById(details.getUser().getId())).thenReturn(Optional.of(details.getUser()));
            when(boxRepository.findFirstByUserId(details.getUser().getId())).thenReturn(Optional.of(box));
            when(multiBoxRepository.findFirstByUserId(details.getUser().getId())).thenReturn(Optional.of(multiBox));
            when(companyRepository.findByCompanyName(companyName)).thenReturn(Optional.of(companies));
            when(boxRepository.findByIdAndSpaceCompanies(toBoxId, companies)).thenReturn(Optional.of(toBox));
            // when
            BoxResponseDto boxResponseDto = boxService.moveBoxWithUser(companyName, toBoxId, boxRequestDto, details);

            // then
            assertEquals(boxResponseDto.getBoxName(), boxRequestDto.getBoxName());
            assertEquals(boxResponseDto.getX(), boxRequestDto.getX());
            assertEquals(boxResponseDto.getY(), boxRequestDto.getY());
            assertEquals(details.getUser(), toBox.getUser());
            assertEquals(details.getUser().getUsername(), toBox.getUsername());
        }

        @Test
        void multiBox_isPresent_true_유저_등록_이동() {
            //given
            String companyName = "호랑이";
            Long toBoxId = 1L;
            Box toBox = Box.builder()
                    .id(toBoxId)
                    .boxName("to box")
                    .x("2")
                    .y("2")
                    .space(space)
                    .user(null)
                    .build();
            details = new UserDetailsImpl(User.builder().role(UserRoleEnum.USER).build(), "test");
            BoxRequestDto boxRequestDto = new BoxRequestDto("박스", "3", "3");
            when(userRepository.findById(details.getUser().getId())).thenReturn(Optional.of(details.getUser()));
            when(multiBoxRepository.findFirstByUserId(details.getUser().getId())).thenReturn(Optional.of(multiBox));
            when(companyRepository.findByCompanyName(companyName)).thenReturn(Optional.of(companies));
            when(boxRepository.findByIdAndSpaceCompanies(toBoxId, companies)).thenReturn(Optional.of(toBox));

            //when
            BoxResponseDto boxResponseDto = boxService.moveBoxWithUser(companyName, toBoxId, boxRequestDto, details);
            //then
            assertNotNull(boxResponseDto);
            assertEquals(toBox.getBoxName(), boxResponseDto.getBoxName());
            assertEquals(boxRequestDto.getX(), boxResponseDto.getX());
            assertEquals(boxRequestDto.getY(), boxResponseDto.getY());
            assertNull(multiBox.getUser());
            assertNull(multiBox.getUsername());
            assertEquals(details.getUser(), toBox.getUser());
            assertEquals(details.getUser().getUsername(), toBox.getUsername());
            verify(multiBoxRepository, times(1)).findFirstByUserId(details.getUser().getId());
            verify(boxRepository, times(1)).findByIdAndSpaceCompanies(toBoxId, companies);
        }

        @Test
        void 모든_조건_false_유저_등록_이동() {
            //given
            String companyName = "호랑이";
            Long toBoxId = 1L;
            Box toBox = Box.builder()
                    .id(toBoxId)
                    .boxName("to box")
                    .x("2")
                    .y("2")
                    .space(space)
                    .user(null)
                    .build();
            details = new UserDetailsImpl(User.builder().role(UserRoleEnum.USER).build(), "test");
            BoxRequestDto boxRequestDto = new BoxRequestDto("박스", "3", "3");
            when(userRepository.findById(details.getUser().getId())).thenReturn(Optional.of(details.getUser()));
            when(companyRepository.findByCompanyName(companyName)).thenReturn(Optional.of(companies));
            when(boxRepository.findByIdAndSpaceCompanies(toBoxId, companies)).thenReturn(Optional.of(toBox));

            //when
            BoxResponseDto boxResponseDto = boxService.moveBoxWithUser(companyName, toBoxId, boxRequestDto, details);
            //then
            assertNotNull(boxResponseDto);
            assertEquals(toBox.getBoxName(), boxResponseDto.getBoxName());
            assertEquals(boxRequestDto.getX(), boxResponseDto.getX());
            assertEquals(boxRequestDto.getY(), boxResponseDto.getY());
            assertEquals(details.getUser(), toBox.getUser());
            assertEquals(details.getUser().getUsername(), toBox.getUsername());
            verify(boxRepository, times(1)).findByIdAndSpaceCompanies(toBoxId, companies);
            verify(boxRepository, times(1)).save(toBox);
        }
    }

    @Nested
    @DisplayName("Box 권한 없음 예외 케이스")
    class NotPermissionExceptionCase {
        // given
        String companyName = "호랑이";
        Long spaceId = 1L;
        Long boxId = 1L;
        UserDetailsImpl details = new UserDetailsImpl(User.builder().role(UserRoleEnum.USER).build(), "test");
        BoxRequestDto requestDto = new BoxRequestDto("BoxTest", "200", "300");

        public static void NOT_HAVE_PERMISSION_EXCEPTION(SpaceErrorCode expectedErrorCode, Executable executable) {
            SpaceException exception = assertThrows(SpaceException.class, executable);
            assertEquals(expectedErrorCode, exception.getErrorCode());
        }

        @Test
        void Box_생성_권한_예외_테스트() {
            // when & then
            NotPermissionExceptionCase.NOT_HAVE_PERMISSION_EXCEPTION(SpaceErrorCode.NOT_HAVE_PERMISSION, () -> {
                boxService.createBox(companyName, spaceId, requestDto, details);
            });
        }

        @Test
        void Box_수정_권한_예외_테스트() {
            // when & then
            NotPermissionExceptionCase.NOT_HAVE_PERMISSION_EXCEPTION(SpaceErrorCode.NOT_HAVE_PERMISSION, () -> {
                boxService.updateBox(companyName, boxId, requestDto, details);
            });
        }

        @Test
        void Box_삭제_권한_예외_테스트() {
            // when & then
            NotPermissionExceptionCase.NOT_HAVE_PERMISSION_EXCEPTION(SpaceErrorCode.NOT_HAVE_PERMISSION, () -> {
                boxService.deleteBox(companyName, boxId, details);
            });
        }
    }

    @Nested
    @DisplayName("Box 메서드 예외 케이스")
    class MethodExceptionCase {
        @Test
        void 해당_회사_없음() {
            //given
            String companyName = "호랑이";
            Long boxId = 1L;
            when(companyRepository.findByCompanyName(companyName)).thenReturn(Optional.empty());

            //When,Then
            SpaceException exception = assertThrows(SpaceException.class, () -> {
                boxService.findCompanyNameAndBoxId(companyName, boxId);
            });
            assertEquals(SpaceErrorCode.COMPANIES_NOT_FOUND, exception.getErrorCode());
        }

        @Test
        void 해당_회사_아이디_없음() {
            //given
            String companyName = "testCompany";
            Long boxId = 1L;
            Companies companies = Companies.builder()
                    .companyName("testCompany")
                    .build();

            when(companyRepository.findByCompanyName(companyName)).thenReturn(Optional.of(companies));
            when(boxRepository.findByIdAndSpaceCompanies(boxId, companies)).thenReturn(Optional.empty());

            //when,Then
            SpaceException exception = assertThrows(SpaceException.class, () -> {
                boxService.findCompanyNameAndBoxId(companyName, boxId);
            });
            assertEquals(SpaceErrorCode.BOX_NOT_FOUND, exception.getErrorCode());
        }
    }

    @Nested
    @DisplayName("유저 이동 toBox NotNull")
    class moveBoxWithUser_toBoxNotNull {
        @Test
        void 유저_등록_이동_toBox_NotNull() {
            // given
            String companyName = "호랑이";
            Long toBoxId = 1L;
            BoxRequestDto boxRequestDto = new BoxRequestDto("박스", "3", "3");
            Box toBox = Box.builder()
                    .id(toBoxId)
                    .boxName("to box")
                    .x("2")
                    .y("2")
                    .space(space)
                    .user(User.builder().username("test").build())
                    .build();
            when(userRepository.findById(details.getUser().getId())).thenReturn(Optional.of(details.getUser()));
            when(boxRepository.findFirstByUserId(details.getUser().getId())).thenReturn(Optional.of(box));
            when(companyRepository.findByCompanyName(companyName)).thenReturn(Optional.of(companies));
            when(boxRepository.findByIdAndSpaceCompanies(toBoxId, companies)).thenReturn(Optional.of(toBox));

            // when, then
            SpaceException exception = assertThrows(SpaceException.class, () -> {
                boxService.moveBoxWithUser(companyName, toBoxId, boxRequestDto, details);
            });
            assertEquals(exception.getErrorCode(), SpaceErrorCode.BOX_ALREADY_IN_USER);
            verify(boxRepository, times(1)).findFirstByUserId(details.getUser().getId());
            verify(boxRepository, times(1)).findByIdAndSpaceCompanies(toBoxId, companies);
        }

        @Test
        void 유저_찾기_예외(){
            String companyName = "호랑이";
            Long toBoxId = 1L;
            BoxRequestDto boxRequestDto = new BoxRequestDto("박스", "3", "3");

            when(userRepository.findById(any())).thenReturn(Optional.empty());

            SpaceException exception = assertThrows(SpaceException.class, () -> {
                boxService.moveBoxWithUser(companyName, toBoxId, boxRequestDto, details);
            });
            assertEquals(exception.getErrorCode(), SpaceErrorCode.USER_NOT_FOUND);
        }
    }
}






