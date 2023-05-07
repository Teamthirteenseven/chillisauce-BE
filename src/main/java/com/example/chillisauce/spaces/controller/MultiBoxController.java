package com.example.chillisauce.spaces.controller;

import com.example.chillisauce.message.ResponseMessage;
import com.example.chillisauce.security.UserDetailsImpl;
import com.example.chillisauce.spaces.dto.request.MultiBoxRequestDto;
import com.example.chillisauce.spaces.service.MultiBoxService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class MultiBoxController {
    private final MultiBoxService multiBoxService;
    //MultiBox 생성
    @PostMapping("/multiBox/{companyName}/{spaceId}")
    public ResponseEntity<ResponseMessage<String>> createMultiBox
            (@PathVariable("companyName")String companyName , @PathVariable("spaceId") Long spaceId, @RequestBody MultiBoxRequestDto multiBoxRequestDto, @AuthenticationPrincipal UserDetailsImpl details){
        multiBoxService.createMultiBox(companyName,spaceId, multiBoxRequestDto, details);
        return ResponseMessage.responseSuccess("MultiBox 생성 성공","");
    }
    //MultiBox 수정
    @PatchMapping("/multiBox/{companyName}/{multiBoxId}")
    public ResponseEntity<ResponseMessage<String>> updateMultiBox
            (@PathVariable("companyName") String companyName,@PathVariable("multiBoxId") Long multiBoxId, @RequestBody MultiBoxRequestDto multiBoxRequestDto, @AuthenticationPrincipal UserDetailsImpl details){
        multiBoxService.updateMultiBox(companyName, multiBoxId, multiBoxRequestDto, details);
        return ResponseMessage.responseSuccess("MultiBox 수정 성공","");
    }
    //MultiBox 삭제
    @DeleteMapping("/multiBox/{companyName}/{multiBoxId}")
    public ResponseEntity<ResponseMessage<String>> deleteBox
            (@PathVariable("companyName") String companyName, @PathVariable("multiBoxId") Long multiBoxId, @AuthenticationPrincipal UserDetailsImpl details){
        multiBoxService.deleteMultiBox(companyName, multiBoxId,details);
        return ResponseMessage.responseSuccess("MultiBox 삭제 완료","");
    }

}
