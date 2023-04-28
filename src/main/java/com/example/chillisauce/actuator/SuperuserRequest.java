package com.example.chillisauce.actuator;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SuperuserRequest {
    String username;
    String password;
}
