package com.example.chillisauce.spaces.controller;

import com.example.chillisauce.security.UserDetailsImpl;
import com.example.chillisauce.spaces.dto.MrRequestDto;
import com.example.chillisauce.spaces.dto.MrResponseDto;
import com.example.chillisauce.spaces.service.MrService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MrController {
    private final MrService mrService;
    @PostMapping("/mr/{spaceid}")
    public MrResponseDto createMr (@PathVariable("spaceid") Long spaceid, @RequestBody MrRequestDto mrRequestDto, @AuthenticationPrincipal UserDetailsImpl userDetails){
        return mrService.createMr(spaceid, mrRequestDto, userDetails.getUser());
    }
}
