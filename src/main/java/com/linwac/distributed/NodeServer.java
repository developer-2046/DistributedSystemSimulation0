package com.linwac.distributed;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NodeServer implements Runnable {

    private final int port;
    private final Node node;
    private final ExecutorService exec;

    public NodeServer(int port, Node node) {
        this.port = port;
        this.node = node;
        this.exec = Executors.newCachedThreadPool();
    }

    @Override
    public void run() {
        try(ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Node: " + node.getNodeId() + " is listening on port " + port);
            while (true) {
                Socket socket = serverSocket.accept();
                exec.submit(new NodeHandler(socket, node));

            }

        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

}
