package com.example.springgame;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class Setting {
    @GetMapping("/Setting")
    public String AboutPage(){
        return "Setting";
    }
}
