package com.example.chillisauce.spaces.service;


import com.example.chillisauce.reservations.service.ReservationService;
import com.example.chillisauce.security.UserDetailsImpl;
import com.example.chillisauce.spaces.dto.*;
import com.example.chillisauce.spaces.entity.*;
import com.example.chillisauce.spaces.exception.SpaceErrorCode;
import com.example.chillisauce.spaces.exception.SpaceException;
import com.example.chillisauce.spaces.repository.BoxRepository;
import com.example.chillisauce.spaces.repository.FloorRepository;
import com.example.chillisauce.spaces.repository.MrRepository;
import com.example.chillisauce.spaces.repository.SpaceRepository;
import com.example.chillisauce.users.entity.Companies;
import com.example.chillisauce.users.entity.UserRoleEnum;
import com.example.chillisauce.users.repository.CompanyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;


@Service
@Slf4j
@RequiredArgsConstructor
public class SpaceService {
    private final SpaceRepository spaceRepository;
    private final CompanyRepository companyRepository;
    private final FloorRepository floorRepository;

    private final ReservationService reservationService;
    private final MrRepository mrRepository;

    private final BoxRepository boxRepository;


    //플로우 안에 공간 생성
    @Transactional
    public SpaceResponseDto createSpaceinfloor(String companyName, SpaceRequestDto spaceRequestDto, UserDetailsImpl details, Long floorId) {
        if (!details.getUser().getRole().equals(UserRoleEnum.ADMIN)) {
            throw new SpaceException(SpaceErrorCode.NOT_HAVE_PERMISSION);
        }
        Floor floor = floorRepository.findById(floorId).orElseThrow(
                () -> new SpaceException(SpaceErrorCode.FLOOR_NOT_FOUND)
        );
        Companies companies = companyRepository.findByCompanyName(companyName).orElseThrow(
                () -> new SpaceException(SpaceErrorCode.COMPANIES_NOT_FOUND)
        );
        Space space = spaceRepository.save(new Space(spaceRequestDto, companies, floor));
        floor.getSpaces().add(space);
        return new SpaceResponseDto(space);
    }

    //공간생성
    @Transactional
    public SpaceResponseDto createSpace(String companyName, SpaceRequestDto spaceRequestDto, UserDetailsImpl details) {
        if (!details.getUser().getRole().equals(UserRoleEnum.ADMIN)) {
            throw new SpaceException(SpaceErrorCode.NOT_HAVE_PERMISSION);
        }

        Companies companies = companyRepository.findByCompanyName(companyName).orElseThrow(
                () -> new SpaceException(SpaceErrorCode.COMPANIES_NOT_FOUND)
        );
        Space space = spaceRepository.save(new Space(spaceRequestDto, companies));
        return new SpaceResponseDto(space);
    }

    //전체 공간 조회
    @Transactional
//    @Cacheable(cacheNames = "SpaceResponseDtoList", key = "#companyName")
    public List<SpaceResponseDto> allSpacelist(String companyName, UserDetailsImpl details) {
        if (!details.getUser().getCompanies().getCompanyName().equals(companyName)) {
            throw new SpaceException(SpaceErrorCode.NOT_HAVE_PERMISSION_COMPANIES);
        }
        Companies companies = companyRepository.findByCompanyName(companyName).orElseThrow(
                () -> new SpaceException(SpaceErrorCode.COMPANIES_NOT_FOUND)
        );
        List<Space> spaceList = spaceRepository.findAllByCompaniesId(companies.getId());
        return spaceList.stream().map(space -> {
            Long floorId = null;
            String floorName = null;
            if (space.getFloor() != null) {
                floorId = space.getFloor().getId();
                floorName = space.getFloor().getFloorName();
            }
            return new SpaceResponseDto(space, floorId, floorName);
        }).collect(Collectors.toList());
    }


