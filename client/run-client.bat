@REM Start up another client container in the host

@echo off
echo Starting Client Container...

REM Set variables
set IMAGE_NAME=bootstrap-client
set NETWORK=node-network

@REM Generate a random hash as the container name suffix
SET SUFFIX=%random%%random%%random%%random%%random%%random%
SET SUFFIX=%SUFFIX:0=a%
SET SUFFIX=%SUFFIX:1=b%
SET SUFFIX=%SUFFIX:2=c%

@REM Unique container name
set CONTAINER_NAME=bootstrap-client-%SUFFIX%

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
