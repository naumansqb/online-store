
package com.pluralsight;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.function.ToDoubleBiFunction;

public class Store {
    private static final String filename="products.csv";
    public static void main(String[] args) {
        ArrayList<Product> inventory = new ArrayList<>();
        ArrayList<Product> cart = new ArrayList<>();

        loadInventory(filename, inventory);

        Scanner scanner = new Scanner(System.in);
        int choice = -1;
        while (choice != 3) {
            System.out.println("\nWelcome to the Online Store!");
            System.out.println("1. Show Products");
            System.out.println("2. Show Cart");
            System.out.println("3. Exit");
            System.out.print("Your choice: ");

            if (!scanner.hasNextInt()) {
                System.out.println("Please enter 1, 2, or 3.");
                scanner.nextLine();
                continue;
            }
            choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1 -> displayProducts(inventory, cart, scanner);
                case 2 -> displayCart(cart, scanner);
                case 3 -> System.out.println("Thank you for shopping with us!");
                default -> System.out.println("Invalid choice!");
            }
        }
        scanner.close();
    }

    /**
     * Reads product data from a file and populates the inventory list.
     * File format (pipe-delimited):
     * id|name|price
     * <p>
     * Example line:
     * A17|Wireless Mouse|19.99
     */
    public static void loadInventory(String fileName, ArrayList<Product> inventory) {
        File file= new File(fileName);
        try{
            if(!file.exists()){
                file.createNewFile();
            }
        }catch(Exception e){
            System.out.println("Error creating a new file");
        }
        try(BufferedReader reader = new BufferedReader(new FileReader(fileName))){
            String line="";
            while((line= reader.readLine())!=null){
                String [] tokens= line.split("\\|");
                if(tokens.length!=3){
                    continue;
                }
                if(line.isBlank()){
                    continue;
                }
                String id= tokens[0].trim();
                String description= tokens[1].trim();
                double price= Double.parseDouble(tokens[2].trim());
                inventory.add(new Product(price,id,description));
            }
        }catch(Exception e){
            System.out.println("Error reading from file");
        }
    }


    public static void displayHeader(){
        System.out.println("-".repeat(67));
        System.out.printf("%-12s | %-40s | %s %n","ID", "Description", "Price");
        System.out.println("-".repeat(67));
    }
    public static void displayCartHeader(){
        System.out.println("-".repeat(80));
        System.out.printf("%-5s | %-12s | %-40s | %s %n","Qty","ID", "Description", "Price");
        System.out.println("-".repeat(80));
    }

    /**
     * Displays all products and lets the user add one to the cart.
     * Typing X returns to the main menu.
     */
    public static void displayProducts(ArrayList<Product> inventory,
                                       ArrayList<Product> cart,
                                       Scanner scan) {
        displayHeader();
        for(Product p: inventory){
            System.out.println(p);
        }
        while(true){
            System.out.println("\nWould you like to buy anything or return back home?");
            System.out.println("Y - Yes");
            System.out.println("X - Return Home");
            System.out.print("Your choice: ");
            String wantingToBuy=scan.nextLine().trim();
            switch (wantingToBuy.toLowerCase()){
                case "y":
                    System.out.println("Enter the ID for the product you'd like: ");
                    String productId= scan.nextLine().trim();
                    Product p= findProductById(productId,inventory);
                    if(p!=null){
                        cart.add(new Product(p.getPrice(),p.getId(),p.getDescription()));
                        System.out.println(p.getId()+" has been added to your cart");
                    }
                    break;
                case "x":
                    System.out.println("Return back home");
                    return;
                default:
                    System.out.println("Invalid Entry. Please input 'Y' or 'X'");
            }
        }
    }

    /**
     * Shows the contents of the cart, calculates the total,
     * and offers the option to check out.
     */
    public static void displayCart(ArrayList<Product> cart, Scanner scanner) {
        if(cart.isEmpty()){
            System.out.println("Nothing in your cart as of now.");
            return;
        }
        System.out.println("Displaying Cart");
        displayCartHeader();

        HashMap<String,Integer> qty= new HashMap<>();

        for (Product p : cart) {
            qty.put(p.getId(), qty.getOrDefault(p.getId(),0)+1);
        }
        double totalCost=0;
        for (Product p : cart) {
            if (qty.containsKey(p.getId())) {
                int totalQty = qty.get(p.getId());
                double subtotal = totalQty * p.getPrice();
                totalCost += subtotal;
                System.out.printf("%-5s | %-12s | %-40s | $%,.2f %n",
                        totalQty, p.getId(), p.getDescription(), subtotal);
                qty.remove(p.getId());
            }
        }

        System.out.println("-".repeat(80));
        System.out.printf("Your total as of now is: $%,.2f%n",totalCost);
        System.out.println("-".repeat(80));

        while(true){
            System.out.println("What would you like to do next? ");
            System.out.println("C- Checkout");
            System.out.println("X- Return Home");
            System.out.println("Your choice: ");
            String toCheckOut= scanner.nextLine().trim().toLowerCase();
            switch (toCheckOut){
                case "c":
                    checkOut(cart,totalCost,scanner);
                    return;
                case "x":
                    System.out.println("Returning Home");
                    return;
                default:
                    System.out.println("Invalid entry. Please enter C or X");
            }
        }
    }

    /**
     * Handles the checkout process:
     * 1. Confirm that the user wants to buy.
     * 2. Accept payment and calculate change.
     * 3. Display a simple receipt.
     * 4. Clear the cart.
     */
    public static void checkOut(ArrayList<Product> cart,
                                double totalAmount,
                                Scanner scanner) {
        while (true) {
            System.out.println("Are you sure you'd like to checkout right now?");
            System.out.println("Y - Yes\nX - Return Home");
            String toBuy = scanner.nextLine().trim().toLowerCase();

            switch (toBuy) {
                case "y":
                    double remainingPayment = totalAmount;
                    System.out.printf("Your total is: $%,.2f\n", remainingPayment);
                    while (remainingPayment > 0) {
                        System.out.print("Please pay the amount: ");

                        try {
                            double pay = Double.parseDouble(scanner.nextLine());

                            if (pay <= 0) {
                                System.out.println("Amount must be greater than 0.");
                                continue;
                            }

                            remainingPayment -= pay;

                            if (remainingPayment > 0) {
                                System.out.printf("Remaining balance: %,.2f\n", remainingPayment);
                            } else if (remainingPayment < 0) {
                                double change = -remainingPayment;
                                System.out.printf("Payment complete. Your change is: %,.2f\n", change);
                            } else {
                                System.out.println("Payment complete. Thank you for your purchase!");
                            }

                        } catch (NumberFormatException e) {
                            System.out.println("Invalid input. Please enter a numerical value.");
                        }
                    }
                    displayReceipt(cart,totalAmount);
                    cart.clear();
                    return;
                case "x":
                    System.out.println("Returning home...");
                    return;
                default:
                    System.out.println("Invalid option. Please enter Y or X.");
            }
        }
    }
    public static void displayReceipt(ArrayList<Product>cart, double totalPaid){
        HashMap<String, Integer> checkQty= new HashMap<>();
        for(Product p: cart){
            checkQty.put(p.getId(),checkQty.getOrDefault(p.getId(),0)+1);
        }
        System.out.println("=".repeat(80));
        System.out.printf("%-30s OFFICIAL RECEIPT%n","");
        System.out.println("=".repeat(80));
        displayCartHeader();
        for(Product p: cart){
            if(checkQty.containsKey(p.getId())){
                int qty= checkQty.get(p.getId());
                double total= qty * p.getPrice();
                System.out.printf("%-5s | %-12s | %-40s | $%,.2f %n",
                        qty, p.getId(), p.getDescription(), total);
                checkQty.remove(p.getId());
            }
        }
        System.out.println("-".repeat(80));
        System.out.printf("%-25s Total Paid: $%,.2f%n","", totalPaid);
        System.out.println("=".repeat(80));
        System.out.printf("%-25s THANK YOU FOR YOUR PURCHASE!%n","");
        System.out.println("=".repeat(80));
    }

    /**
     * Searches a list for a product by its id.
     *
     * @return the matching Product, or null if not found
     */
    public static Product findProductById(String id, ArrayList<Product> inventory) {
        for (Product p : inventory) {
            if (p.getId().equalsIgnoreCase(id)) {
                return p;
            }
        }
        System.out.println("No item with ID: '" + id + "' exists in our catalog");
        return null;
    }
}

 