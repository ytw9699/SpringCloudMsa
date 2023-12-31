package com.example.userservice2.service;

import com.example.userservice2.client.OrderServiceClient;
import com.example.userservice2.dto.UserDto;
import com.example.userservice2.jpa.UserEntity;
import com.example.userservice2.jpa.UserRepository;
import com.example.userservice2.vo.ResponseOrder;
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
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.client.RestTemplate;

@RequiredArgsConstructor
@Service
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final Environment env;
    private final RestTemplate restTemplate;
    private final OrderServiceClient orderServiceClient;
    private final CircuitBreakerFactory circuitBreakerFactory;
    @Override
    public UserDto createUser(UserDto userDto) {

        userDto.setUserId(UUID.randomUUID().toString());

        ModelMapper mapper = new ModelMapper();
                    mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        UserEntity userEntity = mapper.map(userDto, UserEntity.class);

        userEntity.setEncryptedPwd(passwordEncoder.encode(userDto.getPwd()));

        userRepository.save(userEntity);

        UserDto returnUserDto = mapper.map(userEntity, UserDto.class);

        return returnUserDto;
    }

    @Override
    public UserDto getUserByUserId(String userId) {

        UserEntity userEntity = userRepository.findByUserId(userId);

        if (userEntity == null)
            throw new UsernameNotFoundException("User not found");//적절한 예외는 아니지만 일단 임시용

        UserDto userDto = new ModelMapper().map(userEntity, UserDto.class);

       // List<ResponseOrder> orders = new ArrayList<>();

        /* Using as rest template */
      /*  String orderUrl = String.format(env.getProperty("order_service.url"), userId);

        ResponseEntity<List<ResponseOrder>> orderListResponse = restTemplate.exchange(orderUrl, HttpMethod.GET,
                    null, new ParameterizedTypeReference<List<ResponseOrder>>() {});

        List<ResponseOrder> ordersList = orderListResponse.getBody();
*/

        /* Using as feign client */
        /* Feign Exception Handling */
      /*  List<ResponseOrder> ordersList = null;

        try {// 404 예외가 발생하면 주문리스트만 빼고 응답하자
            ordersList = orderServiceClient.getOrders(userId);
        }catch (FeignException ex){
            log.error(ex.getMessage());
        }*/

        /* ErrorDecoder */
        //List<ResponseOrder> ordersList = orderServiceClient.getOrders(userId);

        log.info("Before user call");
        CircuitBreaker circuitBreaker = circuitBreakerFactory.create("circuitbreaker");

        List<ResponseOrder> ordersList = circuitBreaker.run(() -> orderServiceClient.getOrders(userId),
                throwable -> new ArrayList<>());//문제가 생기면 비어있는 값을 반환
        log.info("After user call");

        userDto.setOrders(ordersList);

        return userDto;
    }

    @Override
    public Iterable<UserEntity> getUserByAll() {
        return userRepository.findAll();
    }

    @Override
    public UserDto getUserDetailsByEmail(String email) {

        UserEntity userEntity = userRepository.findByEmail(email);

        if (userEntity == null)
            throw new UsernameNotFoundException(email);

        UserDto userDto = new ModelMapper().map(userEntity, UserDto.class);

        return userDto;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        final UserEntity userEntity = userRepository.findByEmail(username);//username = email

        if (userEntity == null)
            throw new UsernameNotFoundException(username);

        return new User(
                userEntity.getEmail(),
                userEntity.getEncryptedPwd(),//암호화된 비밀번호를 비교하자
                true,
                true,
                true,
                true,
                new ArrayList<>()
        );
    }
}