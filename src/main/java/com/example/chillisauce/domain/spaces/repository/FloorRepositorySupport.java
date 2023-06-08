package com.example.chillisauce.domain.spaces.repository;

import com.example.chillisauce.domain.spaces.dto.response.FloorResponseDto;

import java.util.List;

public interface FloorRepositorySupport {

    List<FloorResponseDto> getFloorAllList(String companyName);

    void clearAllReservationsForFloor(Long floorId);
}
