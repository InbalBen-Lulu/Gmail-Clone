FROM gcc:latest

RUN apt-get update && apt-get install -y cmake

COPY . /usr/src/project
WORKDIR /usr/src/project

RUN mkdir build
WORKDIR /usr/src/project/build

RUN cmake .. && make

CMD ["./app"]
