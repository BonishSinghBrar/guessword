package com.example.springgame;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class sign {
    @GetMapping("/sign")
    public String signupPage(){
        return "sign";
    }
}
