package com.tistory.jaimemin.userservice.controller;

import com.tistory.jaimemin.userservice.dto.UserDto;
import com.tistory.jaimemin.userservice.entity.UserEntity;
import com.tistory.jaimemin.userservice.service.UserService;
import com.tistory.jaimemin.userservice.vo.Greeting;
import com.tistory.jaimemin.userservice.vo.RequestUser;
import com.tistory.jaimemin.userservice.vo.ResponseUser;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/")
@RequiredArgsConstructor
public class UserController {

    private final Greeting greeting;

    private final UserService userService;

    private final Environment environment;

    @GetMapping("/health_check")
    public String status() {
        return String.format("It's Working in User Service on PORT %s", environment.getProperty("local.server.port"));
    }

    @GetMapping("/welcome")
    public String welcome() {
        return greeting.getMessage();
    }

    @PostMapping("/users")
    public ResponseEntity<ResponseUser> createUser(@RequestBody RequestUser user) {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        UserDto userDto = modelMapper.map(user, UserDto.class);
        userService.createUser(userDto);

        ResponseUser responseUser = modelMapper.map(userDto, ResponseUser.class);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(responseUser);
    }

    @GetMapping("/users")
    public ResponseEntity<List<ResponseUser>> getUsers() {
        Iterable<UserEntity> users = userService.getAllUsers();
        List<ResponseUser> result = new ArrayList<>();
        users.forEach(v -> {
            result.add(new ModelMapper().map(v, ResponseUser.class));
        });

        return ResponseEntity.status(HttpStatus.OK)
                .body(result);
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<ResponseUser> getUser(@PathVariable("userId") String userId) {
        UserDto userDto = userService.getUserById(userId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(new ModelMapper().map(userDto, ResponseUser.class));
    }
}
