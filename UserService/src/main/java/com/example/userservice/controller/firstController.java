package com.example.userservice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.servlet.http.HttpServletRequest;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/first-service")
public class firstController {

    private final Environment env;

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
    public String check1(HttpServletRequest request){

        log.info("server port="+request.getServerPort());

        return env.getProperty("local.server.port");
    }
}
