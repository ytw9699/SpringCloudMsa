package com.example.userservice.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/first-service")
public class firstController {

    @GetMapping("/welcome")
    public String welcome(){
        log.info("first service welcome");
        return "first service welcome";
    }

    @GetMapping("/message")
    public String message(@RequestHeader("first-request") String header){
        log.info(header);
        return "message="+header;
    }

    @GetMapping("/check")
    public String check1(){
        return "check1";
    }
}
