package com.example.chillisauce.spaces.service;

import com.example.chillisauce.reservations.service.ReservationService;
import com.example.chillisauce.security.UserDetailsImpl;
import com.example.chillisauce.spaces.dto.MrRequestDto;
import com.example.chillisauce.spaces.dto.MrResponseDto;
import com.example.chillisauce.spaces.entity.Mr;
import com.example.chillisauce.spaces.entity.Space;
import com.example.chillisauce.spaces.exception.SpaceErrorCode;
import com.example.chillisauce.spaces.exception.SpaceException;
import com.example.chillisauce.spaces.repository.MrRepository;
import com.example.chillisauce.users.entity.Companies;
import com.example.chillisauce.users.entity.UserRoleEnum;
import com.example.chillisauce.users.repository.CompanyRepository;
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

    //미팅룸 생성
    @Transactional
    @CacheEvict(cacheNames = {"SpaceResponseDtoList", "FloorResponseDtoList"}, allEntries = true)
    public MrResponseDto createMr (String companyName, Long spaceId, MrRequestDto mrRequestDto, UserDetailsImpl details){
        if (!details.getUser().getRole().equals(UserRoleEnum.ADMIN) && !details.getUser().getRole().equals(UserRoleEnum.MANAGER)) {
            throw new SpaceException(SpaceErrorCode.NOT_HAVE_PERMISSION);
        }
        Space space = spaceService.findCompanyNameAndSpaceId(companyName,spaceId);
        Mr mr = new Mr(mrRequestDto);
        mrRepository.save(mr);
        space.addLocation(mr);
        return new MrResponseDto(mr);
    }

    //Mr 개별 수정
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
    //Mr 개별 삭제
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
    //Mr 전체 조회
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


    //companyName find , MrId 두개 합쳐놓은 메서드
    public Mr findCompanyNameAndMrId(String companyName, Long mrId) {
        Companies company = companyRepository.findByCompanyName(companyName).orElseThrow(
                () -> new SpaceException(SpaceErrorCode.COMPANIES_NOT_FOUND)
        );
        return mrRepository.findByIdAndSpaceCompanies(mrId, company).orElseThrow(
                () -> new SpaceException(SpaceErrorCode.MR_NOT_FOUND)
        );
    }



}
