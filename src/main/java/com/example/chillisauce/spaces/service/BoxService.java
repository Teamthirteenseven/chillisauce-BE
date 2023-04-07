package com.example.chillisauce.spaces.service;

import com.example.chillisauce.security.UserDetailsImpl;
import com.example.chillisauce.spaces.dto.BoxRequestDto;
import com.example.chillisauce.spaces.dto.BoxResponseDto;
import com.example.chillisauce.spaces.entity.Box;
import com.example.chillisauce.spaces.entity.Space;
import com.example.chillisauce.spaces.exception.SpaceErrorCode;
import com.example.chillisauce.spaces.exception.SpaceException;
import com.example.chillisauce.spaces.BoxRepository;
import com.example.chillisauce.users.entity.Companies;
import com.example.chillisauce.users.entity.User;
import com.example.chillisauce.users.entity.UserRoleEnum;
import com.example.chillisauce.users.repository.CompanyRepository;
import com.example.chillisauce.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class BoxService {
    private final BoxRepository boxRepository;
    private final CompanyRepository companyRepository;
    private final SpaceService spaceService;
    private final UserRepository userRepository;


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
    @Transactional
    public BoxResponseDto updateBox(String companyName, Long boxId, BoxRequestDto boxRequestDto, UserDetailsImpl details) {
        if (!details.getUser().getRole().equals(UserRoleEnum.ADMIN)) {
            throw new SpaceException(SpaceErrorCode.NOT_HAVE_PERMISSION);
        }
        Box box = findCompanyNameAndBoxId(companyName,boxId);
        box.updateBox(boxRequestDto,details.getUser());
        boxRepository.save(box);
        return new BoxResponseDto(box);
    }
    //Box 개별 삭제
    @Transactional
    public BoxResponseDto deleteBox(String companyName, Long boxId, UserDetailsImpl details) {
        if (!details.getUser().getRole().equals(UserRoleEnum.ADMIN)) {
            throw new SpaceException(SpaceErrorCode.NOT_HAVE_PERMISSION);
        }
        Box box = findCompanyNameAndBoxId(companyName,boxId);
        boxRepository.deleteById(boxId);
        return new BoxResponseDto(box);
    }

    // Box 에 User 정보 업데이트
//    @Transactional
//    public BoxResponseDto updateBoxUser(BoxRequestDto boxRequestDto, String companyName, Long boxId, UserDetailsImpl details) {
//        Box box = findCompanyNameAndBoxId(companyName, boxId);
//        User user = userRepository.findById(details.getUser().getId()).orElseThrow(
//                () -> new SpaceException(SpaceErrorCode.USER_NOT_FOUND)
//        );
//        if (box.user != null){
//            throw new SpaceException(SpaceErrorCode.BOX_ALREADY_IN_USER);
//        }
//
//        box.updateBox(boxRequestDto.getBoxName(), boxRequestDto.getX(), boxRequestDto.getY(), user);
//        boxRepository.save(box);
//        return new BoxResponseDto(box);
//    }
//    //Box 이동
//    @Transactional
//    public BoxResponseDto moveBox(String companyName, Long fromBoxId, Long toBoxId, UserDetailsImpl details) {
//        Box fromBox = findCompanyNameAndBoxId(companyName, fromBoxId);
//        Box toBox = findCompanyNameAndBoxId(companyName, toBoxId);
//        User user = userRepository.findById(details.getUser().getId()).orElseThrow(
//                () -> new SpaceException(SpaceErrorCode.USER_NOT_FOUND)
//        );
//        log.info("Moving box {} from company {} to box {} for user {}", fromBoxId, companyName, toBoxId, details.getUsername());
//
//        // 기존 박스에서 user 정보 삭제
//        fromBox.setUser(null);
//        fromBox.setUsername(null);
//        // 새로운 박스에 user 정보 업데이트
//        if (toBox.user == null) {
//            toBox.setUser(user);
//            toBox.setUsername(user.getUsername());
//        } else {
//            throw new SpaceException(SpaceErrorCode.BOX_ALREADY_IN_USER);
//        }
//
//        toBox = boxRepository.findById(toBoxId).orElseThrow(
//                () -> new SpaceException(SpaceErrorCode.BOX_NOT_FOUND)
//        );
//        return new BoxResponseDto(toBox);
//    }
    //box 에 user 정보 업데이트 및 user box 이동
    @Transactional
    public BoxResponseDto moveBoxWithUser(String companyName, Long fromBoxId, Long toBoxId, BoxRequestDto boxRequestDto, UserDetailsImpl details) {
        Box fromBox = findCompanyNameAndBoxId(companyName, fromBoxId);
        Box toBox = findCompanyNameAndBoxId(companyName, toBoxId);
        User user = userRepository.findById(details.getUser().getId()).orElseThrow(
                () -> new SpaceException(SpaceErrorCode.USER_NOT_FOUND)
        );
        log.info("Moving box {} from company {} to box {} for user {}", fromBoxId, companyName, toBoxId, details.getUsername());

        // 기존 박스에서 user 정보 삭제
        fromBox.setUser(null);
        fromBox.setUsername(null);

        // 새로운 박스에 user 정보 업데이트
        if (toBox.user == null) {
            toBox.updateBox(boxRequestDto, user);
            boxRepository.save(toBox);
        } else {
            throw new SpaceException(SpaceErrorCode.BOX_ALREADY_IN_USER);
        }

        return new BoxResponseDto(toBox);
    }

    //companyName find , BoxId 두개 합쳐놓은 메서드
    private Box findCompanyNameAndBoxId(String companyName, Long boxId) {
        Companies company = companyRepository.findByCompanyName(companyName).orElseThrow(
                () -> new SpaceException(SpaceErrorCode.COMPANIES_NOT_FOUND)
        );
        return boxRepository.findByIdAndSpaceCompanies(boxId, company).orElseThrow(
                () -> new SpaceException(SpaceErrorCode.BOX_NOT_FOUND)
        );
    }

}
