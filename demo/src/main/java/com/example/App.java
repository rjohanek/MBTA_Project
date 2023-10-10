package com.example;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.AbstractMap.SimpleEntry;

/*
 * This is the main program that prints responses to the three questions
 * on the MBTA Broad take home assignment.
 */
public class App {

    /*
     * This is the main method that uses the MBTA tool to make connections with the
     * MBTA server.
     * It calls the MBTA tool and Tree to retrieve, calcuate, and print the
     * requested data.
     */
    public static void main(String[] args) {

        // problem 1 solution
        // get the routes, prints long names, returns map from ids to longnames
        HashMap<String, String> routesIdToName = MBTATool.getSubwayRoutes(
                "https://api-v3.mbta.com/routes/?filter[type]=0,1");
        System.out.println("Problem 1 Solution:");
        System.out.print("The names of all train routes are ");
        System.out.println(routesIdToName.values().toString().replace("[", "").replace("]", ""));

        // problem 2 solution
        // get the stops for each route, returns tree representing subway system
        Tree mbtaTree = MBTATool.generateMBTATree(
                "https://api-v3.mbta.com/stops?include=route&filter[route]=", routesIdToName);
        SimpleEntry<String, Integer> most = mbtaTree.getBranchWithMostNodes();
        SimpleEntry<String, Integer> least = mbtaTree.getBranchWithLeastNodes();
        HashMap<String, ArrayList<String>> connecting = mbtaTree.getConnectingNodes();
        System.out.println("Problem 2 Solution:");
        System.out.print("The route with the most number of stops is ");
        System.out.print(most.getKey() + " with ");
        System.out.println(most.getValue() + " stops");
        System.out.print("The route with the least number of stops is ");
        System.out.print(least.getKey() + " with ");
        System.out.println(least.getValue() + " stops");
        System.out.println("The connecting stops are as follows ");
        for (final Map.Entry<String, ArrayList<String>> entry : connecting.entrySet()) {
            System.out.println(entry.getKey());
            System.out.print("  connects to the following routes: ");
            System.out.println(entry.getValue().toString().replace("[", "").replace("]", ""));
        }

        // verify there are enough arguments
        // then find a path between two stops
        if (args.length > 1) {
            String start = args[0];
            String end = args[1];
            // problem 3 solution
            // search the tree using BFS
            ArrayList<String> path = mbtaTree.find_path(start, end);
            System.out.println("Problem 3 Solution");
            System.out.print("The route between " + start + " and " + end + " is ");
            System.out.println(path.toString().replace("[", "").replace("]", ""));
        }
    }
}
