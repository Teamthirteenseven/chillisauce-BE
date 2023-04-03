package com.example.chillisauce.spaces.controller;

import com.example.chillisauce.message.ResponseMessage;
import com.example.chillisauce.security.UserDetailsImpl;
import com.example.chillisauce.spaces.dto.BoxRequestDto;
import com.example.chillisauce.spaces.dto.BoxResponseDto;
import com.example.chillisauce.spaces.dto.SpaceRequestDto;
import com.example.chillisauce.spaces.dto.SpaceResponseDto;
import com.example.chillisauce.spaces.service.SpaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
public class SpaceController {
    private final SpaceService spaceService;


    @PostMapping("/{companyName}/space")
    public SpaceResponseDto createSpace
            (@PathVariable("companyName") String companyName, @RequestBody SpaceRequestDto spaceRequestDto, @AuthenticationPrincipal UserDetailsImpl Details) {
        return spaceService.createSpace(companyName, spaceRequestDto, Details);

    }

    @GetMapping("/{companyName}/space/{spaceid}")
    public ResponseEntity<ResponseMessage> getSpacelist(@PathVariable("companyName") String companyName, @PathVariable("spaceid") Long spaceId) {

        return ResponseMessage.responseSuccess("사무실 조회 성공",spaceService.getSpacelist(companyName, spaceId));
    }
}
