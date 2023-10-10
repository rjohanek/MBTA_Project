## About this project
This project represents my solutions to the Broad take home assignment. 

App.java
The project can be ran through the Main Application which prints the responses to each of the three questions.

Connection.java
This class represents a connection to the server, as specified by the given url. It is a general class that can be used for connections to any server.

MBTATool.java
The solution to problem 1 and some helper methods for representing MBTA data  reside in this tool. 
It can get data from the server, by relying on a connection, and manipulate the data, and print the results. 

Tree.java
This class represents a generalized tree structure with branches and nodes. It does not represent MBTA system in the simplest way, but it is highly extendable. This class is used to search the MBTA system and returns the solutions for problems 2 and 3.

## How to Run
This project can be run through the main method in the App.java file. 
Be sure to pass in the names of two stops as arguments for problem 3.
