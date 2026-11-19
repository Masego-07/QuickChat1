package com.mycompany.quickchat1;

import java.io.File;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

/**
 * PART 3 tests, using the test data from the POE brief.
 *
 * Test data (the IDs are set by hand so the IDs and hashes are predictable):
 *   1: +27834557896  "Did you get the cake?"                                        Sent
 *   2: +27838884567  "Where are you? You are late! I have asked you to be on time."  Stored
 *   3: +27834484567  "Yohoooo, I am at your gate."                                  Disregard
 *   4: 0838884567    "It is dinner time!"                                           Sent
 *   5: +27838884567  "Ok, I am leaving without you."                                Stored
 *
 * @author masego
 */
public class MessageManagerTest {

    private static final String TEST_FILE = "test_managerTest.json";

    private MessageManager manager;

    @BeforeEach
    public void loadTestData() {
        Message.setStorageFile(TEST_FILE);
        new File(TEST_FILE).delete();

        manager = new MessageManager();
        manager.setSender("Kyle Peters (+27838968976)");
        addMessage("1000000001", 1, "+27834557896", "Did you get the cake?", MessageManager.SENT);
        addMessage("2000000002", 2, "+27838884567",
                "Where are you? You are late! I have asked you to be on time.", MessageManager.STORED);
        addMessage("3000000003", 3, "+27834484567", "Yohoooo, I am at your gate.", MessageManager.DISREGARDED);
        // Message 4 is the "developer" entry from the brief; its ID is the number given as test data
        addMessage("0838884567", 4, "0838884567", "It is dinner time!", MessageManager.SENT);
        addMessage("5000000005", 5, "+27838884567", "Ok, I am leaving without you.", MessageManager.STORED);
    }

    @AfterEach
    public void tearDown() {
        new File(TEST_FILE).delete();
        Message.setStorageFile("storedMessages.json");
    }

    private void addMessage(String id, int number, String recipient, String text, String flag) {
        Message message = new Message(number, recipient, text);
        message.setMessageID(id);
        manager.addMessage(message, flag);
    }

    // ---- Arrays correctly populated ----

    @Test
    public void testSentMessagesArrayCorrectlyPopulated() {
        assertArrayEquals(new String[]{"Did you get the cake?", "It is dinner time!"},
                manager.getSentMessages());
    }

    @Test
    public void testStoredAndDisregardedArraysPopulated() {
        assertArrayEquals(new String[]{
            "Where are you? You are late! I have asked you to be on time.",
            "Ok, I am leaving without you."}, manager.getStoredMessages());
        assertArrayEquals(new String[]{"Yohoooo, I am at your gate."}, manager.getDisregardedMessages());
    }

    @Test
    public void testHashAndIDArraysPopulated() {
        assertEquals(5, manager.getMessageCount());
        assertEquals("0838884567", manager.getMessageIDs()[3]);
        assertEquals("20:2:WHERETIME", manager.getMessageHashes()[1]);
    }

    // ---- Display the longest message ----

    @Test
    public void testDisplayLongestMessage() {
        assertEquals("Where are you? You are late! I have asked you to be on time.",
                manager.getLongestStoredMessage());
    }

    // ---- Search for message ID ----

    @Test
    public void testSearchForMessageID() {
        assertEquals("Recipient: 0838884567\nMessage: It is dinner time!",
                manager.searchByMessageID("0838884567"));
    }

    @Test
    public void testSearchForMessageIDNotFound() {
        assertEquals("Message ID 9999999999 not found.", manager.searchByMessageID("9999999999"));
    }

    // ---- Search all messages for a recipient ----

    @Test
    public void testSearchByRecipient() {
        assertEquals("\"Where are you? You are late! I have asked you to be on time.\"\n"
                   + "\"Ok, I am leaving without you.\"\n",
                manager.searchByRecipient("+27838884567"));
    }

    // ---- Delete a message using a message hash ----

    @Test
    public void testDeleteMessageUsingHash() {
        assertEquals("Message: \"Where are you? You are late! I have asked you to be on time.\" successfully deleted.",
                manager.deleteByHash("20:2:WHERETIME"));
        // Removed from every array, and the rest moved up
        assertEquals(4, manager.getMessageCount());
        assertArrayEquals(new String[]{"Ok, I am leaving without you."}, manager.getStoredMessages());
        assertEquals("Message ID 2000000002 not found.", manager.searchByMessageID("2000000002"));
        assertEquals("Recipient: 0838884567\nMessage: It is dinner time!",
                manager.searchByMessageID("0838884567"));
    }

    @Test
    public void testDeleteUnknownHash() {
        assertEquals("Message hash 99:9:NOPE not found.", manager.deleteByHash("99:9:NOPE"));
        assertEquals(5, manager.getMessageCount());
    }

    // ---- Display report ----

    @Test
    public void testDisplayReport() {
        String report = manager.displayReport();
        assertTrue(report.contains("Message Hash: 10:1:DIDCAKE"));
        assertTrue(report.contains("Recipient: +27834557896"));
        assertTrue(report.contains("Message: Did you get the cake?"));
        assertTrue(report.contains("Message: It is dinner time!"));
        assertTrue(report.contains("Message: Ok, I am leaving without you."));
        assertFalse(report.contains("Yohoooo"));   // disregarded messages are not in the report
        assertTrue(report.endsWith("Total messages in report: 4"));
    }

    // ---- Sender and recipient of stored messages ----

    @Test
    public void testDisplayStoredSendersAndRecipients() {
        assertEquals("Sender: Kyle Peters (+27838968976) | Recipient: +27838884567\n"
                   + "Sender: Kyle Peters (+27838968976) | Recipient: +27838884567\n",
                manager.displayStoredSendersAndRecipients());
    }

    // ---- Read the JSON file into the stored array ----

    @Test
    public void testStoredMessagesLoadFromJson() {
        Message stored1 = new Message(2, "+27838884567",
                "Where are you? You are late! I have asked you to be on time.");
        Message stored2 = new Message(5, "+27838884567", "Ok, I am leaving without you.");
        stored1.storeMessage();
        stored2.storeMessage();

        MessageManager fresh = new MessageManager();
        assertEquals("2 stored message(s) loaded from " + TEST_FILE + ".", fresh.loadStoredMessages(TEST_FILE));
        assertArrayEquals(new String[]{
            "Where are you? You are late! I have asked you to be on time.",
            "Ok, I am leaving without you."}, fresh.getStoredMessages());
    }

    @Test
    public void testLoadWhenNoFileExists() {
        MessageManager fresh = new MessageManager();
        assertEquals("No stored messages on file yet.", fresh.loadStoredMessages("does_not_exist.json"));
    }
}
