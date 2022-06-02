package com.hasan.service.impl;

import com.hasan.exception.SubscriptionNotFoundException;
import com.hasan.exception.UserNotFoundException;
import com.hasan.model.*;
import com.hasan.service.DiscountService;
import com.hasan.service.PriceService;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DiscountServiceImpl implements DiscountService {

    private final PriceService priceService;

    public DiscountServiceImpl(PriceService priceService) {
        this.priceService = priceService;
    }


    @Override
    public Cart applyDiscountForPerValueToCart(Cart cart, Integer perDollar, Integer discount) {
        cart.getOrders().forEach(order -> {
            BigDecimal discountFactor = Arrays.stream(order.getTotalPrice()
                    .divideAndRemainder(BigDecimal.valueOf(perDollar)))
                    .findFirst()
                    .orElse(BigDecimal.ZERO);

            BigDecimal amountOfDiscount = discountFactor.multiply(BigDecimal.valueOf(discount));

            order.setTotalPrice(
                    order.getTotalPrice()
                            .subtract(amountOfDiscount)
            );
        });

        cart = priceService.getCartWithPriceCalculated(cart);
        return cart;
    }

    @Override
    public Cart applyPercentageBasedDiscountToCart(Cart cart) {
        if (cart.getUser() == null)
            throw new UserNotFoundException("User data not found in Cart");

        if (cart.getUser().getSubscriptions() == null)
            throw new SubscriptionNotFoundException("User does not have any subscriptions");

        SubscriptionType bestSubscriptionType = getBestSubscriptionType(cart.getUser());

        List<Benefit> subscriptionBenefits = cart.getUser().getSubscriptions()
                .stream()
                .filter(subscription -> subscription.getType().equals(bestSubscriptionType))
                .map(Subscription::getBenefits)
                .findFirst()
                .orElseThrow();


        int discountRate = subscriptionBenefits.stream()
                .findFirst()
                .map(Benefit::getDiscountRate)
                .orElseThrow();


        List<ProductOrder> discountedProductOrders = cart.getOrders()
                .stream()
                .filter(order ->
                        order.getProduct()
                                .getCategory()
                                .getIsDiscounted()
                                .equals(true))
                .collect(Collectors.toList());

        List<ProductOrder> notDiscountedProductOrders = cart.getOrders()
                .stream()
                .filter(order ->
                        order.getProduct()
                                .getCategory()
                                .getIsDiscounted()
                                .equals(false))
                .collect(Collectors.toList());

        List<ProductOrder> percentBasedDiscountAppliedProductOrders = discountedProductOrders.stream()
                .map(order -> new ProductOrder(
                        order.getProduct(),
                        order.getCount(),
                        order.getTotalPrice()
                                .multiply(BigDecimal.valueOf(100)
                                        .subtract(BigDecimal.valueOf(discountRate)))
                                .divide(BigDecimal.valueOf(100))
                )).collect(Collectors.toList());


        List<ProductOrder> discountAppliedProductList = getMergedProductOrders(
                notDiscountedProductOrders,
                percentBasedDiscountAppliedProductOrders
        );

        cart.setOrders(discountAppliedProductList);

        return priceService.getCartWithPriceCalculated(cart);
    }

    private SubscriptionType getBestSubscriptionType(User user) {

        return user
                .getSubscriptions()
                .stream()
                .map(Subscription::getType)
                .sorted(Comparator.comparing(SubscriptionType::getPriority))
                .findFirst()
                .orElseThrow();
    }

    private List<ProductOrder> getMergedProductOrders(
            List<ProductOrder> productOrdersA,
            List<ProductOrder> productOrdersB) {

        return Stream.concat(
                productOrdersA.stream(),
                productOrdersB.stream()
        ).collect(Collectors.toList());
    }

}
