package com.example.chillisauce.spaces.controller;

import com.example.chillisauce.security.UserDetailsImpl;
import com.example.chillisauce.spaces.dto.BoxRequestDto;
import com.example.chillisauce.spaces.dto.BoxResponseDto;
import com.example.chillisauce.spaces.service.BoxService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class BoxController {

    private final BoxService boxService;

    @PostMapping("/box/{spaceid}")
    public BoxResponseDto createBox (@PathVariable("spaceid") Long spaceid,@RequestBody BoxRequestDto boxRequestDto, @AuthenticationPrincipal UserDetailsImpl userDetails){
        return boxService.createBox(spaceid, boxRequestDto, userDetails.getUser());
    }


}
