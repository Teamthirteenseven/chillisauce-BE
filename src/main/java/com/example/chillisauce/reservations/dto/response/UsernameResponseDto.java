package com.example.chillisauce.reservations.dto.response;

import com.example.chillisauce.reservations.dto.ReservationUserWrapper;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UsernameResponseDto {
    String username;

    public UsernameResponseDto(ReservationUserWrapper dto){
        username = dto.getUsername();
    }
}
