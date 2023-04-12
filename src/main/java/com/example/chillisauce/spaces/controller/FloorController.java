package com.example.chillisauce.spaces.controller;

import com.example.chillisauce.message.ResponseMessage;
import com.example.chillisauce.security.UserDetailsImpl;
import com.example.chillisauce.spaces.dto.FloorRequestDto;
import com.example.chillisauce.spaces.service.FloorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class FloorController {

    private final FloorService floorService;
    //floor 생성
    @PostMapping("/floors/{companyName}")
    public ResponseEntity<ResponseMessage> createFloor
            (@PathVariable("companyName") String companyName, @RequestBody FloorRequestDto floorRequestDto, @AuthenticationPrincipal UserDetailsImpl details) {
        floorService.createFloor(companyName, floorRequestDto, details);
        return ResponseMessage.responseSuccess("Floor 생성 성공","");
    }
    //floor 선택조회
    @GetMapping("/floors/{companyName}/{floorId}")
    public ResponseEntity<ResponseMessage> getFloorlist
            (@PathVariable("companyName") String companyName, @PathVariable("floorId") Long floorId, @AuthenticationPrincipal UserDetailsImpl details) {

        return ResponseMessage.responseSuccess("해당 Floor 조회 성공",floorService.getFloorlist(companyName, floorId, details));
    }
    //floor만 조회
    @GetMapping("/floors/{companyName}")
    public ResponseEntity<ResponseMessage> getFloor(@PathVariable("companyName") String companyName, @AuthenticationPrincipal UserDetailsImpl details){

        return ResponseMessage.responseSuccess("Floor 조회 성공",floorService.getFloor(companyName,details));
    }
    //floor 수정
    @PatchMapping("/floors/{companyName}/{floorId}")
    public ResponseEntity<ResponseMessage> updateFloor
            (@PathVariable("companyName") String companyName, @PathVariable("floorId") Long floorId, @RequestBody FloorRequestDto floorRequestDto, @AuthenticationPrincipal UserDetailsImpl details){
        floorService.updateFloor(companyName,floorId,floorRequestDto,details);
        return ResponseMessage.responseSuccess("Floor 수정 성공","");
    }

    //floor 삭제
    @DeleteMapping("/floors/{companyName}/{floorId}")
    public ResponseEntity<ResponseMessage> deleteFloor
            (@PathVariable("companyName") String companyName, @PathVariable("floorId") Long floorId, @AuthenticationPrincipal UserDetailsImpl details){
        floorService.deleteFloor(companyName,floorId,details);
        return ResponseMessage.responseSuccess("Floor 삭제 성공", "");
    }

}
