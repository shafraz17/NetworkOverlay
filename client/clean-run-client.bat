@REM Clean previous & build a fresh docker image & start up a container 

@echo off
echo Starting Docker build and run process...

REM Set variables
set IMAGE_NAME=bootstrap-client
set CONTAINER_NAME=bootstrap-client
set NETWORK=node-network

REM Check if a container with the same name is already running
echo Checking for existing containers...
docker container ls -a --filter name=%CONTAINER_NAME%
if %ERRORLEVEL% equ 0 (
    echo Stopping existing container...
    docker stop %CONTAINER_NAME%
    docker rm %CONTAINER_NAME%
)

REM Remove existing image if it exists
echo Checking for existing image...
docker image inspect %IMAGE_NAME% >nul 2>&1
if %ERRORLEVEL% equ 0 (
    echo Removing existing image: %IMAGE_NAME%
    docker rmi -f %IMAGE_NAME%
    if %ERRORLEVEL% neq 0 (
        echo Error: Failed to remove existing image.
        exit /b 1
    )
) else (
    echo No existing image found. Proceeding with build.
)

REM Build the Docker image
echo Building Docker image...
docker build -t %IMAGE_NAME% .
if %ERRORLEVEL% neq 0 (
    echo Error: Docker build failed.
    exit /b %ERRORLEVEL%
)

REM Run the Docker container
echo Running Docker container...
docker run -it --name %CONTAINER_NAME% --network %NETWORK% %IMAGE_NAME%
if %ERRORLEVEL% neq 0 (
    echo Error: Failed to start Docker container.
    exit /b %ERRORLEVEL%
)

echo Docker container is now running and accepting commands...
echo Container name: %CONTAINER_NAME%
echo Run below commands in a seperate terminal window for debugging
echo To stop the container, run: docker stop %CONTAINER_NAME%
echo To view container logs, run: docker logs %CONTAINER_NAME%
