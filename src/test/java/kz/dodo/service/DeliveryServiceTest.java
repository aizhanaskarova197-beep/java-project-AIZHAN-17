package kz.dodo.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import kz.dodo.model.Courier;
import kz.dodo.model.Drink;
import kz.dodo.model.Order;
import kz.dodo.model.OrderStatus;
import org.junit.jupiter.api.Test;

class DeliveryServiceTest {

    private Order readyDeliveryOrder(int id) {
        Order order = new Order(id, true, null);
        order.addItem(new Drink("Кола", 700, 0.5));
        order.startCooking();
        order.markReady();
        return order;
    }

    @Test
    void firstFreeCourierIsAssignedAutomatically() {
        DeliveryService service = new DeliveryService();
        Courier first = new Courier("Ерлан");
        service.addCourier(first);
        service.addCourier(new Courier("Айгерім"));
        Order order = readyDeliveryOrder(1);
        assertTrue(service.assignCourier(order));
        assertSame(first, order.getCourier());
        assertTrue(first.isBusy());
        assertEquals(OrderStatus.DELIVERING, order.getStatus());
    }

    @Test
    void noCourierMeansOrderWaits() {
        DeliveryService service = new DeliveryService();
        Order order = readyDeliveryOrder(1);
        assertFalse(service.assignCourier(order));
        assertEquals(OrderStatus.READY, order.getStatus());
    }

    @Test
    void courierReturnsToQueueAfterDelivery() {
        DeliveryService service = new DeliveryService();
        Courier courier = new Courier("Ерлан");
        service.addCourier(courier);
        Order order = readyDeliveryOrder(1);
        service.assignCourier(order);
        service.completeDelivery(order);
        assertEquals(OrderStatus.DONE, order.getStatus());
        assertFalse(courier.isBusy());
        assertTrue(service.assignCourier(readyDeliveryOrder(2)));
    }
}
