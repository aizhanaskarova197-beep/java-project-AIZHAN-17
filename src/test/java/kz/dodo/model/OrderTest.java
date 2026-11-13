package kz.dodo.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class OrderTest {

    private Order orderWithItems(boolean delivery) {
        Order order = new Order(1, delivery, new Customer("Айжан", "", "Абай 1"));
        order.addItem(new Pizza("Пепперони", 3500, 30));
        order.addItem(new Drink("Кола", 700, 0.5));
        return order;
    }

    @Test
    void totalIsSumOfItemPrices() {
        assertEquals(5250, orderWithItems(true).calculateTotal());
    }

    @Test
    void statusChangesInCorrectOrder() {
        Order order = orderWithItems(true);
        order.startCooking();
        order.markReady();
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
    void pickupOrderCannotBeDelivered() {
        Order order = orderWithItems(false);
        order.startCooking();
        order.markReady();
        assertThrows(IllegalStateException.class, () -> order.startDelivery(new Courier("Ерлан")));
    }

    @Test
    void removeItemWithWrongIndexIsRejected() {
        Order order = orderWithItems(false);
        assertThrows(IllegalArgumentException.class, () -> order.removeItem(5));
    }
}
