package com.example.chillisauce.spaces.service;

import com.example.chillisauce.reservations.service.ReservationService;
import com.example.chillisauce.security.UserDetailsImpl;
import com.example.chillisauce.spaces.dto.request.FloorRequestDto;
import com.example.chillisauce.spaces.dto.response.FloorResponseDto;
import com.example.chillisauce.spaces.entity.Floor;
import com.example.chillisauce.spaces.entity.Mr;
import com.example.chillisauce.spaces.exception.SpaceErrorCode;
import com.example.chillisauce.spaces.exception.SpaceException;
import com.example.chillisauce.spaces.repository.FloorRepository;
import com.example.chillisauce.spaces.repository.MrRepository;
import com.example.chillisauce.users.entity.Companies;
import com.example.chillisauce.users.entity.UserRoleEnum;
import com.example.chillisauce.users.repository.CompanyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;



@Service
@RequiredArgsConstructor
public class FloorService {
    private final CompanyRepository companyRepository;
    private final FloorRepository floorRepository;
    private final ReservationService reservationService;
    private final MrRepository mrRepository;

    //Floor 생성
    @Transactional
    @CacheEvict(cacheNames = {"SpaceResponseDtoList", "FloorResponseDtoList"}, allEntries = true)
    public FloorResponseDto createFloor(String companyName, FloorRequestDto floorRequestDto, UserDetailsImpl details) {
        if (!details.getUser().getRole().equals(UserRoleEnum.ADMIN) && !details.getUser().getRole().equals(UserRoleEnum.MANAGER)) {
            throw new SpaceException(SpaceErrorCode.NOT_HAVE_PERMISSION);
        }
        Companies companies = companyRepository.findByCompanyName(companyName).orElseThrow(
                () -> new SpaceException(SpaceErrorCode.COMPANIES_NOT_FOUND)
        );
        Floor floor = floorRepository.save(new Floor(floorRequestDto, companies));
        return new FloorResponseDto(floor);
    }
    //Floor 선택 조회
    @Transactional
    @Cacheable(cacheNames = "FloorResponseDtoList", key ="#companyName + '_' + #floorId")
    public List<FloorResponseDto> getFloorlist(String companyName, Long floorId, UserDetailsImpl details) {
        if (!details.getUser().getCompanies().getCompanyName().equals(companyName)){
            throw new SpaceException(SpaceErrorCode.NOT_HAVE_PERMISSION_COMPANIES);
        }
        Floor floor = findCompanyNameAndFloorId(companyName,floorId);
        List<FloorResponseDto> floorResponseDto = new ArrayList<>();
        floorResponseDto.add(new FloorResponseDto(floor));

        return floorResponseDto;
    }

    //Floor 전체 조회
    @Transactional
    @Cacheable(cacheNames = "FloorResponseDtoList", key = "#companyName")
    public List<FloorResponseDto> getFloor (String companyName, UserDetailsImpl details) {
        if (!details.getUser().getCompanies().getCompanyName().equals(companyName)) {
            throw new SpaceException(SpaceErrorCode.NOT_HAVE_PERMISSION_COMPANIES);
        }
        Companies companies = companyRepository.findByCompanyName(companyName).orElseThrow(
                () -> new SpaceException(SpaceErrorCode.COMPANIES_NOT_FOUND)
        );
        List<Floor> floorList = floorRepository.findAllByCompaniesId(companies.getId());
        List<FloorResponseDto> floorResponseDto = new ArrayList<>();
        for (Floor floor : floorList) {

            floorResponseDto.add(new FloorResponseDto(floor));
        }
        return floorResponseDto;
    }
    //floor 수정
    @Transactional
    @CacheEvict(cacheNames = {"SpaceResponseDtoList", "FloorResponseDtoList"}, allEntries = true)
    public FloorResponseDto updateFloor (String companyName, Long floorId, FloorRequestDto floorRequestDto, UserDetailsImpl details){
        if (!details.getUser().getRole().equals(UserRoleEnum.ADMIN) && !details.getUser().getRole().equals(UserRoleEnum.MANAGER)) {
            throw new SpaceException(SpaceErrorCode.NOT_HAVE_PERMISSION);
        }
        Floor floor = findCompanyNameAndFloorId(companyName, floorId);
        floor.updateFloor(floorRequestDto);
        floorRepository.save(floor);
        return new FloorResponseDto(floor);
    }

    //floor 삭제
    @Transactional
    @CacheEvict(cacheNames = {"SpaceResponseDtoList", "FloorResponseDtoList"}, allEntries = true)
    public FloorResponseDto deleteFloor(String companyName, Long floorId, UserDetailsImpl details) {
        if (!details.getUser().getRole().equals(UserRoleEnum.ADMIN) && !details.getUser().getRole().equals(UserRoleEnum.MANAGER)) {
            throw new SpaceException(SpaceErrorCode.NOT_HAVE_PERMISSION);
        }
        Floor floor = findCompanyNameAndFloorId(companyName, floorId);
        List<Mr> mrList = mrRepository.findByFloor(floor);
        for (Mr mr : mrList) {
            reservationService.deleteMeetingRoomInReservations(mr.getId(), details);
        }
        mrRepository.deleteAll(mrList);
        floorRepository.delete(floor);
//        mrRepository.clearAllReservationsForFloor(floorId);
//        spaceRepository.clearAllReservationsForFloor(floorId);
//        floorRepository.delete(floor);
        return new FloorResponseDto(floor);
    }


    public Floor findCompanyNameAndFloorId(String companyName, Long floorId) {
        Companies company = companyRepository.findByCompanyName(companyName).orElseThrow(
                () -> new SpaceException(SpaceErrorCode.COMPANIES_NOT_FOUND)
        );
        return floorRepository.findByIdAndCompanies(floorId, company).orElseThrow(
                () -> new SpaceException(SpaceErrorCode.SPACE_NOT_FOUND)
        );
    }

}
