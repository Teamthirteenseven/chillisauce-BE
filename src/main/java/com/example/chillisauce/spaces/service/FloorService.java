package com.example.chillisauce.spaces.service;

import com.example.chillisauce.security.UserDetailsImpl;
import com.example.chillisauce.spaces.dto.request.FloorRequestDto;
import com.example.chillisauce.spaces.dto.response.FloorResponseDto;
import com.example.chillisauce.spaces.entity.Floor;
import com.example.chillisauce.spaces.exception.SpaceErrorCode;
import com.example.chillisauce.spaces.exception.SpaceException;
import com.example.chillisauce.spaces.repository.FloorRepository;
import com.example.chillisauce.spaces.repository.SpaceRepository;
import com.example.chillisauce.users.entity.Companies;
import com.example.chillisauce.users.entity.UserRoleEnum;
import com.example.chillisauce.users.repository.CompanyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;



@Service
@RequiredArgsConstructor
public class FloorService {
    private final CompanyRepository companyRepository;
    private final FloorRepository floorRepository;
    private final SpaceRepository spaceRepository;


    /**
     * 플로어 생성
     */
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


    /**
     * 플로어 전체 조회
     */
    @Transactional
    @Cacheable(cacheNames = "FloorResponseDtoList", key = "#companyName")
    public List<FloorResponseDto> getFloor (String companyName, UserDetailsImpl details) {
        if (!details.getUser().getCompanies().getCompanyName().equals(companyName)) {
            throw new SpaceException(SpaceErrorCode.NOT_HAVE_PERMISSION_COMPANIES);
        }
        List<FloorResponseDto> floorResponseDto = spaceRepository.getFloorAllList(companyName);

        return floorResponseDto;
    }
    /**
     * 플로어 개별 수정
     */
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

    /**
     * 플로어 삭제
     */
    @Transactional
    @CacheEvict(cacheNames = {"SpaceResponseDtoList", "FloorResponseDtoList"}, allEntries = true)
    public FloorResponseDto deleteFloor(String companyName, Long floorId, UserDetailsImpl details) {
        if (!details.getUser().getRole().equals(UserRoleEnum.ADMIN) && !details.getUser().getRole().equals(UserRoleEnum.MANAGER)) {
            throw new SpaceException(SpaceErrorCode.NOT_HAVE_PERMISSION);
        }
        Floor floor = findCompanyNameAndFloorId(companyName, floorId);

        spaceRepository.clearAllReservationsForFloor(floorId);
        floorRepository.delete(floor);
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
