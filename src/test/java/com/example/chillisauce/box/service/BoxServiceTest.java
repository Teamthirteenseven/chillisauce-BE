package com.example.chillisauce.box.service;

import com.example.chillisauce.security.UserDetailsImpl;
import com.example.chillisauce.spaces.BoxRepository;
import com.example.chillisauce.spaces.SpaceRepository;
import com.example.chillisauce.spaces.dto.BoxRequestDto;
import com.example.chillisauce.spaces.dto.BoxResponseDto;
import com.example.chillisauce.spaces.entity.Box;
import com.example.chillisauce.spaces.entity.Space;
import com.example.chillisauce.spaces.exception.SpaceErrorCode;
import com.example.chillisauce.spaces.exception.SpaceException;
import com.example.chillisauce.spaces.service.BoxService;
import com.example.chillisauce.spaces.service.SpaceService;
import com.example.chillisauce.users.entity.Companies;
import com.example.chillisauce.users.entity.User;
import com.example.chillisauce.users.entity.UserRoleEnum;
import com.example.chillisauce.users.repository.CompanyRepository;
import com.example.chillisauce.users.repository.UserRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.context.SpringBootTest;

import javax.validation.ConstraintViolationException;
import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest
class BoxServiceTest {

    @Mock
    private CompanyRepository companyRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SpaceRepository spaceRepository;

    @Mock
    private BoxRepository boxRepository;

    private static BoxService boxService;
    private SpaceService spaceService;

    private Companies companies;
    private User user;
    private UserDetailsImpl details;
    private Space space;
    private Box box;

    @FunctionalInterface
    interface TriConsumer<T, U, V> {
        void accept(T t, U u, V v);
    }

    @BeforeEach
    void setup() {
        spaceService = Mockito.mock(SpaceService.class);
        boxService = new BoxService(boxRepository, companyRepository, spaceService, userRepository);

        companies = Companies.builder()
                .companyName("호랑이")
                .certification("CERTIFIED")
                .build();
        Mockito.lenient().when(companyRepository.findByCompanyName(Mockito.anyString())).thenReturn(Optional.of(companies));

        user = User.builder()
                .id(0L)
                .email("tiger@tiger")
                .username("")
                .password("test")
                .role(UserRoleEnum.ADMIN)
                .companies(companies)
                .build();
        Mockito.lenient().when(userRepository.save(Mockito.any(User.class))).thenReturn(new User());

        details = new UserDetailsImpl(user, null);

        space = Space.builder()
                .id(1L)
                .spaceName("박스 생성 테스트")
                .companies(companies)
                .build();
        Mockito.lenient().when(spaceRepository.save(Mockito.any(Space.class))).thenReturn(new Space());


        box = Box.builder()
                .boxName("이민재자리")
                .x("777")
                .y("777")
                .space(space)
                .build();
        Mockito.lenient().when(boxRepository.save(Mockito.any(Box.class))).thenReturn(box);
    }


    @DisplayName("박스 생성")
    @Test
    void createBox() {
        Mockito.lenient().when(boxRepository.save(Mockito.any(Box.class))).thenReturn(box);
        when(spaceService.findCompanyNameAndSpaceId(Mockito.anyString(), Mockito.anyLong())).thenReturn(space);

        //given
        BoxRequestDto boxRequestDto = new BoxRequestDto("이민재자리", "777", "777");
        BoxResponseDto boxResponseDto = boxService.createBox("호랑이", 1L, boxRequestDto, details);

        //then
        assertEquals("이민재자리", boxResponseDto.getBoxName());
        assertEquals("777", boxResponseDto.getX());
        assertEquals("777", boxResponseDto.getY());
    }


    @DisplayName("박스 개별 수정")
    @Test
    void updateBoxTest() {
        Box savedBox = boxRepository.save(box);

        when(boxRepository.findByIdAndSpaceCompanies(Mockito.eq(savedBox.getId()), Mockito.any(Companies.class)))
                .thenReturn(Optional.of(savedBox));


        BoxRequestDto boxRequestDto = new BoxRequestDto("장혁진자리_수정", "888", "888");

        //when
        BoxResponseDto boxResponseDto = boxService.updateBox("호랑이", savedBox.getId(), boxRequestDto, details);

        //then
        assertEquals("장혁진자리_수정", boxResponseDto.getBoxName());
        assertEquals("888", boxResponseDto.getX());
        assertEquals("888", boxResponseDto.getY());
    }

    @DisplayName("박스 삭제")
    @Test
    void deleteBoxTest() {
        Box savedBox = boxRepository.save(box);

        when(boxRepository.findByIdAndSpaceCompanies(Mockito.eq(savedBox.getId()), Mockito.any(Companies.class)))
                .thenReturn(Optional.of(savedBox));

        //when
        boxService.deleteBox("호랑이", savedBox.getId(), details);

        //then
        assertNull(boxRepository.findById(savedBox.getId()).orElse(null));
    }

