package com.example.chillisauce.spaces.service;

import com.example.chillisauce.security.UserDetailsImpl;
import com.example.chillisauce.spaces.dto.response.LocationDto;
import com.example.chillisauce.spaces.entity.Location;
import com.example.chillisauce.spaces.entity.UserLocation;
import com.example.chillisauce.spaces.repository.LocationRepository;
import com.example.chillisauce.spaces.repository.UserLocationRepository;
import com.example.chillisauce.users.entity.Companies;
import com.example.chillisauce.users.entity.User;
import com.example.chillisauce.users.entity.UserRoleEnum;
import com.example.chillisauce.users.repository.CompanyRepository;
import com.example.chillisauce.users.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class LocationServiceTest {
    @InjectMocks
    private LocationService locationService;

    @Mock
    private LocationRepository locationRepository;
    @Mock
    private UserRepository userRepository;

    @Mock
    private UserLocationRepository userLocationRepository;
    @Mock
    private CompanyRepository companyRepository;

    private UserDetailsImpl details;

    private UserLocation userLocation;

    private Companies companies;




    @BeforeEach
    void setup() {
        User user = User.builder()
                .role(UserRoleEnum.ADMIN)
                .build();
        details = new UserDetailsImpl(user, null);
        Location location = new Location("테스트", "200", "300");
        userLocation = new UserLocation();
        userLocation.setLocation(location);
        companies = Companies.builder().build();

    }

    @Nested
    @DisplayName("성공케이스")
    class SuccessCase {
        @Test
        void UserLocation_isPresent_true_유저_등록_이동() {
            // given
            Long locationId = 2L;
            String companyName = "test";
            Location differentLocation = new Location("다른 테스트", "100", "100");
            details = new UserDetailsImpl(User.builder().role(UserRoleEnum.USER).build(), "test");

            when(userRepository.findById(details.getUser().getId())).thenReturn(Optional.of(details.getUser()));
            when(companyRepository.findByCompanyName(companyName)).thenReturn(Optional.of(companies));
            when(locationRepository.findByIdAndSpaceCompanies(locationId, companies)).thenReturn(Optional.of(differentLocation));
            when(userLocationRepository.findByUserId(details.getUser().getId())).thenReturn(Optional.of(userLocation));
            // when
            LocationDto locationDto = locationService.moveWithUser(companyName,locationId,details);

            // then
            assertEquals(locationDto.getLocationName(), differentLocation.getLocationName());
            assertEquals(locationDto.getX(), differentLocation.getX());
            assertEquals(locationDto.getY(), differentLocation.getY());
        }
    }
}
