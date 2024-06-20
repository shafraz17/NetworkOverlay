# Bootstrap Server for Network Overlay System

This project implements a bootstrap server for a network overlay system using UDP datagram sockets. The server listens on port 55555 and handles registration, unregistration, and echo commands from clients.

## Features

- **Registration (`REG`)**: Clients can register with the server by providing their IP, port, and username.
- **Unregistration (`UNREG`)**: Clients can unregister from the server.
- **Echo (`ECHO`)**: Clients can request the server to list all registered nodes.

## Requirements

- Java Development Kit (JDK) 8 or higher

## Getting Started

### Clone the Repository

```sh
git clone https://github.com/shafraz17/NetowrkOverlay.git
cd NetworkOverlay
```

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

Everything is set to play with the provided commands...

## Client Commands

- **REG**: Register with the server.
  - Prompts: IP, port, username
  - Example:
    ```plaintext
    0048 REG 127.0.0.1 55556 user1
    ```
- **UNREG**: Unregister from the server.
  - Prompts: IP, port, username
  - Example:
    ```plaintext
    0048 UNREG 127.0.0.1 55556 user1
    ```
- **ECHO**: Request the server to list all registered nodes.
  - Example:
    ```plaintext
    0012 ECHO
    ```
