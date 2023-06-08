package com.example.chillisauce.domain.spaces.service;

import com.example.chillisauce.domain.spaces.dto.request.BoxRequestDto;
import com.example.chillisauce.domain.spaces.dto.response.BoxResponseDto;
import com.example.chillisauce.domain.spaces.exception.SpaceErrorCode;
import com.example.chillisauce.domain.spaces.exception.SpaceException;
import com.example.chillisauce.domain.spaces.repository.BoxRepository;
import com.example.chillisauce.global.security.UserDetailsImpl;
import com.example.chillisauce.domain.spaces.entity.Box;
import com.example.chillisauce.domain.spaces.entity.Space;
import com.example.chillisauce.domain.users.entity.Companies;
import com.example.chillisauce.domain.users.entity.UserRoleEnum;
import com.example.chillisauce.domain.users.repository.CompanyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Slf4j
@RequiredArgsConstructor
public class BoxService {
    private final BoxRepository boxRepository;
    private final CompanyRepository companyRepository;
    private final SpaceService spaceService;


    /**
     * 박스 생성
     */
    @Transactional
    @CacheEvict(cacheNames = {"SpaceResponseDtoList", "FloorResponseDtoList"}, allEntries = true)
    public BoxResponseDto createBox(String companyName, Long spaceId, BoxRequestDto boxRequestDto, UserDetailsImpl details) {
        if (!details.getUser().getRole().equals(UserRoleEnum.ADMIN) && !details.getUser().getRole().equals(UserRoleEnum.MANAGER)) {
            throw new SpaceException(SpaceErrorCode.NOT_HAVE_PERMISSION);
        }
        Space space = spaceService.findCompanyNameAndSpaceId(companyName, spaceId);
        Box box = new Box(boxRequestDto, space);

        boxRepository.save(box);

        return new BoxResponseDto(box);
    }

    /**
     * 박스 수정
     */
    @Transactional
    @CacheEvict(cacheNames = {"SpaceResponseDtoList", "FloorResponseDtoList"}, allEntries = true)
    public BoxResponseDto updateBox(String companyName, Long boxId, BoxRequestDto boxRequestDto, UserDetailsImpl details) {
        if (!details.getUser().getRole().equals(UserRoleEnum.ADMIN) && !details.getUser().getRole().equals(UserRoleEnum.MANAGER)) {
            throw new SpaceException(SpaceErrorCode.NOT_HAVE_PERMISSION);
        }
        Box box = findCompanyNameAndBoxId(companyName, boxId);
        box.updateBox(boxRequestDto);
        boxRepository.save(box);
        return new BoxResponseDto(box);
    }

    /**
     * 박스 개별 삭제
     */
    @Transactional
    @CacheEvict(cacheNames = {"SpaceResponseDtoList", "FloorResponseDtoList"}, allEntries = true)
    public BoxResponseDto deleteBox(String companyName, Long boxId, UserDetailsImpl details) {
        if (!details.getUser().getRole().equals(UserRoleEnum.ADMIN) && !details.getUser().getRole().equals(UserRoleEnum.MANAGER)) {
            throw new SpaceException(SpaceErrorCode.NOT_HAVE_PERMISSION);
        }
        Box box = findCompanyNameAndBoxId(companyName, boxId);
        boxRepository.deleteById(boxId);
        return new BoxResponseDto(box);
    }

    public Box findCompanyNameAndBoxId(String companyName, Long boxId) {
        Companies company = companyRepository.findByCompanyName(companyName).orElseThrow(
                () -> new SpaceException(SpaceErrorCode.COMPANIES_NOT_FOUND)
        );

        return boxRepository.findByIdAndSpaceCompanies(boxId, company).orElseThrow(
                () -> new SpaceException(SpaceErrorCode.BOX_NOT_FOUND)
        );
    }
}