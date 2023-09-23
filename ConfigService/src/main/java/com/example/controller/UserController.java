package com.example.controller;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/")
public class UserController {

    @GetMapping("/welcome")
    public String welcome(){
        return "welcome";
    }

}