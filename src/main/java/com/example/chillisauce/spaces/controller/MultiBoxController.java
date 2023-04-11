package com.example.chillisauce.spaces.controller;

import com.example.chillisauce.message.ResponseMessage;
import com.example.chillisauce.security.UserDetailsImpl;
import com.example.chillisauce.spaces.dto.BoxRequestDto;
import com.example.chillisauce.spaces.dto.BoxResponseDto;
import com.example.chillisauce.spaces.dto.MultiBoxRequestDto;
import com.example.chillisauce.spaces.service.MultiBoxService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class MultiBoxController {
    private final MultiBoxService multiBoxService;

    @PostMapping("/multibox/{companyName}/{spaceId}")
    public ResponseEntity<ResponseMessage> createMultiBox
            (@PathVariable("companyName")String companyName , @PathVariable("spaceId") Long spaceId, @RequestBody MultiBoxRequestDto multiBoxRequestDto, @AuthenticationPrincipal UserDetailsImpl details){
        multiBoxService.createMultiBox(companyName,spaceId, multiBoxRequestDto, details);
        return ResponseMessage.responseSuccess("MultiBox 생성 성공","");
    }

    @PatchMapping("/multibox/{companyName}/{multiBoxId}")
    public ResponseEntity<ResponseMessage> updateMultiBox
            (@PathVariable("companyName") String companyName,@PathVariable("multiBoxId") Long multiBoxId, @RequestBody MultiBoxRequestDto multiBoxRequestDto, @AuthenticationPrincipal UserDetailsImpl details){
        multiBoxService.updateMultiBox(companyName, multiBoxId, multiBoxRequestDto, details);
        return ResponseMessage.responseSuccess("MultiBox 수정 성공","");
    }

    @DeleteMapping("/multibox/{companyName}/{multiBoxId}")
    public ResponseEntity<ResponseMessage> deleteBox
            (@PathVariable("companyName") String companyName, @PathVariable("multiBoxId") Long multiBoxId, @AuthenticationPrincipal UserDetailsImpl details){
        multiBoxService.deleteMultiBox(companyName, multiBoxId,details);
        return ResponseMessage.responseSuccess("MultiBox 삭제 완료","");
    }

    @PatchMapping("/multibox/{companyName}/{fromBoxId}/move/{toBoxId}/user")
    public ResponseEntity<ResponseMessage> moveMultiBoxWithUser(@PathVariable String companyName, @PathVariable Long fromMultiBoxId, @PathVariable Long toMultiBoxId, @RequestBody MultiBoxRequestDto multiBoxRequestDto, @AuthenticationPrincipal UserDetailsImpl details,@PathVariable Long fromBoxId) {
        multiBoxService.moveMultiBoxWithUser(companyName, fromMultiBoxId, toMultiBoxId, multiBoxRequestDto, details, fromBoxId);
        return ResponseMessage.responseSuccess("사용자 등록 및 이동 완료", "");
    }
}
