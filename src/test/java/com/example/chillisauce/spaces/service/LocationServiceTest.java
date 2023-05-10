package com.example.chillisauce.spaces.service;

import com.example.chillisauce.security.UserDetailsImpl;
import com.example.chillisauce.spaces.dto.response.LocationDto;
import com.example.chillisauce.spaces.entity.Box;
import com.example.chillisauce.spaces.entity.Location;
import com.example.chillisauce.spaces.entity.MultiBox;
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
import static com.example.chillisauce.fixture.FixtureFactory.*;
import java.util.Optional;

import static com.example.chillisauce.spaces.entity.QBox.box;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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


    @Nested
    @DisplayName("성공케이스")
    class SuccessCase {
        Location next = Box_생성_아이디_지정(2L);
        Location prev = MultiBox_생성_아이디_지정(1L);
        @Test
        void UserLocation_isPresent_true_유저_멀티박스_박스_이동() {
            // given
            Long locationId = 2L;
            Companies company = Company_생성();
            UserDetailsImpl details = details_권한_USER(company);
            Location differentLocation = Box_생성_아이디_지정(1L);
            UserLocation userLocation = UserLocation_생성_Location(prev, details.getUser());


            when(locationRepository.findByIdAndCompanyName(eq(locationId), any())).thenReturn(Optional.of(next));
            when(userLocationRepository.findByUserId(details.getUser().getId())).thenReturn(Optional.of(userLocation));

            // when
            LocationDto locationDto = locationService.moveWithUser(company.getCompanyName(),locationId,details);

            // then
            assertEquals(locationDto.getLocationName(), differentLocation.getLocationName());
            assertEquals(locationDto.getX(), differentLocation.getX());
            assertEquals(locationDto.getY(), differentLocation.getY());
        }
    }
}
