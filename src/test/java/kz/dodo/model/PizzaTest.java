package kz.dodo.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class PizzaTest {

    @Test
    void priceDependsOnSize() {
        assertEquals(3500, new Pizza("Пепперони", 3500, 25).getPrice());
        assertEquals(4550, new Pizza("Пепперони", 3500, 30).getPrice());
        assertEquals(5600, new Pizza("Пепперони", 3500, 35).getPrice());
    }

    @Test
    void wrongSizeIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> new Pizza("Пепперони", 3500, 40));
    }
}
