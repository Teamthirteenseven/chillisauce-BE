package com.example.chillisauce.spaces.repository;

import com.example.chillisauce.spaces.dto.BoxResponseDto;
import com.example.chillisauce.spaces.dto.MultiBoxResponseDto;

import java.util.List;

public interface LocationRepositorySupport {

    List<BoxResponseDto> getBoxResponseDtos(Long spaceId);
    List<MultiBoxResponseDto> getMultiBoxResponseDtos(Long spaceId);
}
