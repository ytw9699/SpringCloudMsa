package com.example.userservice2.error;

import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@RequiredArgsConstructor
@Component
public class FeignErrorDecoder implements ErrorDecoder {

    private final Environment env;

    @Override
    public Exception decode(String methodKey, Response response) {

        switch (response.status()) {

            case 400:
                break;

            case 404:
                if(methodKey.contains("getOrders")) { //해당 메소드의 경우만
                    return new ResponseStatusException(
                    HttpStatus.valueOf(response.status()),env.getProperty("order_service.exception.orders_is_empty"));
                }
                break;

            default:
                return new Exception(response.reason());
        }

        return null;
    }
}