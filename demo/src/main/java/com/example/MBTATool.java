package com.example;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MBTATool {


	public MBTATool(){}


    /*
     * This method is the solution to problem 1.
     * It makes a connection, reads the response, and parses the data
     * to retrieve the names of each route in the response.
     * It prints the names.
     * 
     * Note: This method was updated for problem 2.
     * It now also retreives and returns the IDs of each subway route.
     * The IDs are needed for problem 2.
     * 
     * Note: An improvement would be returning a dictionary of ID to name
     * so that both values are returned and can be accessed separately.
     */
    public static ArrayList<String> get_subway_routes(String url) {

		Connection connection = new Connection(url);

        // if the connection is successful, read the response from the connection
        JSONArray data = connection.get_data();

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

    /*
     * This method is the solution to problem 2.
     * According to the API we are unable to filter the stops by type of route,
     * so we must get the stops for each individual route at a time.
     * Using a base url and a list of route IDs,
     * it makes a unique connection for each route,
     * reads each response which represent a list of stops
     * and retrieves the names of each stop in the response.
     * This information is kept in a Map from route to a list of its stops.
     * Then, it also adds the stop as an entry in a map from stop to a list
     * of routes the stop is on. If it already exists in the map,
     * the current route is added to the list stored as the value.
     * These two maps will make up the Tree used for problem 3
     * 
     * As the method iterates through all of the given route IDs, it
     * keeps track of the route with the most and least stops so far
     * to return once all routes' stops have been retrieved.
     * This way the stops don't need to be iterated through again to be counted.
     * 
     * Note: This return type of this method was created with problem 3 in mind.
     * While there are other simpler ways to represent the data, these maps
     * will act as a tree that will allow us to search the routes.
     * 
     * 
     */
    public static Tree get_subway_stops(String url, ArrayList<String> routes) {

        // from name of route to list of stops on it
        HashMap<String, ArrayList<String>> routes_to_stops = new HashMap<String, ArrayList<String>>();

        // from name of stop to list of routes it is on
        HashMap<String, ArrayList<String>> stops_to_routes = new HashMap<String, ArrayList<String>>();

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
            JSONArray data = connection.get_data();

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

            // add this route and its stops to the routes_to_stops map
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



}
