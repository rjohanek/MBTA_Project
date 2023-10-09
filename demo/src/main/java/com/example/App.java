package com.example;

import java.util.HashMap;

/*
 * This is the main program that prints responses to the three questions
 * on the MBTA Broad take home assignment.
 */
public class App {

    /*
     * This is the main method that uses the MBTA tool to make connections with the
     * MBTA server.
     * It calls the MBTA Rool to retrieve, calcuate, and print the requested data.
     */
    public static void main(String[] args) {

        // problem 1 solution
        // get the routes, prints long names, returns map from ids to longnames
        HashMap<String, String> routesIdToName = MBTATool.getSubwayRoutes(
            "https://api-v3.mbta.com/routes/?filter[type]=0,1");

        // problem 2 solution
        // get the stops for each route, returns tree representing subway system
        Tree mbtaTree = MBTATool.generateTree(
                "https://api-v3.mbta.com/stops?include=route&filter[route]=", routesIdToName);
        // prints requested information
        mbtaTree.getRouteWithMostStops();
        mbtaTree.getRouteWithLeastStops();
        mbtaTree.getConnectingStops();

        // verify there are enough arguments
        // then find a path between two stops
        if (args.length > 2) {
            String start = args[1];
            String end = args[2];
            // problem 3 solution
            // search the tree using BFS, prints path
            mbtaTree.find_path(start, end);
        }

    }

}
