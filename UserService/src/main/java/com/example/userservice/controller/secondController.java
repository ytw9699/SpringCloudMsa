package com.example.userservice.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/second-service")
public class secondController {

    @GetMapping("/welcome")
    public String welcome(){
        log.info("second service welcome");
        return "second service welcome";
    }

    @GetMapping("/message")
    public String message(@RequestHeader("second-request") String header){
        log.info(header);
        return "message="+header;
    }

    @GetMapping("/check")
    public String check2(){
        return "check2";
    }
}