package com.example.chillisauce.spaces.controller;

import com.example.chillisauce.message.ResponseMessage;
import com.example.chillisauce.security.UserDetailsImpl;
import com.example.chillisauce.spaces.dto.BoxRequestDto;
import com.example.chillisauce.spaces.dto.MrRequestDto;
import com.example.chillisauce.spaces.dto.MrResponseDto;
import com.example.chillisauce.spaces.service.MrService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class MrController {
    private final MrService mrService;
    @PostMapping("/mr/{companyName}/{spaceId}")
    public ResponseEntity<ResponseMessage> createMr
            (@PathVariable("companyName") String companyName,@PathVariable("spaceId") Long spaceId, @RequestBody MrRequestDto mrRequestDto, @AuthenticationPrincipal UserDetailsImpl details){
        mrService.createMr(companyName, spaceId, mrRequestDto, details);
        return ResponseMessage.responseSuccess("미팅룸 생성 성공","");
    }

    @PatchMapping("/mr/{companyName}/{mrId}")
    public ResponseEntity<ResponseMessage> updateMr
            (@PathVariable("companyName") String companyName, @PathVariable("mrId") Long mrId, @RequestBody MrRequestDto mrRequestDto, @AuthenticationPrincipal UserDetailsImpl details){
        mrService.updateMr(companyName, mrId, mrRequestDto, details);
        return ResponseMessage.responseSuccess("미팅룸 수정 성공","");
    }

    @DeleteMapping("/mr/{companyName}/{mrId}")
    public ResponseEntity<ResponseMessage> deleteMr
            (@PathVariable("companyName") String companyName, @PathVariable("mrId") Long mrId, @AuthenticationPrincipal UserDetailsImpl details){
        mrService.deleteMr(companyName, mrId,details);
        return ResponseMessage.responseSuccess("미팅룸 삭제 완료","");
    }
}
