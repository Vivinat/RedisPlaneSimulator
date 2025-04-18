# RedisPlaneSimulator

Redis Plane Simulator is a Java-based application that simulates the movement of airplanes and their communication with a command tower using Redis as the messaging backbone. My goal was to demonstrante how Redis can be utilized for real-time communication in distributed systems. On each simulation, a graph-based world map is generated. Airplanes receive an itinerary and can travel between nodes to arrive at their destinations. Once arriving, they make contact with the command tower of the destination, requesting landing via channel-based communication using Redis. The tower will only allow them to land if there is enough space on the airport. All functionalities where tested using Unit Tests.

## Features

- **Airplane Simulation**: Simulates multiple airplanes with unique IDs, positions, and flight paths.
- **Command Tower Communication**: A centralized command tower that sends instructions to and receives updates from airplanes.
- **Redis Integration**: Uses Redis Pub/Sub for real-time messaging between airplanes and the command tower.
- **Scalability**: Designed to handle multiple airplanes concurrently.
- **Extensibility**: Modular codebase makes it easy to add new features or modify existing ones.

## How It Works

### System Architecture
1. **Redis Pub/Sub**: 
   - Redis is used to facilitate communication between the airplanes and the command tower.
   - Airplanes publish their status (e.g., position, altitude) to specific Redis channels.
   - The command tower subscribes to these channels to receive updates and send commands.

2. **Airplane Simulation**:
   - Each airplane is represented as a Java object with attributes like position, speed, and direction.
   - A simulation loop updates the airplane's state and publishes updates to Redis.

3. **Command Tower**:
   - The command tower listens for airplane updates and can send control commands (e.g., change altitude, adjust course).
   - It acts as the central authority for managing the airspace.

### Key Components
- **Airplane Class**:
  - Represents an airplane with properties such as ID, position, speed, and direction.
  - Contains methods to simulate movement and respond to commands.

- **Command Tower Class**:
  - Manages the communication with airplanes via Redis.
  - Listens for updates and sends commands based on predefined logic or user input.

- **Redis Pub/Sub Integration**:
  - Redis channels are used for publishing and subscribing to events.
  - Ensures real-time, low-latency communication between the components.

## Installation

### Prerequisites
- Java 8 or higher
- Redis server installed and running
