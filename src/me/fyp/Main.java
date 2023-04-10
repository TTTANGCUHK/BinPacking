package me.fyp;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import java.io.File;
import java.util.*;

public class Main {
    public static int IMAGE_WIDTH  = 1400;
    public static int IMAGE_HEIGHT = 700;
    public static final int BIN_WIDTH  = 170;
    public static final int BIN_HEIGHT = 220;
    public final int ITEM_SIZE = 20;

    public final boolean TXT = false;

    private String dataSetMsg = "";

    private ArrayList<Bin> bins;
    private ArrayList<Item> items;
    private ArrayList<Item> copy;
    private ArrayList<Item> mItems;
    Random rngObj;

    private Graphics pen;
    private ImageIcon icon;

    int k1 = 0, k2 = 0, i = 0;

    // Initialization / Constructor
    public Main() {
        BufferedImage img = new BufferedImage(IMAGE_WIDTH, IMAGE_HEIGHT, BufferedImage.TYPE_INT_ARGB);
        icon = new ImageIcon(img);
        pen = img.createGraphics();
        bins = new ArrayList<>();
        items = new ArrayList<>();
        copy = new ArrayList<>();
        mItems = new ArrayList<>();
        rngObj = new Random();
    }


    public int mAlg(Bin bin, Item item) {
        // Always put the first item at the left-bottom corner
        if (bin.countItems() == 0) {
            // Set the location of the item and update the remaining dimension of the bin
            item.setLoc(i, bin.getXLoc(), bin.getYLoc() + (bin.getHeight() - item.getHeight()));
            bin.setRemainingHeight(bin.getHeight() - item.getHeight());
            bin.setRemainingWidth(bin.getWidth() - item.getWidth());
            bin.setUpperRemainingWidth(item.getWidth());
            // Add the item to bin
            bin.addItem(item);
            return 1;
        } else {
            // If the item can be packed on top of the first item packed
            if (item.getHeight() <= bin.getRemainingHeight()) {
                // Check if there is enough space on top of the first item packed
                // In order to place the item
                if (bin.getUpperRemainingWidth() >= item.getWidth()) {
                    // Set the location of the item and update the remaining dimension of the bin
                    item.setLoc(i, bin.getXLoc() + bin.getUpperNextLoc(), bin.getYLoc() + bin.getRemainingHeight() - item.getHeight());
                    bin.setUpperRemainingWidth(bin.getUpperRemainingWidth() - item.getWidth());
                    // Add the item to bin
                    bin.addItem(item);
                    return 1;
                } else { // Case that the item cannot be packed on top of the first item packed
                    return 0;
                }
            } else {
                // Put the item next to the first item packed
                // Check if there is enough space for the next item
                if (bin.getFirstWidth() + item.getWidth() <= bin.getWidth()) {
                    // Set the location of the item and add the item to the bin
                    item.setLoc(i, bin.getXLoc() + bin.getFirstWidth(), bin.getYLoc() + (bin.getHeight() - item.getHeight()));
                    bin.addItem(item);
                    // Set the bin is full
                    bin.setFull();
                    return 1;
                }
                // Case that no item can be packed
                return -1;
            }
        }
    }

