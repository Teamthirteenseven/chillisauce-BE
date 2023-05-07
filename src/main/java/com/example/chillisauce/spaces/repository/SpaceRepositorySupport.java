package com.example.chillisauce.spaces.repository;

import com.example.chillisauce.spaces.dto.BoxResponseDto;
import com.example.chillisauce.spaces.dto.MrResponseDto;
import com.example.chillisauce.spaces.dto.MultiBoxResponseDto;
import com.example.chillisauce.spaces.dto.SpaceResponseDto;
import com.example.chillisauce.spaces.entity.Box;
import com.example.chillisauce.spaces.entity.Mr;
import com.example.chillisauce.spaces.entity.MultiBox;

import java.util.List;

public interface SpaceRepositorySupport {
    List<SpaceResponseDto> getSpacesWithLocations(Long spaceId);
    List<SpaceResponseDto> getSpaceAllList(String companyName);
    void clearAllReservationsForSpace(Long spaceId);
    void clearAllReservationsForFloor(Long floorId);

    List<BoxResponseDto> getBoxList();
    List<MrResponseDto>getMrList();
    List<MultiBoxResponseDto> getMultiboxList();
}
