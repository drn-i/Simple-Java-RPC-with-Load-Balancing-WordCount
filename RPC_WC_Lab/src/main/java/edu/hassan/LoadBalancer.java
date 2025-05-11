package edu.hassan;

import java.io.*;
import java.net.*;

public class LoadBalancer {
    private static int lastServer = 0;
    private static final int[] SERVER_PORTS = {8080, 8081};

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(9090);
        System.out.println("Load Balancer started on 9090");

        while (true) {
            Socket clientSocket = serverSocket.accept();
            new Thread(() -> {
                try (
                        BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                        PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)
                ) {
                    String textChunk = in.readLine();
                    int serverPort = SERVER_PORTS[lastServer];
                    lastServer = (lastServer + 1) % SERVER_PORTS.length;

                    Socket serverSocketInternal = new Socket("localhost", serverPort);
                    PrintWriter serverOut = new PrintWriter(serverSocketInternal.getOutputStream(), true);
                    BufferedReader serverIn = new BufferedReader(new InputStreamReader(serverSocketInternal.getInputStream()));

                    serverOut.println(textChunk);
                    String response = serverIn.readLine();
                    out.println(response);

                    serverSocketInternal.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }
}