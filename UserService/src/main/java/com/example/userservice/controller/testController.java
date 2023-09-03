package com.example.userservice.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class testController {

    @GetMapping("/second-service")
    public String second(){
        System.out.println("second service");
        return "second service";
    }

    @GetMapping("/first-service")
    public String service(){
        System.out.println("first service");
        return "first service";
    }
}
