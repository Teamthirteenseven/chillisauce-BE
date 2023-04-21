package com.example.chillisauce.spaces.controller;

import com.example.chillisauce.message.ResponseMessage;
import com.example.chillisauce.security.UserDetailsImpl;
import com.example.chillisauce.spaces.dto.BoxRequestDto;
import com.example.chillisauce.spaces.dto.BoxResponseDto;
import com.example.chillisauce.spaces.service.BoxService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class BoxController {

    private final BoxService boxService;

    @PostMapping("/boxes/{companyName}/{spaceId}")
    public ResponseEntity<ResponseMessage> createBox
            (@PathVariable("companyName")String companyName ,@PathVariable("spaceId") Long spaceId,@RequestBody BoxRequestDto boxRequestDto, @AuthenticationPrincipal UserDetailsImpl details){
        boxService.createBox(companyName,spaceId, boxRequestDto, details);
        return ResponseMessage.responseSuccess("박스 생성 성공","");
    }
    @PatchMapping("/boxes/{companyName}/{boxId}")
    public ResponseEntity<ResponseMessage> updateBox
            (@PathVariable("companyName") String companyName,@PathVariable("boxId") Long boxId, @RequestBody BoxRequestDto boxRequestDto, @AuthenticationPrincipal UserDetailsImpl details){
        boxService.updateBox(companyName, boxId, boxRequestDto, details);
        return ResponseMessage.responseSuccess("박스 수정 성공","");
    }
//
    @DeleteMapping("/boxes/{companyName}/{boxId}")
    public ResponseEntity<ResponseMessage> deleteBox
            (@PathVariable("companyName") String companyName, @PathVariable("boxId") Long boxId, @AuthenticationPrincipal UserDetailsImpl details){
        boxService.deleteBox(companyName, boxId,details);
        return ResponseMessage.responseSuccess("박스 삭제 완료","");
    }

//    @PatchMapping("/box/{companyName}/{boxId}/user")
//    public ResponseEntity<ResponseMessage> updateBoxUser(@PathVariable String companyName, @PathVariable Long boxId, @RequestBody BoxRequestDto boxRequestDto, @AuthenticationPrincipal UserDetailsImpl details){
//        boxService.updateBoxUser(boxRequestDto,companyName,boxId,details);
//        return ResponseMessage.responseSuccess("유저 등록 완료", "");
//    }
//    //fromBoxId, toBoxId 는 Box 엔티티의 속성이 아니라 API 에서 사용하는 PathVariable 중 하나 임 DB 컬럼을 따로 만들 필요가 없음.
//    @PatchMapping("/box/{companyName}/{fromBoxId}/move/{toBoxId}")
//    public ResponseEntity<ResponseMessage> moveBox(@PathVariable String companyName, @PathVariable Long fromBoxId, @PathVariable Long toBoxId, @AuthenticationPrincipal UserDetailsImpl details) {
//        BoxResponseDto response = boxService.moveBox(companyName, fromBoxId, toBoxId, details);
//        return ResponseMessage.responseSuccess("유저 이동 완료", response);
//    }

//    @PatchMapping("/boxes/{companyName}/{toBoxId}/move")
//    public ResponseEntity<ResponseMessage> moveBoxWithUser(@PathVariable String companyName,  @PathVariable Long toBoxId, @RequestBody BoxRequestDto boxRequestDto, @AuthenticationPrincipal UserDetailsImpl details) {
//        boxService.moveBoxWithUser(companyName, toBoxId, boxRequestDto, details);
//        return ResponseMessage.responseSuccess("사용자 등록 및 이동 완료", "");
//    }
}
