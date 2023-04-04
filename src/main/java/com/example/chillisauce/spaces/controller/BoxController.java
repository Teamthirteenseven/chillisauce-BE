package com.example.chillisauce.spaces.controller;

import com.example.chillisauce.message.ResponseMessage;
import com.example.chillisauce.security.UserDetailsImpl;
import com.example.chillisauce.spaces.dto.BoxRequestDto;
import com.example.chillisauce.spaces.service.BoxService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class BoxController {

    private final BoxService boxService;

    @PostMapping("/box/{companyName}/{spaceId}")
    public ResponseEntity<ResponseMessage> createBox
            (@PathVariable("companyName")String companyName ,@PathVariable("spaceId") Long spaceId,@RequestBody BoxRequestDto boxRequestDto, @AuthenticationPrincipal UserDetailsImpl details){
        return ResponseMessage.responseSuccess("박스 생성 성공",boxService.createBox(companyName,spaceId, boxRequestDto, details));
    }

    @PatchMapping("/box/{companyName}/{boxId}")
    public ResponseEntity<ResponseMessage> updateBox
            (@PathVariable("companyName") String companyName,@PathVariable("boxId") Long boxId, @RequestBody BoxRequestDto boxRequestDto, @AuthenticationPrincipal UserDetailsImpl details){
        return ResponseMessage.responseSuccess("박스 수정 성공",boxService.updateBox(companyName, boxId, boxRequestDto, details));
    }

    @DeleteMapping("/box/{companyName}/{boxId}")
    public ResponseEntity<ResponseMessage> deleteBox
            (@PathVariable("companyName") String companyName, @PathVariable("boxId") Long boxId, @AuthenticationPrincipal UserDetailsImpl details){
        return ResponseMessage.responseSuccess("박스 삭제 완료",boxService.deleteBox(companyName, boxId,details));
    }

}
