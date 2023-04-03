package com.example.chillisauce.spaces.service;


import com.example.chillisauce.security.UserDetailsImpl;
import com.example.chillisauce.spaces.dto.SpaceRequestDto;
import com.example.chillisauce.spaces.dto.SpaceResponseDto;
import com.example.chillisauce.spaces.entity.Space;
import com.example.chillisauce.spaces.exception.SpaceErrorCode;
import com.example.chillisauce.spaces.exception.SpaceException;
import com.example.chillisauce.spaces.repository.SpaceRepository;
import com.example.chillisauce.users.entity.User;
import com.example.chillisauce.users.entity.UserRoleEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SpaceService {
    private final SpaceRepository spaceRepository;


    //공간 생성
    @Transactional
    public SpaceResponseDto createSpace(String companyName, SpaceRequestDto spaceRequestDto, UserDetailsImpl Details) {
        if (!Details.getUser().getRole().equals(UserRoleEnum.ADMIN)) {
            throw new SpaceException(SpaceErrorCode.NOT_HAVE_PERMISSION);
        }
        Space space = spaceRepository.save(new Space(spaceRequestDto));
        return new SpaceResponseDto(space);
    }
    //공간 조회
    @Transactional
    public List<SpaceResponseDto> getSpacelist(String companyName, Long spaceId) {
        Space space = spaceRepository.findById(spaceId).orElseThrow(
                () -> new SpaceException(SpaceErrorCode.SPACE_NOT_FOUND)
        );
        List<SpaceResponseDto> spaceResponseDto = new ArrayList<>();
        spaceResponseDto.add(new SpaceResponseDto(space));

        return spaceResponseDto;
    }

}
