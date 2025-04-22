Run main program with Docker:
    docker build -f Dockerfile -t bloom-app .
    docker run --rm -v $(pwd)/data:/app/data bloom-app

Run unit tests with Docker:
    docker build -f Dockerfile.test -t bloom-tests .
    docker run --rm bloom-tests