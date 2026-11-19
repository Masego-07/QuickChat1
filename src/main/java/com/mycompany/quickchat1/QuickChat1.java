/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.quickchat1;
import java.util.Scanner;

/**
 * MAIN CLASS: brings Part 1 (Login), Part 2 (Message) and Part 3 (MessageManager) together.
 *
 * @author masego
 */
public class QuickChat1 {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Login user = new Login();

        // =================== PART 1: register and log in ===================
        System.out.println("=== QuickChat Registration ===");

        boolean registeredSuccessfully = false;

        while (!registeredSuccessfully) {
            System.out.print("Enter first name: ");
            user.setFirstName(scanner.nextLine());

            System.out.print("Enter last name: ");
            user.setLastName(scanner.nextLine());

            System.out.print("Enter a username (must contain '_' and be at most 5 characters): ");
            user.setUsername(scanner.nextLine());

            System.out.print("Enter a password (8+ characters, capital letter, number, special character): ");
            user.setPassword(scanner.nextLine());

            System.out.print("Enter South African cell number (e.g. +27838968976): ");
            user.setCellPhoneNumber(scanner.nextLine());

            String result = user.registerUser();
            System.out.println(result);

            registeredSuccessfully = result.startsWith("Username successfully");
        }

        System.out.println("\n=== QuickChat Login ===");

        boolean loggedIn = false;
        int attempts = 0;

        while (!loggedIn && attempts < 3) {
            System.out.print("Enter username: ");
            String loginUsername = scanner.nextLine();

            System.out.print("Enter password: ");
            String loginPassword = scanner.nextLine();

            String status = user.returnLoginStatus(loginUsername, loginPassword);
            System.out.println(status);

            loggedIn = user.loginUser(loginUsername, loginPassword);
            attempts++;
        }

        // Users may only send messages once they have logged in
        if (!loggedIn) {
            System.out.println("Too many failed login attempts. Goodbye.");
            scanner.close();
            return;
        }

        // =================== PART 2: send messages ===================
        System.out.println("\nWelcome to QuickChat.");

        // PART 3: the manager keeps every message in arrays.
        // Stored messages from earlier sessions are read from the JSON file into the arrays.
        MessageManager manager = new MessageManager();
        manager.setSender(user.getFirstName() + " " + user.getLastName()
                + " (" + user.getCellPhoneNumber() + ")");
        System.out.println(manager.loadStoredMessages(Message.getStorageFile()));

        int numberOfMessages = readNumber(scanner, "How many messages do you want to send? ");
        int messagesEntered = 0;
        boolean running = true;

        // WHILE loop: keeps the app running until the user chooses Quit
        while (running) {
            System.out.println("\n1) Send Messages");
            System.out.println("2) Show recently sent messages");
            System.out.println("3) Quit");
            System.out.println("4) Stored Messages");
            int option = readNumber(scanner, "Choose an option: ");

            switch (option) {
                case 1:
                    if (messagesEntered >= numberOfMessages) {
                        System.out.println("You have reached your message limit.");
                        break;
                    }

                    // FOR loop: only lets the user enter the number of messages they chose
                    for (int i = messagesEntered; i < numberOfMessages; i++) {
                        System.out.println("\nMessage " + (i + 1) + " of " + numberOfMessages);
                        Message message = captureMessage(scanner, i);

                        System.out.println("\n1) Send Message");
                        System.out.println("2) Disregard Message");
                        System.out.println("3) Store Message to send later");
                        int messageOption = readNumber(scanner, "Choose an option: ");
                        while (messageOption < 1 && messageOption > 3) {
                            System.out.println("Please choose 1, 2 or 3.");
                            messageOption = readNumber(scanner, "Choose an option: ");
                        }

                        System.out.println(message.sentMessage(messageOption));

                        if (messageOption == 1) {
                            manager.addMessage(message, MessageManager.SENT);
                            System.out.println(message.printDetails());
                        } else if (messageOption == 2) {
                            // The brief's disregard prompt: "Press 0 to delete the message."
                            while (!scanner.nextLine().trim().equals("0")) {
                                System.out.println("Press 0 to delete the message.");
                            }
                            manager.addMessage(message, MessageManager.DISREGARDED);
                            System.out.println("Message deleted.");
                        } else {
                            manager.addMessage(message, MessageManager.STORED);
                            System.out.println(message.printDetails());
                        }

                        messagesEntered++;
                    }
                    System.out.println("\nTotal number of messages sent: " + Message.returnTotalMessages());
                    break;

                case 2:
                    System.out.println(Message.printMessages());
                    break;

                case 3:
                    running = false;
                    System.out.println("Total number of messages sent: " + Message.returnTotalMessages());
                    System.out.println("Goodbye!");
                    break;

                case 4:
                    showStoredMessagesMenu(scanner, manager);
                    break;

                default:
                    System.out.println("Invalid option. Please choose 1, 2, 3 or 4.");
            }
        }

