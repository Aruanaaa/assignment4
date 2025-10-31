// src/main/java/graph/models/Node.java
package graph.models;

public class Node {
    private String id;
    private int duration;

    public Node() {}

    public Node(String id, int duration) {
        this.id = id;
        this.duration = duration;
    }

    // Геттеры и сеттеры
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public int getDuration() { return duration; }
    public void setDuration(int duration) { this.duration = duration; }

    @Override
    public String toString() {
        return id;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Node node = (Node) obj;
        return id.equals(node.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}