package com.example;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;

import org.junit.jupiter.api.Test;

/**
 * Unit tests for the MBTATool.
 */
public class MBTAToolTest {
	String problem1URL = "https://api-v3.mbta.com/routes/?filter[type]=0,1";
	String problem1URLNoFilter = "https://api-v3.mbta.com/routes/";
	String problem2URL = "https://api-v3.mbta.com/stops?include=route&filter[route]=";
	Collection<String> expected = new ArrayList<String>(Arrays.asList("Red Line", "Blue Line", "Green Line C",
			"Mattapan Trolley", "Green Line B", "Green Line E", "Green Line D", "Orange Line"));
	Collection<String> someStops = new ArrayList<String>(Arrays.asList("Saint Mary's Street", "Longwood", "Park Street",
			"Beaconsfield", "Government Center", "Milton", "Coolidge Corner", "East Somerville", "Longwood Medical Area",
			"JFK/UMass", "Harvard", "Washington Square", "North Station", "Magoun Square"));

	@Test
	public void testGetSubwayRoutes() {
		HashMap<String, String> response = MBTATool.getSubwayRoutes(problem1URL);
		assertTrue(response.values().containsAll(expected));
		assertEquals(response.values().size(), expected.size());
	}

	@Test
	public void testGetSubwayRoutes2() {
		HashMap<String, String> response = MBTATool.getSubwayRoutes(problem1URLNoFilter);
		assertTrue(response.values().containsAll(expected));
		assertTrue(response.values().size() > expected.size());
	}

	@Test
	public void testGenerateTree() {
		HashMap<String, String> routes = MBTATool.getSubwayRoutes(problem1URL);
		Tree response = MBTATool.generateMBTATree(problem2URL, routes);
		assertEquals(response.getNodeToBranches().size(), 125);
		assertEquals(response.getBranchToNodes().size(), expected.size());
		assertTrue(response.getNodeToBranches().keySet().containsAll(someStops));
		assertTrue(response.getBranchToNodes().keySet().containsAll(expected));
		assertTrue(noEmptyLists(response.getNodeToBranches().values()));
		assertTrue(noEmptyLists(response.getBranchToNodes().values()));
	}

	/**
	 * Ensure the given list of lists does not contain any empty lists.
	 * Returns true if all lists are populated with at least one item.
	 */
	private static boolean noEmptyLists(Collection<ArrayList<String>> list) {
		for (ArrayList<String> l : list) {
			if (l.size() < 1) {
				return false;
			}
		}
		return true;
	}
}