    // Looping the item placing
    public void paintByUser() {
        bins.add(new Bin(pen,BIN_HEIGHT, BIN_WIDTH));
        createItems(ITEM_SIZE);

        for ( ; ; )
        {
            // Title string
            String title   = "Painter";
            // Message to show how many item is added and how many bin is used
            String message = "Number of Item: " + (ITEM_SIZE - items.size()) + "\nNumber of Bin: " + (k1 + 1);
            // Buttons to be pressed
            Object[] options = {"Next Item", "Print", "Dataset"};
            int response;
            // Same as array, 0 is the first button pressed
            response = JOptionPane.showOptionDialog(null, message, title, JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, icon, options, null);
            // Show which button is pressed in the console
            System.out.println("Response = " + response);

            // If "Next Item" is pressed
            // Put an item
            if(response == 0) {
                // Do whenever there is an item that not yet packed
                if (items.size() > 0) {
                    // Prevent the out of bound
                    if (k2 >= items.size()) {
                        System.out.println("out of bound, k2 = 0");
                        k2 = 0;
                    }

                    // Call for the algorithm
                    int flag = mAlg(bins.get(k1), items.get(k2));

                    // Debug message for the flag checking
                    System.out.println("flag = " + flag);

                    // When an item is packed (flag == 1)
                    // Count for the item packed and remove it from the arraylist
                    if (flag == 1) {
                        i++;
                        items.remove(k2);
                    }
                    // When the item cannot be packed on top of the first item packed
                    else if (flag == 0) {
                        // Do binary search to find the fittest item to pack on top of the first item packed
                        int k_temp = k2;
                        k2 = items.indexOf(binarySearch(items, bins.get(k1).getRemainingHeight()));
                        System.out.println("R-Height = " + bins.get(k1).getRemainingHeight());
                        System.out.println("k2 = " + k2);
                        if (k2 != -1)
                            System.out.println("height = " + items.get(k2).getHeight());

                        // Check if all items cannot be packed on top of the first item packed
                        // Meaning k_temp is already the result of binary search
                        if (k_temp == k2 || k2 == -1) {
                            System.out.println("k_temp==k2 or k2 == -1, add new bin");
                            System.out.println(bins.get(k1).toString());
                            bins.get(k1).setFull();
                            bins.add(new Bin(pen,BIN_HEIGHT, BIN_WIDTH));
                            k1++;
                            k2 = 0;
                        }
                    } else {
                        // Other cases: Try the next item
                        if (k2 + 1 < items.size()) {
                            System.out.println("k2++, k2 = " + k2);
                            k2++;
                        }
                        // If all cannot be packed, just add a new bin
                        else {
                            System.out.println("k2+1 >= size, add new bin");
                            System.out.println(bins.get(k1).toString());
                            bins.get(k1).setFull();
                            bins.add(new Bin(pen,BIN_HEIGHT, BIN_WIDTH));
                            k1++;
                        }
                    }

                    // If the bin is full, add a new one
                    if (bins.get(k1).isFull()) {
                        System.out.println("bin full, add new bin");
                        System.out.println(bins.get(k1).toString());
                        bins.add(new Bin(pen,BIN_HEIGHT, BIN_WIDTH));
                        k1++;
                    }
                }
            }

            // Print message
            else if (response == 1) {

                for (Item item : copy) {
                    System.out.println(item.getHeight() + "x" + item.getWidth() + "x1");
                }

                // Print the dataset
                System.out.println("=====");
                System.out.println(dataSetMsg);
                // Print the information of the bins
                for (int m = 0; m <= k1; m++)
                    System.out.println(bins.get(m).toString());
            }

            // Print the dataset
            else if (response == 2) {
                for (Item item : copy) {
                    System.out.println(item.getHeight() + "x" + item.getWidth() + "x1");
                }
            }

            // If "X" is pressed (i.e., closing the window), return to finish the program
            else {
                return;
            }
        }
    }

    // Modified Binary Search for the Item with height
    // For the items that place on top of the first item
    public Item binarySearch(List<Item> list, int value) {
        // Set the index to the middle of the list
        int index = list.size() / 2;
        // Return null if no item inside the list
        if (list.size() == 0)
            return null;
        // Return the only element inside the list
        if (list.size() == 1)
            return list.get(index);
        // Return the
        if (list.get(index).getHeight() == value)
            return list.get(index);
        // Return the left sublist
        else if (list.get(index).getHeight() > value)
            return binarySearch(list.subList(0, index), value);
        // Return the right sublist
        else
            return binarySearch(list.subList(index + 1, list.size()), value);
    }



    public static void main(String[] args) throws InterruptedException {
        // Create paint object
        Main main = new Main();
        // Call for painting loop
        main.paintByUser();
    }

    public void getData() throws Exception {
        Scanner scanner = new Scanner(new File("data.txt"));
        while(scanner.hasNextLine()) {
            String data = scanner.next();
            String[] splitData = data.split("x");
            mItems.add(new Item(pen, Integer.parseInt(splitData[0]), Integer.parseInt(splitData[1])));
        }
        scanner.close();
    }

    public void createItems(int size) {
        if (TXT) {
            try {
                getData();
            } catch (Exception e) {
                e.printStackTrace();
            }
            items = mItems;
        } else {
            // Create n items and add them into an arraylist
            for (int i=0; i<size; i++) {
                items.add(new Item(pen, rngObj.nextInt(BIN_HEIGHT) + 1, rngObj.nextInt(BIN_WIDTH) + 1));
            }
        }

        // Rotate the item
        for (Item item : items) {
            if (item.getHeight() < item.getWidth()) {
                int temp = item.getHeight();
                item.setHeight(item.getWidth());
                item.setWidth(temp);
            }
        }

        // Sort the item list
        Collections.sort(items);

        // Save the data for message printing
        for (Item item : items) {
            copy.add(item);
            dataSetMsg = "".concat(dataSetMsg + item + " ");
        }
        System.out.println(dataSetMsg);
    }


