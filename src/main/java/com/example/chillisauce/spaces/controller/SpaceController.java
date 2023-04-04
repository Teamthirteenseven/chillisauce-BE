package com.example.chillisauce.spaces.controller;

import com.example.chillisauce.message.ResponseMessage;
import com.example.chillisauce.security.UserDetailsImpl;
import com.example.chillisauce.spaces.dto.SpaceRequestDto;
import com.example.chillisauce.spaces.service.SpaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
public class SpaceController {
    private final SpaceService spaceService;

    //사무실 생성
    @PostMapping("/{companyName}/space")
    public ResponseEntity<ResponseMessage> createSpace
            (@PathVariable("companyName") String companyName, @RequestBody SpaceRequestDto spaceRequestDto, @AuthenticationPrincipal UserDetailsImpl details) {
        return ResponseMessage.responseSuccess("사무실 추가 성공",spaceService.createSpace(companyName, spaceRequestDto, details));

    }
    //사무실 조회
    @GetMapping("/{companyName}/space/{spaceId}")
    public ResponseEntity<ResponseMessage> getSpacelist
            (@PathVariable("companyName") String companyName, @PathVariable("spaceId") Long spaceId) {

        return ResponseMessage.responseSuccess("사무실 조회 성공",spaceService.getSpacelist(companyName, spaceId));
    }
    //사무실 전체 수정
    @PutMapping("/{companyName}/space/{spaceId}")
    public ResponseEntity<ResponseMessage> updateSpace
            (@PathVariable("companyName") String companyName, @PathVariable("spaceId") Long spaceId,
                                      @RequestBody SpaceRequestDto spaceRequestDto, @AuthenticationPrincipal UserDetailsImpl details) {
        return ResponseMessage.responseSuccess("사무실 수정 성공",spaceService.updateSpace(companyName, spaceId, spaceRequestDto,details));
    }

    @DeleteMapping("/{companyName}/space/{spaceId}")
    public ResponseEntity<ResponseMessage> deleteSpace
            (@PathVariable("companyName") String companyName, @PathVariable("spaceId") Long spaceId, @AuthenticationPrincipal UserDetailsImpl details){
        return ResponseMessage.responseSuccess("사무실 삭제 성공",spaceService.deleteSpace(companyName,spaceId,details));
    }
}
