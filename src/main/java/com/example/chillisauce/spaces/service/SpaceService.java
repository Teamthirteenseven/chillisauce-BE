package com.example.chillisauce.spaces.service;


import com.example.chillisauce.reservations.service.ReservationService;
import com.example.chillisauce.security.UserDetailsImpl;
import com.example.chillisauce.spaces.dto.SpaceRequestDto;
import com.example.chillisauce.spaces.dto.SpaceResponseDto;
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
    @CacheEvict(cacheNames = {"SpaceResponseDtoList", "FloorResponseDtoList"}, allEntries = true)
    public SpaceResponseDto createSpaceInFloor(String companyName, SpaceRequestDto spaceRequestDto, UserDetailsImpl details, Long floorId) {
        if (!details.getUser().getRole().equals(UserRoleEnum.ADMIN) && !details.getUser().getRole().equals(UserRoleEnum.MANAGER)) {
            throw new SpaceException(SpaceErrorCode.NOT_HAVE_PERMISSION);
        }
        Floor floor = floorRepository.findById(floorId).orElseThrow(
                () -> new SpaceException(SpaceErrorCode.FLOOR_NOT_FOUND)
        );
        Companies companies = companyRepository.findByCompanyName(companyName).orElseThrow(
                () -> new SpaceException(SpaceErrorCode.COMPANIES_NOT_FOUND)
        );
        if (!details.getUser().getCompanies().getCompanyName().equals(companyName)) {
            throw new SpaceException(SpaceErrorCode.NOT_HAVE_PERMISSION_COMPANIES);
        }
        Space space = spaceRepository.save(new Space(spaceRequestDto,floor,companies));
        floor.getSpaces().add(space);
        return new SpaceResponseDto(space);
    }

    //공간생성
    @Transactional
    @CacheEvict(cacheNames = {"SpaceResponseDtoList", "FloorResponseDtoList"}, allEntries = true)
    public SpaceResponseDto createSpace(String companyName, SpaceRequestDto spaceRequestDto, UserDetailsImpl details) {
        if (!details.getUser().getRole().equals(UserRoleEnum.ADMIN) && !details.getUser().getRole().equals(UserRoleEnum.MANAGER)) {
            throw new SpaceException(SpaceErrorCode.NOT_HAVE_PERMISSION);
        }

        Companies companies = companyRepository.findByCompanyName(companyName).orElseThrow(
                () -> new SpaceException(SpaceErrorCode.COMPANIES_NOT_FOUND)
        );
        if (!details.getUser().getCompanies().getCompanyName().equals(companyName)) {
            throw new SpaceException(SpaceErrorCode.NOT_HAVE_PERMISSION_COMPANIES);
        }
        Space space = spaceRepository.save(new Space(spaceRequestDto, companies));
        return new SpaceResponseDto(space);
    }

    /**
     * 개선 후 전체 조회 QueryDsl
     */
//    @Transactional
//    @Cacheable(cacheNames = "SpaceResponseDtoList", key = "#companyName")
//    public List<SpaceResponseDto> allSpacelist(String companyName, UserDetailsImpl details) {
//        if (!details.getUser().getCompanies().getCompanyName().equals(companyName)) {
//            throw new SpaceException(SpaceErrorCode.NOT_HAVE_PERMISSION_COMPANIES);
//        }
//        List<SpaceResponseDto> spaceResponseDto = spaceRepository.getSpaceAllList(companyName);
//
//        return spaceResponseDto;
//    }

    /**
     * 개선 전 전체 조회
     */
    @Transactional
    @Cacheable(cacheNames = "SpaceResponseDtoList", key = "#companyName")
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

    /**
     * 개선 후 선택 조회 QueryDsl
     */
//    @Transactional
//    @Cacheable(cacheNames = "SpaceResponseDtoList", key = "#companyName + '_' + #spaceId")
//    public List<SpaceResponseDto> getSpacelist(String companyName, Long spaceId, UserDetailsImpl details) {
//        if (!details.getUser().getCompanies().getCompanyName().equals(companyName)) {
//            throw new SpaceException(SpaceErrorCode.NOT_HAVE_PERMISSION_COMPANIES);
//        }
//        List<SpaceResponseDto> spaceResponseDto = spaceRepository.getSpacesWithLocations(spaceId);
//
//
//        return spaceResponseDto;
//    }

    /**
     * 개선 전 선택 조회
     */
    @Transactional
    @Cacheable(cacheNames = "SpaceResponseDtoList", key = "#companyName + '_' + #spaceId")
    public List<SpaceResponseDto> getSpacelist(String companyName, Long spaceId, UserDetailsImpl details) {
        if (!details.getUser().getCompanies().getCompanyName().equals(companyName)) {
            throw new SpaceException(SpaceErrorCode.NOT_HAVE_PERMISSION_COMPANIES);
        }
        Space space = findCompanyNameAndSpaceId(companyName, spaceId);

        Map<Long, List<UserLocation>> userLocationMap = boxRepository.findAllLocationsWithUserLocations().stream()
                .filter(obj -> ((Location) obj[0]).getSpace().getId().equals(space.getId()))
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




    //공간 개별 수정
    @Transactional
    @CacheEvict(cacheNames = {"SpaceResponseDtoList", "FloorResponseDtoList"}, allEntries = true)
    public SpaceResponseDto updateSpace(String companyName, Long spaceId, SpaceRequestDto spaceRequestDto, UserDetailsImpl details) {
        if (!details.getUser().getRole().equals(UserRoleEnum.ADMIN) && !details.getUser().getRole().equals(UserRoleEnum.MANAGER)) {
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
    @CacheEvict(cacheNames = {"SpaceResponseDtoList", "FloorResponseDtoList"}, allEntries = true)
    public SpaceResponseDto deleteSpace(String companyName, Long spaceId, UserDetailsImpl details) {
        if (!details.getUser().getRole().equals(UserRoleEnum.ADMIN) && !details.getUser().getRole().equals(UserRoleEnum.MANAGER)) {
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
        /**
         * 1차 개선 아래 주석 JPQL 서브쿼리
         */
//        mrRepository.clearAllReservationsForSpace(spaceId);
//        spaceRepository.deleteById(spaceId);
        /**
         * 2차 개선 아래 주석 QueryDSL   (delete 를 따로 호출하는게 성능이 좋은지 QueryDsl 에 포함하는게 좋은지도 궁금)
         */
//        spaceRepository.clearAllReservationsForSpace(spaceId);
//        spaceRepository.deleteById(spaceId);
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


}
