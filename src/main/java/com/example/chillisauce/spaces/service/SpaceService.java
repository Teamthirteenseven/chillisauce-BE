package com.example.chillisauce.spaces.service;


import com.example.chillisauce.reservations.service.ReservationService;
import com.example.chillisauce.security.UserDetailsImpl;
import com.example.chillisauce.spaces.dto.request.SpaceRequestDto;
import com.example.chillisauce.spaces.dto.response.SpaceListResponseDto;
import com.example.chillisauce.spaces.dto.response.SpaceResponseDto;
import com.example.chillisauce.spaces.entity.Floor;
import com.example.chillisauce.spaces.entity.Location;
import com.example.chillisauce.spaces.entity.Mr;
import com.example.chillisauce.spaces.entity.Space;
import com.example.chillisauce.spaces.exception.SpaceErrorCode;
import com.example.chillisauce.spaces.exception.SpaceException;
import com.example.chillisauce.spaces.repository.*;
import com.example.chillisauce.users.entity.Companies;
import com.example.chillisauce.users.entity.UserRoleEnum;
import com.example.chillisauce.users.repository.CompanyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StopWatch;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


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
    private final LocationRepository locationRepository;



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
    @Transactional
    @Cacheable(cacheNames = "SpaceResponseDtoList", key = "#companyName")
    public List<SpaceListResponseDto> allSpacelist(String companyName, UserDetailsImpl details) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        if (!details.getUser().getCompanies().getCompanyName().equals(companyName)) {
            throw new SpaceException(SpaceErrorCode.NOT_HAVE_PERMISSION_COMPANIES);
        }
        List<SpaceListResponseDto> spaceResponseDto = spaceRepository.getSpaceAllList(companyName);
        stopWatch.stop();
        log.info(stopWatch.prettyPrint());

        return spaceResponseDto;
    }


    /**
     * 개선 후 선택 조회 QueryDsl
     */
    @Transactional
    @Cacheable(cacheNames = "SpaceResponseDtoList", key = "#companyName + '_' + #spaceId")
    public SpaceResponseDto getSpacelist(String companyName, Long spaceId, UserDetailsImpl details) {
        if (!details.getUser().getCompanies().getCompanyName().equals(companyName)) {
            throw new SpaceException(SpaceErrorCode.NOT_HAVE_PERMISSION_COMPANIES);
        }

        return spaceRepository.getSpacesWithLocations(spaceId);
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
