package kz.dodo.model;

/** Мәзір позициясының абстракт класы (пицца, сусын осыдан мұра алады). */
public abstract class MenuItem {
    private final String name;
    private final double basePrice;

    protected MenuItem(String name, double basePrice) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Атауы бос болмауы керек");
        }
        if (basePrice < 0) {
            throw new IllegalArgumentException("Баға теріс болмауы керек");
        }
        this.name = name;
        this.basePrice = basePrice;
    }

    public String getName() {
        return name;
    }

    public double getBasePrice() {
        return basePrice;
    }

    /** Соңғы баға. Ішкі кластар қайта анықтай алады (полиморфизм). */
    public double getPrice() {
        return basePrice;
    }

    /** Позицияның қысқаша сипаттамасы (чекте және асханада көрсетіледі). */
    public abstract String getDescription();
}
