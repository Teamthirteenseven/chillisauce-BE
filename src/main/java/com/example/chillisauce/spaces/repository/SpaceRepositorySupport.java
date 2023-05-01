package com.example.chillisauce.spaces.repository;

import com.example.chillisauce.spaces.dto.SpaceResponseDto;

import java.util.List;

public interface SpaceRepositorySupport {
    List<SpaceResponseDto> getSpacesWithLocations(Long spaceId);
}
