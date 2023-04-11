package com.example.chillisauce.spaces.service;

import com.example.chillisauce.security.UserDetailsImpl;
import com.example.chillisauce.spaces.dto.BoxRequestDto;
import com.example.chillisauce.spaces.dto.BoxResponseDto;
import com.example.chillisauce.spaces.dto.MultiBoxRequestDto;
import com.example.chillisauce.spaces.dto.MultiBoxResponseDto;
import com.example.chillisauce.spaces.entity.Box;
import com.example.chillisauce.spaces.entity.MultiBox;
import com.example.chillisauce.spaces.entity.Space;
import com.example.chillisauce.spaces.exception.SpaceErrorCode;
import com.example.chillisauce.spaces.exception.SpaceException;
import com.example.chillisauce.spaces.repository.MultiBoxRepository;
import com.example.chillisauce.users.entity.Companies;
import com.example.chillisauce.users.entity.User;
import com.example.chillisauce.users.entity.UserRoleEnum;
import com.example.chillisauce.users.repository.CompanyRepository;
import com.example.chillisauce.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
@Service
@RequiredArgsConstructor
public class MultiBoxService {

    private final SpaceService spaceService;
    private final MultiBoxRepository multiBoxRepository;
    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;

    private final BoxService boxService;
    //multiBox 생성
    @Transactional
    public MultiBoxResponseDto createMultiBox (String companyName, Long spaceId, MultiBoxRequestDto multiBoxRequestDto, UserDetailsImpl details){
        if (!details.getUser().getRole().equals(UserRoleEnum.ADMIN)) {
            throw new SpaceException(SpaceErrorCode.NOT_HAVE_PERMISSION);
        }
        Space space = spaceService.findCompanyNameAndSpaceId(companyName,spaceId);

        MultiBox multiBox = new MultiBox(multiBoxRequestDto);
        multiBoxRepository.save(multiBox);
        space.addMultiBox(multiBox);
        return new MultiBoxResponseDto(multiBox);
    }
    //multiBox 개별 수정
    @Transactional
    public MultiBoxResponseDto updateMultiBox(String companyName, Long multiBoxId, MultiBoxRequestDto multiBoxRequestDto, UserDetailsImpl details) {
        if (!details.getUser().getRole().equals(UserRoleEnum.ADMIN)) {
            throw new SpaceException(SpaceErrorCode.NOT_HAVE_PERMISSION);
        }
        MultiBox multiBox = findCompanyNameAndBoxId(companyName,multiBoxId);
        multiBox.updateMultiBox(multiBoxRequestDto);
        multiBoxRepository.save(multiBox);
        return new MultiBoxResponseDto(multiBox);
    }
    //multiBox 개별 삭제
    @Transactional
    public MultiBoxResponseDto deleteMultiBox(String companyName, Long multiBoxId, UserDetailsImpl details) {
        if (!details.getUser().getRole().equals(UserRoleEnum.ADMIN)) {
            throw new SpaceException(SpaceErrorCode.NOT_HAVE_PERMISSION);
        }
        MultiBox multiBox = findCompanyNameAndBoxId(companyName,multiBoxId);
        multiBoxRepository.deleteById(multiBoxId);
        return new MultiBoxResponseDto(multiBox);
    }
    //박스 이동
    @Transactional
    public MultiBoxResponseDto moveMultiBoxWithUser
            (String companyName, Long fromMultiBoxId, Long toMultiId, MultiBoxRequestDto multiBoxRequestDto, UserDetailsImpl details,Long fromBoxId) {
        MultiBox fromMultiBox = findCompanyNameAndBoxId(companyName, fromMultiBoxId);
        MultiBox toMultiBox = findCompanyNameAndBoxId(companyName, toMultiId);
        Box fromBox = boxService.findCompanyNameAndBoxId(companyName, fromBoxId);
        User user = userRepository.findById(details.getUser().getId()).orElseThrow(
                () -> new SpaceException(SpaceErrorCode.USER_NOT_FOUND)
        );

        // 기존 박스에서 user 정보 삭제
        fromBox.setUsername(null);
        fromBox.setUser(null);
        fromMultiBox.setUser(null);
        fromMultiBox.setUsername(null);

        // 새로운 박스에 user 정보 업데이트

        toMultiBox.updateMultiBox(multiBoxRequestDto, user);
        multiBoxRepository.save(toMultiBox);


        return new MultiBoxResponseDto(toMultiBox);
    }

    public MultiBox findCompanyNameAndBoxId(String companyName, Long multiBoxId) {
        Companies company = companyRepository.findByCompanyName(companyName).orElseThrow(
                () -> new SpaceException(SpaceErrorCode.COMPANIES_NOT_FOUND)
        );
        return multiBoxRepository.findByIdAndSpaceCompanies(multiBoxId, company).orElseThrow(
                () -> new SpaceException(SpaceErrorCode.BOX_NOT_FOUND)
        );
    }
}
