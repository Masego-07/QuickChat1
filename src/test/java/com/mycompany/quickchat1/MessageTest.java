/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.quickchat1;
import java.io.File;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 *
 * @author masego
 */
public class MessageTest {

    private static final String TEST_FILE = "test_messageTest.json";

    // Store to a separate test file so the real storedMessages.json stays clean
    @BeforeEach
    public void setUp() {
        Message.setStorageFile(TEST_FILE);
    }

    @AfterEach
    public void tearDown() {
        new File(TEST_FILE).delete();
        Message.setStorageFile("storedMessages.json");
    }

   //Message length

    @Test
    public void testMessageLengthSuccess(){
        Message msg = new Message(0,"+27718693002", "Hi Mike, can you join us for dinner tonight?");
       assertEquals("Message ready to send.", msg.checkMessageLength());

    }
    @Test
    public void testMessageLengthFailure(){
        String longText = "A".repeat(260);
        //10 characters too long
        Message msg= new Message(0, "+27718693002", longText);
        assertEquals("Message exceeds 250 characters by 10; please reduce the size.",
                msg.checkMessageLength());

    }
//Recipient number
    @Test
    public void testRecipientCellSuccess(){
        Message msg = new Message(0,"+27718693002","Hi Mike, can you join us for dinner tonight?");
        assertEquals("Cell phone number successfully captured.",
                msg.checkRecipientCell());

    }
    @Test
    public void testRecipientCellFailure(){
        Message msg = new Message(1,"08575975889", "Hi Keegan, did you receive the payment?");
        assertEquals("Cell phone number is incorrectly formatted or does not contain an international code. "
                + "Please correct the number and try again.",
                msg.checkRecipientCell());

    }
//Message Hash
    @Test
    public void testMessageHashTestCase1() {
        Message msg = new Message(0, "+27718693002", "Hi Mike, can you join us for dinner tonight?");
        msg.setMessageID("0012345678"); // known ID so the hash is predictable
        assertEquals("00:0:HITONIGHT", msg.getMessageHash());

    }
    @Test
    public void testMessageHashesInLoop() {
        String[] recipients = {"+27718693002", "08575975889"};
        String[] texts = {
            "Hi Mike, can you join us for dinner tonight?",
            "Hi Keegan, did you receive the payment?"
        };
        String[] expected = {"00:0:HITONIGHT", "00:1:HIPAYMENT"};

        for (int i = 0; i < texts.length; i++) {
            Message msg = new Message(i, recipients[i], texts[i]);
            msg.setMessageID("0012345678");
            assertEquals(expected[i], msg.getMessageHash());
        }
    }

    // Message ID
    @Test
    public void testMessageIDCreated() {
        Message msg = new Message(0, "+27718693002", "Hi Mike, can you join us for dinner tonight?");
        System.out.println("Message ID generated: " + msg.getMessageID());
        assertTrue(msg.checkMessageID());
        assertEquals(10, msg.getMessageID().length());
    }

    // The ID is built with substring, so it must ALWAYS be exactly 10 digits (tested 100 times)
    @Test
    public void testGeneratedIDsAreAlwaysTenDigits() {
        for (int i = 0; i < 100; i++) {
            String id = Message.generateMessageID();
            assertEquals(10, id.length());
            assertTrue(id.matches("\\d{10}"));
        }
    }

    //Send / Disregard / Store
    @Test
    public void testSentMessageSend() {
        Message msg = new Message(0, "+27718693002", "Hi Mike, can you join us for dinner tonight?");
        assertEquals("Message successfully sent.", msg.sentMessage(1));
    }

    @Test
    public void testSentMessageDisregard() {
        Message msg = new Message(1, "08575975889", "Hi Keegan, did you receive the payment?");
        assertEquals("Press 0 to delete the message.", msg.sentMessage(2));
    }

    @Test
    public void testSentMessageStore() {
        Message msg = new Message(2, "+27718693002", "Hi Mike, can you join us for dinner tonight?");
        assertEquals("Message successfully stored.", msg.sentMessage(3));
        assertTrue(new File(TEST_FILE).exists());
    }

    // Total messages
    @Test
    public void testReturnTotalMessages() {
        int before = Message.returnTotalMessages();
        Message msg = new Message(0, "+27718693002", "Hi Mike, can you join us for dinner tonight?");
        msg.sentMessage(1);
        assertEquals(before + 1, Message.returnTotalMessages());
    }

}
