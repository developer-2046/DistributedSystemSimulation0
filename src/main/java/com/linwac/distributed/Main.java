package com.linwac.distributed;

public class Main {
    public static void main(String[] args) {
        Node node1 = new Node("Node1");
        Node node2 = new Node("Node2");
        Node node3 = new Node("Node3");

        consistentHashing consistentHashing = new consistentHashing(3);
        consistentHashing.addNode(node1);
        consistentHashing.addNode(node2);
        consistentHashing.addNode(node3);

        int port1 = 5000;
        int port2 = 5001;
        int port3 = 5002;

        Thread node1Server = new Thread(new NodeServer(port1, node1));
        Thread node2Server = new Thread(new NodeServer(port2, node2));
        Thread node3Server = new Thread(new NodeServer(port3, node3));

        node1Server.start();
        node2Server.start();
        node3Server.start();

        // Wait for servers to start
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Example usage of consistent hashing
        String key = "exampleKey";
        Node responsibleNode = consistentHashing.getNode(key);
        if (responsibleNode != null) {
            System.out.println("Key '" + key + "' is assigned to node: " + responsibleNode.getNodeId());

            // Simulate putting data
            responsibleNode.put(key, "exampleValue");
            System.out.println("Data stored on node " + responsibleNode.getNodeId() + ": " + responsibleNode.get(key));
        } else {
            System.out.println("No node found for key: " + key);
        }
    }
}

