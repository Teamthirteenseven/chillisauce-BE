package com.example.chillisauce.spaces.service;

import com.example.chillisauce.spaces.dto.MrRequestDto;
import com.example.chillisauce.spaces.dto.MrResponseDto;
import com.example.chillisauce.spaces.entity.Mr;
import com.example.chillisauce.spaces.entity.Space;
import com.example.chillisauce.spaces.exception.SpaceErrorCode;
import com.example.chillisauce.spaces.exception.SpaceException;
import com.example.chillisauce.spaces.repository.MrRepository;
import com.example.chillisauce.spaces.repository.SpaceRepository;
import com.example.chillisauce.users.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MrService {
    private final SpaceRepository spaceRepository;
    private final MrRepository mrRepository;
    @Transactional
    public MrResponseDto createMr (Long spaceid, MrRequestDto mrRequestDto, User user){
        Space space = spaceRepository.findById(spaceid).orElseThrow(
                () -> new SpaceException(SpaceErrorCode.SPACE_NOT_FOUND)
        );
        Mr mr = new Mr(mrRequestDto, user);
        mrRepository.saveAndFlush(mr);
        space.getMrs().add(mr);
        return new MrResponseDto(mr);
    }

}
