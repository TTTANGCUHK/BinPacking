package me.fyp;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import java.util.*;

public class Main {
    public static final int IMAGE_WIDTH  = 700;
    public static final int IMAGE_HEIGHT = 600;
    public final int ITEM_SIZE = 20;

    private Bin bin;
    private ArrayList<Item> items;
    Random rngObj;

    private Graphics pen;
    private ImageIcon icon;

    int k = 0;

    // Initialization / Constructor
    public Main() {
        BufferedImage img = new BufferedImage(IMAGE_WIDTH, IMAGE_HEIGHT, BufferedImage.TYPE_INT_ARGB);
        icon = new ImageIcon(img);
        pen = img.createGraphics();
        items = new ArrayList<>();
        rngObj = new Random();
    }

    // Looping the item placing
    public void paintByUser() throws InterruptedException {
        bin = new Bin(300, 210);
        createItems(ITEM_SIZE);
        clearCanvas();

        for ( ; ; )
        {
            // Title string
            String title   = "Painter";
            // Message to show how many item is added
            String message = "Number of Item: " + k;
            // Buttons to be pressed
            Object[] options = {"Next Item", "Clear"};
            int response;
            // Same as array, 0 is the first button pressed
            response = JOptionPane.showOptionDialog(null, message, title, JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, icon, options, null);
            // Show which button is pressed in the console
            System.out.println("Response = " + response);

            // If "Next Item" is pressed
            // Put an item
            if(response == 0) {
                if (k < ITEM_SIZE) {
                    putItem(k);
                    k++;
                }
            }
            // If "Clear" is pressed, clear the canvas and rebuild bins
            else if(response == 1) {
                clearCanvas();
            }
            // If "X" is pressed (i.e., closing the window), return to finish the program
            else {
                return;
            }
        }
    }

    // Paint the bin
    public void paintRectangle(int leftX, int bottomY, int height, int width) {
        // Bin color is set to white
        pen.setColor(Color.WHITE);
        // Place the bin at left bottom corner with 5px margin
        pen.fillRect(leftX + 5, IMAGE_HEIGHT + bottomY - height - 5, width, height);
    }

    // Paint the item with color R->G->B
    public void paintItem(int leftX, int bottomY, int height, int width) {
        if (k % 3 == 0) {
            pen.setColor(Color.RED);
        } else if (k % 3 == 1) {
            pen.setColor(Color.GREEN);
        } else {
            pen.setColor(Color.BLUE);
        }
        // TO NEED MODIFY AS NO ALGORITHM IS IMPLEMENTED
        pen.fillRect(leftX + 5, IMAGE_HEIGHT + bottomY - height - 5, width, height);
    }

    // Clear the canvas and rebuild the bins
    public void clearCanvas()
    {
        pen.setColor(Color.BLACK);
        pen.clearRect(0, 0, IMAGE_WIDTH, IMAGE_HEIGHT);
        paintRectangle(0, 0, bin.getHeight(), bin.getWidth());
        paintRectangle(bin.getWidth() + 5, 0, bin.getHeight(), bin.getWidth());
        paintRectangle((bin.getWidth() + 5) * 2, 0, bin.getHeight(), bin.getWidth());
    }

    public static void main(String[] args) throws InterruptedException {
        // Create paint object
        Main obj = new Main();
        // Call for painting loop
        obj.paintByUser();
    }

    public void createItems(int size) {
        // Create n items and add them into an arraylist
        for (int i=0; i<size; i++) {
            items.add(new Item(rngObj.nextInt(bin.getHeight() + 1), rngObj.nextInt(bin.getWidth() + 1)));
        }
        // Sort the item list
        Collections.sort(items);

//        for (int i=0; i<size; i++) {
//            System.out.println(items.get(i).getHeight());
//        }
    }

    public void putItem(int index) {
        paintItem(0, 0, items.get(index).getHeight(), items.get(index).getWidth());
    }

}