Build the application:
    docker build -f Dockerfile -t project-app .
Run the application:
    docker run --rm project-app


Build the tests:
    docker build -f Dockerfile.test -t project-tests .
Run the tests:
    docker run --rm project-tests
