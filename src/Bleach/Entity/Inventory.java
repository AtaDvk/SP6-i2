package Bleach.Entity;

import java.util.ArrayList;
import java.util.List;

public class Inventory {
    private List<InventoryItem> items; // List of items in the inventory.
    private int size; // How many items the inventory can hold.

    public Inventory() {
	size = 45;
	items = new ArrayList<>();
    }

    public boolean addItem(InventoryItem item) {
	if (items.size() < size) {
	    items.add(item);
	    return true;
	}
	return false;
    }

    public InventoryItem getItem(int index) {
	if (index < 0 || index >= items.size()) {
	    return null;
	}
	return items.get(index);
    }
}
