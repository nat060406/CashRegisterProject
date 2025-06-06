/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package arocenanathaniel.finals;

/**
 *
 * @author NATHANIEL AROCENA
 */
import java.util.*;
import java.util.ArrayList;
import java.io.*;
import java.util.regex.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Finals {

    static ArrayList<String> usernames = new ArrayList<>();
    static ArrayList<String> passwords = new ArrayList<>();
    static String currentUser = "";

    public static void main(String[] args) {
        Scanner nyek = new Scanner(System.in);

        System.out.println("-------- User Sign-up --------");
        signup(nyek);

        System.out.println("\n-------- User Log-in --------");
        login(nyek);

        System.out.println("\nLogin successful! Welcome to James Reid's Cafe!");

        boolean anotherTransaction;
        do {
            ArrayList<String> productNames = new ArrayList<>();
            ArrayList<Double> productPrices = new ArrayList<>();
            ArrayList<Integer> productQuantities = new ArrayList<>();

            initializeProducts(productNames, productPrices, productQuantities);

            showAvailFoods(productNames, productPrices);
            addProductsToCart(nyek, productNames, productPrices, productQuantities);

            int itemsAdded = 0;
            for (int qty : productQuantities) {
                if (qty > 0) itemsAdded++;
            }
            if (itemsAdded < 4) {
                System.out.println("\nPlease add at least 4 items before proceeding.");
                addProductsToCart(nyek, productNames, productPrices, productQuantities);
            }

            displayOrders(productNames, productPrices, productQuantities);
            updateOrder(nyek, productNames, productQuantities);
            removeOrder(nyek, productNames, productQuantities);
            displayOrders(productNames, productPrices, productQuantities);

            double total = calculateTotalPrice(productPrices, productQuantities);
            total = applyDiscount(nyek, total);

            System.out.printf("\nTotal Price: Php%.2f%n", total);
            double cashAmount = acceptPayment(nyek, total);
            double change = calculateChange(cashAmount, total);
            printReceipt(productNames, productPrices, productQuantities, total, cashAmount, change);
            logTransaction(productNames, productPrices, productQuantities, total, cashAmount, change);

            System.out.print("\nWould you like to make another transaction? (yes/no): ");
            anotherTransaction = nyek.nextLine().equalsIgnoreCase("yes");

        } while (anotherTransaction);

        System.out.println("\nThank you for visiting James Reid's Cafe! Goodbye!");
    }

    public static void signup(Scanner nyek) {
        while (true) {
            System.out.print("Create your username (must have 5-15 alphanumeric characters): ");
            String username = nyek.nextLine();

            System.out.print("Create a password (must have 8-20 characters, at least 1 uppercase, 1 number): ");
            String password = nyek.nextLine();

            boolean usernameValid = Pattern.matches("^[a-zA-Z0-9]{5,15}$", username);
            boolean passwordValid = Pattern.matches("^(?=.*[A-Z])(?=.*\\d).{8,20}$", password);

            if (usernameValid && passwordValid) {
                usernames.add(username);
                passwords.add(password);
                System.out.println("Signup successful!");
                break;
            } else {
                System.out.println("\nInvalid username or password. Try again.\n");
            }
        }
    }

    public static void login(Scanner nyek) {
        while (true) {
            System.out.print("Enter your username: ");
            String username = nyek.nextLine();
            System.out.print("Enter your password: ");
            String password = nyek.nextLine();

            for (int i = 0; i < usernames.size(); i++) {
                if (usernames.get(i).equals(username) && passwords.get(i).equals(password)) {
                    currentUser = username;
                    return;
                }
            }
            System.out.println("\nIncorrect username or password. Try again.\n");
        }
    }

    public static void initializeProducts(ArrayList<String> productNames, ArrayList<Double> productPrices, ArrayList<Integer> productQuantities) {
        productNames.addAll(List.of(
            "Biscoff Latte", "Matcha Kastila", "Caramel Macchiato", "Matcha Oreo", "Biscoff Matcha",
            "Creme Brulee Latte", "Coffee Jelly", "Choco Temptation", "Matcha Berry", "James Reid's Signature",
            "Extra Shot of Espresso", "Whipped Cream", "Vanilla Syrup", "Caramel Syrup", "Oat Milk",
            "Croissant", "Macaroons", "Red Velvet Crinkles", "Cinnamon Roll", "Cheesecake Slice"
        ));

        productPrices.addAll(List.of(
            150.00, 180.00, 170.00, 190.00, 200.00,
            220.00, 250.00, 130.00, 160.00, 210.00,
            30.00, 20.00, 15.00, 15.00, 25.00,
            50.00, 60.00, 70.00, 80.00, 120.00
        ));

        for (int i = 0; i < productNames.size(); i++) {
            productQuantities.add(0);
        }
    }

    public static void showAvailFoods(ArrayList<String> productNames, ArrayList<Double> productPrices) {
        System.out.println("\n----- James Reid's Cafe Menu -----");
        System.out.println("* Drinks *");
        for (int i = 0; i < 10; i++) {
            System.out.printf("%d. %-25s Php%.2f%n", i + 1, productNames.get(i), productPrices.get(i));
        }
        System.out.println("\n*** Add-ons *");
        for (int i = 10; i < 15; i++) {
            System.out.printf("%d. %-25s Php%.2f%n", i + 1, productNames.get(i), productPrices.get(i));
        }
        System.out.println("\n*** Pastries *");
        for (int i = 15; i < 20; i++) {
            System.out.printf("%d. %-25s Php%.2f%n", i + 1, productNames.get(i), productPrices.get(i));
        }
        System.out.println("\nEnter the product number to add to the cart (or type 'done' to finish):");
    }

    public static void addProductsToCart(Scanner nyek, ArrayList<String> productNames, ArrayList<Double> productPrices, ArrayList<Integer> productQuantities) {
        while (true) {
            String input = nyek.nextLine();
            if (input.equalsIgnoreCase("done")) {
                break;
            }
            try {
                int productId = Integer.parseInt(input) - 1;
                if (productId >= 0 && productId < productNames.size()) {
                    System.out.println("Enter quantity:");
                    int quantity = Integer.parseInt(nyek.nextLine());
                    if (quantity > 0) {
                        int currentQuantity = productQuantities.get(productId);
                        productQuantities.set(productId, currentQuantity + quantity);
                        System.out.printf("%s (x%d) added to your cart.%n", productNames.get(productId), quantity);
                    } else {
                        System.out.println("Quantity must be greater than 0.");
                    }
                } else {
                    System.out.println("Invalid product number. Please try again.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid product number or 'done'.");
            }
        }
    }

    public static void displayOrders(ArrayList<String> productNames, ArrayList<Double> productPrices, ArrayList<Integer> productQuantities) {
        System.out.println("\n--- Your Current Orders ---");
        boolean hasItems = false;
        for (int i = 0; i < productNames.size(); i++) {
            if (productQuantities.get(i) > 0) {
                System.out.printf("%d. %-25s | Qty: %d | Price: Php%.2f%n", i + 1, productNames.get(i), productQuantities.get(i), productPrices.get(i));
                hasItems = true;
            }
        }
        if (!hasItems) {
            System.out.println("Cart is empty.");
        }
    }

    public static void updateOrder(Scanner nyek, ArrayList<String> productNames, ArrayList<Integer> productQuantities) {
        System.out.println("\nDo you want to update an order? (yes/no)");
        String response = nyek.nextLine();
        if (!response.equalsIgnoreCase("yes")) return;

        while (true) {
            try {
                System.out.print("Enter product number to update: ");
                int index = Integer.parseInt(nyek.nextLine()) - 1;
                if (index >= 0 && index < productNames.size() && productQuantities.get(index) > 0) {
                    System.out.print("Enter new quantity: ");
                    int newQty = Integer.parseInt(nyek.nextLine());
                    if (newQty > 0) {
                        productQuantities.set(index, newQty);
                        System.out.printf("%s quantity updated to %d.%n", productNames.get(index), newQty);
                    } else {
                        System.out.println("Quantity must be greater than 0.");
                    }
                    break;
                } else {
                    System.out.println("Invalid product number or item not in cart.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Please enter valid numeric values.");
            }
        }
    }

    public static void removeOrder(Scanner nyek, ArrayList<String> productNames, ArrayList<Integer> productQuantities) {
        System.out.println("\nDo you want to remove an item from your cart? (yes/no)");
        String response = nyek.nextLine();
        if (!response.equalsIgnoreCase("yes")) return;

        while (true) {
            try {
                System.out.print("Enter product number to remove: ");
                int index = Integer.parseInt(nyek.nextLine()) - 1;
                if (index >= 0 && index < productNames.size() && productQuantities.get(index) > 0) {
                    productQuantities.set(index, 0);
                    System.out.printf("%s removed from cart.%n", productNames.get(index));
                    break;
                } else {
                    System.out.println("Invalid product number or item not in cart.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }

    public static double calculateTotalPrice(ArrayList<Double> productPrices, ArrayList<Integer> productQuantities) {
        double totalPrice = 0;
        for (int i = 0; i < productPrices.size(); i++) {
            totalPrice += productPrices.get(i) * productQuantities.get(i);
        }
        return totalPrice;
    }

    public static double applyDiscount(Scanner nyek, double totalPrice) {
        System.out.println("Do you have any discount? (pwd/senior/voucher/none)");
        String response = nyek.nextLine();
        if (response.equalsIgnoreCase("pwd") || response.equalsIgnoreCase("senior")) {
            double discountAmount = totalPrice * 0.30;
            totalPrice -= discountAmount;
            System.out.printf("30%% PWD/Senior Citizen discount applied: Php%.2f%n", discountAmount);
        } else if (response.equalsIgnoreCase("voucher")) {
            double discountAmount = totalPrice * 0.50;
            totalPrice -= discountAmount;
            System.out.printf("50%% Voucher discount applied: Php%.2f%n", discountAmount);
        } else if (!response.equalsIgnoreCase("none")) {
            System.out.println("Invalid discount option, no discount applied.");
        } else {
            System.out.println("No discount applied.");
        }
        return totalPrice;
    }

    public static double acceptPayment(Scanner nyek, double totalPrice) {
        double cashAmount = 0;
        while (cashAmount < totalPrice) {
            System.out.printf("Please enter cash amount (Php): ");
            try {
                cashAmount = nyek.nextDouble();
                nyek.nextLine();
                if (cashAmount < totalPrice) {
                    System.out.println("Insufficient payment. Please pay the total amount.");
                }
            } catch (Exception e) {
                System.out.println("Invalid input. Please enter a valid number.");
                nyek.next();
            }
        }
        return cashAmount;
    }

    public static double calculateChange(double cashAmount, double totalPrice) {
        return cashAmount - totalPrice;
    }

    public static void printReceipt(ArrayList<String> productNames, ArrayList<Double> productPrices, ArrayList<Integer> productQuantities, double totalPrice, double cashAmount, double change) {
        System.out.println("\n--- Receipt ---");
        for (int i = 0; i < productNames.size(); i++) {
            int quantity = productQuantities.get(i);
            if (quantity > 0) {
                double price = productPrices.get(i);
                double totalItemPrice = price * quantity;
                System.out.printf("Product: %-25s | Price: Php%.2f | Quantity: %-2d | Total: Php%.2f%n",
                        productNames.get(i), price, quantity, totalItemPrice);
            }
        }
        System.out.printf("\nTotal Price: Php%.2f%n", totalPrice);
        System.out.printf("Cash Amount: Php%.2f%n", cashAmount);
        System.out.printf("Change: Php%.2f%n", change);
    }

    public static void logTransaction(ArrayList<String> productNames, ArrayList<Double> productPrices, ArrayList<Integer> productQuantities, double totalPrice, double cashAmount, double change) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("transactions.txt", true))) {
            writer.write("----- Transaction -----\n");
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            writer.write("Date/Time: " + dtf.format(LocalDateTime.now()) + "\n");
            writer.write("Cashier: " + currentUser + "\n");

            for (int i = 0; i < productNames.size(); i++) {
                int qty = productQuantities.get(i);
                if (qty > 0) {
                    double price = productPrices.get(i);
                    writer.write(String.format("Item: %-25s | Qty: %d | Price: Php%.2f%n", productNames.get(i), qty, price));
                }
            }

            writer.write(String.format("Total: Php%.2f%n", totalPrice));
            writer.write(String.format("Cash: Php%.2f%n", cashAmount));
            writer.write(String.format("Change: Php%.2f%n", change));
            writer.write("-------------------------\n\n");
        } catch (IOException e) {
            System.out.println("Error writing to transactions.txt");
        }
    }
}

