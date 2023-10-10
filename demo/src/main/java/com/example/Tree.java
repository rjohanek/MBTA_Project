package com.example;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/*
 * A representation of a tree based on its branches and each branches nodes
 * as well as each node and the branches they are on.
 * 
 * Optionally, the tree can include information on which branch has the most nodes. 
 * And which branch has the least nodes.
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

	public HashMap<String, ArrayList<String>> getStopsToRoutes() {
		return (HashMap<String, ArrayList<String>>) stopsToRoutes.clone();
	}

	/**
	 * Calculate the max number of stops any route has and return the
	 * name of the route and its number of stops
	 * 
	 * @return entry of route name to number of stops
	 */
	public SimpleEntry<String, Integer> getRouteWithMostStops() {
		// tracking the current route with the most stops
		// represents name of the route and number of stops on it
		if (Objects.nonNull(this.routesToStops)) {
			SimpleEntry<String, Integer> most = new SimpleEntry<String, Integer>("", 0);
			for (Map.Entry<String, ArrayList<String>> entry : this.routesToStops.entrySet()) {
				ArrayList<String> stops = entry.getValue();
				// keep the most and least values up to date
				if (stops.size() > most.getValue()) {
					most = new SimpleEntry<String, Integer>(entry.getKey(), stops.size());
				}
			}

			return most;
		} else {
			throw new NullPointerException("No routesToStops Map entered");
		}
	}

	/**
	 * Calculate the least number of stops any route has and return the
	 * name of the route and its number of stops
	 * 
	 * @return entry of route name to number of stops
	 */
	public SimpleEntry<String, Integer> getRouteWithLeastStops() {
		if (Objects.nonNull(this.routesToStops)) {
			// tracking the current route with the least stops
			// represents name of the route and number of stops on it
			SimpleEntry<String, Integer> least = new SimpleEntry<String, Integer>("", 999);
			for (Map.Entry<String, ArrayList<String>> entry : this.routesToStops.entrySet()) {
				ArrayList<String> stops = entry.getValue();
				// keep the most and least values up to date
				if (stops.size() < least.getValue()) {
					least = new SimpleEntry<String, Integer>(entry.getKey(), stops.size());
				}
			}

			return least;
		} else {
			throw new NullPointerException("No routesToStops Map entered");
		}
	}

	public HashMap<String, ArrayList<String>> getConnectingStops() {
		if (Objects.nonNull(this.stopsToRoutes)) {
			HashMap<String, ArrayList<String>> connectingStopsToRoutes = new HashMap<>();
			for (final Map.Entry<String, ArrayList<String>> entry : this.stopsToRoutes.entrySet()) {
				if (entry.getValue().size() > 1) {
					connectingStopsToRoutes.put(entry.getKey(), entry.getValue());
				}
			}
			return connectingStopsToRoutes;
		} else {
			throw new NullPointerException("No stopsToRoutes Map entered");
		}

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
		if (Objects.nonNull(this.stopsToRoutes) && Objects.nonNull(this.routesToStops)) {
			if (stopsToRoutes.containsKey(start) && stopsToRoutes.containsKey(end)) {
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
			} else {
				throw new RuntimeException("Invalid stop name");
			}
		} else {
			throw new NullPointerException("No routesToStops or stopsToRoutes entered");
		}
	}


}
