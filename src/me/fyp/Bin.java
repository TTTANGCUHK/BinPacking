package me.fyp;

import java.util.ArrayList;

public class Bin {
    private int height, width, remainingHeight, remainingWidth, itemArea;
    private ArrayList<Item> itemList;

    public Bin(int height, int width) {
        this.height = height;
        this.width = width;
        itemList = new ArrayList<>();
        itemArea = 0;
        remainingHeight = height;
        remainingWidth = width;
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public int getRemainingHeight() {
        return remainingHeight;
    }

    public int getRemainingWidth() {
        return remainingWidth;
    }

    public int getArea() {
        return getHeight() * getWidth();
    }

    // NEED TO DO THE CHECKING IF THE BIN HAS ENOUGH SPACE TO ADD THE ITEM
    public boolean addItem(Item item) {
        if (getArea() > itemArea + item.getArea()) {
            itemList.add(item);
            itemArea += item.getArea();
            remainingHeight -= item.getHeight();
            remainingWidth -= item.getWidth();
            return true;
        }
        return false;
    }

    public int countItems() {
        return itemList.size();
    }
}
