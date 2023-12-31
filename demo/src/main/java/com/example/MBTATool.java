package com.example;

import java.util.ArrayList;
import java.util.HashMap;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * Represents a utility for requesting and representing data from the MBTA API.
 * It directly includes the solution to problem 1.
 * It includes a method to generate a tree representing the route data
 * which is used for the solution to problems 2 and 3.
 */
public class MBTATool {

    /**
     * Read the data from the response into a more manageable format.
     * Returns a JSONArray representing the data from the response.
     */
    private static JSONArray getData(Connection connection) {
        StringBuilder response = connection.readResponse();

        // convert the response to a json object
        JSONParser parser = new JSONParser();
        JSONObject json = null;
        try {
            json = (JSONObject) parser.parse(response.toString());
        } catch (ParseException e) {
            System.out.println("API response could not be parsed");
        }
        if (connection.getResponseCode() > 299) {
            System.out.println("Unsuccessful Connection. Response Code:"
                    + connection.getResponseCode());
        }
        return (JSONArray) json.get("data");
    }

    /*
     * This method is the solution to problem 1.
     * It makes a connection, reads the response, and parses the data
     * to retrieve the names of each route in the response.
     * It prints the names.
     * 
     * Note: This method was updated for problem 2.
     * It now also retreives and returns the IDs of each subway route.
     * The IDs are needed for problem 2.
     */
    public static HashMap<String, String> getSubwayRoutes(String url) {

        Connection connection = new Connection(url);

        // if the connection is successful, read the response from the connection
        JSONArray data = MBTATool.getData(connection);

        // get the long names and the IDs (IDs are for problem 2)
        HashMap<String, String> result = new HashMap<>();
        for (int i = 0; i < data.size(); i++) {
            JSONObject route = (JSONObject) data.get(i);
            String id = route.get("id").toString();
            JSONObject attributes = (JSONObject) route.get("attributes");
            String longName = attributes.get("long_name").toString();
            result.put(id, longName);
        }

        connection.disconnect();

        return result;
    }

    /*
     * This method generates a tree which is used to solve problems 2 and 3.
     * Due to the API design, stops are retreived for each individual route at a
     * time. Using a base url and a list of route IDs, it makes a unique 
     * connection for each route, reads each response, 
     * and retrieves the names of each stop in the response.
     * This information is kept in a Map from route to a list of its stops
     * and a Map from stop to list of routes.
     * 
     * Note: This return type of this method was created with problem 3 in mind.
     * While there are other simpler ways to represent the data, these maps
     * will act as a tree that will allow us to search the routes
     * and can support added functionality later.
     */
    public static Tree generateMBTATree(String url, HashMap<String, String> routes) {

        // from name of route to list of stops on it
        HashMap<String, ArrayList<String>> routesToStops = new HashMap<String, ArrayList<String>>();

        // from name of stop to list of routes it is on
        HashMap<String, ArrayList<String>> stopsToRoutes = new HashMap<String, ArrayList<String>>();

        // for each route, get its stops
        for (String routeID : routes.keySet()) {
            String routeName = routes.get(routeID);

            // create connection, surround URL with a try-catch in case the URL is malformed
            Connection connection = new Connection(url + routeID);

            // read the response and get its data (a list of stops)
            ArrayList<String> stops = new ArrayList<>();
            JSONArray data = MBTATool.getData(connection);

            connection.disconnect();

            // for each stop, get its name and add its route to the stopsToRoutes map
            for (int stopInt = 0; stopInt < data.size(); stopInt++) {
                JSONObject stopObject = (JSONObject) data.get(stopInt);
                JSONObject attributes = (JSONObject) stopObject.get("attributes");
                String name = attributes.get("name").toString();
                stops.add(name);

                // update the stopsToRoutes map to include this stop on this route
                ArrayList<String> newValue = new ArrayList<>();
                if (stopsToRoutes.containsKey(name)) {
                    newValue = stopsToRoutes.get(name);
                    newValue.add(routeName);
                    stopsToRoutes.put(name, newValue);
                } else {
                    newValue.add(routeName);
                    stopsToRoutes.put(name, newValue);
                }
            }
            // add this route and its stops to the routesToStops map
            routesToStops.put(routeName, stops);
        }
        return new Tree(routesToStops, stopsToRoutes);
    }
}