    // Clear the canvas and rebuild the bins
    public void clearCanvas()
    {
        pen.setColor(Color.BLACK);
        pen.clearRect(0, 0, IMAGE_WIDTH, IMAGE_HEIGHT);


        // Clean the item list
        for (Item item : items) {
            items.remove(item);
        }

        // Reset the distance variables and clean the bin list
        bins.get(0).resetDistance();
        for (int j = 0; j < bins.size(); j++) {
            bins.remove(0);
        }

        // Reset the counting variables
        k1 = 0;
        k2 = 0;
        i = 0;

        // Add the first bin
        bins.add(new Bin(pen,BIN_HEIGHT, BIN_WIDTH));
        // Create a new item list
        createItems(ITEM_SIZE);
    }

/*
    public int mAlg(Bin bin, Item item) {
        // Always put the first item at the left-bottom corner
        if (bin.countItems() == 0) {
            // Set the location of the item and update the remaining dimension of the bin
            item.setLoc(i, bin.getXLoc(), bin.getYLoc() + (bin.getHeight() - item.getHeight()));
            bin.setRemainingHeight(bin.getHeight() - item.getHeight());
            bin.setRemainingWidth(bin.getWidth() - item.getWidth());
            bin.setUpperRemainingWidth(item.getWidth());
            // Add the item to bin
            bin.addItem(item);
            return 1;
        } else {
            // If the item can be packed on top of the first item packed
            if (item.getHeight() <= bin.getRemainingHeight()) {
                // Check if there is enough space on top of the first item packed
                // In order to place the item
                if (bin.getUpperRemainingWidth() >= item.getWidth()) {
                    // Set the location of the item and update the remaining dimension of the bin
                    item.setLoc(i, bin.getXLoc() + bin.getUpperNextLoc(),
                            bin.getYLoc() + bin.getRemainingHeight() - item.getHeight());
                    bin.setUpperRemainingWidth(bin.getUpperRemainingWidth() - item.getWidth());
                    // Add the item to bin
                    bin.addItem(item);
                    return 1;
                } else { // Case that the item cannot be packed on top of the first item packed
                    return 0;
                }
            } else {
                // Put the item next to the first item packed
                // Check if there is enough space for the next item
                if (bin.getFirstWidth() + item.getWidth() <= bin.getWidth()) {
                    // Set the location of the item and add the item to the bin
                    item.setLoc(i, bin.getXLoc() + bin.getFirstWidth(),
                            bin.getYLoc() + (bin.getHeight() - item.getHeight()));
                    bin.addItem(item);
                    // Set the bin is full
                    bin.setFull();
                    return 1;
                }
                // Case that no item can be packed
                return -1;
            }
        }
    }

    // Looping the item placing
    public void paintByUser() {
        bins.add(new Bin(pen,BIN_HEIGHT, BIN_WIDTH));
        createItems(ITEM_SIZE);

        for ( ; ; )
        {
            // Title string
            String title   = "Painter";
            // Message to show how many item is added and how many bin is used
            String message = "Number of Item: " + (ITEM_SIZE - items.size()) + "\nNumber of Bin: " + (k1 + 1);
            // Buttons to be pressed
            Object[] options = {"Next Item", "Print", "Dataset"};
            int response;
            // Same as array, 0 is the first button pressed
            response = JOptionPane.showOptionDialog(null, message, title, JOptionPane.DEFAULT_OPTION,
                    JOptionPane.PLAIN_MESSAGE, icon, options, null);

            // If "Next Item" is pressed
            // Put an item
            if(response == 0) {
                // Do whenever there is an item that not yet packed
                if (items.size() > 0) {
                    // Prevent the out of bound
                    if (k2 >= items.size()) {
                        k2 = 0;
                    }

                    // Call for the algorithm
                    int flag = mAlg(bins.get(k1), items.get(k2));

                    // Debug message for the flag checking

                    // When an item is packed (flag == 1)
                    // Count for the item packed and remove it from the arraylist
                    if (flag == 1) {
                        i++;
                        items.remove(k2);
                    }
                    // When the item cannot be packed on top of the first item packed
                    else if (flag == 0) {
                        // Do binary search to find the fittest item to pack on top of the first item packed
                        int k_temp = k2;
                        k2 = items.indexOf(binarySearch(items, bins.get(k1).getRemainingHeight()));

                        // Check if all items cannot be packed on top of the first item packed
                        // Meaning k_temp is already the result of binary search
                        if (k_temp == k2 || k2 == -1) {
                            bins.get(k1).setFull();
                            bins.add(new Bin(pen,BIN_HEIGHT, BIN_WIDTH));
                            k1++;
                            k2 = 0;
                        }
                    } else {
                        // Other cases: Try the next item
                        if (k2 + 1 < items.size()) {
                            k2++;
                        }
                        // If all cannot be packed, just add a new bin
                        else {
                            bins.get(k1).setFull();
                            bins.add(new Bin(pen,BIN_HEIGHT, BIN_WIDTH));
                            k1++;
                        }
                    }

                    // If the bin is full, add a new one
                    if (bins.get(k1).isFull()) {
                        bins.add(new Bin(pen,BIN_HEIGHT, BIN_WIDTH));
                        k1++;
                    }
                }
            }

            // If "X" is pressed (i.e., closing the window), return to finish the program
            else {
                return;
            }
        }
    }
*/
/*
    // Modified Binary Search for the Item with height
    // For the items that place on top of the first item
    public Item binarySearch(List<Item> list, int value) {
        // Set the index to the middle of the list
        int index = list.size() / 2;
        // Return null if no item inside the list
        if (list.size() == 0)
            return null;
        // Return the only element inside the list
        if (list.size() == 1)
            return list.get(index);
        // Return the element if the height is exactly the same
        if (list.get(index).getHeight() == value)
            return list.get(index);
            // Return the left sublist
        else if (list.get(index).getHeight() > value)
            return binarySearch(list.subList(0, index), value);
            // Return the right sublist
        else
            return binarySearch(list.subList(index + 1, list.size()), value);
    }*/


}





















