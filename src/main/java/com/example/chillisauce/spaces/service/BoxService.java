package com.example.chillisauce.spaces.service;

import com.example.chillisauce.security.UserDetailsImpl;
import com.example.chillisauce.spaces.dto.BoxRequestDto;
import com.example.chillisauce.spaces.dto.BoxResponseDto;
import com.example.chillisauce.spaces.entity.Box;
import com.example.chillisauce.spaces.entity.MultiBox;
import com.example.chillisauce.spaces.entity.Space;
import com.example.chillisauce.spaces.exception.SpaceErrorCode;
import com.example.chillisauce.spaces.exception.SpaceException;
import com.example.chillisauce.spaces.repository.BoxRepository;
import com.example.chillisauce.spaces.repository.MultiBoxRepository;
import com.example.chillisauce.users.entity.Companies;
import com.example.chillisauce.users.entity.User;
import com.example.chillisauce.users.entity.UserRoleEnum;
import com.example.chillisauce.users.repository.CompanyRepository;
import com.example.chillisauce.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class BoxService {
    private final BoxRepository boxRepository;
    private final CompanyRepository companyRepository;
    private final SpaceService spaceService;
    private final UserRepository userRepository;

    private final MultiBoxRepository multiBoxRepository;


    //Box 생성
    @Transactional
    public BoxResponseDto createBox(String companyName, Long spaceId, BoxRequestDto boxRequestDto, UserDetailsImpl details) {
        if (!details.getUser().getRole().equals(UserRoleEnum.ADMIN)) {
            throw new SpaceException(SpaceErrorCode.NOT_HAVE_PERMISSION);
        }
        Space space = spaceService.findCompanyNameAndSpaceId(companyName, spaceId);

        Box box = new Box(boxRequestDto);
        boxRepository.save(box);
        space.addBox(box);//box.setSpace(space); 기존 set addBox 메서드로 교체
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

    @Transactional
    public BoxResponseDto moveBoxWithUser(String companyName, Long toBoxId, BoxRequestDto boxRequestDto, UserDetailsImpl details) {
        User user = userRepository.findById(details.getUser().getId()).orElseThrow(
                () -> new SpaceException(SpaceErrorCode.USER_NOT_FOUND)
        );

        Optional<Box> box = boxRepository.findFirstByUserId(details.getUser().getId());
        Optional<MultiBox> multiBox = multiBoxRepository.findFirstByUserId(details.getUser().getId());
        if (box.isPresent()) {
            Box boxInstance = box.get();
            Box toBox = findCompanyNameAndBoxId(companyName, toBoxId);

            // 기존 박스에서 user 정보 삭제
            boxInstance.setUser(null);
            boxInstance.setUsername(null);
            // 새로운 박스에 user 정보 업데이트
            if (toBox.user == null) {
                toBox.updateBox(boxRequestDto, user);
            } else {
                throw new SpaceException(SpaceErrorCode.BOX_ALREADY_IN_USER);
            }

            return new BoxResponseDto(toBox);
        } else if (multiBox.isPresent()) {
            Box toBox = findCompanyNameAndBoxId(companyName, toBoxId);

            multiBox.get().setUser(null);
            multiBox.get().setUsername(null);

            if (toBox.user == null) {
                toBox.updateBox(boxRequestDto, user);
            } else {
                throw new SpaceException(SpaceErrorCode.BOX_ALREADY_IN_USER);
            }

            return new BoxResponseDto(toBox);
        } else {
            Box toBox = findCompanyNameAndBoxId(companyName, toBoxId);

            if (toBox.user == null) {
                toBox.updateBox(boxRequestDto, user);
                boxRepository.save(toBox);
            } else {
                throw new SpaceException(SpaceErrorCode.BOX_ALREADY_IN_USER);
            }

            return new BoxResponseDto(toBox);
    }

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