    //공간 선택 조회
    @Transactional
//    @Cacheable(cacheNames = "SpaceResponseDtoList", key = "#companyName + '_' + #spaceId")
    public List<SpaceResponseDto> getSpacelist(String companyName, Long spaceId, UserDetailsImpl details) {
        if (!details.getUser().getCompanies().getCompanyName().equals(companyName)) {
            throw new SpaceException(SpaceErrorCode.NOT_HAVE_PERMISSION_COMPANIES);
        }
        Space space = findCompanyNameAndSpaceId(companyName, spaceId);

        Map<Long, List<UserLocation>> userLocationMap = boxRepository.findAllLocationsWithUserLocations().stream()
                .filter(obj -> ((Location) obj[0]).getSpace().getId().equals(space.getId())) //쿼리 결과를 필터링 각 위치에 ID에 대한 사용자 위치 목록을 맵으로
                .collect(Collectors.groupingBy(obj -> ((Location) obj[0]).getId(),
                        Collectors.mapping(obj -> (UserLocation) obj[1], Collectors.toList())));

        List<Object[]> locationsWithUserLocations = space.getLocations().stream()
                .map(location -> new Object[]{location, userLocationMap.get(location.getId())})
                .collect(Collectors.toList());


        Long floorId = space.getFloor() != null ? space.getFloor().getId() : null;
        String floorName = space.getFloor() != null ? space.getFloor().getFloorName() : null;

        SpaceResponseDto responseDto = new SpaceResponseDto(space, floorId, floorName, locationsWithUserLocations);
        return Collections.singletonList(responseDto);
    }

//    @Transactional
//    public List<SpaceResponseDto> getSpacelist(String companyName, Long spaceId, UserDetailsImpl details) {
//        if (!details.getUser().getCompanies().getCompanyName().equals(companyName)) {
//            throw new SpaceException(SpaceErrorCode.NOT_HAVE_PERMISSION_COMPANIES);
//        }
//        Space space = findCompanyNameAndSpaceId(companyName, spaceId);
//        SpaceResponseDto responseDto;
//        if (space.getFloor() != null) {
//            responseDto = new SpaceResponseDto(space, space.getFloor().getId(), space.getFloor().getFloorName());
//        } else {
//            responseDto = new SpaceResponseDto(space, null, null);
//        }
//        return Collections.singletonList(responseDto);
//    }

    //공간 개별 수정
    @Transactional
    public SpaceResponseDto updateSpace(String companyName, Long spaceId, SpaceRequestDto spaceRequestDto, UserDetailsImpl details) {
        if (!details.getUser().getRole().equals(UserRoleEnum.ADMIN)) {
            throw new SpaceException(SpaceErrorCode.NOT_HAVE_PERMISSION);
        }
        Space space = findCompanyNameAndSpaceId(companyName, spaceId);
        Optional<Long> floorIdOptional = spaceRequestDto.getFloorId();

        Floor floor = null;
        if (floorIdOptional.isPresent()) {
            Long floorId = floorIdOptional.get();
            floor = floorRepository.findById(floorId).orElseThrow(
                    () -> new SpaceException(SpaceErrorCode.FLOOR_NOT_FOUND));
        }
        space.updateSpace(spaceRequestDto, floor);
        spaceRepository.save(space);
        return new SpaceResponseDto(space);
    }

    //공간 삭제
    @Transactional
    public SpaceResponseDto deleteSpace(String companyName, Long spaceId, UserDetailsImpl details) {
        if (!details.getUser().getRole().equals(UserRoleEnum.ADMIN)) {
            throw new SpaceException(SpaceErrorCode.NOT_HAVE_PERMISSION);
        }
        Space space = findCompanyNameAndSpaceId(companyName, spaceId);
        List<Location> allLocations = space.getLocations();
        List<Mr> mrList = new ArrayList<>();

        for (Location location : allLocations) {
            if (location instanceof Mr) {
                mrList.add((Mr) location);
            }
        }
        for (Mr mr : mrList) {
            reservationService.deleteMeetingRoomInReservations(mr.getId(), details);
        }
        mrRepository.deleteAll(mrList);
        spaceRepository.deleteById(spaceId);
        return new SpaceResponseDto(space);
    }

    public Space findCompanyNameAndSpaceId(String companyName, Long spaceId) {
        Companies company = companyRepository.findByCompanyName(companyName).orElseThrow(
                () -> new SpaceException(SpaceErrorCode.COMPANIES_NOT_FOUND)
        );
        return spaceRepository.findByIdAndCompanies(spaceId, company).orElseThrow(
                () -> new SpaceException(SpaceErrorCode.SPACE_NOT_FOUND)
        );
    }
    //주어진 위치 ID에 해당하는 첫 번째 UserLocation 객체를 반환하는 메서드
    private UserLocation getFirstUserLocation(Map<Long, List<UserLocation>> userLocationMap, Long locationId) {
        List<UserLocation> userLocationsAtLocation = userLocationMap.getOrDefault(locationId, Collections.emptyList());
        return userLocationsAtLocation.isEmpty() ? null : userLocationsAtLocation.get(0);
        //userLocationMap 에서 locationId 사용하여 userLocation 객체 리스트를 검색 값이 없으면 null 값을 반환
        // 리스트가 있으면 userLocationsAtLocation.get(0); 객체반환
     }

}
