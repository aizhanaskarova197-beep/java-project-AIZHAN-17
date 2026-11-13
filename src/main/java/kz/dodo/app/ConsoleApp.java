package kz.dodo.app;

import kz.dodo.exception.EmptyOrderException;
import kz.dodo.model.Bill;
import kz.dodo.model.Courier;
import kz.dodo.model.Customer;
import kz.dodo.model.Drink;
import kz.dodo.model.MenuItem;
import kz.dodo.model.Order;
import kz.dodo.model.OrderStatus;
import kz.dodo.model.Pizza;
import kz.dodo.service.DeliveryService;
import kz.dodo.service.Kitchen;
import kz.dodo.service.Menu;
import kz.dodo.storage.CsvStorage;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.Set;

/** Консольдік мәзір: бағдарламаны іске қосу нүктесі. */
public class ConsoleApp {
    private static final String MENU_FILE = "data/menu.csv";
    private static final String ORDERS_FILE = "data/orders.csv";

    private final List<Order> history = new ArrayList<>();
    private final Kitchen kitchen = new Kitchen();
    private final DeliveryService deliveryService = new DeliveryService();
    private final CsvStorage storage = new CsvStorage();
    private final Scanner scanner = new Scanner(System.in, StandardCharsets.UTF_8);
    private Menu menu;
    private Order currentOrder;
    private int nextOrderId = 1;

