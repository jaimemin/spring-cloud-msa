package com.tistory.jaimemin.userservice.service;

import com.tistory.jaimemin.userservice.client.OrderServiceClient;
import com.tistory.jaimemin.userservice.dto.UserDto;
import com.tistory.jaimemin.userservice.entity.UserEntity;
import com.tistory.jaimemin.userservice.error.FeignErrorDecoder;
import com.tistory.jaimemin.userservice.repository.UserRepository;
import com.tistory.jaimemin.userservice.vo.ResponseOrder;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final Environment environment;

    private final RestTemplate restTemplate;

    private final UserRepository userRepository;

    private final BCryptPasswordEncoder passwordEncoder;

    private final OrderServiceClient orderServiceClient;

    private final CircuitBreakerFactory circuitBreakerFactory;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity userEntity = userRepository.findByEmail(username);

        if (userEntity == null) {
            throw new UsernameNotFoundException(String.format("User %s does not exist", username));
        }

        return new User(userEntity.getEmail()
                , userEntity.getEncryptedPwd()
                , true
                , true
                , true
                , true
                , new ArrayList<>());
    }

    @Override
    @Transactional
    public UserDto createUser(UserDto userDto) {
        userDto.setUserId(UUID.randomUUID().toString());

        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        UserEntity userEntity = modelMapper.map(userDto, UserEntity.class);
        userEntity.setEncryptedPwd(passwordEncoder.encode(userDto.getPwd()));
        userRepository.save(userEntity);

        return modelMapper.map(userEntity, UserDto.class);
    }

    @Override
    public UserDto getUserById(String userId) {
        UserEntity userEntity = userRepository.findByUserId(userId);

        if (userEntity == null) {
            throw new UsernameNotFoundException(String.format("User %s not found.", userId));
        }

        UserDto userDto = new ModelMapper().map(userEntity, UserDto.class);
        // List<ResponseOrder> orders = getOrdersByRestTemplate(userId);
        /**
         * Using a Feign Client
         * Feign Exception Handling
         */
        // List<ResponseOrder> orders = orderServiceClient.getOrders(userId);
        log.info("Before call orders microservice");
        CircuitBreaker circuitbreaker = circuitBreakerFactory.create("circuitbreaker");
        List<ResponseOrder> orders = circuitbreaker.run(() -> orderServiceClient.getOrders(userId)
                , throwable -> new ArrayList<>());
        log.info("After call orders microservice");

        userDto.setOrders(orders);

        return userDto;
    }

    @Override
    public Iterable<UserEntity> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public UserDto getUserDetailsByEmail(String email) {
        UserEntity userEntity = userRepository.findByEmail(email);

        if (userEntity == null) {
            throw new UsernameNotFoundException(String.format("User %s not found.", email));
        }

        return new ModelMapper().map(userEntity, UserDto.class);
    }

    private List<ResponseOrder> getOrdersByRestTemplate(String userId) {
        String orderUrl = String.format(environment.getProperty("order_service.url"), userId);
        ResponseEntity<List<ResponseOrder>> ordersResponse = restTemplate.exchange(orderUrl
                , HttpMethod.GET
                , null
                , new ParameterizedTypeReference<List<ResponseOrder>>() {
                });
        List<ResponseOrder> orders = ordersResponse.getBody();

        return orders;
    }
}
