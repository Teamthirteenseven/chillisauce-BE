package com.example.chillisauce.spaces.repository;

import com.example.chillisauce.spaces.dto.response.*;

import java.util.List;

public interface SpaceRepositorySupport {
    List<SpaceResponseDto> getSpacesWithLocations(Long spaceId);
    List<SpaceListResponseDto> getSpaceAllList(String companyName);
    void clearAllReservationsForSpace(Long spaceId);
    void clearAllReservationsForFloor(Long floorId);

    List<BoxResponseDto> getBoxList();
    List<MrResponseDto>getMrList();
    List<MultiBoxResponseDto> getMultiboxList();
}
