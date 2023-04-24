package com.example.chillisauce.spaces.service;

import com.example.chillisauce.security.UserDetailsImpl;
import com.example.chillisauce.spaces.dto.BoxRequestDto;
import com.example.chillisauce.spaces.dto.BoxResponseDto;
import com.example.chillisauce.spaces.entity.*;
import com.example.chillisauce.spaces.exception.SpaceErrorCode;
import com.example.chillisauce.spaces.exception.SpaceException;
import com.example.chillisauce.spaces.repository.BoxRepository;
import com.example.chillisauce.users.entity.Companies;
import com.example.chillisauce.users.entity.UserRoleEnum;
import com.example.chillisauce.users.repository.CompanyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Slf4j
@RequiredArgsConstructor
public class BoxService {
    private final BoxRepository boxRepository;
    private final CompanyRepository companyRepository;
    private final SpaceService spaceService;


    //Box 생성
    @Transactional
    public BoxResponseDto createBox(String companyName, Long spaceId, BoxRequestDto boxRequestDto, UserDetailsImpl details) {
        if (!details.getUser().getRole().equals(UserRoleEnum.ADMIN)) {
            throw new SpaceException(SpaceErrorCode.NOT_HAVE_PERMISSION);
        }
        Space space = spaceService.findCompanyNameAndSpaceId(companyName, spaceId);
        Box box = new Box(boxRequestDto);


        boxRepository.save(box);
        space.addLocation(box);//box.setSpace(space); 기존 set addBox 메서드로 교체

        return new BoxResponseDto(box);
    }

    //Box 개별 수정
    @Transactional
    public BoxResponseDto updateBox(String companyName, Long boxId, BoxRequestDto boxRequestDto, UserDetailsImpl details) {
        if (!details.getUser().getRole().equals(UserRoleEnum.ADMIN)) {
            throw new SpaceException(SpaceErrorCode.NOT_HAVE_PERMISSION);
        }
        Box box = findCompanyNameAndBoxId(companyName, boxId);
        box.updateBox(boxRequestDto);
        boxRepository.save(box);
        return new BoxResponseDto(box);
    }

    //Box 개별 삭제
    @Transactional
    public BoxResponseDto deleteBox(String companyName, Long boxId, UserDetailsImpl details) {
        if (!details.getUser().getRole().equals(UserRoleEnum.ADMIN)) {
            throw new SpaceException(SpaceErrorCode.NOT_HAVE_PERMISSION);
        }
        Box box = findCompanyNameAndBoxId(companyName, boxId);
        boxRepository.deleteById(boxId);
        return new BoxResponseDto(box);
    }

    //companyName find , BoxId 두개 합쳐놓은 메서드
    public Box findCompanyNameAndBoxId(String companyName, Long boxId) {
        Companies company = companyRepository.findByCompanyName(companyName).orElseThrow(
                () -> new SpaceException(SpaceErrorCode.COMPANIES_NOT_FOUND)
        );
        return boxRepository.findByIdAndSpaceCompanies(boxId, company).orElseThrow(
                () -> new SpaceException(SpaceErrorCode.BOX_NOT_FOUND)
        );
    }

}

