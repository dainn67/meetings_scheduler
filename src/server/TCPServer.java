package server;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class TCPServer {
    static int count = 0;
    static String username;
    static String password;
    static String tableName;

    public static void main(String[] args) {
        final int serverPort = 12345;
        try (ServerSocket serverSocket = new ServerSocket(serverPort)) {
            System.out.println("Server waiting for connections...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected: " + clientSocket.getInetAddress());

                // Handle messages from the client
                new Thread(() -> handleClient(clientSocket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        createTable();
    }

    private static void handleClient(Socket clientSocket) {
        try (DataInputStream dataInputStream = new DataInputStream(clientSocket.getInputStream())) {
            while (true) {
                // Read the length of the incoming message
                int length = dataInputStream.readInt();

                if (length == -1) {
                    // End of stream, client disconnected
                    System.out.println("Client disconnected: " + clientSocket.getInetAddress());
                    break;
                }

                // Read the message based on the length
                byte[] messageBytes = new byte[length];
                dataInputStream.readFully(messageBytes);

                // Convert the message to a String
                String message = new String(messageBytes, StandardCharsets.UTF_8);
                System.out.println("Received from client: " + message);
                switch (count) {
                    case 0: {
                        username = message;
                        System.out.println("username = " + message);
                        break;
                    }
                    case 1: {
                        password = message;
                        System.out.println("password = " + message);
                        break;
                    }
                    case 2: {
                        tableName = message;
                        System.out.println("table name = " + message);
//                        createTable();
                        break;
                    }
                }
                count++;
            }
        } catch (IOException e) {
            System.out.println("Client disconnected: " + clientSocket.getInetAddress());
        }
    }

    public static Connection connect() {
        String url = "jdbc:postgresql://localhost:5432/postgres";
        java.sql.Connection conn = null;
        try {
            conn = DriverManager.getConnection(url, username, password);
        } catch (SQLException e) {
            System.out.println("Exception: " + e);
        }
        return conn;
    }

    public static void createTable() {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS " + tableName + " ("
                + "id SERIAL PRIMARY KEY,"
                + "column1 VARCHAR(30),"
                + "column2 VARCHAR(30),"
                + "column3 VARCHAR(30)"
                + ");";

        try (Connection connection = connect()) {
            if (connection != null) {
                Statement statement = connection.createStatement();
                // Execute the query to create the table

                statement.execute(createTableSQL);
                System.out.println("Table created successfully.");


            }
        } catch (SQLException e) {
            System.out.println("Table created unsuccessfully: " + e);
//            throw new RuntimeException("Error creating table: " + e.getMessage(), e);
        }
    }
}

