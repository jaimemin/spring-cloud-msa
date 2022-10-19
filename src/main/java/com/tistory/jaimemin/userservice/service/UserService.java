package com.tistory.jaimemin.userservice.service;

import com.tistory.jaimemin.userservice.dto.UserDto;
import com.tistory.jaimemin.userservice.entity.UserEntity;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {

    UserDto createUser(UserDto userDto);

    UserDto getUserById(String userId);

    Iterable<UserEntity> getAllUsers();

    UserDto getUserDetailsByEmail(String userName);
}
