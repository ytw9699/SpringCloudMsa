package com.example.userservice2.controller;

import com.example.userservice2.dto.UserDto;
import com.example.userservice2.jpa.UserEntity;
import com.example.userservice2.service.UserService;
import com.example.userservice2.vo.Greeting;
import com.example.userservice2.vo.RequestUser;
import com.example.userservice2.vo.ResponseUser;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/")
public class UserController {

    private final Environment env;
    private final Greeting greeting;
    private final UserService userService;

    @GetMapping("/health_check")
    public String status() {
        return String.format("It's Working in User Service"
                + ", port(local.server.port) = " +env.getProperty("local.server.port")
        );
    }

    @GetMapping("/welcome")
    public String welcome(){
        return env.getProperty("greeting.message");
    }

    @GetMapping("/welcome2")
    public String welcome2(){
        return greeting.getMessage();
    }

    /** 회원 가입 */
    @PostMapping("/users")
    public ResponseEntity createUser(@RequestBody RequestUser user) {
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