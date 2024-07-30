package com.linwac.distributed;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class Node {
    private final String nodeId;
    private final Map<String, String> dataStore;

    public Node(String nodeId) {
        this.nodeId = nodeId;
        this.dataStore = new HashMap<>();
    }

    public String getNodeId() {
        return nodeId;
    }
    public void put(String key, String value) {
        dataStore.put(key, value);
    }
    public String get(String key) {
        return dataStore.get(key);
    }

    public String sendRequest(String host, int port, String request) {
        try(Socket socket = new Socket(host, port);
  PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream())
        )){
            out.println(request);
            return in.readLine();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
