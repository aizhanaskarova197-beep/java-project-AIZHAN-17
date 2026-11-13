package kz.dodo.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import kz.dodo.exception.EmptyOrderException;
import kz.dodo.model.Drink;
import kz.dodo.model.Order;
import kz.dodo.model.OrderStatus;
import org.junit.jupiter.api.Test;

class KitchenTest {

    private Order orderWithDrink(int id) {
        Order order = new Order(id, false, null);
        order.addItem(new Drink("Кола", 700, 0.5));
        return order;
    }

    @Test
    void emptyOrderIsRejected() {
        Kitchen kitchen = new Kitchen();
        assertThrows(EmptyOrderException.class, () -> kitchen.addOrder(new Order(1, false, null)));
        assertEquals(0, kitchen.getQueueSize());
    }

    @Test
    void ordersAreCookedInFifoOrder() throws EmptyOrderException {
        Kitchen kitchen = new Kitchen();
        Order first = orderWithDrink(1);
        Order second = orderWithDrink(2);
        kitchen.addOrder(first);
        kitchen.addOrder(second);
        assertSame(first, kitchen.cookNext());
        assertSame(second, kitchen.cookNext());
        assertNull(kitchen.cookNext());
        assertEquals(OrderStatus.READY, first.getStatus());
    }
}
