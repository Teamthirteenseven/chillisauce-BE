package com.example.chillisauce.spaces.repository;

import com.example.chillisauce.spaces.dto.response.FloorResponseDto;

import java.util.List;

public interface FloorRepositorySupport {

    List<FloorResponseDto> getFloorAllList(String companyName);

    void clearAllReservationsForFloor(Long floorId);
}
