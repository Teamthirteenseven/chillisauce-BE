package com.example.chillisauce.domain.reservations.dto.response;

import com.example.chillisauce.domain.reservations.dto.ReservationUserWrapper;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UsernameResponse {
    String username;

    public UsernameResponse(ReservationUserWrapper dto){
        username = dto.getUsername();
    }
}
