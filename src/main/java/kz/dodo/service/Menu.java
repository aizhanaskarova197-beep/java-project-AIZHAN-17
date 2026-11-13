package kz.dodo.service;

import kz.dodo.model.MenuItem;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/** Мәзір: позиция нөмірі бойынша жылдам табылады. */
public class Menu {
    private final Map<Integer, MenuItem> items = new HashMap<>();
    private int nextNumber = 1;

    /** Жаңа позиция қосып, оның нөмірін қайтарады. */
    public int addItem(MenuItem item) {
        int number = nextNumber++;
        items.put(number, item);
        return number;
    }

    /** Нөмір бойынша позиция; жоқ болса null. */
    public MenuItem getItem(int number) {
        return items.get(number);
    }

    /** Нөмір бойынша сұрыпталған, өзгертуге болмайтын көрініс. */
    public Map<Integer, MenuItem> getAll() {
        return Collections.unmodifiableMap(new TreeMap<>(items));
    }
}
