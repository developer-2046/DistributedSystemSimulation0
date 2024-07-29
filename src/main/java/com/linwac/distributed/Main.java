package com.linwac.distributed;
import java.util.Map;
public class Main {

    public static void main(String[] args) {
        Node node1 = new Node("node1");
        Node node2 = new Node("node2");

        node1.put("key1", "value1");
        System.out.println(node1.get("key1"));

    }



}
