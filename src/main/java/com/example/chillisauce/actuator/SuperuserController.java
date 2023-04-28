package com.example.chillisauce.actuator;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class SuperuserController {

    @PostMapping("/superuser/signup")
    public String signup() {
        return null;
    }
}
