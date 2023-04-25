package com.example.chillisauce.spaces.service;

import com.example.chillisauce.security.UserDetailsImpl;
import com.example.chillisauce.spaces.dto.LocationDto;
import com.example.chillisauce.spaces.entity.Box;
import com.example.chillisauce.spaces.entity.Location;
import com.example.chillisauce.spaces.entity.UserLocation;
import com.example.chillisauce.spaces.exception.SpaceErrorCode;
import com.example.chillisauce.spaces.exception.SpaceException;
import com.example.chillisauce.spaces.repository.LocationRepository;
import com.example.chillisauce.spaces.repository.UserLocationRepository;
import com.example.chillisauce.users.entity.Companies;
import com.example.chillisauce.users.entity.User;
import com.example.chillisauce.users.repository.CompanyRepository;
import com.example.chillisauce.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LocationService {

    private final UserLocationRepository userLocationRepository;
    private final UserRepository userRepository;
    private final LocationRepository locationRepository;

    private final CompanyRepository companyRepository;


    @Transactional
    public LocationDto moveWithUser(Long locationId, UserDetailsImpl details) {
        User user = userRepository.findById(details.getUser().getId()).orElseThrow(
                () -> new SpaceException(SpaceErrorCode.USER_NOT_FOUND)
        );
        Location location = locationRepository.findById(locationId).orElseThrow(
                () -> new SpaceException(SpaceErrorCode.SPACE_NOT_FOUND)
        );

        Optional<UserLocation> userLocation = userLocationRepository.findByUserId(user.getId()); //사용자 현재위치
        Optional<UserLocation> targetUserLocation = userLocationRepository.findByLocationId(locationId); //이동하려는 위치에 있는 사용자

        if (userLocation.isPresent() && userLocation.get().getLocation().equals(location)) { //사용자가 이미 이 위치에 있다면 예외
            throw new SpaceException(SpaceErrorCode.USER_ALREADY_AT_LOCATION);
        }

        if (targetUserLocation.isPresent() && location instanceof Box) { //위치에 이미 다른 사용자가 있으면 (Box 타입일 경우 예외)
            throw new SpaceException(SpaceErrorCode.BOX_ALREADY_IN_USER);
        }
        if (userLocation.isEmpty()) { //없으면
            location.setUsername(user.getUsername());
            locationRepository.save(location);
            UserLocation newUserLocation = userLocationRepository.save(new UserLocation(location, user));
            return new LocationDto(newUserLocation.getLocation(), user.getUsername());
        } else {
            Location previousLocation = userLocation.get().getLocation();
            previousLocation.setUsername(null); // 이전 위치의 username null
            locationRepository.save(previousLocation);

            userLocation.get().setLocation(location);
            location.setUsername(user.getUsername());
            locationRepository.save(location);
            userLocationRepository.save(userLocation.get());
            return new LocationDto(userLocation.get().getLocation(), user.getUsername());
        }
    }

//    public Location findCompanyNameAndBoxId(String companyName, Long locationId) {
//        Companies company = companyRepository.findByCompanyName(companyName).orElseThrow(
//                () -> new SpaceException(SpaceErrorCode.COMPANIES_NOT_FOUND)
//        );
//        return locationRepository.findByIdAndSpaceCompanies(locationId, company).orElseThrow(
//                () -> new SpaceException(SpaceErrorCode.BOX_NOT_FOUND)
//        );
//    }
}
