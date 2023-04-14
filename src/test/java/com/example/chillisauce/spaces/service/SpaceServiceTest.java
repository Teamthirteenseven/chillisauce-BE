package com.example.chillisauce.spaces.service;
import com.example.chillisauce.security.UserDetailsImpl;
import com.example.chillisauce.spaces.dto.SpaceRequestDto;
import com.example.chillisauce.spaces.dto.SpaceResponseDto;
import com.example.chillisauce.spaces.entity.Floor;
import com.example.chillisauce.spaces.entity.Space;
import com.example.chillisauce.spaces.exception.SpaceErrorCode;
import com.example.chillisauce.spaces.exception.SpaceException;
import com.example.chillisauce.spaces.repository.FloorRepository;
import com.example.chillisauce.spaces.repository.SpaceRepository;
import com.example.chillisauce.users.entity.Companies;
import com.example.chillisauce.users.entity.User;
import com.example.chillisauce.users.entity.UserRoleEnum;
import com.example.chillisauce.users.repository.CompanyRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class SpaceServiceTest {

    @Mock
    private CompanyRepository companyRepository;
    @Mock
    private SpaceRepository spaceRepository;
    @Mock
    private FloorRepository floorRepository;
    @InjectMocks
    private SpaceService spaceService;
    private Companies companies;
    private UserDetailsImpl details;
    private Space space;
    private Floor floor;


    @BeforeEach
    void setup() {

        floor = Floor.builder().build();
        companies = Companies.builder()
                .companyName("testCompany")
                .build();
        User user = User.builder()
                .role(UserRoleEnum.ADMIN)
                .companies(companies)
                .build();
        details = new UserDetailsImpl(user, null);
        space = Space.builder()
                .spaceName("테스트 Space")
                .floor(floor)
                .companies(companies)
                .build();

    }

    @Nested
    @DisplayName("성공케이스")
    class SuccessCase {
        @Test
        void Floor_안에_Space_생성() {

            //given
            String companyName = "testCompany";
            Long floorId = 1L;

            SpaceRequestDto spaceRequestDto = new SpaceRequestDto("테스트 Space");
            when(spaceRepository.save(any(Space.class))).thenReturn(space);
            when(floorRepository.findById(floorId)).thenReturn(Optional.of(floor));
            when(companyRepository.findByCompanyName(companyName)).thenReturn(Optional.of(Companies.builder().build()));

            //when
            SpaceResponseDto spaceResponseDto = spaceService.createSpaceinfloor("testCompany", spaceRequestDto, details, 1L);

            //then
            assertNotNull(spaceResponseDto);
            assertEquals("테스트 Space", spaceResponseDto.getSpaceName());

        }


        @Test
        void Space_생성() {

            //given
            String companyName = "testCompany";

            SpaceRequestDto spaceRequestDto = new SpaceRequestDto("Space 생성 테스트");
            when(spaceRepository.save(any(Space.class))).thenReturn(space);
            when(companyRepository.findByCompanyName(companyName)).thenReturn(Optional.of(companies));
            //when
            SpaceResponseDto spaceResponseDto = spaceService.createSpace(companyName, spaceRequestDto, details);

            //then
            assertNotNull(spaceResponseDto);
            assertEquals("테스트 Space", spaceResponseDto.getSpaceName());
        }

        @Test
        void Space_공간_전체_조회() {
            String companyName = "testCompany";

            List<Space> spaceList = Collections.singletonList(space);
            List<SpaceResponseDto> responseDto = spaceList.stream()
                    .map(SpaceResponseDto::new)
                    .toList();
            when(companyRepository.findByCompanyName(eq(companyName))).thenReturn(Optional.of(Companies.builder().build()));
            when(spaceRepository.findAllByCompaniesId(any())).thenReturn(spaceList);


            //when
            List<SpaceResponseDto> result = spaceService.allSpacelist(companyName, details);

            //Then
            assertNotNull(result);
            assertEquals(result.size(), responseDto.size());
            assertThat(responseDto).allSatisfy(responseSpace -> {
                assertThat(responseSpace.getSpaceName()).isEqualTo("테스트 Space");
            });
        }

        @Test
        void Space_공간_선택_조회() {
            //given
            String companyName = "testCompany";
            Long spaceId = 1L;

            Space space = Space.builder()
                    .spaceName("테스트 Space")
                    .floor(floor)
                    .companies(companies)
                    .build();

            when(companyRepository.findByCompanyName(eq(companyName))).thenReturn(Optional.of(Companies.builder().build()));
            when(spaceRepository.findByIdAndCompanies(anyLong(), any(Companies.class))).thenReturn(Optional.of(space));

            //when
            List<SpaceResponseDto> result = spaceService.getSpacelist(companyName, spaceId, details);

            //Then
            assertNotNull(result);
            assertEquals(1, result.size());
            SpaceResponseDto spaceResponseDto = result.get(0); //spaceResponseDto 리스트 첫번째 요소를 호출
            assertEquals("테스트 Space", spaceResponseDto.getSpaceName());
            assertEquals(floor.getId(), spaceResponseDto.getFloorId());
            assertEquals(floor.getFloorName(), spaceResponseDto.getFloorName());

        }


        @Test
        void Space_공간_수정() {
            String companyName = "testComapny";
            Long spaceId = 1L;

            when(companyRepository.findByCompanyName(eq(companyName))).thenReturn(Optional.of(Companies.builder().build()));
            when(spaceRepository.findByIdAndCompanies(anyLong(), any(Companies.class))).thenReturn(Optional.of(space));
            SpaceRequestDto spaceRequestDto = new SpaceRequestDto("수정 입니까?");
            //when
            SpaceResponseDto spaceResponseDto = spaceService.updateSpace(companyName, spaceId, spaceRequestDto, details);

            //Then
            assertNotNull(spaceResponseDto);
            assertEquals("수정 입니까?", spaceResponseDto.getSpaceName());
        }

        @Test
        void Space_공간_삭제() {
            //given
            String companyName = "testCompany";
            Long spaceId = 1L;

            when(companyRepository.findByCompanyName(eq(companyName))).thenReturn(Optional.of(Companies.builder().build()));
            when(spaceRepository.findByIdAndCompanies(anyLong(), any(Companies.class))).thenReturn(Optional.of(space));
            doNothing().when(spaceRepository).deleteById(spaceId);
            //when
            SpaceResponseDto spaceResponseDto = spaceService.deleteSpace(companyName, spaceId, details);

            //Then
            assertNotNull(spaceResponseDto);
            assertEquals("테스트 Space", spaceResponseDto.getSpaceName());
        }
    }

    @Nested
    @DisplayName("예외 케이스")
    class ExceptionCase {

        @BeforeEach
        void setup() {
            MockitoAnnotations.openMocks(this);
        }

        @Test
        void Space_공간_전체_조회_예외_테스트() {
            String companyName = "testCompany";
            String differentCompanyName = "differentCompany";

            Companies companies = Companies.builder()
                    .companyName(differentCompanyName)
                    .build();

            User user = User.builder()
                    .role(UserRoleEnum.ADMIN)
                    .companies(companies)
                    .build();

            UserDetailsImpl details = new UserDetailsImpl(user, null);

            SpaceException exception = assertThrows(SpaceException.class, () -> {
                spaceService.allSpacelist(companyName, details);
            });
            assertThat(exception.getErrorCode()).isEqualTo(SpaceErrorCode.NOT_HAVE_PERMISSION_COMPANIES);
        }

        @Test
        void Space_공간_선택_조회_예외_테스트() {
            String companyName = "testCompany";
            String differentCompanyName = "differentCompany";
            Long spaceId = 1L;
            Companies companies = Companies.builder()
                    .companyName(differentCompanyName)
                    .build();

            User user = User.builder()
                    .role(UserRoleEnum.ADMIN)
                    .companies(companies)
                    .build();

            UserDetailsImpl details = new UserDetailsImpl(user, null);

            SpaceException exception = assertThrows(SpaceException.class, () -> {
                spaceService.getSpacelist(companyName, spaceId, details);
            });
            assertThat(exception.getErrorCode()).isEqualTo(SpaceErrorCode.NOT_HAVE_PERMISSION_COMPANIES);
        }

        @Test
        void Floor_안에_Space_생성_권한_없음() {
            //given
            String companyName = "testCompany";
            Long floorId = 1L;
            UserDetailsImpl details = new UserDetailsImpl(User.builder().role(UserRoleEnum.USER).build(), "test");

            //When,Then
            SpaceException exception = assertThrows(SpaceException.class, () -> {
                SpaceRequestDto requestDto = new SpaceRequestDto("테스트 space");
                spaceService.createSpaceinfloor(companyName, requestDto, details, floorId);
            });
            assertEquals(SpaceErrorCode.NOT_HAVE_PERMISSION, exception.getErrorCode());
        }

        @Test
        void 공간_생성_권한_없음() {
            //given
            String companyName = "testCompany";
            UserDetailsImpl details = new UserDetailsImpl(User.builder().role(UserRoleEnum.USER).build(), "test");

            //When,Then
            SpaceException exception = assertThrows(SpaceException.class, () -> {
                SpaceRequestDto requestDto = new SpaceRequestDto("테스트 space");
                spaceService.createSpace(companyName, requestDto, details);
            });
            assertEquals(SpaceErrorCode.NOT_HAVE_PERMISSION, exception.getErrorCode());
        }

        @Test
        void 공간_수정_권한_없음() {
            //given
            String companyName = "testCompany";
            Long spaceId = 1L;
            UserDetailsImpl details = new UserDetailsImpl(User.builder().role(UserRoleEnum.USER).build(), "test");

            //When,Then
            SpaceException exception = assertThrows(SpaceException.class, () -> {
                SpaceRequestDto requestDto = new SpaceRequestDto("테스트 space");
                spaceService.updateSpace(companyName, spaceId, requestDto, details);
            });
            assertEquals(SpaceErrorCode.NOT_HAVE_PERMISSION, exception.getErrorCode());
        }

        @Test
        void 공간_삭제_권한_없음() {
            //given
            String companyName = "testCompany";
            Long spaceId = 1L;
            UserDetailsImpl details = new UserDetailsImpl(User.builder().role(UserRoleEnum.USER).build(), "test");

            //When,Then
            SpaceException exception = assertThrows(SpaceException.class, () -> {
                spaceService.deleteSpace(companyName, spaceId, details);
            });
            assertEquals(SpaceErrorCode.NOT_HAVE_PERMISSION, exception.getErrorCode());
        }
    }
}

