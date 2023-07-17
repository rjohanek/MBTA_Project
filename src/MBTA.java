package src;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class MBTA {

	private static HttpURLConnection connection;
	private static int response_code;

	// main method runs calling the other two methods which print results as well as
	// return them
	// for testing purposes
	public static void main(String[] args) {
		get_subway_routes();
	}

	// establishes a connection with the server at the given URL
	private static void open_connection(URL url) {
		try {
			// open up the connection
			connection = (HttpURLConnection) url.openConnection();

			// setup how to request on the connection
			connection.setRequestMethod("GET");
			connection.setConnectTimeout(10000);
			connection.setReadTimeout(10000);

			// response_code determines if the connection was successful
			response_code = connection.getResponseCode();
			System.out.println(response_code);

		} catch (IOException e) {
			System.out.println("connection not opened successfully");
		}
	}

	public static List<String> get_subway_routes() {

		// create the reader to read the response from the client
		// and the string buffer to append the response onto once read
		BufferedReader reader;
		String line;
		StringBuffer response = new StringBuffer();
		ArrayList<String> long_names = new ArrayList<>();

		// create connection, surround URL with a try-catch in case the URL is malformed
		URL subway_URL;
		try {
			subway_URL = new URL("https://api-v3.mbta.com/routes/?");
			open_connection(subway_URL);
		} catch (MalformedURLException e) {
			System.out.println("subway_URL was malformed");
		}

		// check to make sure the connection was successful
		if (response_code > 299) {
			System.out.println("Unsuccessful Connection. Response Code:" + response_code);
		}
		// if the connection is successful, read the response from the connection
	
		else {
			try {
				reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

				while ((line = reader.readLine()) != null) {
					response.append(line);
				}
				reader.close();
			} catch (IOException ex) {
				System.out.println("There was an issue reading the server response " +
						"that caused an IOException to be thrown.");
			}

			// convert the response to a json object
			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject) parser.parse(response.toString());

			// get the long names
			long_names = json.get("long_names");
			System.out.println(long_names);
		}
		connection.disconnect();
		return long_names;
	}

}
