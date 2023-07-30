package com.example;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

/*
 * A representation of a tree based on its branches and each branches leaves
 * as well as each leaf and the branches they are on.
 */
public class Tree {

	// The MBTA system organized by route, a map from route to its stops
	HashMap<String, ArrayList<String>> routes_to_stops = new HashMap<String, ArrayList<String>>();

	// The MBTA system organized by stop, a map from stop to its routes
	HashMap<String, ArrayList<String>> stops_to_routes = new HashMap<String, ArrayList<String>>();

	/**
	 * Basic constructor of a Tree
	 * 
	 * @param routes_to_stops representing branches and their nodes
	 * @param stops_to_routes representing nodes and their branches
	 */
	public Tree(HashMap<String, ArrayList<String>> routes_to_stops, HashMap<String, ArrayList<String>> stops_to_routes) {
		this.routes_to_stops = routes_to_stops;
		this.stops_to_routes = stops_to_routes;
	}

	public HashMap<String, ArrayList<String>> getRoutes_to_stops() {
		return (HashMap<String, ArrayList<String>>) routes_to_stops.clone();
	}

	public HashMap<String, ArrayList<String>> getStops_to_routes() {
		return (HashMap<String, ArrayList<String>>) stops_to_routes.clone();
	}

	/*
	 * Depth First Search to ensure fewest transfers.
	 * Choose a route that the starting stop is on and search it.
	 * At each stop, check to see if it is the end stop.
	 * If it isn't, keep track of any unexplored connecting routes.
	 * If the ending stop is on one of these routes, return the path so far.
	 * Otherwise, add the route to the frontier.
	 * When done exploring the current route, continue
	 * with the next route on the frontier, until either
	 * the ending stop is found or all routes have been searched.
	 */
	public ArrayList<String> find_path(String start, String end) {
		// all possible starting branches and the path up to this point
		// for the first starting branches its a list of lists of one
		ArrayList<ArrayList<String>> startingBranches = new ArrayList<>();
		for (String route : stops_to_routes.get(start)) {
			ArrayList<String> tempPath = new ArrayList<>();
			tempPath.add(route);
			startingBranches.add(tempPath);

		}

		// all possible ending branches
		ArrayList<String> endingBranches = stops_to_routes.get(end);

		// explored branches
		ArrayList<String> explored = new ArrayList<>();

		// // if the start and end exist on the same branch, return that branch
		// for (String branch : endingBranches) {
		// if (startingBranches.contains(branch)) {
		// return new ArrayList<>(Collections.singletonList(branch));
		// }
		// }

		// DFS
		// else, choose the first starting branch and explore each of its stops
		// if one of the stops connects to a route that is an ending branch,
		// return the path of routes followed
		// else, add the route to the end of the list of starting routes and
		// start the process again with the next route

		while (startingBranches.size() > 0) {
			ArrayList<String> path = startingBranches.get(0);
			String mostRecent = path.get(path.size() - 1);

			ArrayList<String> stops = routes_to_stops.get(mostRecent);
			for (String stop : stops) {
				if (stop == end) {
					return path;
				}
				// get the routes for this stop and filter them
				ArrayList<String> routes = stops_to_routes.get(stop);
				for (String route : routes) {
					if (explored.contains(route)) {
						routes.remove(route);
					}
				}
				// if a new route is an ending route, return the path
				// else add route to list and continue
				for (String r : routes) {
					if (endingBranches.contains(r)) {
						path.add(r);
						return path;
					} else {
						explored.add(mostRecent);
						path.add(r); // does this update starting branches ?? i want it to
					}
				}
			}
		}
		// ending stop was never found
		return new ArrayList<>();
	}

}
