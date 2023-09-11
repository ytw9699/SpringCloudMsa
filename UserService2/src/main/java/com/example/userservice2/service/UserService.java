package com.example.userservice2.service;

import com.example.userservice2.dto.UserDto;
import com.example.userservice2.jpa.UserEntity;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {

    UserDto createUser(UserDto userDto);
    UserDto getUserByUserId(String userId);
    Iterable<UserEntity> getUserByAll();//UserEntity를 추후 dto로 변경해도됨
}