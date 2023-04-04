package com.example.chillisauce.spaces.service;

import com.example.chillisauce.security.UserDetailsImpl;
import com.example.chillisauce.spaces.dto.BoxRequestDto;
import com.example.chillisauce.spaces.dto.BoxResponseDto;
import com.example.chillisauce.spaces.entity.Box;
import com.example.chillisauce.spaces.entity.Space;
import com.example.chillisauce.spaces.exception.SpaceErrorCode;
import com.example.chillisauce.spaces.exception.SpaceException;
import com.example.chillisauce.spaces.repository.BoxRepository;
import com.example.chillisauce.spaces.repository.SpaceRepository;
import com.example.chillisauce.users.entity.Companies;
import com.example.chillisauce.users.entity.UserRoleEnum;
import com.example.chillisauce.users.repository.CompanyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BoxService {
    private final BoxRepository boxRepository;
    private final SpaceRepository spaceRepository;
    private final CompanyRepository companyRepository;

    private final SpaceService spaceService;

    //Box 생성
    @Transactional
    public BoxResponseDto createBox (String companyName, Long spaceId, BoxRequestDto boxRequestDto, UserDetailsImpl details){
        if (!details.getUser().getRole().equals(UserRoleEnum.ADMIN)) {
            throw new SpaceException(SpaceErrorCode.NOT_HAVE_PERMISSION);
        }
        Space space = spaceService.findCompanyNameAndSpaceId(companyName,spaceId);

        Box box = new Box(boxRequestDto);
        boxRepository.saveAndFlush(box);
        space.addBox(box);//box.setSpace(space); 기존 set addBox 메서드로 교체
        return new BoxResponseDto(box);
    }
    //Box 개별 수정
    public BoxResponseDto updateBox(String companyName, Long boxId, BoxRequestDto boxRequestDto, UserDetailsImpl details) {
        if (!details.getUser().getRole().equals(UserRoleEnum.ADMIN)) {
            throw new SpaceException(SpaceErrorCode.NOT_HAVE_PERMISSION);
        }
        Box box = findCompanyNameAndBoxId(companyName,boxId);
        box.updateBox(boxRequestDto);
        boxRepository.save(box);
        return new BoxResponseDto(box);
    }
    //Box 개별 삭제
    public BoxResponseDto deleteBox(String companyName, Long boxId, UserDetailsImpl details) {
        if (!details.getUser().getRole().equals(UserRoleEnum.ADMIN)) {
            throw new SpaceException(SpaceErrorCode.NOT_HAVE_PERMISSION);
        }
        Box box = findCompanyNameAndBoxId(companyName,boxId);
        boxRepository.deleteById(boxId);
        return new BoxResponseDto(box);
    }

    //companyName find , BoxId 두개 합쳐놓은 메서드
    private Box findCompanyNameAndBoxId(String companyName, Long boxId) {
        Companies company = companyRepository.findByCompanyName(companyName).orElseThrow(
                () -> new SpaceException(SpaceErrorCode.COMPANIES_NOT_FOUND)
        );
        return boxRepository.findByIdAndSpaceCompanies(boxId, company).orElseThrow(
                () -> new SpaceException(SpaceErrorCode.SPACE_NOT_FOUND)
        );
    }

}
