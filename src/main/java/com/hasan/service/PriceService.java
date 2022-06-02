package com.hasan.service;

import com.hasan.model.Cart;
import com.hasan.model.ProductOrder;

public interface PriceService {

    ProductOrder getProductOrderWithPriceCalculated(ProductOrder order);

    Cart getCartWithPriceCalculated(Cart cart);
}
