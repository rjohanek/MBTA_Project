package com.example;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests
 */
public class ConnectionTest {

    String problem1URL = "https://api-v3.mbta.com/routes/?filter[type]=0,1";
    String incorrectURL = "https://api-v3.mbta.com/routes/?filtertype]=0,1";

    @Test
    void testConnection() {
        Connection connection = new Connection(problem1URL);
        assertEquals(200, connection.getResponseCode());
    }

    @Test
    void testConnection2() {
        Connection connection = new Connection(incorrectURL);
        assertEquals(400, connection.getResponseCode());
    }

    @Test
    void testReadResponse() {
        Connection connection = new Connection(problem1URL);
        StringBuilder response = connection.readResponse();
        assertFalse(response.length() == 0);
    }

    @Test
    void testReadResponse2() {
        Connection connection = new Connection(incorrectURL);
        StringBuilder response = connection.readResponse();
        assertTrue(response.length() == 0);
    }
}
