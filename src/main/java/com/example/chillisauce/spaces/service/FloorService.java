package com.example.chillisauce.spaces.service;

import com.example.chillisauce.security.UserDetailsImpl;
import com.example.chillisauce.spaces.dto.FloorRequestDto;
import com.example.chillisauce.spaces.dto.FloorResponseDto;
import com.example.chillisauce.spaces.dto.SpaceRequestDto;
import com.example.chillisauce.spaces.dto.SpaceResponseDto;
import com.example.chillisauce.spaces.entity.Floor;
import com.example.chillisauce.spaces.entity.Space;
import com.example.chillisauce.spaces.exception.SpaceErrorCode;
import com.example.chillisauce.spaces.exception.SpaceException;
import com.example.chillisauce.spaces.repository.FloorRepository;
import com.example.chillisauce.users.entity.Companies;
import com.example.chillisauce.users.entity.UserRoleEnum;
import com.example.chillisauce.users.repository.CompanyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;



@Service
@RequiredArgsConstructor
public class FloorService {
    private final CompanyRepository companyRepository;
    private final FloorRepository floorRepository;
    //Floor 생성
    @Transactional
    public FloorResponseDto createFloor(String companyName, FloorRequestDto floorRequestDto, UserDetailsImpl details) {
        if (!details.getUser().getRole().equals(UserRoleEnum.ADMIN)) {
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
    public List<FloorResponseDto> getFloorlist(String companyName, Long floorId, UserDetailsImpl details) {
        if (!details.getUser().getCompanies().getCompanyName().equals(companyName)){
            throw new SpaceException(SpaceErrorCode.COMPANIES_NOT_FOUND);
        }
        Floor floor = findCompanyNameAndSpaceId(companyName,floorId);
        List<FloorResponseDto> floorResponseDto = new ArrayList<>();
        floorResponseDto.add(new FloorResponseDto(floor));

        return floorResponseDto;
    }

    //Floor 전체 조회
    @Transactional
    public List<FloorResponseDto> getFloor (String companyName, UserDetailsImpl details) {
        if (!details.getUser().getCompanies().getCompanyName().equals(companyName)) {
            throw new SpaceException(SpaceErrorCode.COMPANIES_NOT_FOUND);
        }
        List<Floor> floorList = floorRepository.findAll();
        List<FloorResponseDto> floorResponseDto = new ArrayList<>();
        for (Floor floors : floorList){
            floorResponseDto.add(new FloorResponseDto(floors.getId(), floors.getFloorName()));
        }
        return floorResponseDto;
    }
    //floor 수정
    @Transactional
    public FloorResponseDto updateFloor (String companyName, Long floorId, FloorRequestDto floorRequestDto, UserDetailsImpl details){
        if (!details.getUser().getRole().equals(UserRoleEnum.ADMIN)) {
            throw new SpaceException(SpaceErrorCode.NOT_HAVE_PERMISSION);
        }
        Floor floor = findCompanyNameAndSpaceId(companyName, floorId);
        floor.updateFloor(floorRequestDto);
        floorRepository.save(floor);
        return new FloorResponseDto(floor);
    }

    //floor 삭제
    @Transactional
    public FloorResponseDto deleteFloor (String companyName, Long floorId, UserDetailsImpl details) {
        if (!details.getUser().getRole().equals(UserRoleEnum.ADMIN)) {
            throw new SpaceException(SpaceErrorCode.NOT_HAVE_PERMISSION);
        }
        Floor floor = findCompanyNameAndSpaceId(companyName, floorId);
        floorRepository.deleteById(floorId);
        return new FloorResponseDto(floor);
    }

    public Floor findCompanyNameAndSpaceId(String companyName, Long floorId) {
        Companies company = companyRepository.findByCompanyName(companyName).orElseThrow(
                () -> new SpaceException(SpaceErrorCode.COMPANIES_NOT_FOUND)
        );
        return floorRepository.findByIdAndCompanies(floorId, company).orElseThrow(
                () -> new SpaceException(SpaceErrorCode.SPACE_NOT_FOUND)
        );
    }

}