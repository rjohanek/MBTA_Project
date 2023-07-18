package com.example;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;

public class Tree {

	// The MBTA system organized by route, a map from route to its stops
	HashMap<String, ArrayList<String>> routes_to_stops = new HashMap<String, ArrayList<String>>();

	// The MBTA system organized by stop, a map from stop to its routes
	HashMap<String, ArrayList<String>> stops_to_routes = new HashMap<String, ArrayList<String>>();

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


	public ArrayList<String> find_path(String start, String end) {
		// all possible starting branches
		ArrayList<String> startingBranches = stops_to_routes.get(start);
		// all possible ending branches
		ArrayList<String> endingBranches = stops_to_routes.get(end);

		// explored branches
		ArrayList<String> explored = new ArrayList<>();

		// paths 
		ArrayList<ArrayList<String>> paths = new ArrayList<>();

		// if the start and end exist on the same branch, return that branch
		for (String branch : endingBranches) {
			if (startingBranches.contains(branch)) {
				return new ArrayList<>(Collections.singletonList(branch));
			}
		}

		// DFS
		// else, choose the first starting branch and explore each of its stops
		// if one of the stops connects to a route that is an ending branch, 
		// return the path of routes followed
		// else, add the route to the end of the list of starting routes and
		// start the process again with the next route
		// TODO: update startingbranches to include path up to this point
		while (startingBranches.size() > 0) {
			ArrayList<String> path = new ArrayList<>();
			String first = startingBranches.get(0);
			path.add(0, first);

			ArrayList<String> stops = routes_to_stops.get(first);
			for (String stop : stops) {
				// get the routes for this stop and filter them 
				ArrayList<String> routes = stops_to_routes.get(stop);
				for (String route : routes) {
					if (explored.contains(route)) {
						routes.remove(route);
					}
				}
				// if new route is ending route, return
				// else add route to list and continue
				for (String r : routes) {
					if (endingBranches.contains(r)) {
						path.add(1, r);
						return path;
					} else {
						explored.add(first);
						startingBranches.add(r);
					}

				}
			}
		}
}
	
}
