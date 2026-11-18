package kz.dodo.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import kz.dodo.exception.WrongItemException;
import org.junit.jupiter.api.Test;

class OrderTest {

    private Order orderWithItems(boolean delivery) {
        Order order = new Order(1, delivery, new Customer("Айжан", "", "Абай 1"));
        order.addItem(new Pizza("Пепперони", 3500, 30));
        order.addItem(new Drink("Кола", 700, 0.5));
        return order;
    }

    private Order readyOrder(boolean delivery) {
        Order order = orderWithItems(delivery);
        order.startCooking();
        order.markReady();
        return order;
    }

    @Test
    void totalIsSumOfItemPrices() {
        assertEquals(5250, orderWithItems(true).calculateTotal());
    }

    @Test
    void statusChangesInCorrectOrder() throws WrongItemException {
        Order order = readyOrder(true);
        order.verifyPacked(new ArrayList<>(order.getItems()));
        order.startDelivery(new Courier("Ерлан"));
        order.complete();
        assertEquals(OrderStatus.DONE, order.getStatus());
    }

    @Test
    void skippingStatusIsRejected() {
        Order order = orderWithItems(true);
        assertThrows(IllegalStateException.class, order::markReady);
        assertThrows(IllegalStateException.class, order::complete);
    }

    @Test
    void correctPackIsVerifiedInAnyOrder() throws WrongItemException {
        Order order = readyOrder(false);
        order.verifyPacked(List.of(new Drink("Кола", 700, 0.5), new Pizza("Пепперони", 3500, 30)));
        assertTrue(order.isVerified());
    }

    @Test
    void wrongPizzaInPackIsDetected() {
        Order order = readyOrder(false);
        List<MenuItem> packed = List.of(new Pizza("Гавай", 3600, 30), new Drink("Кола", 700, 0.5));
        WrongItemException e = assertThrows(WrongItemException.class, () -> order.verifyPacked(packed));
        assertTrue(e.getMessage().contains("Пепперони"));
        assertTrue(e.getMessage().contains("Гавай"));
        assertFalse(order.isVerified());
    }

    @Test
    void wrongSizeInPackIsDetected() {
        Order order = readyOrder(false);
        List<MenuItem> packed = List.of(new Pizza("Пепперони", 3500, 35), new Drink("Кола", 700, 0.5));
        assertThrows(WrongItemException.class, () -> order.verifyPacked(packed));
    }

    @Test
    void unverifiedOrderCannotBeHandedOver() {
        Order pickup = readyOrder(false);
        assertThrows(IllegalStateException.class, pickup::complete);
        Order delivery = readyOrder(true);
        assertThrows(IllegalStateException.class, () -> delivery.startDelivery(new Courier("Ерлан")));
    }

    @Test
    void pickupOrderCannotBeDelivered() throws WrongItemException {
        Order order = readyOrder(false);
        order.verifyPacked(new ArrayList<>(order.getItems()));
        assertThrows(IllegalStateException.class, () -> order.startDelivery(new Courier("Ерлан")));
    }

    @Test
    void removeItemWithWrongIndexIsRejected() {
        Order order = orderWithItems(false);
        assertThrows(IllegalArgumentException.class, () -> order.removeItem(5));
    }
}
