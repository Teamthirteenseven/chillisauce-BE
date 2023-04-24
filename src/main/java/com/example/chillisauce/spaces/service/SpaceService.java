package com.example.chillisauce.spaces.service;


import com.example.chillisauce.reservations.service.ReservationService;
import com.example.chillisauce.security.UserDetailsImpl;
import com.example.chillisauce.spaces.dto.*;
import com.example.chillisauce.spaces.entity.Floor;
import com.example.chillisauce.spaces.entity.Location;
import com.example.chillisauce.spaces.entity.Mr;
import com.example.chillisauce.spaces.entity.Space;
import com.example.chillisauce.spaces.exception.SpaceErrorCode;
import com.example.chillisauce.spaces.exception.SpaceException;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
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
        SpaceResponseDto responseDto;
        if (space.getFloor() != null) {
            responseDto = new SpaceResponseDto(space, space.getFloor().getId(), space.getFloor().getFloorName());
        } else {
            responseDto = new SpaceResponseDto(space, null, null);
        }
        return Collections.singletonList(responseDto);
    }

    //공간 개별 수정
    @Transactional
//    @CacheEvict(cacheNames = "SpaceResponseDtoList", allEntries = true)
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
//    @CacheEvict(cacheNames = "SpaceResponseDtoList", allEntries = true)
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

}
