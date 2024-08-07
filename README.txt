# Bootstrap Server for Network Overlay System

This project implements a bootstrap server for a network overlay system
using UDP datagram sockets. The server listens on port `55555` and
handles registration, unregistration, and echo commands from clients.

## Getting Started

### Clone the Repository

git clone https://github.com/shafraz17/NetowrkOverlay.git
cd NetworkOverlay

## Requirements

-   Java Development Kit (JDK) 17

## Initializing the Overlay Network

> Starting up server & client nodes are automated with shell & batch
> scripts for both OS platforms. Following table summarizes the usage of
> each script. Use as necessary.

  ----------------------------------------------------------------------------------
  Script                        Usage            Usage Notes
  ----------------------------- ---------------- -----------------------------------
  clean-run-server.(sh/bat)   Initialize the   Build & run a fresh docker
                                server           container for the server. Should be
                                                 executed once for the bootstrap
                                                 server application.

  clean-run-client.(sh/bat)   Initialize the   Build & run a fresh docker
                                worker node for  container for a worker node. Should
                                the first time   be executed once for a single host
                                as a client      which runs bootstrap client
                                                 application.

  run-client.(sh/bat)         Start up another Build & run docker container from
                                worker node in   exsiting image for a worker node.
                                an already       Can be executed multiple times in a
                                initialized host docker host to spin up multiple
                                                 worker nodes inside the same host
                                                 machine.
  ----------------------------------------------------------------------------------

- Execute the appropriate script based on your role within the network.
- (Server - `clean-run-server.*`/Workers - `*-run-client.*`).

1.  Initialize a Docker Overlay network. The VPS server as the Swarm
    Manager Node.

    1.  docker swarm init.

2.  Join other worker nodes to the Swarm Network using the provided
    command.

3.  Create an Overlay network from within the Swarm Manager using
    below command.

    1.  docker network create --driver overlay --attachable node-network.

4.  Build the BootstrapServer docker image in the VPS Server.

    1.  This is automated via the script:
        clean-run-server.sh`/`clean-run-server.bat.

5.  Start up client application in each worker node.

    1.  This is automated via the script:
        clean-run-client.sh`/`clean-run-client.bat OR
        run-client.sh`/`run-client.bat.

6.  Proceed with the command executions from within each worker node as
    required.

## Client Commands

### Everything is set to play with the provided commands

-   REG: Register with the server.
    -   Prompts: IP, port, username

    -   Example:
        0048 REG 127.0.0.1 55556 user1
        
-   UNREG: Unregister from the server.
    -   Prompts: IP, port, username

    -   Example:
        0048 UNREG 127.0.0.1 55556 user1
        
-   ECHO: Request the server to list all registered nodes.
    -   Example:
        0012 ECHO

## Features

-   Registration (REG): Clients can register with the server by
    providing their IP, port, and username.
-   Unregistration (UNREG): Clients can unregister from the
    server.
-   Echo (ECHO): Clients can request the server to list all
    registered nodes.

## Manual execution of the applications (without Docker)

### Compile the Server and Client

javac org/uom/tesla/TestClient.java
javac org/uom/tesla/BootstrapServer.java

### Running the Server and Clients

User the below commands in separate terminals

java org/uom/tesla/TestClient
java org/uom/tesla/BootstrapServer


