package com.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * Represents a connection to a given url, 
 * from which data can be read and the response code can be checked.
 */
public class Connection {

    private HttpURLConnection connection;
    private int responseCode;

    /*
     * Get the response code of this connection.
     */
    public int getResponseCode() {
        return responseCode;
    }

    /*
     * Constructor for a Connection
     */
    public Connection(String url) {
        openConnection(url);
    }

    /**
     * Establishes a connection with the server at the given URL.
     * Sets the connection and the response code on this.
     */
    private void openConnection(String stringURL) {
        URL url;
        try {
            url = new URL(stringURL);
            try {
                // open up the connection
                connection = (HttpURLConnection) url.openConnection();

                // setup how to request on the connection
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(10000);
                connection.setReadTimeout(10000);

                // response_code determines if the connection was successful
                responseCode = connection.getResponseCode();

                if (responseCode > 299) {
                    System.out.println("Unsuccessful Connection. Response Code:"
                    + responseCode);
                }

            } catch (IOException e) {
                System.out.println("connection not opened successfully");
            }

        } catch (MalformedURLException e) {
            System.out.println("subway_URL was malformed");
        }
    }

    /**
     * Disconnect from the server.
     */
    public void disconnect() {
        connection.disconnect();
    }

    /**
     * Using the connection, get the input stream and read all the input into a
     * string buffer
     * Returns the string buffer containing the response.
     */
    public StringBuffer readResponse() {
        BufferedReader reader;
        StringBuffer response = new StringBuffer();
        String line;
        try {
            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();
        } catch (IOException ex) {
            System.out.println("There was an issue reading the server response " +
                    "that caused an IOException to be thrown.");
        }
        return response;
    }
}
