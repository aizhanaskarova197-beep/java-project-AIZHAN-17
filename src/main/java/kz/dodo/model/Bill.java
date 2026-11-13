package kz.dodo.model;

import java.util.List;
import java.util.Locale;

/** Чек: тапсырыс құрамын толық көрсетеді және соманы шығарады. */
public class Bill implements Payable {
    private final Order order;

    public Bill(Order order) {
        this.order = order;
    }

    @Override
    public double calculateTotal() {
        return order.calculateTotal();
    }

    public String print() {
        StringBuilder sb = new StringBuilder();
        sb.append("========== ЧЕК, тапсырыс №").append(order.getId()).append(" ==========\n");
        List<MenuItem> items = order.getItems();
        for (int i = 0; i < items.size(); i++) {
            MenuItem item = items.get(i);
            sb.append(String.format(Locale.ROOT, "%d. %-28s %7.0f тг%n", i + 1, item.getDescription(), item.getPrice()));
        }
        sb.append("------------------------------------------\n");
        sb.append(String.format(Locale.ROOT, "Барлығы: %.0f тг%n", calculateTotal()));
        sb.append(order.isDelivery() ? "Алу тәсілі: жеткізу\n" : "Алу тәсілі: өзі алып кету\n");
        sb.append("==========================================");
        return sb.toString();
    }
}
