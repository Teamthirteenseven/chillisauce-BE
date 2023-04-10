package com.example.chillisauce.spaces.controller;

import com.example.chillisauce.message.ResponseMessage;
import com.example.chillisauce.security.UserDetailsImpl;
import com.example.chillisauce.spaces.dto.SpaceRequestDto;
import com.example.chillisauce.spaces.entity.Floor;
import com.example.chillisauce.spaces.service.SpaceService;
import com.example.chillisauce.users.entity.Companies;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
public class SpaceController {
    private final SpaceService spaceService;

    //플로우 안에 공간 생성
    @PostMapping("/{companyName}/{floorId}")
    public ResponseEntity<ResponseMessage> createSpaceinfloor
            (@PathVariable("companyName") String companyName, @RequestBody SpaceRequestDto spaceRequestDto, @AuthenticationPrincipal UserDetailsImpl details,@PathVariable("floorId") Long floorId) {
        spaceService.createSpaceinfloor(companyName, spaceRequestDto, details, floorId);
        return ResponseMessage.responseSuccess("공간 생성 성공","");
    }
    //공간 생성
    @PostMapping("/spaces/{companyName}/space")
    public ResponseEntity<ResponseMessage> createSpace
            (@PathVariable("companyName") String companyName, @RequestBody SpaceRequestDto spaceRequestDto, @AuthenticationPrincipal UserDetailsImpl details) {
        spaceService.createSpace(companyName, spaceRequestDto, details);
        return ResponseMessage.responseSuccess("공간 생성 성공","");
    }

    //공간 전체조회
    @GetMapping("/{companyName}/space")
    public ResponseEntity<ResponseMessage> allSpacelist
            (@PathVariable("companyName") String companyName,@AuthenticationPrincipal UserDetailsImpl details) {

        return ResponseMessage.responseSuccess("공간 조회 성공",spaceService.allSpacelist(companyName, details));
    }

    //공간 선택조회
    @GetMapping("/{companyName}/space/{spaceId}")
    public ResponseEntity<ResponseMessage> getSpacelist
            (@PathVariable("companyName") String companyName, @PathVariable("spaceId") Long spaceId, @AuthenticationPrincipal UserDetailsImpl details) {

        return ResponseMessage.responseSuccess("공간 조회 성공",spaceService.getSpacelist(companyName, spaceId, details));
    }
    //공간 개별 수정
    @PatchMapping("/{companyName}/space/{spaceId}")
    public ResponseEntity<ResponseMessage> updateSpace
            (@PathVariable("companyName") String companyName, @PathVariable("spaceId") Long spaceId,
                                      @RequestBody SpaceRequestDto spaceRequestDto, @AuthenticationPrincipal UserDetailsImpl details) {
        spaceService.updateSpace(companyName, spaceId, spaceRequestDto,details);
        return ResponseMessage.responseSuccess("공간 수정 성공","");
    }
    //공간 삭제
    @DeleteMapping("/{companyName}/space/{spaceId}")
    public ResponseEntity<ResponseMessage> deleteSpace
            (@PathVariable("companyName") String companyName, @PathVariable("spaceId") Long spaceId, @AuthenticationPrincipal UserDetailsImpl details){
        spaceService.deleteSpace(companyName,spaceId,details);
        return ResponseMessage.responseSuccess("공간 삭제 성공","");
    }
}
