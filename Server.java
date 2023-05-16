package org.wale;


import java.io.*;
import java.net.*;
import java.util.*;

public class Server {
    private static ArrayList<ClientHandler> clients = new ArrayList<>();

    public static void main(String[] args) throws IOException {
        ServerSocket server = new ServerSocket(1234);
        System.out.println("Server started on port 1234.");

        while (true) {
            Socket socket = server.accept();
            System.out.println("New client connected: " + socket);

            ClientHandler clientHandler = new ClientHandler(socket);
            clients.add(clientHandler);
            clientHandler.start();
        }
    }

    static class ClientHandler extends Thread {
        private String name;
        private Socket socket;
        private BufferedReader reader;
        private PrintWriter writer;

        public ClientHandler(Socket socket) throws IOException {
            this.socket = socket;
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new PrintWriter(socket.getOutputStream(), true);
            writer.println("Enter your name:");
            name = reader.readLine();
            writer.println("Welcome to the chat, " + name + "!");
            broadcast(name + " has joined the chat.");
        }

        public void run() {
            try {
                while (true) {
                    String message = reader.readLine();
                    if (message == null) {
                        return;
                    }

                    System.out.println("Received message from " + name + ": " + message);

                    broadcast(name + ": " + message);
                }
            } catch (IOException e) {
                System.out.println("Error in client handler: " + e.getMessage());

            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    System.out.println("Error closing socket: " + e.getMessage());
                }

                clients.remove(this);
                broadcast(name + " has left the chat.");
            }
        }

        private void broadcast(String message) {
            for (ClientHandler client : clients) {
                if (client != this) {
                    client.writer.println(message);
                }
            }
        }
    }
}
