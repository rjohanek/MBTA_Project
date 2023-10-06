package com.example;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/*
 * A representation of a tree based on its branches and each branches leaves
 * as well as each leaf and the branches they are on.
 */
public class Tree {

	// The MBTA system organized by route, a map from route to its stops
	HashMap<String, ArrayList<String>> routesToStops = new HashMap<String, ArrayList<String>>();

	// The MBTA system organized by stop, a map from stop to its routes
	HashMap<String, ArrayList<String>> stopsToRoutes = new HashMap<String, ArrayList<String>>();

	/**
	 * Basic constructor of a Tree
	 * 
	 * @param routesToStops representing branches and their nodes
	 * @param stopsToRoutes representing nodes and their branches
	 */
	public Tree(HashMap<String, ArrayList<String>> routesToStops, HashMap<String, ArrayList<String>> stopsToRoutes) {
		this.routesToStops = routesToStops;
		this.stopsToRoutes = stopsToRoutes;
	}

	public HashMap<String, ArrayList<String>> getRoutesToStops() {
		return (HashMap<String, ArrayList<String>>) routesToStops.clone();
	}

	public HashMap<String, ArrayList<String>> getStops_to_routes() {
		return (HashMap<String, ArrayList<String>>) stopsToRoutes.clone();
	}

	/*
	 * Breadth First Search to ensure fewest transfers.
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
    // all possible paths up to this point
		// currently representing starting branches as a list of lists of length one
		ArrayList<ArrayList<String>> possible_paths = new ArrayList<>();
		for (String route : stopsToRoutes.get(start)) {
			ArrayList<String> tempPath = new ArrayList<>();
			tempPath.add(route);
			possible_paths.add(tempPath);

		}

		// all possible ending branches
		ArrayList<String> endingBranches = stopsToRoutes.get(end);

		// explored branches
		ArrayList<String> explored = new ArrayList<>();

		// if the start and end exist on the same branch, return that branch
		for (String branch : endingBranches) {
			if (possible_paths.contains(Collections.singletonList(branch))) {
			return new ArrayList<>(Collections.singletonList(branch));
		}
		}

		// else, choose the first starting branch and explore each of its stops
		// if one of the stops connects to a route, add it to the frontier
		// and check if it is an ending branch,
		// if it is, return the path of routes followed
		// else, continue searching the current route
		// once all stops on the current route have been explored
		// start the process again with the next route

		while (possible_paths.size() > 0) {
			ArrayList<String> path = possible_paths.get(0);
			String current_route = path.get(path.size() - 1);

			ArrayList<String> stops = routesToStops.get(current_route);
			for (String stop : stops) {

				if (stop == end) {
					return path;
				}
				
				// get the routes for this stop and filter them
				ArrayList<String> unexplored = new ArrayList<String>();
				ArrayList<String> routes = stopsToRoutes.get(stop);
								for (String route : routes) {
					if (!explored.contains(route)) {
						unexplored.add(route);
					}
				}

				// if a new route is an ending route, return the path
				// else add route to list and continue
				for (String r : unexplored) {
					ArrayList<String> tempPath = new ArrayList<>(path);
					tempPath.add(r);
					possible_paths.add(tempPath);
					if (endingBranches.contains(r)) {
						return tempPath;
					} 
				}
			}
		}
		// ending stop was never found
		return new ArrayList<>();
	}

}
