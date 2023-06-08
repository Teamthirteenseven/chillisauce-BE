package com.example.chillisauce.domain.spaces.service;

import com.example.chillisauce.domain.spaces.dto.request.MrRequestDto;
import com.example.chillisauce.domain.spaces.exception.SpaceErrorCode;
import com.example.chillisauce.domain.spaces.exception.SpaceException;
import com.example.chillisauce.domain.spaces.repository.MrRepository;
import com.example.chillisauce.domain.reservations.service.ReservationService;
import com.example.chillisauce.global.security.UserDetailsImpl;
import com.example.chillisauce.domain.spaces.dto.response.MrResponseDto;
import com.example.chillisauce.domain.spaces.entity.Mr;
import com.example.chillisauce.domain.spaces.entity.Space;
import com.example.chillisauce.domain.users.entity.Companies;
import com.example.chillisauce.domain.users.entity.UserRoleEnum;
import com.example.chillisauce.domain.users.repository.CompanyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MrService {
    private final MrRepository mrRepository;
    private final CompanyRepository companyRepository;
    private final SpaceService spaceService;

    private final ReservationService reservationService;

    /**
     * 회의실 생성
     */
    @Transactional
    @CacheEvict(cacheNames = {"SpaceResponseDtoList", "FloorResponseDtoList"}, allEntries = true)
    public MrResponseDto createMr (String companyName, Long spaceId, MrRequestDto mrRequestDto, UserDetailsImpl details){
        if (!details.getUser().getRole().equals(UserRoleEnum.ADMIN) && !details.getUser().getRole().equals(UserRoleEnum.MANAGER)) {
            throw new SpaceException(SpaceErrorCode.NOT_HAVE_PERMISSION);
        }
        Space space = spaceService.findCompanyNameAndSpaceId(companyName,spaceId);
        Mr mr = new Mr(mrRequestDto, space);
        mrRepository.save(mr);
        return new MrResponseDto(mr);
    }

    /**
     * 회의실 수정
     */
    @Transactional
    @CacheEvict(cacheNames = {"SpaceResponseDtoList", "FloorResponseDtoList"}, allEntries = true)
    public MrResponseDto updateMr(String companyName, Long mrId, MrRequestDto mrRequestDto, UserDetailsImpl details) {
        if (!details.getUser().getRole().equals(UserRoleEnum.ADMIN) && !details.getUser().getRole().equals(UserRoleEnum.MANAGER)) {
            throw new SpaceException(SpaceErrorCode.NOT_HAVE_PERMISSION);
        }
        Mr mr = findCompanyNameAndMrId(companyName,mrId);
        mr.updateMr(mrRequestDto);
        mrRepository.save(mr);
        return new MrResponseDto(mr);
    }
    /**
     * 회의실 삭제
     */
    @Transactional
    @CacheEvict(cacheNames = {"SpaceResponseDtoList", "FloorResponseDtoList"}, allEntries = true)
    public MrResponseDto deleteMr(String companyName, Long mrId, UserDetailsImpl details) {
        if (!details.getUser().getRole().equals(UserRoleEnum.ADMIN) && !details.getUser().getRole().equals(UserRoleEnum.MANAGER)) {
            throw new SpaceException(SpaceErrorCode.NOT_HAVE_PERMISSION);
        }
        Mr mr = findCompanyNameAndMrId(companyName,mrId);
        reservationService.deleteMeetingRoomInReservations(mrId, null);
        mrRepository.deleteById(mrId);
        return new MrResponseDto(mr);
    }
    /**
     * 회의실 전체조회
     */
    @Transactional
    @CacheEvict(cacheNames = {"SpaceResponseDtoList", "FloorResponseDtoList"}, allEntries = true)
    public List<MrResponseDto> mrlist(String companyName, UserDetailsImpl details) {
        if (!details.getUser().getCompanies().getCompanyName().equals(companyName)) {
            throw new SpaceException(SpaceErrorCode.NOT_HAVE_PERMISSION_COMPANIES);
        }

        Companies companies = companyRepository.findByCompanyName(companyName).orElseThrow(
                () -> new SpaceException(SpaceErrorCode.COMPANIES_NOT_FOUND)
        );

        List<Mr> mrList = mrRepository.findAllByCompaniesId(companies.getId());

        return mrList.stream()
                .map(MrResponseDto::new)
                .collect(Collectors.toList());
    }


    public Mr findCompanyNameAndMrId(String companyName, Long mrId) {
        Companies company = companyRepository.findByCompanyName(companyName).orElseThrow(
                () -> new SpaceException(SpaceErrorCode.COMPANIES_NOT_FOUND)
        );
        return mrRepository.findByIdAndSpaceCompanies(mrId, company).orElseThrow(
                () -> new SpaceException(SpaceErrorCode.MR_NOT_FOUND)
        );
    }



}
