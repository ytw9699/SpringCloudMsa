package com.example.userservice.controller;

import com.example.userservice.dto.UserDto;
import com.example.userservice.service.UserService;
import com.example.userservice.vo.Greeting;
import com.example.userservice.vo.RequestUser;
import com.example.userservice.vo.ResponseUser;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;
import com.example.userservice.jpa.UserEntity;

@RestController
@RequestMapping("/user-service")
public class UserController {

    private Environment env;
    private final UserService userService;

    @Autowired
    private Greeting greeting;

    @Autowired
    public UserController(Environment env,  UserService userService){
        this.userService = userService;
        this.env = env;
    }

    @GetMapping("/health_check")
    public String status() {
        return String.format("It's Working in User Service"
                + ", port(local.server.port) = " +env.getProperty("local.server.port"));
    }

    @GetMapping("/welcome")
    public String welcome(){
        //return env.getProperty("greeting.message");
        return greeting.getMessage();
    }

    /** 회원 가입 */
    @PostMapping("/users")
    public ResponseEntity<ResponseUser> createUser(@RequestBody RequestUser user) {

        ModelMapper mapper = new ModelMapper();

        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        UserDto userDto = mapper.map(user, UserDto.class);

        UserDto result = userService.createUser(userDto);

        ResponseUser responseUser = mapper.map(result, ResponseUser.class);

        return ResponseEntity.status(HttpStatus.CREATED).body(responseUser);
    }

    /** 유저 리스트 조회 */
    @GetMapping("/users")
    public ResponseEntity<List<ResponseUser>> getUsers() {
        final Iterable<UserEntity> userList = userService.getUserByAll();

        List<ResponseUser> results = new ArrayList<>();

        ModelMapper mapper = new ModelMapper();

        userList.forEach(user -> {
            results.add(mapper.map(user, ResponseUser.class));
        });

        return ResponseEntity.status(HttpStatus.OK).body(results);
    }

    /** 유저 조회 */
    @GetMapping("/users/{userId}")
    public ResponseEntity<ResponseUser> getUser(@PathVariable String userId) {

        final UserDto userDto = userService.getUserByUserId(userId);

        ResponseUser result = new ModelMapper().map(userDto, ResponseUser.class);

        return ResponseEntity.status(HttpStatus.OK).body(result);
    }
}