package com.hasan.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ProductCategory {
    private String name;
    private Boolean isDiscounted = false;

}
