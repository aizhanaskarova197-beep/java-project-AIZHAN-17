package kz.dodo.model;

/** Клиент. */
public class Customer {
    private final String name;
    private final String phone;
    private final String address;

    public Customer(String name, String phone, String address) {
        this.name = name;
        this.phone = phone;
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public String getAddress() {
        return address;
    }
}
