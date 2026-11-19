/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.quickchat1;
import java.util.Scanner;

/**
 
 * @author masego
 */
public class QuickChat1 {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Login user = new Login();

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
        //new part two code
        if (loggedIn) {
    System.out.println("\nWelcome to QuickChat.");
    System.out.print("How many messages do you want to send? ");
    int numberOfMessages = Integer.parseInt(scanner.nextLine());

    int messagesEntered = 0;
    boolean running = true;

    while (running) {
        System.out.println("\n1) Send Messages");
        System.out.println("2) Show recently sent messages");
        System.out.println("3) Quit");
        System.out.print("Choose an option: ");

        String option = scanner.nextLine();

        switch (option) {
            case "1":
                if (messagesEntered >= numberOfMessages) {
                    System.out.println("You have reached your message limit.");
                    break;
                }

                for (int i = messagesEntered; i < numberOfMessages; i++) {
                    System.out.print("Enter recipient cell number: ");
                    String recipient = scanner.nextLine();

                    System.out.print("Enter your message: ");
                    String messageText = scanner.nextLine();

                    Message message = new Message(i, recipient, messageText);

                    System.out.println(message.checkRecipientCell());
                    System.out.println(message.checkMessageLength());

                    System.out.println("\n1) Send Message");
                    System.out.println("2) Disregard Message");
                    System.out.println("3) Store Message to send later");
                    System.out.print("Choose an option: ");

                    int messageOption = Integer.parseInt(scanner.nextLine());
                    System.out.println(message.sentMessage(messageOption));

                    if (messageOption == 1) {
                        System.out.println(message.printDetails());
                    }

                    messagesEntered++;
                }
                break;

            case "2":
                System.out.println("Coming Soon.");
                break;

            case "3":
                running = false;
                System.out.println("Goodbye!");
                break;

            default:
                System.out.println("Invalid option.");
        }
    }
}

        scanner.close();
    }
}

