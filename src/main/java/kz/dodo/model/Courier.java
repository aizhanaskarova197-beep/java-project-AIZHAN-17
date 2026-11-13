package kz.dodo.model;

/** Курьер. */
public class Courier {
    private final String name;
    private boolean busy;

    public Courier(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public boolean isBusy() {
        return busy;
    }

    public void setBusy(boolean busy) {
        this.busy = busy;
    }
}
