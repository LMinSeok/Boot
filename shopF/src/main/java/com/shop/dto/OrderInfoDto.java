package com.shop.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
public class OrderInfoDto {

    private Long orderId;

    private LocalDateTime orderDate;
}
