# Distributed-System Simulation (Java + Sockets)

*A minimal, hack-friendly playground that shows how consistent hashing, node replication and request routing work under the hood.*

> **What this repo is** – a <1 kLOC Java console project. It spins up three local “nodes”, arranges them on a consistent-hash ring, and lets you `PUT` / `GET` key-value pairs over plain TCP sockets. The goal is to illustrate classic distributed-systems ideas (sharding, replication factor, node failure) without heavyweight frameworks.
>
> **What it is *not*** – a production datastore. There is no persistence, no quorum, no gossip. It’s purposely tiny so you can set break-points and watch the algorithm work.

---

## 0 · Project Tree

```text
DistributedSystemSimulation/
├─ pom.xml                          # Maven build – JDK 17, no external deps
└─ src/main/java/com/linwac/distributed/
   ├─ Main.java                     # bootstrap – starts 3 nodes + servers
   ├─ ConsistentHashing.java        # ring logic, virtual replicas
   ├─ Node.java                     # in-memory KV store, basic API
   ├─ NodeServer.java               # listens on port, hands sockets to handler
   ├─ NodeHandler.java              # parses "GET key" | "PUT key value"
   └─ Client.java                   # (stub) — suggested spot for future CLI
```

### Key classes

| class                  | highlight                                                                                                     |
| ---------------------- | ------------------------------------------------------------------------------------------------------------- |
| **ConsistentHashing**  | `addNode()`, `getNode(key)` —  virtual-node hash ring (MD5 integer hash)                                      |
| **Node**               | simple `Map<String,String>` store; helper to forward a request to its chosen peer if it isn’t the owner       |
| **NodeServer/Handler** | one thread per socket; uses `ExecutorService` for concurrency                                                 |
| **Main**               | constructs three `Node`s, adds each to ring with `replicaNumbers = 3`, then spawns servers on ports 5000–5002 |

---

## 1 · Building & Running

```bash
# clone & launch
$ git clone https://github.com/<you>/DistributedSystemSimulation.git
$ cd DistributedSystemSimulation
$ ./mvnw package
$ java -jar target/DistributedSystemSimulation-1.0-SNAPSHOT.jar
```

The program prints:

```
Node: Node1 listening on 5000
Node: Node2 listening on 5001
Node: Node3 listening on 5002
```

### Quick manual test (using netcat)

```bash
# put a KV pair
$ printf "PUT color blue\n" | nc localhost 5001
OK stored on Node2

# retrieve it (the ring may route to a different node)
$ printf "GET color\n" | nc localhost 5000
blue (from Node2)
```

Because the key-to-node mapping is deterministic, any port will forward the request to the correct owner.

---

## 2 · How it works (code walk-through)

1. **Consistent-hash ring**

   ```java
   int hash = key.hashCode() & 0x7fffffff;   // map to non-negative int
   SortedMap<Integer, Node> tail = ring.tailMap(hash);
   return tail.isEmpty() ? ring.get(ring.firstKey()) : ring.get(tail.firstKey());
   ```

   With `replicaNumbers = 3`, each physical node owns three equally spaced points, smoothing hotspot risk when nodes join/leave.

2. **Server loop** – `NodeServer.run()`

   ```java
   while(true) {
       Socket sock = serverSocket.accept();
       exec.submit(new NodeHandler(sock, node));
   }
   ```

   Cached thread-pool keeps the demo responsive under many clients.

3. **Command parsing** – `NodeHandler.run()`

   * splits input on spaces
   * `PUT key value` → `owner.put(key, value)`
   * `GET key` → `owner.get(key)` (may forward via `Socket` if owner ≠ current)

4. **Forwarding** – `Node.forward(String host,int port, String rawRequest)`
   (inside `Node` helper) uses a short-lived socket to the peer node.

---

## 3 · Extending the playground

| idea                      | where to start                                                                      |
| ------------------------- | ----------------------------------------------------------------------------------- |
| **Replication factor >1** | add `Map<String,List<String>>` in `Node` and write‐ahead to second/third successors |
| **Gossip membership**     | swap static `Main` node list for multicast heartbeats                               |
| **Failure injection**     | kill `NodeServer` thread; observe `getNode()` remap                                 |
| **Persistent storage**    | replace `HashMap` with RocksDB or simple file log                                   |
| **Client CLI**            | flesh out `Client.java` with readline + retry logic                                 |

---

## 4 · Interview talking points

* Explain how consistent hashing minimizes re-sharding cost (`O(1/n)` keys move).
* Discuss CAP trade-offs; this demo is CP (strong consistency, no partition tolerance).
* Outline how replication + quorum (`W+R>N`) would shift it towards AP.

---

## 5 · License

MIT © Yuvraj Malik — hack away.
