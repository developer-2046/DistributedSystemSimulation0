package com.linwac.distributed;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class NodeHandler implements Runnable {
    private final Socket socket; //Client Socket
    private final Node node;
    public NodeHandler(Socket socket, Node node) {
        this.socket = socket;
        this.node = node;
    }
    @Override
    public void run() {
        try(BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter output = new PrintWriter(socket.getOutputStream(), true)){
            String request = input.readLine();
            String[] parts = request.split(" ");
            String command = parts[0];
            String key = parts[1];
            String response;

            switch (command) {
                case "PUT":
                String value = parts[2];
                node.put(key, value);
                response = "OK";
                break;
                case "GET":
                    response = node.get(key);
                    if (response == null) {
                        response = "NOT_FOUND";
                        break;
                    }
                default:
                    response = "ERROR";
            }
            output.println(response);
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }


}
