package com.hasan.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class Cart {
    private List<ProductOrder> orders;
    private User user;
    private BigDecimal totalPrice;


}
