package com.hasan.service;

import com.hasan.model.Cart;

public interface DiscountService {

    Cart applyDiscountForPerValueToCart(Cart cart, Integer per, Integer discount);

    Cart applyPercentageBasedDiscountToCart(Cart cart);

}
