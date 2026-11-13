package kz.dodo.model;

/** Сусын: көлемі литрмен. */
public class Drink extends MenuItem {
    private final double volumeLiters;

    public Drink(String name, double basePrice, double volumeLiters) {
        super(name, basePrice);
        if (volumeLiters <= 0) {
            throw new IllegalArgumentException("Көлем 0-ден үлкен болуы керек");
        }
        this.volumeLiters = volumeLiters;
    }

    public double getVolumeLiters() {
        return volumeLiters;
    }

    @Override
    public String getDescription() {
        return getName() + ", " + volumeLiters + " л";
    }
}