    public static void main(String[] args) {
        System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8));
        new ConsoleApp().run();
    }

    private void run() {
        try {
            menu = storage.loadMenu(MENU_FILE);
        } catch (IOException e) {
            System.out.println("Мәзірді оқу қатесі: " + e.getMessage());
            return;
        }
        restoreOrders();
        try {
            boolean running = true;
            while (running) {
                printMainMenu();
                int choice = readInt("Таңдаңыз: ", 0, 9);
                switch (choice) {
                    case 1 -> showMenu();
                    case 2 -> createOrder();
                    case 3 -> addItemToOrder();
                    case 4 -> removeItemFromOrder();
                    case 5 -> checkout();
                    case 6 -> cookNext();
                    case 7 -> deliverOrHandOver();
                    case 8 -> showHistory();
                    case 9 -> addMenuItem();
                    default -> running = false;
                }
            }
        } catch (NoSuchElementException e) {
            System.out.println("Енгізу аяқталды.");
        }
        saveOrders();
        System.out.println("Сау болыңыз! Деректер сақталды.");
    }

    /** Файлдан тарихты жүктейді, NEW тапсырыстарды асхана кезегіне қайтарады, курьерлерді қосады. */
    private void restoreOrders() {
        try {
            history.addAll(storage.loadOrders(ORDERS_FILE));
        } catch (IOException e) {
            System.out.println("Ескерту: тапсырыстар тарихын оқу мүмкін болмады: " + e.getMessage());
        }
        Set<String> busyCouriers = new HashSet<>();
        for (Order order : history) {
            nextOrderId = Math.max(nextOrderId, order.getId() + 1);
            if (order.getStatus() == OrderStatus.NEW) {
                try {
                    kitchen.addOrder(order);
                } catch (EmptyOrderException ignored) {
                    // құрамы бос тапсырыс кезекке қайтарылмайды
                }
            }
            if (order.getStatus() == OrderStatus.DELIVERING && order.getCourier() != null) {
                busyCouriers.add(order.getCourier().getName());
            }
        }
        for (String name : new String[]{"Ерлан", "Айгерім", "Нұрлан"}) {
            if (!busyCouriers.contains(name)) {
                deliveryService.addCourier(new Courier(name));
            }
        }
    }

    private void printMainMenu() {
        System.out.println();
        System.out.println("===== Dodo Pizza Kazakhstan =====");
        System.out.println("1. Мәзірді көрсету");
        System.out.println("2. Жаңа тапсырыс жасау");
        System.out.println("3. Тапсырысқа позиция қосу");
        System.out.println("4. Тапсырыстан позицияны жою");
        System.out.println("5. Тапсырысты рәсімдеу (чек + асханаға жіберу)");
        System.out.println("6. Асхана: келесі тапсырысты дайындау");
        System.out.println("7. Тапсырысты жеткізу / беру");
        System.out.println("8. Тапсырыстар тарихы");
        System.out.println("9. Мәзірге позиция қосу (менеджер)");
        System.out.println("0. Шығу");
    }

    private void showMenu() {
        System.out.println("--- Мәзір (пицца бағасы 25 см үшін; 30 см x1.3, 35 см x1.6) ---");
        for (Map.Entry<Integer, MenuItem> e : menu.getAll().entrySet()) {
            MenuItem item = e.getValue();
            System.out.printf(Locale.ROOT, "%2d. %-28s %6.0f тг%n", e.getKey(), item.getDescription(), item.getPrice());
        }
    }

    private void createOrder() {
        if (currentOrder != null && !currentOrder.getItems().isEmpty()) {
            System.out.println("Алдымен ағымдағы тапсырысты рәсімдеңіз (5).");
            return;
        }
        int type = readInt("Алу тәсілі (1 - жеткізу, 2 - өзі алып кету): ", 1, 2);
        String name = readText("Клиент аты: ");
        String address = "";
        if (type == 1) {
            address = readText("Мекенжай: ");
        }
        currentOrder = new Order(nextOrderId++, type == 1, new Customer(name, "", address));
        System.out.println("Тапсырыс №" + currentOrder.getId() + " жасалды.");
    }

    private void addItemToOrder() {
        if (!hasCurrentOrder()) {
            return;
        }
        showMenu();
        int number = readInt("Позиция нөмірі: ", 1, 1000);
        MenuItem item = menu.getItem(number);
        if (item == null) {
            System.out.println("Мұндай нөмір жоқ.");
            return;
        }
        if (item instanceof Pizza pizza) {
            int size = readSize();
            item = new Pizza(pizza.getName(), pizza.getBasePrice(), size);
        }
        currentOrder.addItem(item);
        System.out.println("Қосылды: " + item.getDescription() + " - " + (long) item.getPrice() + " тг");
    }

    private void removeItemFromOrder() {
        if (!hasCurrentOrder()) {
            return;
        }
        if (currentOrder.getItems().isEmpty()) {
            System.out.println("Тапсырыс бос.");
            return;
        }
        printComposition(currentOrder);
        int number = readInt("Жоятын позиция нөмірі: ", 1, currentOrder.getItems().size());
        currentOrder.removeItem(number - 1);
        System.out.println("Позиция жойылды.");
    }

    private void checkout() {
        if (!hasCurrentOrder()) {
            return;
        }
        try {
            kitchen.addOrder(currentOrder);
        } catch (EmptyOrderException e) {
            System.out.println("Қате: " + e.getMessage() + ". Алдымен позиция қосыңыз (3).");
            return;
        }
        history.add(currentOrder);
        System.out.println(new Bill(currentOrder).print());
        System.out.println("Тапсырыс асхана кезегіне қойылды. Кезекте: " + kitchen.getQueueSize());
        currentOrder = null;
        saveOrders();
    }

    private void cookNext() {
        Order order = kitchen.cookNext();
        if (order == null) {
            System.out.println("Асхана кезегі бос.");
            return;
        }
        System.out.println("Дайындалды: тапсырыс №" + order.getId() + " (статус " + order.getStatus() + ")");
        System.out.println("Асхана тізімі (чектегі құраммен бірдей болуы керек):");
        printComposition(order);
        if (order.isDelivery()) {
            tryAssignCourier(order);
        } else {
            System.out.println("Өзі алып кетуге дайын (7 - клиентке беру).");
        }
        saveOrders();
    }

    private void deliverOrHandOver() {
        List<Order> active = new ArrayList<>();
        for (Order order : history) {
            if (order.getStatus() == OrderStatus.READY || order.getStatus() == OrderStatus.DELIVERING) {
                active.add(order);
                System.out.println("№" + order.getId() + " - " + order.getStatus()
                        + (order.isDelivery() ? " (жеткізу)" : " (өзі алып кету)"));
            }
        }
        if (active.isEmpty()) {
            System.out.println("Жеткізуге немесе беруге дайын тапсырыс жоқ.");
            return;
        }
        int id = readInt("Тапсырыс нөмірі: ", 1, Integer.MAX_VALUE);
        Order selected = null;
        for (Order order : active) {
            if (order.getId() == id) {
                selected = order;
            }
        }
        if (selected == null) {
            System.out.println("Мұндай белсенді тапсырыс жоқ.");
            return;
        }
        if (selected.getStatus() == OrderStatus.DELIVERING) {
            deliveryService.completeDelivery(selected);
            System.out.println("Тапсырыс жеткізілді. Курьер " + selected.getCourier().getName() + " кезекке оралды.");
        } else if (selected.isDelivery()) {
            tryAssignCourier(selected);
        } else {
            selected.complete();
            System.out.println("Тапсырыс клиентке берілді.");
        }
        saveOrders();
    }

    private void showHistory() {
        if (history.isEmpty()) {
            System.out.println("Тапсырыстар әлі жоқ.");
            return;
        }
        for (Order order : history) {
            String customer = order.getCustomer() == null ? "" : order.getCustomer().getName();
            System.out.println("--- Тапсырыс №" + order.getId() + ", " + order.getStatus() + ", клиент: " + customer
                    + (order.getCourier() != null ? ", курьер: " + order.getCourier().getName() : ""));
            printComposition(order);
            System.out.printf(Locale.ROOT, "    Барлығы: %.0f тг%n", order.calculateTotal());
        }
    }

    private void addMenuItem() {
        int type = readInt("Түрі (1 - пицца, 2 - сусын): ", 1, 2);
        String name = readText("Атауы: ").replace(",", " ").replace(";", " ").replace(":", " ");
        double price = readInt("Бағасы (тг): ", 0, 1_000_000);
        try {
            if (type == 1) {
                menu.addItem(new Pizza(name, price, 25));
            } else {
                int ml = readInt("Көлемі (мл): ", 1, 5000);
                menu.addItem(new Drink(name, price, ml / 1000.0));
            }
            storage.saveMenu(menu, MENU_FILE);
            System.out.println("Мәзірге қосылды.");
        } catch (IllegalArgumentException | IOException e) {
            System.out.println("Қате: " + e.getMessage());
        }
    }

    // ---------- көмекші әдістер ----------

    private boolean hasCurrentOrder() {
        if (currentOrder == null) {
            System.out.println("Ағымдағы тапсырыс жоқ. Алдымен жаңа тапсырыс жасаңыз (2).");
            return false;
        }
        return true;
    }

    private void tryAssignCourier(Order order) {
        if (deliveryService.assignCourier(order)) {
            System.out.println("Курьер автоматты тағайындалды: " + order.getCourier().getName()
                    + " (статус " + order.getStatus() + ")");
        } else {
            System.out.println("Бос курьер жоқ, тапсырыс READY күйінде күтеді. Кейінірек 7-ні қайталаңыз.");
        }
    }

    private void printComposition(Order order) {
        List<MenuItem> items = order.getItems();
        for (int i = 0; i < items.size(); i++) {
            System.out.printf(Locale.ROOT, "    %d. %s - %.0f тг%n", i + 1, items.get(i).getDescription(), items.get(i).getPrice());
        }
    }

    private int readSize() {
        while (true) {
            int size = readInt("Өлшемі (25 / 30 / 35 см): ", 1, 100);
            if (size == 25 || size == 30 || size == 35) {
                return size;
            }
            System.out.println("Тек 25, 30 немесе 35.");
        }
    }

    private void saveOrders() {
        try {
            storage.saveOrders(history, ORDERS_FILE);
        } catch (IOException e) {
            System.out.println("Сақтау қатесі: " + e.getMessage());
        }
    }

    private String readText(String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = scanner.nextLine().trim().replace(",", " ").replace(";", " ").replace(":", " ");
            if (!line.isEmpty()) {
                return line;
            }
            System.out.println("Бос болмауы керек.");
        }
    }

    private int readInt(String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            String line = scanner.nextLine().trim();
            try {
                int value = Integer.parseInt(line);
                if (value >= min && value <= max) {
                    return value;
                }
            } catch (NumberFormatException ignored) {
                // төменде қате хабары шығады
            }
            System.out.println("Қате енгізу: " + min + " мен " + max + " аралығындағы сан керек.");
        }
    }
}