        scanner.close();
    }

    // Asks for the recipient and message, and asks AGAIN until both are valid.
    // i is the loop counter, used as the message number.
    private static Message captureMessage(Scanner scanner, int i) {
        System.out.print("Enter recipient cell number (e.g. +27718693002): ");
        String recipient = scanner.nextLine().trim();
        while (!Login.isValidCellNumber(recipient)) {
            System.out.println("Cell phone number is incorrectly formatted or does not contain an "
                    + "international code. Please correct the number and try again.");
            System.out.print("Enter recipient cell number: ");
            recipient = scanner.nextLine().trim();
        }
        System.out.println("Cell phone number successfully captured.");

        System.out.print("Enter your message: ");
        String messageText = scanner.nextLine();
        while (messageText.trim().isEmpty() || messageText.length() > 250) {
            if (messageText.trim().isEmpty()) {
                System.out.println("The message cannot be empty.");
            } else {
                System.out.println("Please enter a message of less than 250 characters.");
            }
            System.out.print("Enter your message: ");
            messageText = scanner.nextLine();
        }

        Message message = new Message(i, recipient, messageText);
        System.out.println(message.checkMessageLength());
        System.out.println("Message ID generated: " + message.getMessageID());
        return message;
    }

    // =================== PART 3: everything we can do with the arrays ===================
    private static void showStoredMessagesMenu(Scanner scanner, MessageManager manager) {
        boolean inMenu = true;

        while (inMenu) {
            System.out.println("\n=== Stored Messages ===");
            System.out.println("1) Display the sender and recipient of all stored messages");
            System.out.println("2) Display the longest stored message");
            System.out.println("3) Search for a message ID");
            System.out.println("4) Search for all messages to a recipient");
            System.out.println("5) Delete a message using the message hash");
            System.out.println("6) Display the full message report");
            System.out.println("7) Back to the main menu");
            int choice = readNumber(scanner, "Choose an option: ");

            switch (choice) {
                case 1:
                    System.out.println(manager.displayStoredSendersAndRecipients());
                    break;
                case 2:
                    System.out.println("Longest stored message: " + manager.getLongestStoredMessage());
                    break;
                case 3:
                    System.out.print("Enter the message ID: ");
                    System.out.println(manager.searchByMessageID(scanner.nextLine().trim()));
                    break;
                case 4:
                    System.out.print("Enter the recipient's cell number: ");
                    System.out.println(manager.searchByRecipient(scanner.nextLine().trim()));
                    break;
                case 5:
                    System.out.print("Enter the message hash: ");
                    System.out.println(manager.deleteByHash(scanner.nextLine().trim().toUpperCase()));
                    break;
                case 6:
                    System.out.println(manager.displayReport());
                    break;
                case 7:
                    inMenu = false;
                    break;
                default:
                    System.out.println("Please choose a number from 1 to 7.");
            }
        }
    }

    // Reads a whole number. Asks again (instead of crashing) if the user types letters.
    private static int readNumber(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a number.");
            }
        }
    }
}
