package kz.dodo.storage;

import kz.dodo.model.Courier;
import kz.dodo.model.Customer;
import kz.dodo.model.Drink;
import kz.dodo.model.MenuItem;
import kz.dodo.model.Order;
import kz.dodo.model.OrderStatus;
import kz.dodo.model.Pizza;
import kz.dodo.service.Menu;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/** CSV файлдармен жұмыс: мәзір және тапсырыстар. */
public class CsvStorage {

    /** menu.csv: type,name,basePrice,extra */
    public Menu loadMenu(String path) throws IOException {
        Menu menu = new Menu();
        List<String> lines = Files.readAllLines(Path.of(path), StandardCharsets.UTF_8);
        for (int i = 1; i < lines.size(); i++) {
            String line = lines.get(i).trim();
            if (line.isEmpty()) {
                continue;
            }
            String[] p = line.split(",", -1);
            try {
                String type = p[0].trim();
                String name = p[1].trim();
                double price = Double.parseDouble(p[2].trim());
                if (type.equals("PIZZA")) {
                    menu.addItem(new Pizza(name, price, 25));
                } else if (type.equals("DRINK")) {
                    menu.addItem(new Drink(name, price, Double.parseDouble(p[3].trim())));
                } else {
                    throw new IllegalArgumentException("Белгісіз түр: " + type);
                }
            } catch (RuntimeException e) {
                throw new IOException(path + ": " + (i + 1) + "-жолда қате: " + line, e);
            }
        }
        return menu;
    }

    public void saveMenu(Menu menu, String path) throws IOException {
        List<String> lines = new ArrayList<>();
        lines.add("type,name,basePrice,extra");
        for (Map.Entry<Integer, MenuItem> e : menu.getAll().entrySet()) {
            MenuItem item = e.getValue();
            if (item instanceof Pizza) {
                lines.add("PIZZA," + clean(item.getName()) + "," + fmt(item.getBasePrice()) + ",");
            } else if (item instanceof Drink d) {
                lines.add("DRINK," + clean(item.getName()) + "," + fmt(item.getBasePrice()) + "," + d.getVolumeLiters());
            }
        }
        write(path, lines);
    }

    /** orders.csv: orderId,status,delivery,customer,courier,items,total */
    public void saveOrders(List<Order> orders, String path) throws IOException {
        List<String> lines = new ArrayList<>();
        lines.add("orderId,status,delivery,customer,courier,items,total");
        for (Order o : orders) {
            List<String> parts = new ArrayList<>();
            for (MenuItem item : o.getItems()) {
                parts.add(encodeItem(item));
            }
            String customer = o.getCustomer() == null ? "" : clean(o.getCustomer().getName());
            String courier = o.getCourier() == null ? "" : clean(o.getCourier().getName());
            lines.add(o.getId() + "," + o.getStatus() + "," + o.isDelivery() + "," + customer + ","
                    + courier + "," + String.join(";", parts) + "," + fmt(o.calculateTotal()));
        }
        write(path, lines);
    }

    /** Файл жоқ болса бос тізім қайтарады. */
    public List<Order> loadOrders(String path) throws IOException {
        List<Order> orders = new ArrayList<>();
        Path file = Path.of(path);
        if (!Files.exists(file)) {
            return orders;
        }
        List<String> lines = Files.readAllLines(file, StandardCharsets.UTF_8);
        for (int i = 1; i < lines.size(); i++) {
            String line = lines.get(i).trim();
            if (line.isEmpty()) {
                continue;
            }
            try {
                String[] p = line.split(",", -1);
                int id = Integer.parseInt(p[0].trim());
                OrderStatus status = OrderStatus.valueOf(p[1].trim());
                boolean delivery = Boolean.parseBoolean(p[2].trim());
                Customer customer = new Customer(p[3].trim(), "", "");
                Courier courier = null;
                if (!p[4].trim().isEmpty()) {
                    courier = new Courier(p[4].trim());
                    courier.setBusy(status == OrderStatus.DELIVERING);
                }
                List<MenuItem> items = new ArrayList<>();
                if (!p[5].trim().isEmpty()) {
                    for (String token : p[5].split(";")) {
                        items.add(decodeItem(token));
                    }
                }
                orders.add(new Order(id, delivery, customer, status, courier, items));
            } catch (RuntimeException e) {
                throw new IOException(path + ": " + (i + 1) + "-жолда қате: " + line, e);
            }
        }
        return orders;
    }

    private String encodeItem(MenuItem item) {
        if (item instanceof Pizza p) {
            return "PIZZA:" + clean(p.getName()) + ":" + fmt(p.getBasePrice()) + ":" + p.getSizeCm();
        }
        Drink d = (Drink) item;
        return "DRINK:" + clean(d.getName()) + ":" + fmt(d.getBasePrice()) + ":" + d.getVolumeLiters();
    }

    private MenuItem decodeItem(String token) {
        String[] p = token.split(":");
        double price = Double.parseDouble(p[2]);
        if (p[0].equals("PIZZA")) {
            return new Pizza(p[1], price, Integer.parseInt(p[3]));
        }
        return new Drink(p[1], price, Double.parseDouble(p[3]));
    }

    private void write(String path, List<String> lines) throws IOException {
        Path file = Path.of(path);
        if (file.getParent() != null) {
            Files.createDirectories(file.getParent());
        }
        Files.write(file, lines, StandardCharsets.UTF_8);
    }

    /** CSV бөлгіштерін алып тастайды. */
    private String clean(String s) {
        return s.replace(",", " ").replace(";", " ").replace(":", " ").trim();
    }

    private String fmt(double value) {
        return value == Math.floor(value) ? String.valueOf((long) value) : String.valueOf(value);
    }
}
