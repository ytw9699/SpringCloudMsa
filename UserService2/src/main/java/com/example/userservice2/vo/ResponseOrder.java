package com.example.userservice2.vo;

import lombok.Data;
import java.util.Date;

@Data
public class ResponseOrder {

    private String productId;
    private Integer qty;//수량
    private Integer unitPrice;//단가
    private Integer totalPrice;//토탈
    private Date createdAt;//주문날짜
    private String orderId;
}
