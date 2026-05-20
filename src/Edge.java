import java.util.*;

public class Edge {
    private int u;
    private int v;
    private int weight;
    private boolean IsDirected;

    public boolean getIsDirected() {
        return IsDirected;
    }

    public void setIsDirected(boolean isDirected) {
        IsDirected = isDirected;
    }

    public Edge(int u, int v, int weight, boolean isDirected) {
        this.u = u;
        this.v = v;
        this.weight = weight;
        IsDirected = isDirected;
    }

    public int getU() {
        return u;
    }

    public void setU(int u) {
        this.u = u;
    }

    public int getV() {
        return v;
    }

    public void setV(int v) {
        this.v = v;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }
}