package com.hasan.service;

import com.hasan.exception.SubscriptionNotFoundException;
import com.hasan.exception.UserNotFoundException;
import com.hasan.model.*;
import com.hasan.service.impl.DiscountServiceImpl;
import com.hasan.service.impl.PriceServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.hasan.model.SubscriptionType.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DiscountServiceTest {

    DiscountService discountService;
    PriceService priceService;

    @BeforeEach
    void setUp() {
        priceService = new PriceServiceImpl();
        discountService = new DiscountServiceImpl(priceService);
    }

    @Test
    void canApplyDiscountForPerValue() {
        int perDollar = 200;
        int discount = 5;

        ProductCategory phoneCategory = new ProductCategory();
        ProductCategory laptopCategory = new ProductCategory();
        phoneCategory.setName("phone");
        laptopCategory.setName("laptop");

        Product phone = new Product();
        phone.setCategory(phoneCategory);
        phone.setPrice(BigDecimal.valueOf(1999.99));
        phone.setName("IPhone");

        Product laptop = new Product();
        laptop.setCategory(laptopCategory);
        laptop.setPrice(BigDecimal.valueOf(4500.50));
        laptop.setName("IPhone");

        ProductOrder phoneOrder = new ProductOrder();
        phoneOrder.setProduct(phone);
        phoneOrder.setCount(1);

        ProductOrder laptopOrder = new ProductOrder();
        laptopOrder.setProduct(laptop);
        laptopOrder.setCount(2);
        ProductOrder priceCalculatedPhoneOrder = priceService.getProductOrderWithPriceCalculated(phoneOrder);
        ProductOrder priceCalculatedLaptopOrder = priceService.getProductOrderWithPriceCalculated(laptopOrder);

        List<ProductOrder> orderList = List.of(
                priceCalculatedLaptopOrder,
                priceCalculatedPhoneOrder
        );

        Cart cart = new Cart();
        cart.setOrders(orderList);


        Cart priceCalculatedCart = priceService.getCartWithPriceCalculated(cart);

        priceCalculatedCart.getOrders().forEach(order -> {
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
        Cart expectedCart = priceCalculatedCart;

        Cart discountAppliedCart = discountService
                .applyDiscountForPerValueToCart(priceCalculatedCart, perDollar, discount);

        BigDecimal actualDiscountedPrice = discountAppliedCart.getTotalPrice();
        BigDecimal expectedDiscountedPrice = expectedCart.getTotalPrice();

        assertEquals(expectedDiscountedPrice, actualDiscountedPrice);


    }

    @Test
    void canApplyPercentageBasedDiscountsToCart() {

        ProductCategory phoneCategory = new ProductCategory();
        ProductCategory laptopCategory = new ProductCategory();
        phoneCategory.setName("phone");
        laptopCategory.setName("laptop");
        laptopCategory.setIsDiscounted(true);

        Product phone = new Product();
        phone.setCategory(phoneCategory);
        phone.setPrice(BigDecimal.valueOf(2000));
        phone.setName("Phone XS");

        Product laptop = new Product();
        laptop.setCategory(laptopCategory);
        laptop.setPrice(BigDecimal.valueOf(4500.50));
        laptop.setName("Laptop GTX1050");

        ProductOrder phoneOrder = new ProductOrder();
        phoneOrder.setProduct(phone);
        phoneOrder.setCount(1);

        ProductOrder laptopOrder = new ProductOrder();
        laptopOrder.setProduct(laptop);
        laptopOrder.setCount(2);
        ProductOrder priceCalculatedPhoneOrder = priceService.getProductOrderWithPriceCalculated(phoneOrder);
        ProductOrder priceCalculatedLaptopOrder = priceService.getProductOrderWithPriceCalculated(laptopOrder);

        List<ProductOrder> orderList = List.of(
                priceCalculatedLaptopOrder,
                priceCalculatedPhoneOrder
        );

        Cart cart = new Cart();
        cart.setOrders(orderList);


        Cart priceCalculatedCart = priceService.getCartWithPriceCalculated(cart);

        Benefit silverCardBenefit = new Benefit();
        silverCardBenefit.setDiscountRate(20);

        Benefit goldCardBenefit = new Benefit();
        goldCardBenefit.setDiscountRate(30);

        Benefit affiliateBenefit = new Benefit();
        affiliateBenefit.setDiscountRate(20);

        Benefit twoYearBenefit = new Benefit();
        twoYearBenefit.setDiscountRate(30);


        Subscription silverCard = new Subscription();
        silverCard.setType(SILVER_CARD);
        silverCard.setBenefits(
                List.of(silverCardBenefit)
        );

        Subscription goldCard = new Subscription();
        goldCard.setType(GOLD_CARD);
        goldCard.setBenefits(
                List.of(goldCardBenefit)
        );
        Subscription affiliate = new Subscription();
        affiliate.setType(AFFILIATE);
        affiliate.setBenefits(
                List.of(affiliateBenefit)
        );

        Subscription twoYearSubs = new Subscription();
        twoYearSubs.setType(TWO_YEAR_MEMBERSHIP);
        twoYearSubs.setBenefits(
                List.of(twoYearBenefit)
        );

        User user = new User();
        user.setUsername("user");
        user.setSubscriptions(
                List.of(
                        silverCard,
                        goldCard

                )
        );

        priceCalculatedCart.setUser(user);

        SubscriptionType bestSubscriptionType = priceCalculatedCart.getUser()
                .getSubscriptions()
                .stream()
                .map(Subscription::getType)
                .sorted(Comparator.comparing(SubscriptionType::getPriority))
                .findFirst()
                .orElseThrow();

        List<Benefit> subscriptionBenefits = user.getSubscriptions()
                .stream()
                .filter(subscription -> subscription.getType().equals(bestSubscriptionType))
                .findFirst()
                .map(Subscription::getBenefits)
                .orElseThrow();

        int discountRate = subscriptionBenefits.stream()
                .findFirst()
                .map(Benefit::getDiscountRate)
                .orElseThrow();


        List<ProductOrder> discountedProductOrders = priceCalculatedCart.getOrders()
                .stream()
                .filter(order -> order.getProduct().getCategory().getIsDiscounted().equals(true))
                .collect(Collectors.toList());

        List<ProductOrder> notDiscountedProductOrders = priceCalculatedCart.getOrders()
                .stream()
                .filter((order) -> order.getProduct().getCategory().getIsDiscounted().equals(false))
                .collect(Collectors.toList());

        List<ProductOrder> percentBasedDiscountAppliedProductOrders = discountedProductOrders.stream()
                .map(order -> new ProductOrder(
                        order.getProduct(),
                        order.getCount(),
                        order.getTotalPrice()
                                .multiply(BigDecimal.valueOf(100 - discountRate))
                                .divide(BigDecimal.valueOf(100))
                )).collect(Collectors.toList());


        List<ProductOrder> discountAppliedProductList = Stream.concat(
                notDiscountedProductOrders.stream(),
                percentBasedDiscountAppliedProductOrders.stream()
        ).collect(Collectors.toList());


        Cart discountedCart = new Cart();
        discountedCart.setUser(user);
        discountedCart.setOrders(discountAppliedProductList);

        Cart expectedCart = priceService.getCartWithPriceCalculated(discountedCart);


        Cart percentBasedDiscountedCart = discountService
                .applyPercentageBasedDiscountToCart(priceCalculatedCart);


        assertEquals(expectedCart.getTotalPrice(), percentBasedDiscountedCart.getTotalPrice());
    }


    @Test
    void shouldThrowException_WhenUserDataNotFoundInCart() {
        Cart cart = new Cart();

        assertThrows(UserNotFoundException.class,
                () -> discountService.applyPercentageBasedDiscountToCart(cart));

    }

    @Test
    void shouldThrowException_WhenUserDoesNotHaveAnySubscriptions() {
        Cart cart = new Cart();
        User user = new User();

        cart.setUser(user);


        assertThrows(SubscriptionNotFoundException.class,
                () -> discountService.applyPercentageBasedDiscountToCart(cart));

    }

}