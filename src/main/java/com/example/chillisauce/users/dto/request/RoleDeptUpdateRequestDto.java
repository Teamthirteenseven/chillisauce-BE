package com.example.chillisauce.users.dto.request;

import com.example.chillisauce.users.entity.UserRoleEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RoleDeptUpdateRequestDto {
    private UserRoleEnum role;
    private boolean updateRole;
//    private String dept;
}
