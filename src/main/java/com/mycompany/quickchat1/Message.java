/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.quickchat1;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 *
 * @author masego
 */

// Message class; stores, validates,and processes QuickChat messages.

public class Message {
    private  static int totalMessagesSent = 0;
    private static final List<String> sentMessages = new ArrayList<>();
    
    String messageID;
    int messageNumber;
    String recipient;
    String messageText;
    String messageHash;

// Normal constructor: saves the details, makes a random ID and the hash.

public Message (int messageNumber, String recipient, String messageText){
   this.messageNumber = messageNumber;
   this.recipient = recipient;
   this.messageText = messageText;
   this.messageID = generateMesssageID();
   this.messageHash = createMessageHash();
    
}
    
//Builds a random 10-digit ID using a loop 
public static String generateMesssageID(){
    Random random = new Random();
    StringBuilder id = new StringBuilder();
    for (int i =0; i< 10; i++){
        id.append(random.nextInt(10));
    }
    return id.toString();
    
}
//Used by unit tests to set a known ID (Hash is updated too)
public void setMessageID(String messageID){
    this.messageID = messageID;
    this.messageHash = createMessageHash();
}
//ID must not be more than ten characters
public boolean checkMessageID(){
return messageID != null && messageID.length() <= 10;
}   
//Message must not be more than 250 characters
public String checkMessageLength (){
    if (messageText.length()<=250){
        return "Message ready to send.";
       
    }
    int over = messageText.length() - 250;
    return  "Message exceeds 250 characters by" + over + ";please reduce the size.";
}
//Reuse the same regex from the login class
public String checkRecipientCell(){
    if (recipient.matches("^\\+27 [0-9]{9}$")){
  return "Cell phone number successfully captured.";
  
}
    return "Cell phone number is inccorrectly formatted or does not contain an international code."
            + "please correct the number and try again";
}

//First 2 digits of ID: message number: FIRSTLAST word, all caps
public String createMessageHash() {
    String[] words = messageText.trim().split("\\s+");

    String firstWord = words[0].replaceAll("[^a-zA-Z0-9]", "");
    String lastWord = words[words.length - 1].replaceAll("[^a-zA-Z0-9]", "");

    return (messageID.substring(0, 2) + ":"
            + messageNumber + ":"
            + firstWord + lastWord).toUpperCase();
}
// Choice comes from Main: 1 = send, 2 = disregard, 3 = store
    public String sentMessage(int choice) {
        switch (choice) {
            case 1:
                totalMessagesSent++;
                sentMessages.add(printDetails());
                return "Message successfully sent.";
            case 2:
                return "Press 0 to delete the message.";
            case 3:
                storeMessage();
                return "Message successfully stored.";
            default:
                return "Invalid option.";
        }
    }

    // Order required by the brief: ID, Hash, Recipient, Message
    public String printDetails() {
        return "Message ID: " + messageID
             + "\nMessage Hash: " + messageHash
             + "\nRecipient: " + recipient
             + "\nMessage: " + messageText;
    }

    // All messages sent while the program is running
    public static String printMessages() {
        return String.join("\n\n", sentMessages);
    }

    // Total number of messages sent
    public static int returnTotalMessages() {
        return totalMessagesSent;
    }

    /*
     * Stores this message in storedMessages.json
     * Reference: [1] Google, "Gson User Guide," GitHub. [Online].
     * Available: https://github.com/google/gson/blob/main/UserGuide.md
     * [Accessed: Oct. 19, 2026].
     */
    public void storeMessage() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        File file = new File("storedMessages.json");
        List<Message> messages = new ArrayList<>();

        // Load existing messages so they are not overwritten
        if (file.exists()) {
            try (Reader reader = new FileReader(file)) {
                Type listType = new TypeToken<List<Message>>(){}.getType();
                List<Message> existing = gson.fromJson(reader, listType);
                if (existing != null) {
                    messages.addAll(existing);
                }
            } catch (IOException e) {
                System.out.println("Error reading file: " + e.getMessage());
            }
        }

        messages.add(this);
try (Writer writer = new FileWriter(file)) {
    gson.toJson(messages, writer);
} catch (IOException e) {
    System.out.println("Error saving message: " + e.getMessage());
}
        
}
   // Getters
    public String getMessageID() { return messageID; }
    public String getMessageHash() { return messageHash; }
    public String getRecipient() { return recipient; }
    public String getMessage() { return messageText; } 
}