    @DisplayName("사용자 Box 등록 및 Box 이동 테스트")
    @Test
    void testMoveBoxWithUser() {
        // given
        Long fromBoxId = 1L;
        Long toBoxId = 2L;

        Companies companies = Companies.builder()
                .companyName("test")
                .certification("A")
                .build();
        Mockito.lenient().when(companyRepository.findByCompanyName(Mockito.anyString())).thenReturn(Optional.of(companies));

        User user = User.builder()
                .id(1L)
                .email("test@test.com")
                .username("test")
                .password("test")
                .role(UserRoleEnum.USER)
                .companies(companies)
                .build();
        Mockito.lenient().when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(user));

        Box fromBox = Box.builder()
                .id(fromBoxId)
                .boxName("from box")
                .x("1")
                .y("1")
                .space(new Space())
                .user(user)
                .build();

        when(boxRepository.findByIdAndSpaceCompanies(Mockito.eq(fromBox.getId()), Mockito.any(Companies.class)))
                .thenReturn(Optional.of(fromBox));


        Box toBox = Box.builder()
                .id(toBoxId)
                .boxName("to box")
                .x("2")
                .y("2")
                .space(new Space())
                .user(null)
                .build();

        when(boxRepository.findByIdAndSpaceCompanies(Mockito.eq(toBox.getId()), Mockito.any(Companies.class)))
                .thenReturn(Optional.of(toBox));


        BoxRequestDto boxRequestDto = new BoxRequestDto("박스", "3", "3");

        // when
        BoxResponseDto boxResponseDto = boxService.moveBoxWithUser("test", fromBox.getId(), toBox.getId(), boxRequestDto, new UserDetailsImpl(user, user.getUsername()));

        // then
        assertEquals(boxResponseDto.getBoxName(), boxRequestDto.getBoxName());
        assertEquals(boxResponseDto.getX(), boxRequestDto.getX());
        assertEquals(boxResponseDto.getY(), boxRequestDto.getY());
        assertNull(fromBox.getUser());
        assertNull(fromBox.getUsername());
        assertEquals(user, toBox.getUser());
        assertEquals(user.getUsername(), toBox.getUsername());
    }
    @DisplayName("유저 확인")
    @Test
    void findByIdUser() {
        Long id = 1L;
        User user = User.builder()
                .id(1L)
                .email("mouse@mouse")
                .username("test")
                .password("test")
                .role(UserRoleEnum.USER)
                .companies(companies)
                .build();
        Mockito.when(userRepository.findById(user.getId())).thenReturn(Optional.empty());

        // when & then
        SpaceException exception = assertThrows(SpaceException.class, () -> {
            userRepository.findById(id).orElseThrow(() -> new SpaceException(SpaceErrorCode.USER_NOT_FOUND));
        });

        assertThat(exception.getErrorCode()).isEqualTo(SpaceErrorCode.USER_NOT_FOUND);
    }



    @DisplayName("공용테스트 - 권한없음 메서드")
    @ParameterizedTest
    @MethodSource("provideTriConsumer")
    void Commontest_NotHavePermission(TriConsumer<Long, BoxRequestDto, UserDetailsImpl> operation) {
        // given
        Companies companies = Companies.builder()
                .companyName("쥐")
                .certification("A")
                .build();
        Mockito.lenient().when(companyRepository.findByCompanyName(Mockito.anyString())).thenReturn(Optional.of(companies));

        User user = User.builder()
                .id(0L)
                .email("mouse@mouse")
                .username("")
                .password("test")
                .role(UserRoleEnum.USER)
                .companies(companies)
                .build();
        Mockito.lenient().when(userRepository.save(Mockito.any(User.class))).thenReturn(new User());

        UserDetailsImpl details = new UserDetailsImpl(user, null);

        Space space = Space.builder()
                .id(1L)
                .spaceName("박스 생성 권한 없음 테스트")
                .companies(companies)
                .build();
        Mockito.lenient().when(spaceRepository.save(Mockito.any(Space.class))).thenReturn(new Space());

        BoxRequestDto boxRequestDto = new BoxRequestDto("이민재자리", "777", "777");

        // when, then
        SpaceException exception = assertThrows(SpaceException.class, () -> {
            operation.accept(1L, boxRequestDto, details);
        });
        assertEquals(SpaceErrorCode.NOT_HAVE_PERMISSION, exception.getErrorCode());
    }
    @DisplayName("권한 없음 통합 테스트")
    private static Stream<TriConsumer<Long, BoxRequestDto, UserDetailsImpl>> provideTriConsumer() {

        TriConsumer<Long, BoxRequestDto, UserDetailsImpl> createBox = (spaceId, boxRequestDto, details) -> boxService.createBox("쥐", spaceId, boxRequestDto, details);
        TriConsumer<Long, BoxRequestDto, UserDetailsImpl> updateBox = (spaceId, boxRequestDto, details) -> boxService.updateBox("쥐", spaceId, boxRequestDto, details);
        TriConsumer<Long, BoxRequestDto, UserDetailsImpl> deleteBox = (spaceId, boxRequestDto, details) -> boxService.deleteBox("쥐", spaceId, details);

        return Stream.of(createBox, updateBox, deleteBox);
    }



}
