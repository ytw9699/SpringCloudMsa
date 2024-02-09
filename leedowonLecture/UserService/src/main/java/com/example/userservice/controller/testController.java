package com.example.userservice.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class testController {

    @GetMapping("/second-service")
    public String second(){
        return "test2";
    }

    @GetMapping("/first-service")
    public String service(){
        return "test1";
    }
}
