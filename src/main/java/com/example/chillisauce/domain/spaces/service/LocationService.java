package com.example.chillisauce.domain.spaces.service;

import com.example.chillisauce.domain.spaces.entity.Location;
import com.example.chillisauce.domain.spaces.entity.UserLocation;
import com.example.chillisauce.global.security.UserDetailsImpl;
import com.example.chillisauce.domain.spaces.dto.response.LocationDto;
import com.example.chillisauce.domain.spaces.entity.Box;
import com.example.chillisauce.domain.spaces.exception.SpaceErrorCode;
import com.example.chillisauce.domain.spaces.exception.SpaceException;
import com.example.chillisauce.domain.spaces.repository.LocationRepository;
import com.example.chillisauce.domain.spaces.repository.UserLocationRepository;
import com.example.chillisauce.domain.users.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LocationService {

    private final UserLocationRepository userLocationRepository;
    private final LocationRepository locationRepository;
    /**
     * 사용자 이동
     */
    @Transactional
    @CacheEvict(cacheNames = {"SpaceResponseDtoList", "FloorResponseDtoList"}, allEntries = true)
    public LocationDto moveWithUser(String companyName, Long locationId, UserDetailsImpl details) {

        User user = details.getUser();

        // 유저가 같은 회사원인지 검증
        if (!user.getCompanies().getCompanyName().equals(companyName)) {
            throw new SpaceException(SpaceErrorCode.COMPANIES_NOT_FOUND);
        }

        // companyName 과 정확히 같은 회사에 속한 스페이스를 찾고, locationId와 같은 로케이션 찾기
        // 없으면 404
        Location target = findCompanyNameAndId(companyName, locationId);

        // target 이 박스이면
        if (target instanceof Box) {
            // 유저가 있는지 확인하고 있으면 예외
            if(userLocationRepository.findByLocationId(target.getId()).isPresent()) {
                throw new SpaceException(SpaceErrorCode.BOX_ALREADY_IN_USER);
            }
        }

        // 사용자 id - 위치 정보
        Optional<UserLocation> userLocation = userLocationRepository.findByUserId(user.getId());

        UserLocation result;

        if (userLocation.isPresent()) {
            // 위치정보 있으면 업데이트
            result = userLocation.get().update(target, user);
        } else {
            // 없으면 저장
            result = userLocationRepository.save(new UserLocation(target, user));
        }

        return new LocationDto(result.getLocation(), result.getUsername());
    }


    public Location findCompanyNameAndId(String companyName, Long locationId) {
        return locationRepository.findByIdAndCompanyName(locationId, companyName).orElseThrow(
                () -> new SpaceException(SpaceErrorCode.BOX_NOT_FOUND)
        );
    }
}

