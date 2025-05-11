package edu.hassan;

import java.io.*;
import java.net.*;
import java.nio.file.*;
import java.util.*;

public class RPC_Client {
    private static Map<String, Integer> parseResult(String response) {
        Map<String, Integer> map = new HashMap<>();
        if (response != null && !response.isEmpty()) {
            String[] pairs = response.split(",");
            for (String pair : pairs) {
                if (pair.isEmpty()) continue;
                String[] kv = pair.split(":");
                if (kv.length == 2)
                    map.put(kv[0], Integer.parseInt(kv[1]));
            }
        }
        return map;
    }

    private static Map<String, Integer> mergeMaps(Map<String, Integer> a, Map<String, Integer> b) {
        Map<String, Integer> result = new HashMap<>(a);
        for (Map.Entry<String, Integer> entry : b.entrySet()) {
            result.put(entry.getKey(), result.getOrDefault(entry.getKey(), 0) + entry.getValue());
        }
        return result;
    }

    public static void main(String[] args) throws IOException {
        String filePath = (args.length > 0) ? args[0] : "sample.txt";
        List<String> lines = Files.readAllLines(Paths.get(filePath));

        //Before splitting
        System.out.println("This the input file before splitting");
        System.out.println(lines);
        System.out.println("---------------------------");
        System.out.println("");

        //Here split the input into two half parts
        List<String> firstHalfLines = new ArrayList<>();
        List<String> secondHalfLines = new ArrayList<>();

        for (String line : lines) {
            String[] words = line.trim().split("\\s+");
            int half = words.length / 2;
            String firstHalf = String.join(" ", Arrays.copyOfRange(words, 0, half));
            String secondHalf = String.join(" ", Arrays.copyOfRange(words, half, words.length));
            firstHalfLines.add(firstHalf);
            secondHalfLines.add(secondHalf);
        }

        String part1 = String.join(" ", firstHalfLines);
        String part2 = String.join(" ", secondHalfLines);

//        int mid = lines.size() / 2;
//        String part1 = String.join(" ", lines.subList(0, mid));
//        String part2 = String.join(" ", lines.subList(mid, lines.size()));

        System.out.println("The first Part");
        System.out.println(part1);
        System.out.println("---------------------------");
        System.out.println("");

        System.out.println("The Second Part");
        System.out.println(part2);
        System.out.println("---------------------------");
        System.out.println("");

        Map<String, Integer> result1 = sendChunk(part1, "localhost", 8080);

        Map<String, Integer> result2 = sendChunk(part2, "localhost", 8081);

        Map<String, Integer> finalResult = mergeMaps(result1, result2);

        System.out.println("Server 1 (Part 1) Results:");
        for (Map.Entry<String, Integer> entry : result1.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }

        System.out.println("---------------------------");

        System.out.println("Server 2 (Part 2) Results:");
        for (Map.Entry<String, Integer> entry : result2.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }

        System.out.println("---------------------------");

        System.out.println("All WordCount Results:");
        for (Map.Entry<String, Integer> entry : finalResult.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
    }

    private static Map<String, Integer> sendChunk(String chunk, String host, int port) throws IOException {
        try (
                Socket socket = new Socket(host, port);
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))
        ) {
            out.println(chunk);
            String response = in.readLine();
            return parseResult(response);
        }
    }
}