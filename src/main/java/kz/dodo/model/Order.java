package kz.dodo.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Тапсырыс. Құрамы (items) нақты сақталады, статус тек дұрыс ретімен өзгереді:
 * NEW -> COOKING -> READY -> DELIVERING -> DONE (өзі алып кетсе: READY -> DONE).
 */
public class Order implements Payable {
    private final int id;
    private final List<MenuItem> items = new ArrayList<>();
    private OrderStatus status = OrderStatus.NEW;
    private final boolean delivery;
    private final Customer customer;
    private Courier courier;

    public Order(int id, boolean delivery, Customer customer) {
        this.id = id;
        this.delivery = delivery;
        this.customer = customer;
    }

    /** Файлдан қалпына келтіру үшін конструктор. */
    public Order(int id, boolean delivery, Customer customer, OrderStatus status,
                 Courier courier, List<MenuItem> items) {
        this(id, delivery, customer);
        this.status = status;
        this.courier = courier;
        this.items.addAll(items);
    }

    public void addItem(MenuItem item) {
        if (item == null) {
            throw new IllegalArgumentException("Позиция бос болмауы керек");
        }
        items.add(item);
    }

    /** index 0-ден басталады. */
    public void removeItem(int index) {
        if (index < 0 || index >= items.size()) {
            throw new IllegalArgumentException("Мұндай позиция жоқ: " + (index + 1));
        }
        items.remove(index);
    }

    public int getId() {
        return id;
    }

    public boolean isDelivery() {
        return delivery;
    }

    public List<MenuItem> getItems() {
        return Collections.unmodifiableList(items);
    }

    public OrderStatus getStatus() {
        return status;
    }

    public Customer getCustomer() {
        return customer;
    }

    public Courier getCourier() {
        return courier;
    }

    public void startCooking() {
        if (status != OrderStatus.NEW) {
            throw new IllegalStateException("Дайындауға тек NEW тапсырыс алынады, қазір: " + status);
        }
        status = OrderStatus.COOKING;
    }

    public void markReady() {
        if (status != OrderStatus.COOKING) {
            throw new IllegalStateException("Дайын деп тек COOKING тапсырысты белгілеуге болады, қазір: " + status);
        }
        status = OrderStatus.READY;
    }

    public void startDelivery(Courier courier) {
        if (!delivery) {
            throw new IllegalStateException("Бұл тапсырыс өзі алып кетуге арналған");
        }
        if (status != OrderStatus.READY) {
            throw new IllegalStateException("Жеткізуге тек READY тапсырыс беріледі, қазір: " + status);
        }
        if (courier == null) {
            throw new IllegalArgumentException("Курьер көрсетілмеген");
        }
        this.courier = courier;
        status = OrderStatus.DELIVERING;
    }

    public void complete() {
        OrderStatus required = delivery ? OrderStatus.DELIVERING : OrderStatus.READY;
        if (status != required) {
            throw new IllegalStateException("Аяқтау үшін статус " + required + " болуы керек, қазір: " + status);
        }
        status = OrderStatus.DONE;
    }

    @Override
    public double calculateTotal() {
        double total = 0;
        for (MenuItem item : items) {
            total += item.getPrice();
        }
        return total;
    }
}
