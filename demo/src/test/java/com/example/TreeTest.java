package com.example;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.AbstractMap.SimpleEntry;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for the MBTATool.
 */
public class TreeTest {
	static String problem1URL = "https://api-v3.mbta.com/routes/?filter[type]=0,1";
	static String problem1URLNoFilter = "https://api-v3.mbta.com/routes/";
	static String problem2URL = "https://api-v3.mbta.com/stops?include=route&filter[route]=";
	static ArrayList<String> expectedRouteNames = new ArrayList<String>(
			Arrays.asList("Red Line", "Blue Line", "Green Line C",
					"Mattapan Trolley", "Green Line B", "Green Line E", "Green Line D", "Orange Line"));
	static ArrayList<String> expectedRouteIds = new ArrayList<String>(Arrays.asList("Red", "Blue", "Green-C",
			"Mattapan", "Green-B", "Green-E", "Green-D", "Orange"));
	static HashMap<String, String> routes = new HashMap<String, String>();

	@BeforeAll
	private static void init() {
		for (int i = 0; i < expectedRouteNames.size(); i++) {
			routes.put(expectedRouteIds.get(i), expectedRouteNames.get(i));
		}
	}

	@Test
	public void testGetRouteWithMostStops() {
		Tree tree = MBTATool.generateMBTATree(problem2URL, routes);
		SimpleEntry<String, Integer> response = tree.getRouteWithMostStops();
		SimpleEntry<String, Integer> expected = new SimpleEntry<String, Integer>("Green Line E", 25);
		assertEquals(expected, response);
	}

	@Test
	public void testGetRouteWithMostStops2() {
		Tree tree = new Tree(null, null);
		assertThrows(NullPointerException.class, () -> {
			tree.getRouteWithMostStops();
		});
	}

	@Test
	public void testGetRouteWithLeastStops() {
		Tree tree = MBTATool.generateMBTATree(problem2URL, routes);
		SimpleEntry<String, Integer> response = tree.getRouteWithLeastStops();
		SimpleEntry<String, Integer> expected = new SimpleEntry<String, Integer>("Mattapan Trolley", 8);
		assertEquals(expected, response);
	}

	@Test
	public void testGetRouteWithLeastStops2() {
		Tree tree = new Tree(null, null);
		assertThrows(NullPointerException.class, () -> {
			tree.getRouteWithLeastStops();
		});
	}

	@Test
	public void testGetConnectingStops() {
		Tree tree = MBTATool.generateMBTATree(problem2URL, routes);
		HashMap<String, ArrayList<String>> response = tree.getConnectingStops();
		assertEquals(response.size(), 14);
		assertTrue(response.keySet().contains("Government Center"));
		assertEquals(response.get("Government Center"), new ArrayList<String>(
				Arrays.asList("Blue Line", "Green Line C", "Green Line B", "Green Line E", "Green Line D")));
		assertTrue(noEmptyLists(response.values()));
	}

	@Test
	public void testGetConnectingStops2() {
		Tree tree = new Tree(null, null);
		assertThrows(NullPointerException.class, () -> {
			tree.getConnectingStops();
		});
	}

	@Test
	public void testFindPath() {
		Tree tree = MBTATool.generateMBTATree(problem2URL, routes);
		ArrayList<String> response = tree.find_path("Forest Hills", "Government Center");
		ArrayList<String> expected = new ArrayList<String>(Arrays.asList("Orange Line"));
		assertEquals(expected, response);
	}

	@Test
	public void testFindPath1() {
		Tree tree = MBTATool.generateMBTATree(problem2URL, routes);
		ArrayList<String> response = tree.find_path("Forest Hills", "Ashmont");
		ArrayList<String> expected = new ArrayList<String>(Arrays.asList("Orange Line", "Red Line"));
		assertEquals(expected, response);
	}

	@Test
	public void testFindPath2() {
		Tree tree = MBTATool.generateMBTATree(problem2URL, routes);
		assertThrows(RuntimeException.class, () -> {
			tree.find_path("Forrest Hills", "Ashmont");
		});
	}

	@Test
	public void testFindPath3() {
		Tree tree = new Tree(null, null);
		assertThrows(NullPointerException.class, () -> {
			tree.find_path("Forest Hills", "Ashmont");
		});
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
