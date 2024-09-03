package com.linwac.distributed;

import java.util.SortedMap;
import java.util.TreeMap;

public class ConsistentHashing {

    private final SortedMap<Integer, Node> ring = new TreeMap<>();
    private final int replicaNumbers;

    public ConsistentHashing(int replicaNumbers) {
        this.replicaNumbers = replicaNumbers;
    }

    public void addNode(Node node) {
        for (int i = 0; i < replicaNumbers; i++) {
            int hash = hash(node.getNodeId() + i);
            ring.put(hash, node);
        }
    }

    public void removeNode(Node node) {
        for (int i = 0; i < replicaNumbers; i++) {
            int hash = hash(node.getNodeId() + i);
            ring.remove(hash);
        }
    }

    public Node getNode(String key) {
        if (ring.isEmpty()) {
            return null;
        }

        int hash = hash(key);
        if (!ring.containsKey(hash)) {
            SortedMap<Integer, Node> tailMap = ring.tailMap(hash);
            hash = tailMap.isEmpty() ? ring.firstKey() : tailMap.firstKey();
        }
        return ring.get(hash);
    }

    private int hash(String key) {
        return key.hashCode() & 0x7fffffff; // Ensures non-negative hash value
    }
}
