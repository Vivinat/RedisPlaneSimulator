# PlaneSimulator
Redis Plane Simulator is a Java-based application that simulates the movement of airplanes and their communication with a command tower using Redis as the messaging backbone. My goal was to demonstrante how Redis can be utilized for real-time communication in distributed systems. On each simulation, a graph-based world map is generated. Airplanes receive an itinerary and can travel between nodes to arrive at their destinations. Once arriving, they make contact with the command tower of the destination, requesting landing via channel-based communication using Redis. The tower will only allow them to land if there is enough space on the airport. All functionalities where tested using Unit Tests.

## Features
Simulates airplane movements in a virtual airspace.
Facilitates communication between airplanes and a command tower using Redis Pub/Sub.
Demonstrates real-time data exchange in distributed systems.
Unit tests located under src/test/java, particularly in PlaneSimulatorTest.java

## Prerequisites
- Java Development Kit (JDK) 8 or higher
- Apache Maven
- Redis Server

