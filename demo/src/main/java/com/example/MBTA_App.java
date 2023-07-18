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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.AbstractMap.SimpleEntry;

public class MBTA_App {

    private static HttpURLConnection connection;
    private static int response_code;

    // main method runs calling the other two methods which print results as well as
    // return them
    // for testing purposes
    public static void main(String[] args) {
        ArrayList<String> ids = get_subway_routes("https://api-v3.mbta.com/routes/?filter[type]=0,1");

        // unable to filter the stops by type of route they are on,
        // so we must get the stops for each individual route at a time
        Tree mbtaTree = get_subway_stops(
                "https://api-v3.mbta.com/stops?include=route&filter[route]=", ids);

        if (args.length > 2) {
            String start = args[1];
            String end = args[2];
            find_path(mbtaTree, start, end);
        }

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
        System.out.println(long_names.toString().replace("[", "").replace("]", ""));
        connection.disconnect();
        return ids;

    }

    // problem 2
    // get the number of stops for each route
    public static Tree get_subway_stops(String url, ArrayList<String> routes) {

        // from name of route to list of stops on it
        HashMap<String, ArrayList<String>> routes_to_stops = new HashMap<String, ArrayList<String>>();

        // from name of stop to list of routes it is on
        HashMap<String, ArrayList<String>> stops_to_routes = new HashMap<String, ArrayList<String>>();

        // tracking the current route with the most stops and the least stops
        // represents name of the route and number of stops on it
        SimpleEntry<String, Integer> most = new SimpleEntry<String, Integer>("", 0);
        SimpleEntry<String, Integer> least = new SimpleEntry<String, Integer>("", 99);

        // for each route, get its stops, count them, and update the current most and least values
        for (int routeInt = 0; routeInt < routes.size(); routeInt++) {
            String route = routes.get(routeInt);

            // create connection, surround URL with a try-catch in case the URL is malformed
            open_connection(url + route);

            // if the connection is successful, read the response and get its data (a list of stops)
            ArrayList<String> stops = new ArrayList<>();
            StringBuffer response = read_response();
            JSONArray data = get_data(response);

            connection.disconnect();

            // for each stop, get its name and add its route to the stops_to_routes map
            for (int stopInt = 0; stopInt < data.size(); stopInt++) {
                JSONObject stopObject = (JSONObject) data.get(stopInt);
                JSONObject attributes = (JSONObject) stopObject.get("attributes");
                String name = attributes.get("name").toString();
                stops.add(name);

                // update the stops_to_routes map to include this stop on this route
                ArrayList<String> newValue = new ArrayList<>();
                if (stops_to_routes.containsKey(name)) {
                    newValue = stops_to_routes.get(name);
                    newValue.add(route);
                    stops_to_routes.put(name, newValue);
                } else {
                    newValue.add(route);
                    stops_to_routes.put(name, newValue);
                }
            }

            // keep the most and least values up to date
            if (stops.size() > most.getValue()) {
                most = new SimpleEntry<String, Integer>(routes.get(routeInt), stops.size());
            } else if (stops.size() < least.getValue()) {
                least = new SimpleEntry<String, Integer>(routes.get(routeInt), stops.size());
            }

            // add this route and its stops to the  routes_to_stops map
            routes_to_stops.put(routes.get(routeInt), stops);
        }

        System.out.println("Problem 2 Solution");
        System.out.print("The route with the most number of stops is ");
        System.out.print(most.getKey() + " with ");
        System.out.println(most.getValue() + " stops");

        System.out.print("The route with the least number of stops is ");
        System.out.print(least.getKey() + " with ");
        System.out.println(least.getValue() + " stops");

        System.out.println("The stops that connect multiple routes are: ");
        for (final Map.Entry<String, ArrayList<String>> entry : stops_to_routes.entrySet()) {
            if (entry.getValue().size() > 1) {
                System.out.println(entry.getKey());
                System.out.print("  connects to the following routes: ");
                System.out.println(entry.getValue().toString().replace("[", "").replace("]", ""));
            }
        }

        return new Tree(routes_to_stops, stops_to_routes);
    }

    public static String find_path(Tree tree, String start, String end) {
        // all possible starting branches
        ArrayList<String> startingBranches = tree.getStops_to_routes().get(start);
        // all possible ending branches
        ArrayList<String> endingBranches = tree.getRoutes_to_stops().get(end);

        //
    }

}
