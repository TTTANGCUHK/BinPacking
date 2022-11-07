package me.fyp;

public class Item implements Comparable{
    private int height, width;

    public Item(int height, int width) {
        this.height = height;
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public int getArea() {
        return getHeight() * getWidth();
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    // Use for sorting the object by variable height
    @Override
    public int compareTo(Object o) {
        int compareH = ((Item) o).getHeight();
        return compareH - this.height;
    }
}