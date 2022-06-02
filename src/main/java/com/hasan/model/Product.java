package com.hasan.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor

public class Product {
    private String name;
    private BigDecimal price;
    private ProductCategory category;

}
