package com.example.chillisauce.users.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserListResponseDto {
    List<UserDetailResponseDto> userList;
}
