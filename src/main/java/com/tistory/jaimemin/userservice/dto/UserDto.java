package com.tistory.jaimemin.userservice.dto;

import com.tistory.jaimemin.userservice.vo.ResponseOrder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class UserDto {

    private String email;

    private String name;

    private String pwd;

    private String userId;

    private LocalDateTime createdAt;

    private String encryptedPwd;

    private List<ResponseOrder> orders;
}
