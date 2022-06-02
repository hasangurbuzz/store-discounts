package com.hasan.service;

import com.hasan.model.Cart;
import com.hasan.model.Product;
import com.hasan.model.ProductCategory;
import com.hasan.model.ProductOrder;
import com.hasan.service.impl.PriceServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PriceServiceTest {

    PriceService priceService;

    @BeforeEach
    void setUp() {
        priceService = new PriceServiceImpl();

    }

    @Test
    void canCalculate_TotalPriceOfProducts_InProductOrder() {
        ProductCategory phoneCategory = new ProductCategory();
        phoneCategory.setName("phone");

        Product phone = new Product();
        phone.setCategory(phoneCategory);
        phone.setPrice(BigDecimal.valueOf(1999.99));
        phone.setName("IPhone");

        ProductOrder phoneOrder = new ProductOrder();
        phoneOrder.setProduct(phone);
        phoneOrder.setCount(2);

        BigDecimal phonePrice = phoneOrder.getProduct().getPrice();
        int count = phoneOrder.getCount();

        BigDecimal totalPriceOfPhoneOrder = phonePrice.multiply(BigDecimal.valueOf(count));

        ProductOrder actualOrder = priceService.getProductOrderWithPriceCalculated(phoneOrder);
        phoneOrder.setTotalPrice(totalPriceOfPhoneOrder);

        assertEquals(phoneOrder.getTotalPrice(), actualOrder.getTotalPrice());

    }

    @Test
    void canCalculate_TotalPriceOfProductOrders_InCart() {
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

        BigDecimal phoneOrderTotalPrice = priceCalculatedPhoneOrder.getTotalPrice();
        BigDecimal laptopOrderTotalPrice = priceCalculatedLaptopOrder.getTotalPrice();

        BigDecimal expectedTotalCartPrice = phoneOrderTotalPrice.add(laptopOrderTotalPrice);

        Cart actualCart = priceService.getCartWithPriceCalculated(cart);
        BigDecimal actualCartTotalPrice = actualCart.getTotalPrice();

        assertEquals(expectedTotalCartPrice, actualCartTotalPrice);


    }
}