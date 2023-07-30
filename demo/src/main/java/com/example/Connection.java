package com.example;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class Connection {

    private HttpURLConnection connection;
    private int response_code;

    /*
     * Constructor for a Connection
     */
    public Connection(String url) {
        open_connection(url);
    }

    /*
     * Establishes a connection with the server at the given URL.
     * Sets the connection and the response code on this.
     */
    private void open_connection(String stringURL) {
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
                response_code = connection.getResponseCode();

                if (response_code > 299) {
                    System.out.println("Unsuccessful Connection. Response Code:" + response_code);
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

    /*
     * Using the connection, get the input stream and read all the input into a
     * string buffer
     * Returns the string buffer containing the response.
     */
    private StringBuffer read_response() {
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

    /*
     * Read the data from the response into a more manageable format.
     * Returns a JSONArray representing the data from the response
     */
    public JSONArray get_data() {
        StringBuffer response = read_response();

        // convert the response to a json object
        JSONParser parser = new JSONParser();
        JSONObject json = null;
        try {
            json = (JSONObject) parser.parse(response.toString());
        } catch (ParseException e) {
            System.out.println("API response could not be parsed");
        }
        if (response_code > 299) {
            System.out.println("Unsuccessful Connection. Response Code:" + response_code);
        }
        return (JSONArray) json.get("data");
    }
}