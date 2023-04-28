package com.example.chillisauce.actuator;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

@RestController
@RequiredArgsConstructor
public class SuperuserController {
    private final SuperuserService superuserService;

    @PostMapping("/superuser/login")
    public String loginSuperuser(@RequestBody SuperuserRequest request, HttpServletResponse response) {
        return superuserService.loginSuperuser(request, response);
    }
}
