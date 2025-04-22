FROM gcc:latest

WORKDIR /app

COPY . .

# Compile all .cpp files from src/ recursively
RUN g++ -std=c++17 -Wall -Wextra -I./src $(find ./src -name '*.cpp') -o main

CMD ["./main"]