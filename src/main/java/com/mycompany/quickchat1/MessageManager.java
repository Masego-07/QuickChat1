package com.mycompany.quickchat1;

import com.google.gson.Gson;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Arrays;

/**
 * PART 3: MessageManager keeps every message in ARRAYS and reports on them.
 *
 * JSON CODE ATTRIBUTION
 * Reference: [1] Google, "Gson User Guide," GitHub. [Online].
 * Available: https://github.com/google/gson/blob/main/UserGuide.md
 * [Accessed: Nov. 18, 2026].
 * (gson.fromJson(reader, Message[].class) turns the JSON file straight into an array of messages)
 *
 * @author masego
 */
public class MessageManager {

    public static final int MAX_MESSAGES = 100;

    public static final String SENT = "Sent";
    public static final String STORED = "Stored";
    public static final String DISREGARDED = "Disregarded";

    //  Parallel arrays: the SAME index belongs to the SAME message ----
    // e.g. messageIDs[2], messageHashes[2], recipients[2], messages[2] and flags[2]
    // all describe message number 3.
    private final String[] messageIDs = new String[MAX_MESSAGES];
    private final String[] messageHashes = new String[MAX_MESSAGES];
    private final String[] recipients = new String[MAX_MESSAGES];
    private final String[] messages = new String[MAX_MESSAGES];
    private final String[] flags = new String[MAX_MESSAGES];
    private int messageCount = 0;

    // One array per flag, as the brief asks
    private final String[] sentMessages = new String[MAX_MESSAGES];
    private int sentCount = 0;
    private final String[] disregardedMessages = new String[MAX_MESSAGES];
    private int disregardedCount = 0;
    private final String[] storedMessages = new String[MAX_MESSAGES];
    private int storedCount = 0;

    // The logged-in user who sends the messages (name and cell number)
    private String sender = "";

    public void setSender(String sender) {
        this.sender = sender;
    }

    // Puts a message into the arrays. The counter is always the next free index.
    // flag must be SENT, STORED or DISREGARDED.
    public void addMessage(Message message, String flag) {
        if (messageCount >= MAX_MESSAGES) {
            System.out.println("Message limit reached; the message was not recorded.");
            return;
        }
        messageIDs[messageCount] = message.getMessageID();
        messageHashes[messageCount] = message.getMessageHash();
        recipients[messageCount] = message.getRecipient();
        messages[messageCount] = message.getMessage();
        flags[messageCount] = flag;
        messageCount++;

        if (flag.equals(SENT)) {
            sentMessages[sentCount] = message.getMessage();
            sentCount++;
        } else if (flag.equals(STORED)) {
            storedMessages[storedCount] = message.getMessage();
            storedCount++;
        } else if (flag.equals(DISREGARDED)) {
            disregardedMessages[disregardedCount] = message.getMessage();
            disregardedCount++;
        }
    }

    // Reads the stored messages from the JSON file INTO the arrays
    public String loadStoredMessages(String fileName) {
        File file = new File(fileName);
        if (!file.exists()) {
            return "No stored messages on file yet.";
        }
        Gson gson = new Gson();
        try (Reader reader = new FileReader(file)) {
            Message[] fromFile = gson.fromJson(reader, Message[].class);   // JSON text -> array
            if (fromFile == null) {
                return "No stored messages on file yet.";
            }
            for (int i = 0; i < fromFile.length; i++) {
                addMessage(fromFile[i], STORED);
            }
            return fromFile.length + " stored message(s) loaded from " + fileName + ".";
        } catch (IOException e) {
            return "Could not read " + fileName + ": " + e.getMessage();
        }
    }

    // a) Sender and recipient of every stored message
    public String displayStoredSendersAndRecipients() {
        String result = "";
        for (int i = 0; i < messageCount; i++) {
            if (flags[i].equals(STORED)) {
                result = result + "Sender: " + sender + " | Recipient: " + recipients[i] + "\n";
            }
        }
        if (result.equals("")) {
            return "There are no stored messages.";
        }
        return result;
    }

    // b) The longest stored message
    public String getLongestStoredMessage() {
        if (storedCount == 0) {
            return "There are no stored messages.";
        }
        String longest = storedMessages[0];
        for (int i = 1; i < storedCount; i++) {
            if (storedMessages[i].length() > longest.length()) {
                longest = storedMessages[i];
            }
        }
        return longest;
    }

