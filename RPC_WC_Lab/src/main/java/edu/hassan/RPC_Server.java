package edu.hassan;

import java.io.*;
import java.net.*;
import java.util.Map;

public class RPC_Server {
    public static void main(String[] args) throws IOException {
        int port = Integer.parseInt(args[0]);
        WordCountServiceImpl service = new WordCountServiceImpl();
        ServerSocket serverSocket = new ServerSocket(port);
        System.out.println("Server started on port " + port);

        while (true) {
            Socket socket = serverSocket.accept();
            new Thread(() -> {
                try (
                        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                        PrintWriter out = new PrintWriter(socket.getOutputStream(), true)
                ) {
                    String textChunk = in.readLine();
//
                    System.out.println("[" + java.time.LocalDateTime.now() + "] A Request received from port: "
                            + socket.getInetAddress().getHostAddress() + ":" + socket.getPort());
                    System.out.println("The text request received: " +
                            (textChunk != null ? textChunk.substring(0, Math.min(100, textChunk.length())) : "null"));
                    System.out.println("");

                    Map<String, Integer> result = service.countWords(textChunk);
                    StringBuilder sb = new StringBuilder();
                    for (Map.Entry<String, Integer> entry : result.entrySet()) {
                        sb.append(entry.getKey()).append(":").append(entry.getValue()).append(",");
                    }
                    out.println(sb.toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }
}