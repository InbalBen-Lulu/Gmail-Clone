Build the application:
    docker build -f Dockerfile -t project-app .
Run the application:
    docker run --rm project-app

Run main program with Docker:
    docker build -f Dockerfile -t project-app .
    docker run --rm -v $(pwd)/data:/app/data project-app

Run unit tests with Docker:
    docker build -f Dockerfile.test -t  project-tests .
    docker run --rm project-tests