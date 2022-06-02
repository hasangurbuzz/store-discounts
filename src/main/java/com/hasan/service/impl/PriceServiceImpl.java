package com.hasan.service.impl;

import com.hasan.model.Cart;
import com.hasan.model.ProductOrder;
import com.hasan.service.PriceService;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

public class PriceServiceImpl implements PriceService {

    @Override
    public ProductOrder getProductOrderWithPriceCalculated(ProductOrder order) {
        BigDecimal price = order.getProduct().getPrice();
        int count = order.getCount();
        BigDecimal totalPrice = price.multiply(BigDecimal.valueOf(count));
        order.setTotalPrice(totalPrice);
        return order;

    }

    @Override
    public Cart getCartWithPriceCalculated(Cart cart) {
        List<BigDecimal> allOrdersPriceList = cart.getOrders()
                .stream()
                .map(ProductOrder::getTotalPrice)
                .collect(Collectors.toList());

        BigDecimal totalPriceOfCart = allOrdersPriceList
                .stream()
                .reduce(BigDecimal.ZERO,BigDecimal::add);

        cart.setTotalPrice(totalPriceOfCart);

        return cart;

    }
}
