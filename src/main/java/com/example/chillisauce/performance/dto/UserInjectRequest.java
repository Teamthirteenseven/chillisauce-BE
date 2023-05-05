package com.example.chillisauce.performance.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @param count 생성할 유저 수
 */
@Builder
public record UserInjectRequest(String email, String password, String passwordCheck, String username, Integer count) {
