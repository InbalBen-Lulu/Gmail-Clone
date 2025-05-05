import socket
import sys

def main():
    # Check for correct number of command-line arguments
    if len(sys.argv) != 3:
        print("Usage: python3 client.py <server_ip> <server_port>")
        return
    server_ip = sys.argv[1]
    server_port = int(sys.argv[2])

    # Create a TCP socket
    s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    
    # Connect to the server using IP and port
    s.connect((server_ip, server_port))

    try:
        while True:
            user_input = input()

            # If the input is empty or whitespace, replace with a special marker
            if not user_input.strip():
                user_input = "<EMPTY>"

            # Try to encode the input in UTF-8; if it fails, use an error marker
            try:
                encoded = user_input.encode('utf-8')
            except UnicodeEncodeError:
                encoded = b"<INVALID>"

            # Send the encoded message to the server
            s.send(encoded)

            # Receive and print the server's response
            response = s.recv(4096).decode('utf-8')
            print(response)

    except KeyboardInterrupt:
        # Handle Ctrl+C to exit gracefully
        print("\nClient terminated.")
    except Exception as e:
        # Handle any unexpected error
        print(f"\nUnexpected error: {e}")
    finally:
        # Always close the socket on exit
        s.close()

if __name__ == "__main__":
    main()