package com.hasan;

import com.hasan.model.*;
import com.hasan.service.DiscountService;
import com.hasan.service.PriceService;
import com.hasan.service.impl.DiscountServiceImpl;
import com.hasan.service.impl.PriceServiceImpl;

import java.math.BigDecimal;
import java.util.List;

public class App {

    static PriceService priceService;
    static DiscountService discountService;

    static void init() {
        priceService = new PriceServiceImpl();
        discountService = new DiscountServiceImpl(priceService);
    }

    static void printAllOrdersInCart(Cart cart) {
        cart.getOrders()
                .forEach(
                        order ->
                                System.out.println(
                                        "Product Name: " + order.getProduct().getName() + " | " +
                                                "Product Price: " + order.getProduct().getPrice() + " | " +
                                                "Count: " + order.getCount() + " | " +
                                                "Total Order Price: " + order.getTotalPrice()

                                )
                );
        System.out.println("Total Cart Price: " + cart.getTotalPrice());
    }

    static void consoleDivider(){
        System.out.println("__________________________________________________");
    }



    public static void main(String[] args) {
        init();

        Subscription goldCardSub = new Subscription();
        Subscription silverCardSub = new Subscription();
        Subscription affiliateSub = new Subscription();
        Subscription twoYearMemberSub = new Subscription();

        Benefit goldCardBenefit = new Benefit();
        Benefit silverCardBenefit = new Benefit();
        Benefit affiliateBenefit = new Benefit();
        Benefit twoYearMemberBenefit = new Benefit();

        goldCardBenefit.setDiscountRate(30);
        silverCardBenefit.setDiscountRate(20);
        affiliateBenefit.setDiscountRate(10);
        twoYearMemberBenefit.setDiscountRate(5);


        goldCardSub.setType(SubscriptionType.GOLD_CARD);
        silverCardSub.setType(SubscriptionType.SILVER_CARD);
        affiliateSub.setType(SubscriptionType.AFFILIATE);
        twoYearMemberSub.setType(SubscriptionType.TWO_YEAR_MEMBERSHIP);

        goldCardSub.setBenefits(List.of(goldCardBenefit));
        silverCardSub.setBenefits(List.of(silverCardBenefit));
        affiliateSub.setBenefits(List.of(affiliateBenefit));
        twoYearMemberSub.setBenefits(List.of(twoYearMemberBenefit));

        User user = new User();
        user.setUsername("user");
        user.setSubscriptions(
                List.of(
                        goldCardSub,
                        silverCardSub,
                        affiliateSub,
                        twoYearMemberSub
                )
        );


        ProductCategory phoneCategory = new ProductCategory();
        phoneCategory.setName("phone");

        Product phone = new Product();
        phone.setCategory(phoneCategory);
        phone.setPrice(BigDecimal.valueOf(1000));
        phone.setName("IPhone");
        ProductCategory tvCategory = new ProductCategory();
        tvCategory.setName("tv");
        tvCategory.setIsDiscounted(true);

        Product tv = new Product();
        tv.setCategory(tvCategory);
        tv.setPrice(BigDecimal.valueOf(3000));
        tv.setName("Lcd Tv");

        ProductOrder phoneOrder = new ProductOrder();
        phoneOrder.setProduct(phone);
        phoneOrder.setCount(1);

        ProductOrder phoneOrderCalculated = priceService.getProductOrderWithPriceCalculated(phoneOrder);

        ProductOrder tvOrder = new ProductOrder();
        tvOrder.setProduct(tv);
        tvOrder.setCount(1);

        ProductOrder tvOrderCalculated = priceService.getProductOrderWithPriceCalculated(tvOrder);


        Cart cart = new Cart();
        cart.setUser(user);
        cart.setOrders(
                List.of(
                        tvOrderCalculated,
                        phoneOrderCalculated)
        );

        cart = priceService.getCartWithPriceCalculated(cart);



        System.out.println("Original Cart");
        printAllOrdersInCart(cart);

        consoleDivider();

        cart = discountService.applyDiscountForPerValueToCart(cart,200,5);
        System.out.println("5$ discount applied for every 200$ : ");
        printAllOrdersInCart(cart);

        consoleDivider();

        cart = discountService.applyPercentageBasedDiscountToCart(cart);
        System.out.println("Discount Applied Cart");
        printAllOrdersInCart(cart);





    }
}
