#!/bin/bash

echo "Starting Docker build and run process..."

# Set variables
IMAGE_NAME="bootstrap-server"
CONTAINER_NAME="bootstrap-server"
PORT=55555
NETWORK="node-network"

# Check if a container with the same name is already running
echo "Checking for existing containers..."
if [ "$(docker container ls -a --filter name=$CONTAINER_NAME)" ]; then
    echo "Stopping existing container..."
    docker stop $CONTAINER_NAME
    docker rm $CONTAINER_NAME
fi

# Remove existing image if it exists
echo "Checking for existing image..."
if docker image inspect "$IMAGE_NAME" &>/dev/null; then
    echo "Removing existing image: $IMAGE_NAME"
    docker rmi -f "$IMAGE_NAME"
    if [ $? -ne 0 ]; then
        echo "Error: Failed to remove existing image."
        exit 1
    fi
else
    echo "No existing image found. Proceeding with build."
fi

# Build the Docker image
echo "Building Docker image..."
docker build -t $IMAGE_NAME .
if [ $? -ne 0 ]; then
    echo "Error: Docker build failed."
    exit 1
fi

# Run the Docker container
echo "Running Docker container..."
docker run -d --name $CONTAINER_NAME -p $PORT:$PORT/udp --network $NETWORK $IMAGE_NAME
if [ $? -ne 0 ]; then
    echo "Error: Failed to start Docker container."
    exit 1
fi

echo "Docker container is now running in the background."
echo "Container name: $CONTAINER_NAME"
echo "To stop the container, run: docker stop $CONTAINER_NAME"
echo "To view container logs, run: docker logs $CONTAINER_NAME"

# Exit the script
exit 0
