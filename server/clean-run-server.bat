@echo off
echo Starting Docker build and run process...

REM Set variables
set IMAGE_NAME=bootstrap-server
set CONTAINER_NAME=bootstrap-server
set PORT=55555
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
docker run -d --name %CONTAINER_NAME% -p %PORT%:%PORT%/udp --network %NETWORK% %IMAGE_NAME%
if %ERRORLEVEL% neq 0 (
    echo Error: Failed to start Docker container.
    exit /b %ERRORLEVEL%
)

echo Docker container is now running in the background.
echo Container name: %CONTAINER_NAME%
echo To stop the container, run: docker stop %CONTAINER_NAME%
echo To view container logs, run: docker logs %CONTAINER_NAME%

REM Exit the script
exit /b 0