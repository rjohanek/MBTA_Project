package com.example;

import java.util.ArrayList;

public class App {

    /*
     * This is the main method that uses the MBTA tool to make connections with the
     * MBTA server.
     * It retrieves, calcuates, and prints the requested data.
     */
    public static void main(String[] args) {

        // get the routes, prints long names
        ArrayList<String> ids = MBTATool.get_subway_routes("https://api-v3.mbta.com/routes/?filter[type]=0,1");

        // get the stops for each route, returns tree representing subway system
        Tree mbtaTree = MBTATool.get_subway_stops(
                "https://api-v3.mbta.com/stops?include=route&filter[route]=", ids);

        // verify there are enough arguments
        // then find a path between two stops, print it
        if (args.length > 2) {
            String start = args[1];
            String end = args[2];
            mbtaTree.find_path(start, end);
        }

    }

}
