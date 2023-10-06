package com.example;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * Represents a tool for requesting data from the MBTA API.
 * The methods of this class are designed with chaining in mind.
 * It directly includes solutions to problems 1 and 2.
 * The result of problem 1 is required input for problem 2.
 * The result of problem 2 is required input for problem 3.
 * The solution for problem 3 is found on Tree.
 */
public class MBTATool {

    public MBTATool() {
    }

    /**
     * Read the data from the response into a more manageable format.
     * Returns a JSONArray representing the data from the response.
     */
    public static JSONArray getData(Connection connection) {
        StringBuffer response = connection.readResponse();

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
     * Note: This method was updated for problem 2.
     * It now also retreives and returns the IDs of each subway route.
     * The IDs are needed for problem 2.
     * Note: An improvement would be returning a dictionary of ID to name
     * so that both values are returned and can be accessed separately.
     */
    public static ArrayList<String> getSubwayRoutes(String url) {

        Connection connection = new Connection(url);

        // if the connection is successful, read the response from the connection
        JSONArray data = MBTATool.getData(connection);

        // get the long names and the IDs (for problem 2)
        ArrayList<String> longNames = new ArrayList<>();
        ArrayList<String> ids = new ArrayList<>();
        for (int i = 0; i < data.size(); i++) {
            JSONObject route = (JSONObject) data.get(i);
            String id = route.get("id").toString();
            JSONObject attributes = (JSONObject) route.get("attributes");
            String longName = attributes.get("long_name").toString();
            longNames.add(longName);
            ids.add(id);
        }
        System.out.println("Problem 1 Solution:");
        System.out.println(longNames.toString().replace("[", "").replace("]", ""));
        connection.disconnect();
        return ids;
    }

    /*
     * This method is the solution to problem 2.
     * According to the API we are unable to filter the stops by type of route,
     * so we must get the stops for each individual route at a time.
     * Using a base url and a list of route IDs,
     * it makes a unique connection for each route,
     * reads each response, which represents a list of stops,
     * and retrieves the names of each stop in the response.
     * This information is kept in a Map from route to a list of its stops.
     * Then, it also adds the stop as an entry in a map from stop to a list
     * of routes the stop is on. If it already exists in the map,
     * the current route is added to the list stored as the value.
     * These two maps will make up the Tree used for problem 3
     * As the method iterates through all of the given route IDs, it
     * keeps track of the route with the most and least stops so far
     * to return once all routes' stops have been retrieved.
     * This way the stops don't need to be iterated through again to be counted.
     * Note: This return type of this method was created with problem 3 in mind.
     * While there are other simpler ways to represent the data, these maps
     * will act as a tree that will allow us to search the routes.
     */
    public static Tree getSubwayStops(String url, ArrayList<String> routes) {

        // from name of route to list of stops on it
        HashMap<String, ArrayList<String>> routesToStops = new HashMap<String, ArrayList<String>>();

        // from name of stop to list of routes it is on
        HashMap<String, ArrayList<String>> stopsToRoutes = new HashMap<String, ArrayList<String>>();

        // tracking the current route with the most stops and the least stops
        // represents name of the route and number of stops on it
        SimpleEntry<String, Integer> most = new SimpleEntry<String, Integer>("", 0);
        SimpleEntry<String, Integer> least = new SimpleEntry<String, Integer>("", 99);

        // for each route, get its stops, count them, and update the current most and
        // least values
        for (int routeInt = 0; routeInt < routes.size(); routeInt++) {
            String route = routes.get(routeInt);

            // create connection, surround URL with a try-catch in case the URL is malformed
            Connection connection = new Connection(url + route);

            // if the connection is successful, read the response and get its data (a list
            // of stops)
            ArrayList<String> stops = new ArrayList<>();
            JSONArray data = MBTATool.getData(connection);

            connection.disconnect();

            // for each stop, get its name and add its route to the stops_to_routes map
            for (int stopInt = 0; stopInt < data.size(); stopInt++) {
                JSONObject stopObject = (JSONObject) data.get(stopInt);
                JSONObject attributes = (JSONObject) stopObject.get("attributes");
                String name = attributes.get("name").toString();
                stops.add(name);

                // update the stops_to_routes map to include this stop on this route
                ArrayList<String> newValue = new ArrayList<>();
                if (stopsToRoutes.containsKey(name)) {
                    newValue = stopsToRoutes.get(name);
                    newValue.add(route);
                    stopsToRoutes.put(name, newValue);
                } else {
                    newValue.add(route);
                    stopsToRoutes.put(name, newValue);
                }
            }

            // keep the most and least values up to date
            if (stops.size() > most.getValue()) {
                most = new SimpleEntry<String, Integer>(routes.get(routeInt), stops.size());
            } else if (stops.size() < least.getValue()) {
                least = new SimpleEntry<String, Integer>(routes.get(routeInt), stops.size());
            }

            // add this route and its stops to the routes_to_stops map
            routesToStops.put(routes.get(routeInt), stops);
        }

        System.out.println("Problem 2 Solution");
        System.out.print("The route with the most number of stops is ");
        System.out.print(most.getKey() + " with ");
        System.out.println(most.getValue() + " stops");

        System.out.print("The route with the least number of stops is ");
        System.out.print(least.getKey() + " with ");
        System.out.println(least.getValue() + " stops");

        System.out.println("The stops that connect multiple routes are: ");
        for (final Map.Entry<String, ArrayList<String>> entry : stopsToRoutes.entrySet()) {
            if (entry.getValue().size() > 1) {
                System.out.println(entry.getKey());
                System.out.print("  connects to the following routes: ");
                System.out.println(entry.getValue().toString().replace("[", "").replace("]", ""));
            }
        }

        return new Tree(routesToStops, stopsToRoutes);
    }
}
