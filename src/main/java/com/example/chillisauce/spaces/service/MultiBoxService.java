package com.example.chillisauce.spaces.service;

import com.example.chillisauce.security.UserDetailsImpl;
import com.example.chillisauce.spaces.dto.request.MultiBoxRequestDto;
import com.example.chillisauce.spaces.dto.response.MultiBoxResponseDto;
import com.example.chillisauce.spaces.entity.MultiBox;
import com.example.chillisauce.spaces.entity.Space;
import com.example.chillisauce.spaces.exception.SpaceErrorCode;
import com.example.chillisauce.spaces.exception.SpaceException;
import com.example.chillisauce.spaces.repository.MultiBoxRepository;
import com.example.chillisauce.users.entity.Companies;
import com.example.chillisauce.users.entity.UserRoleEnum;
import com.example.chillisauce.users.repository.CompanyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
@Service
@RequiredArgsConstructor
public class MultiBoxService {

    private final SpaceService spaceService;
    private final MultiBoxRepository multiBoxRepository;
    private final CompanyRepository companyRepository;

    /**
     * 멀티박스 생성
     */
    @Transactional
    @CacheEvict(cacheNames = {"SpaceResponseDtoList", "FloorResponseDtoList"}, allEntries = true)
    public MultiBoxResponseDto createMultiBox(String companyName, Long spaceId, MultiBoxRequestDto multiBoxRequestDto, UserDetailsImpl details) {
        if (!details.getUser().getRole().equals(UserRoleEnum.ADMIN) && !details.getUser().getRole().equals(UserRoleEnum.MANAGER)) {
            throw new SpaceException(SpaceErrorCode.NOT_HAVE_PERMISSION);
        }
        Space space = spaceService.findCompanyNameAndSpaceId(companyName, spaceId);

        MultiBox multiBox = new MultiBox(multiBoxRequestDto);
        multiBoxRepository.save(multiBox);
        space.addLocation(multiBox);
        return new MultiBoxResponseDto(multiBox);
    }
    /**
     * 멀티박스 수정
     */
    @Transactional
    @CacheEvict(cacheNames = {"SpaceResponseDtoList", "FloorResponseDtoList"}, allEntries = true)
    public MultiBoxResponseDto updateMultiBox(String companyName, Long multiBoxId, MultiBoxRequestDto multiBoxRequestDto, UserDetailsImpl details) {
        if (!details.getUser().getRole().equals(UserRoleEnum.ADMIN) && !details.getUser().getRole().equals(UserRoleEnum.MANAGER)) {
            throw new SpaceException(SpaceErrorCode.NOT_HAVE_PERMISSION);
        }
        MultiBox multiBox = findCompanyNameAndMultiBoxId(companyName,multiBoxId);
        multiBox.updateMultiBox(multiBoxRequestDto);
        multiBoxRepository.save(multiBox);
        return new MultiBoxResponseDto(multiBox);
    }
    /**
     * 멀티박스 삭제
     */
    @Transactional
    @CacheEvict(cacheNames = {"SpaceResponseDtoList", "FloorResponseDtoList"}, allEntries = true)
    public MultiBoxResponseDto deleteMultiBox(String companyName, Long multiBoxId, UserDetailsImpl details) {
        if (!details.getUser().getRole().equals(UserRoleEnum.ADMIN) && !details.getUser().getRole().equals(UserRoleEnum.MANAGER)) {
            throw new SpaceException(SpaceErrorCode.NOT_HAVE_PERMISSION);
        }
        MultiBox multiBox = findCompanyNameAndMultiBoxId(companyName,multiBoxId);
        multiBoxRepository.deleteById(multiBoxId);
        return new MultiBoxResponseDto(multiBox);
}
    public MultiBox findCompanyNameAndMultiBoxId(String companyName, Long multiBoxId) {
        Companies company = companyRepository.findByCompanyName(companyName).orElseThrow(
                () -> new SpaceException(SpaceErrorCode.COMPANIES_NOT_FOUND)
        );
        return multiBoxRepository.findByIdAndSpaceCompanies(multiBoxId, company).orElseThrow(
                () -> new SpaceException(SpaceErrorCode.SPACE_NOT_FOUND)
        );
    }
}