    // c) Search for a message ID and show its recipient and message
    public String searchByMessageID(String id) {
        for (int i = 0; i < messageCount; i++) {
            if (messageIDs[i].equals(id)) {
                return "Recipient: " + recipients[i] + "\nMessage: " + messages[i];
            }
        }
        return "Message ID " + id + " not found.";
    }

    // d) Every sent or stored message for one recipient (one message per line)
    public String searchByRecipient(String recipient) {
        String result = "";
        for (int i = 0; i < messageCount; i++) {
            boolean sentOrStored = flags[i].equals(SENT) || flags[i].equals(STORED);
            if (recipients[i].equals(recipient) && sentOrStored) {
                result = result + "\"" + messages[i] + "\"\n";
            }
        }
        if (result.equals("")) {
            return "No messages found for " + recipient + ".";
        }
        return result;
    }

    // e) Delete a message using its hash
    public String deleteByHash(String hash) {
        // Step 1: find the index of the message
        int deleteIndex = -1;
        for (int i = 0; i < messageCount; i++) {
            if (messageHashes[i].equals(hash)) {
                deleteIndex = i;
                break;
            }
        }
        if (deleteIndex == -1) {
            return "Message hash " + hash + " not found.";
        }

        String deletedText = messages[deleteIndex];
        String deletedFlag = flags[deleteIndex];

        // Step 2: remove it from the flag array it belongs to
        if (deletedFlag.equals(SENT)) {
            sentCount = removeText(sentMessages, sentCount, deletedText);
        } else if (deletedFlag.equals(STORED)) {
            storedCount = removeText(storedMessages, storedCount, deletedText);
        } else {
            disregardedCount = removeText(disregardedMessages, disregardedCount, deletedText);
        }

        // Step 3: shift everything after it one place left, in EVERY parallel array
        for (int i = deleteIndex; i < messageCount - 1; i++) {
            messageIDs[i] = messageIDs[i + 1];
            messageHashes[i] = messageHashes[i + 1];
            recipients[i] = recipients[i + 1];
            messages[i] = messages[i + 1];
            flags[i] = flags[i + 1];
        }

        // Step 4: clear the last slot and lower the counter
        messageIDs[messageCount - 1] = null;
        messageHashes[messageCount - 1] = null;
        recipients[messageCount - 1] = null;
        messages[messageCount - 1] = null;
        flags[messageCount - 1] = null;
        messageCount--;

        return "Message: \"" + deletedText + "\" successfully deleted.";
    }

    // Removes the first copy of text from an array by shifting the rest left.
    // Returns the new count.
    private int removeText(String[] array, int count, String text) {
        for (int i = 0; i < count; i++) {
            if (array[i].equals(text)) {
                for (int j = i; j < count - 1; j++) {
                    array[j] = array[j + 1];
                }
                array[count - 1] = null;
                return count - 1;
            }
        }
        return count;
    }

    // f) Full report of every sent and stored message
    public String displayReport() {
        String result = "";
        int listed = 0;
        for (int i = 0; i < messageCount; i++) {
            if (flags[i].equals(SENT) || flags[i].equals(STORED)) {
                result = result + "----------\n"
                       + "Message ID: " + messageIDs[i] + "\n"
                       + "Message Hash: " + messageHashes[i] + "\n"
                       + "Recipient: " + recipients[i] + "\n"
                       + "Message: " + messages[i] + "\n"
                       + "Status: " + flags[i] + "\n";
                listed++;
            }
        }
        if (listed == 0) {
            return "There are no sent or stored messages to report on.";
        }
        return "MESSAGE REPORT\n" + result + "----------\nTotal messages in report: " + listed;
    }

    // Getters: return copies with only the filled slots.

    public String[] getSentMessages() {
        return Arrays.copyOf(sentMessages, sentCount);
    }

    public String[] getDisregardedMessages() {
        return Arrays.copyOf(disregardedMessages, disregardedCount);
    }

    public String[] getStoredMessages() {
        return Arrays.copyOf(storedMessages, storedCount);
    }

    public String[] getMessageHashes() {
        return Arrays.copyOf(messageHashes, messageCount);
    }

    public String[] getMessageIDs() {
        return Arrays.copyOf(messageIDs, messageCount);
    }

    public int getMessageCount() {
        return messageCount;
    }
}
