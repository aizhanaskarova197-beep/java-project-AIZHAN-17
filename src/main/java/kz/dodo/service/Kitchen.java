package kz.dodo.service;

import kz.dodo.exception.EmptyOrderException;
import kz.dodo.model.Order;

import java.util.LinkedList;
import java.util.Queue;

/** Асхана: тапсырыстар кезекпен (FIFO) дайындалады. */
public class Kitchen {
    private final Queue<Order> queue = new LinkedList<>();

    /** Бос тапсырыс қабылданбайды. */
    public void addOrder(Order order) throws EmptyOrderException {
        if (order == null || order.getItems().isEmpty()) {
            throw new EmptyOrderException("Бос тапсырысты асханаға жіберуге болмайды");
        }
        queue.offer(order);
    }

    /** Кезектегі бірінші тапсырысты дайындайды. Кезек бос болса null қайтарады. */
    public Order cookNext() {
        Order order = queue.poll();
        if (order == null) {
            return null;
        }
        order.startCooking();
        order.markReady();
        return order;
    }

    public int getQueueSize() {
        return queue.size();
    }
}
