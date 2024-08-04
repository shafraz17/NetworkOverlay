# Bootstrap Server for Network Overlay System

This project implements a bootstrap server for a network overlay system using UDP datagram sockets. The server listens on port `55555` and handles registration, unregistration, and echo commands from clients.

## Getting Started

### Clone the Repository

```sh
git clone https://github.com/shafraz17/NetowrkOverlay.git
cd NetworkOverlay
```

## Requirements

- Java Development Kit (JDK) 8 or higher

## Initializing the Overlay Network

> Points 4-7 are automated with scripts - `run-client.(sh/bat)` & `run-server.(sh/bat)`. Choose the appropriate script based on your system.
> Execute the appropriate script based on your role within the network. (Server - `run-server`/Workers - `run-client`).

1. Initialize a Docker Overlay network. The VPS server as the Swarm Manager Node.
    1. `docker swarm init`.
2. Join other worker nodes to the Swarm Network using the provided command.
3. Create an `Overlay` network from within the Swarm Manager using below command.
    1. `docker network create --driver overlay --attachable node-network`.
4. Build the `BootstrapServer` docker image in the VPS Server.

    1. ```bash
        cd server
        docker build -t bootstrap-server
       ```

5. Run the docker image for the Bootstrap Server in the Swarm Manager attaching the container to the `node-network` created above.
    1. `docker run -d -p 55555:55555 --network node-network bootstrap-server`.
6. Build the `BootstrapClient` docker image in each worker node.

    1. ```bash
        cd client
        docker build -t bootstrap-client
       ```

7. Run the client containers from within the worker nodes attaching to the `node-network` as well.
    1. `docker run -it --network node-network bootstrap-client`.
8. Proceed with the command executions from within each worker node as required.

## Client Commands

### Everything is set to play with the provided commands

- **`REG`**: Register with the server.
  - Prompts: IP, port, username
  - Example:

    ```plaintext
    0048 REG 127.0.0.1 55556 user1
    ```

- **`UNREG`**: Unregister from the server.
  - Prompts: IP, port, username
  - Example:

    ```plaintext
    0048 UNREG 127.0.0.1 55556 user1
    ```

- **`ECHO`**: Request the server to list all registered nodes.
  - Example:

    ```plaintext
    0012 ECHO
    ```

## Features

- **Registration (`REG`)**: Clients can register with the server by providing their IP, port, and username.
- **Unregistration (`UNREG`)**: Clients can unregister from the server.
- **Echo (`ECHO`)**: Clients can request the server to list all registered nodes.

## Manual execution of the applications (without Docker)

### Compile the Server and Client

```sh
javac BootstrapServer.java
javac BootstrapClient.java
```

### Running the Server and Clients

User the below commands in separate terminals

```sh
java BootstrapServer
java BootstrapClient
```
