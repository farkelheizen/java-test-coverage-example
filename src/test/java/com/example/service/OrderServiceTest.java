package com.example.service;

import com.example.enums.OrderStatus;
import com.example.enums.PaymentMethod;
import com.example.model.Customer;
import com.example.model.Order;
import com.example.model.OrderItem;
import com.example.model.Product;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @InjectMocks
    private OrderService orderService;

    // ------------------------------------------------------------------ placeOrder

    @Test
    void placeOrder_withPaymentMethod_setsStatusToProcessing() {
        Order order = buildOrder(new Customer(), PaymentMethod.CREDIT_CARD, 100.0);
        Order result = orderService.placeOrder(order);
        assertEquals(OrderStatus.PROCESSING, result.getStatus());
    }

    @Test
    void placeOrder_withoutPaymentMethod_setsStatusToCancelled() {
        Order order = buildOrder(new Customer(), null, 100.0);
        Order result = orderService.placeOrder(order);
        assertEquals(OrderStatus.CANCELLED, result.getStatus());
    }

    @Test
    void placeOrder_nullCustomer_throwsIllegalArgumentException() {
        Order order = buildOrder(null, PaymentMethod.CREDIT_CARD, 50.0);
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> orderService.placeOrder(order)
        );
        assertTrue(ex.getMessage().contains("Customer"));
    }

    @Test
    void placeOrder_nullItems_throwsIllegalArgumentException() {
        Order order = new Order();
        order.setCustomer(new Customer());
        order.setItems(null);
        order.setPaymentMethod(PaymentMethod.PAYPAL);

        assertThrows(IllegalArgumentException.class, () -> orderService.placeOrder(order));
    }

    @Test
    void placeOrder_emptyItems_throwsIllegalArgumentException() {
        Order order = new Order();
        order.setCustomer(new Customer());
        order.setItems(Collections.emptyList());
        order.setPaymentMethod(PaymentMethod.PAYPAL);

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> orderService.placeOrder(order)
        );
        assertTrue(ex.getMessage().toLowerCase().contains("item"));
    }

    @Test
    void placeOrder_returnsSameOrderObject() {
        Order order = buildOrder(new Customer(), PaymentMethod.DEBIT_CARD, 200.0);
        Order result = orderService.placeOrder(order);
        assertSame(order, result);
    }

    // ------------------------------------------------------------------ cancelOrder

    @Test
    void cancelOrder_pendingOrder_setsStatusToCancelled() {
        Order order = new Order();
        order.setStatus(OrderStatus.PENDING);
        Order result = orderService.cancelOrder(order);
        assertEquals(OrderStatus.CANCELLED, result.getStatus());
    }

    @Test
    void cancelOrder_processingOrder_setsStatusToCancelled() {
        Order order = new Order();
        order.setStatus(OrderStatus.PROCESSING);
        Order result = orderService.cancelOrder(order);
        assertEquals(OrderStatus.CANCELLED, result.getStatus());
    }

    @Test
    void cancelOrder_shippedOrder_throwsIllegalStateException() {
        Order order = new Order();
        order.setStatus(OrderStatus.SHIPPED);
        assertThrows(IllegalStateException.class, () -> orderService.cancelOrder(order));
    }

    @Test
    void cancelOrder_deliveredOrder_throwsIllegalStateException() {
        Order order = new Order();
        order.setStatus(OrderStatus.DELIVERED);
        assertThrows(IllegalStateException.class, () -> orderService.cancelOrder(order));
    }

    @Test
    void cancelOrder_alreadyCancelledOrder_throwsIllegalStateException() {
        Order order = new Order();
        order.setStatus(OrderStatus.CANCELLED);
        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> orderService.cancelOrder(order)
        );
        assertTrue(ex.getMessage().contains("CANCELLED"));
    }

    @Test
    void cancelOrder_returnsSameOrderObject() {
        Order order = new Order();
        order.setStatus(OrderStatus.PENDING);
        Order result = orderService.cancelOrder(order);
        assertSame(order, result);
    }

    // ------------------------------------------------------------------ calculateTotal

    @Test
    void calculateTotal_subtotalBelow50_addsShipping() {
        Order order = buildOrder(new Customer(), PaymentMethod.CASH, 40.0);
        // total = 40 + 40*0.08 + 9.99 = 40 + 3.20 + 9.99 = 53.19
        double result = orderService.calculateTotal(order);
        assertEquals(40.0 + 40.0 * 0.08 + 9.99, result, 0.001);
    }

    @Test
    void calculateTotal_subtotalAtExactly50_noShipping() {
        Order order = buildOrder(new Customer(), PaymentMethod.CASH, 50.0);
        // total = 50 + 50*0.08 + 0.0 = 50 + 4 = 54
        double result = orderService.calculateTotal(order);
        assertEquals(50.0 + 50.0 * 0.08, result, 0.001);
    }

    @Test
    void calculateTotal_subtotalAbove50_noShipping() {
        Order order = buildOrder(new Customer(), PaymentMethod.CASH, 100.0);
        double result = orderService.calculateTotal(order);
        assertEquals(100.0 + 100.0 * 0.08, result, 0.001);
    }

    @Test
    void calculateTotal_zeroTotal_addsTaxAndShipping() {
        // empty items -> subtotal 0
        Order order = new Order();
        order.setCustomer(new Customer());
        // Don't call placeOrder - just set items to empty for getTotalAmount to return 0
        order.setItems(null);
        double result = orderService.calculateTotal(order);
        // subtotal 0 < 50 => shipping 9.99
        assertEquals(0 + 0 * 0.08 + 9.99, result, 0.001);
    }

    // ------------------------------------------------------------------ helpers

    private Order buildOrder(Customer customer, PaymentMethod paymentMethod, double itemPrice) {
        Order order = new Order();
        order.setCustomer(customer);
        order.setPaymentMethod(paymentMethod);

        Product product = new Product();
        product.setProductId("prod-1");
        product.setName("Test Product");

        OrderItem item = new OrderItem("OI1", product, 1, itemPrice);
        order.setItems(Collections.singletonList(item));
        return order;
    }
}
