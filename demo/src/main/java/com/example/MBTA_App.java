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
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.AbstractMap.SimpleEntry;

public class MBTA_App {

    private static HttpURLConnection connection;
    private static int response_code;

    private static URL routesURL;
    private static String stopsPartialURL;


    // main method runs calling the other two methods which print results as well as
    // return them
    // for testing purposes
    public static void main(String[] args) {
        ArrayList<String> ids = get_subway_routes("https://api-v3.mbta.com/routes/?filter[type]=0,1");

        // unable to filter the stops by type of route they are on,
        // so we must get the stops for each individual route at a time
        get_subway_stops(
                "https://api-v3.mbta.com/stops?include=route&filter[route]=", ids);
    }

    // establishes a connection with the server at the given URL
    private static void open_connection(String url) {
        URL subway_URL;
        try {
            subway_URL = new URL(url);
            try {
                // open up the connection
                connection = (HttpURLConnection) subway_URL.openConnection();

                // setup how to request on the connection
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(10000);
                connection.setReadTimeout(10000);

                // response_code determines if the connection was successful
                response_code = connection.getResponseCode();
                System.out.println(response_code);
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

    /*
     */
    private static StringBuffer read_response() {
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

    private static JSONArray get_data(StringBuffer response) {

        // convert the response to a json object
        JSONParser parser = new JSONParser();
        JSONObject json = null;
        try {
            json = (JSONObject) parser.parse(response.toString());
        } catch (ParseException e) {
            System.out.println("API response could not be parsed");
        }
        System.out.println(json);
        if (response_code > 299) {
            System.out.println("Unsuccessful Connection. Response Code:" + response_code);
        }
        return (JSONArray) json.get("data");
    }

    public static ArrayList<String> get_subway_routes(String url) {

        // create the reader to read the response from the client
        // and the string buffer to append the response onto once read

        // create connection, surround URL with a try-catch in case the URL is malformed
        open_connection(url);

        // if the connection is successful, read the response from the connection

        StringBuffer response = read_response();
        JSONArray data = get_data(response);

        // problem 1 solution
        // get the long names and the IDs (for problem 2)
        ArrayList<String> long_names = new ArrayList<>();
        ArrayList<String> ids = new ArrayList<>();
        for (int i = 0; i < data.size(); i++) {
            JSONObject route = (JSONObject) data.get(i);
            String id = route.get("id").toString();
            JSONObject attributes = (JSONObject) route.get("attributes");
            String long_name = attributes.get("long_name").toString();
            long_names.add(long_name);
            ids.add(id);

        }
        System.out.println("Problem 1 Solution:");
        System.out.println(long_names);
        connection.disconnect();
        return ids;

    }

    // problem 2
    // get the number of stops for each route
    public static HashMap<String, ArrayList<String>> get_subway_stops(String url, ArrayList<String> routes) {
        // from name of route to list of stops on it
        HashMap<String, ArrayList<String>> all_stops = new HashMap<String, ArrayList<String>>();
        // name of route and number of stops on it
        SimpleEntry<String, Integer> most = new SimpleEntry<String, Integer>("", 0);
        SimpleEntry<String, Integer> least = new SimpleEntry<String, Integer>("", 99);
        // already seen stops (to keep track of connecting stops)
        HashSet<String> seenStops = new HashSet<String>();

        // get the stops for each route
        for (int route = 0; route < routes.size(); route++) {
            // create connection, surround URL with a try-catch in case the URL is malformed
            open_connection(url + routes.get(route));

            // if the connection is successful, read the response from the connection
            ArrayList<String> stops = new ArrayList<>();
            StringBuffer response = read_response();
            JSONArray data = get_data(response);
            System.out.println(data);
            for (int stopInt = 0; stopInt < data.size(); stopInt++) {
                JSONObject stopObject = (JSONObject) data.get(stopInt);
                JSONObject attributes = (JSONObject) stopObject.get("attributes");
                String name = attributes.get("name").toString();
                stops.add(name);
            }
            if (stops.size() > most.getValue()) {
                most = new SimpleEntry<String, Integer>(routes.get(route), stops.size());
            }
            else if (stops.size() < least.getValue()) {
                least = new SimpleEntry<String, Integer>(routes.get(route), stops.size());
            }

            connection.disconnect();
            all_stops.put(routes.get(route), stops);

        }
        System.out.println("The route with the most number of stops is: ");
        System.out.println(most.getKey() +" ");
        System.out.println(most.getValue());
        System.out.println("The route with the least number of stops is: ");
        System.out.println(least.getKey() +" ");
        System.out.print(least.getValue());
        System.out.print(all_stops);


        return all_stops;
    }

}
