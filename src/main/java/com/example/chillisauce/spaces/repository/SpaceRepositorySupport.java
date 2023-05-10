package com.example.chillisauce.spaces.repository;

import com.example.chillisauce.spaces.dto.response.FloorResponseDto;
import com.example.chillisauce.spaces.dto.response.SpaceListResponseDto;
import com.example.chillisauce.spaces.dto.response.SpaceResponseDto;

import java.util.List;

public interface SpaceRepositorySupport {
    List<SpaceResponseDto> getSpacesWithLocations(Long spaceId);
    List<SpaceListResponseDto> getSpaceAllList(String companyName);
    List<FloorResponseDto> getFloorAllList(String companyName);
    void clearAllReservationsForSpace(Long spaceId);
    void clearAllReservationsForFloor(Long floorId);

}
