package client;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Objects;
import java.util.Scanner;

public class TCPClient {

    public static void main(String[] args) {
        final String serverAddress = "localhost";
        final int serverPort = 12345;

        try (Socket socket = new Socket(serverAddress, serverPort)) {
            System.out.println("Connected to the server.");

            Scanner scanner = new Scanner(System.in);

            // Get output stream for communication
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(socket.getOutputStream());
            DataOutputStream dataOutputStream = new DataOutputStream(bufferedOutputStream);

            String input;
            while (true){
                System.out.print("Enter value: ");
                input = scanner.nextLine();

                if(Objects.equals(input, "exit")) break;

                dataOutputStream.writeInt(input.length());
                dataOutputStream.write(input.getBytes());
                dataOutputStream.flush();  // Ensure the data is sent immediately
            }

            // Close the scanner
            scanner.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
