package kz.dodo.model;

/** Пицца: өлшеміне қарай бағасы өзгереді. */
public class Pizza extends MenuItem {
    private final int sizeCm;

    public Pizza(String name, double basePrice, int sizeCm) {
        super(name, basePrice);
        if (sizeCm != 25 && sizeCm != 30 && sizeCm != 35) {
            throw new IllegalArgumentException("Өлшем 25, 30 немесе 35 см болуы керек");
        }
        this.sizeCm = sizeCm;
    }

    public int getSizeCm() {
        return sizeCm;
    }

    /** Базалық баға 25 см үшін; 30 см = x1.3, 35 см = x1.6. */
    @Override
    public double getPrice() {
        double coefficient = switch (sizeCm) {
            case 30 -> 1.3;
            case 35 -> 1.6;
            default -> 1.0;
        };
        return Math.round(getBasePrice() * coefficient);
    }

    @Override
    public String getDescription() {
        return getName() + ", " + sizeCm + " см";
    }
}
