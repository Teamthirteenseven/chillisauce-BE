package com.example.chillisauce.spaces.service;

import com.example.chillisauce.spaces.dto.BoxRequestDto;
import com.example.chillisauce.spaces.dto.BoxResponseDto;
import com.example.chillisauce.spaces.entity.Box;
import com.example.chillisauce.spaces.entity.Space;
import com.example.chillisauce.spaces.exception.SpaceErrorCode;
import com.example.chillisauce.spaces.exception.SpaceException;
import com.example.chillisauce.spaces.repository.BoxRepository;
import com.example.chillisauce.spaces.repository.SpaceRepository;
import com.example.chillisauce.users.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BoxService {
    private final BoxRepository boxRepository;
    private final SpaceRepository spaceRepository;

    @Transactional
    public BoxResponseDto createBox (Long spaceid, BoxRequestDto boxRequestDto, User user){
        Space space = spaceRepository.findById(spaceid).orElseThrow(
                () -> new SpaceException(SpaceErrorCode.SPACE_NOT_FOUND)
        );
        Box box = new Box(boxRequestDto, user);
        boxRepository.saveAndFlush(box);
        space.getBoxs().add(box);
        return new BoxResponseDto(box);
    }
}
