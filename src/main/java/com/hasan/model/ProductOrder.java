package com.hasan.model;

import lombok.*;

import java.math.BigDecimal;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class ProductOrder {
    private Product product;
    private Integer count;
    private BigDecimal totalPrice;

}
