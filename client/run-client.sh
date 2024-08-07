#!/bin/bash
# Start up another client container in the host
echo "Starting Client Container..."

# Function to generate a random hash
generate_hash() {
    cat /dev/urandom | tr -dc 'a-zA-Z0-9' | fold -w 8 | head -n 1
}

# Set variables
IMAGE_NAME="bootstrap-client"
NETWORK="node-network"

CONTAINER_NAME="bootstrap-client-$(generate_hash)"

# Run the Docker container
echo "Running Docker container..."
docker run -it --name $CONTAINER_NAME --network $NETWORK $IMAGE_NAME
if [ $? -ne 0 ]; then
    echo "Error: Failed to start Docker container."
    exit 1
fi

echo "Docker container is now running and accepting commands..."
echo "Container name: $CONTAINER_NAME"
echo "Run below commands in a seperate terminal window for debugging"
echo "To stop the container, run: docker stop $CONTAINER_NAME"
echo "To view container logs, run: docker logs $CONTAINER_NAME"
