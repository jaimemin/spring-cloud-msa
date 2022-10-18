package com.tistory.jaimemin.userservice.service;

import com.tistory.jaimemin.userservice.dto.UserDto;
import com.tistory.jaimemin.userservice.entity.UserEntity;

public interface UserService {

    UserDto createUser(UserDto userDto);

    UserDto getUserById(String userId);

    Iterable<UserEntity> getAllUsers();
}
