package org.wale;

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter server IP address: ");
        String serverIp = scanner.nextLine();

        Socket socket = new Socket(serverIp, 1234);
        System.out.println("Connected to server: " + socket);

        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);

        new Thread(() -> {
            try {
                while (true) {
                    String message = reader.readLine();
                    if (message == null) {
                        break;
                    }
                    System.out.println(message);
                }
            } catch (IOException e) {
                System.out.println("Error reading from server: " + e.getMessage());
            }
        }).start();

        //System.out.print("Enter your name: ");
        String name = scanner.nextLine();
        writer.println(name);

        while (true) {
            String message = scanner.nextLine();
            writer.println(message);
        }
    }
}

