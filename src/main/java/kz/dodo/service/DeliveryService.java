package kz.dodo.service;

import kz.dodo.model.Courier;
import kz.dodo.model.Order;
import kz.dodo.model.OrderStatus;

import java.util.LinkedList;
import java.util.Queue;

/** Жеткізу: бос курьерлер кезегі, курьер автоматты тағайындалады. */
public class DeliveryService {
    private final Queue<Courier> freeCouriers = new LinkedList<>();

    public void addCourier(Courier courier) {
        courier.setBusy(false);
        freeCouriers.offer(courier);
    }

    /** Дайын тапсырысқа кезектегі бірінші бос курьерді тағайындайды. Курьер жоқ болса false. */
    public boolean assignCourier(Order order) {
        if (!order.isDelivery() || order.getStatus() != OrderStatus.READY) {
            return false;
        }
        Courier courier = freeCouriers.poll();
        if (courier == null) {
            return false;
        }
        courier.setBusy(true);
        order.startDelivery(courier);
        return true;
    }

    /** Тапсырысты аяқтап, курьерді кезектің соңына қайтарады. */
    public void completeDelivery(Order order) {
        order.complete();
        Courier courier = order.getCourier();
        if (courier != null) {
            courier.setBusy(false);
            if (!hasCourier(courier.getName())) {
                freeCouriers.offer(courier);
            }
        }
    }

    private boolean hasCourier(String name) {
        for (Courier c : freeCouriers) {
            if (c.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }
}
