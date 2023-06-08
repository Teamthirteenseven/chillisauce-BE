package com.example.chillisauce.domain.spaces.controller;

import com.example.chillisauce.global.message.ResponseMessage;
import com.example.chillisauce.global.security.UserDetailsImpl;
import com.example.chillisauce.domain.spaces.dto.request.MrRequestDto;
import com.example.chillisauce.domain.spaces.dto.response.MrResponseDto;
import com.example.chillisauce.domain.spaces.service.MrService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class MrController {
    private final MrService mrService;
    @PostMapping("/mr/{companyName}/{spaceId}")
    public ResponseEntity<ResponseMessage<String>> createMr
            (@PathVariable("companyName") String companyName,@PathVariable("spaceId") Long spaceId, @RequestBody MrRequestDto mrRequestDto, @AuthenticationPrincipal UserDetailsImpl details){
        mrService.createMr(companyName, spaceId, mrRequestDto, details);
        return ResponseMessage.responseSuccess("미팅룸 생성 성공","");
    }

    @PatchMapping("/mr/{companyName}/{mrId}")
    public ResponseEntity<ResponseMessage<String>> updateMr
            (@PathVariable("companyName") String companyName, @PathVariable("mrId") Long mrId, @RequestBody MrRequestDto mrRequestDto, @AuthenticationPrincipal UserDetailsImpl details){
        mrService.updateMr(companyName, mrId, mrRequestDto, details);
        return ResponseMessage.responseSuccess("미팅룸 수정 성공","");
    }

    @DeleteMapping("/mr/{companyName}/{mrId}")
    public ResponseEntity<ResponseMessage<String>> deleteMr
            (@PathVariable("companyName") String companyName, @PathVariable("mrId") Long mrId, @AuthenticationPrincipal UserDetailsImpl details){
        mrService.deleteMr(companyName, mrId,details);
        return ResponseMessage.responseSuccess("미팅룸 삭제 완료","");
    }

    @GetMapping("/mr/{companyName}")
    public ResponseEntity<ResponseMessage<List<MrResponseDto>>> mrlist
            (@PathVariable("companyName") String companyName,@AuthenticationPrincipal UserDetailsImpl details) {

        return ResponseMessage.responseSuccess("미팅룸 조회 성공",mrService.mrlist(companyName, details));
    }
}
