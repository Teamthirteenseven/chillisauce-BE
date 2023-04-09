package com.example.chillisauce.spaces.controller;

import com.example.chillisauce.message.ResponseMessage;
import com.example.chillisauce.security.UserDetailsImpl;
import com.example.chillisauce.spaces.dto.FloorRequestDto;
import com.example.chillisauce.spaces.dto.SpaceRequestDto;
import com.example.chillisauce.spaces.service.FloorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class FloorController {

    private final FloorService floorService;
    @PostMapping("/{companyName}/floor")
    public ResponseEntity<ResponseMessage> createFloor
            (@PathVariable("companyName") String companyName, @RequestBody FloorRequestDto floorRequestDto, @AuthenticationPrincipal UserDetailsImpl details) {
        floorService.createFloor(companyName, floorRequestDto, details);
        return ResponseMessage.responseSuccess("Floor 생성 성공","");
    }

    @GetMapping("/{companyName}/{floorId}")
    public ResponseEntity<ResponseMessage> getFloorlist
            (@PathVariable("companyName") String companyName, @PathVariable("floorId") Long floorId, @AuthenticationPrincipal UserDetailsImpl details) {

        return ResponseMessage.responseSuccess("해당 Floor 조회 성공",floorService.getFloorlist(companyName, floorId, details));
    }
}
