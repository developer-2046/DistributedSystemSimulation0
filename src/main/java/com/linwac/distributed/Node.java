package com.linwac.distributed;

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


}
