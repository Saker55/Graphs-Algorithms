import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;

import static org.testng.Assert.assertEquals;

public class JunitTests {


    private int totalWeight(List<Edge> edges) {
        return edges.stream().mapToInt(Edge::getWeight).sum();
    }


    // addEdge / addDirectedEdge


    @Test
    public void testAddEdgeUndirected() {
        GraphImp g = new GraphImp(3);
        g.addEdge(1, 2, 5);
        int[] fromOne = g.dijkstra(1);
        assertEquals(fromOne[2], 5, "dist 1->2 must be 5");
        int[] fromTwo = g.dijkstra(2);
        assertEquals(fromTwo[1], 5, "dist 2->1 must be 5 (undirected)");
    }

    @Test
    public void testAddDirectedEdgeOneWay() {
        GraphImp g = new GraphImp(3);
        g.addDirectedEdge(1, 2, 7);
        int[] fromOne = g.dijkstra(1);
        assertEquals(fromOne[2], 7, "directed edge 1->2 weight 7");
        int[] fromTwo = g.dijkstra(2);
        assertEquals(fromTwo[1], Integer.MAX_VALUE, "no reverse edge 2->1");
    }


    // Prim MST


    @Test
    public void testPrimSimpleTriangle() {
        GraphImp g = new GraphImp(3);
        g.addEdge(1, 2, 1);
        g.addEdge(2, 3, 2);
        g.addEdge(1, 3, 10);
        List<Edge> mst = g.primMST();
        assertEquals(mst.size(), 2, "MST of 3-node graph has 2 edges");
        assertEquals(totalWeight(mst), 3, "MST weight 1+2=3");
    }

    @Test
    public void testPrimFourNodes() {
        // Edges: (1-2,4),(1-3,2),(2-3,5),(2-4,1),(3-4,3)
        // MST:   1-3(2) + 3-4(3) + 2-4(1) = 6
        GraphImp g = new GraphImp(4);
        g.addEdge(1, 2, 4);
        g.addEdge(1, 3, 2);
        g.addEdge(2, 3, 5);
        g.addEdge(2, 4, 1);
        g.addEdge(3, 4, 3);
        List<Edge> mst = g.primMST();
        assertEquals(mst.size(), 3, "MST of 4-node graph has 3 edges");
        assertEquals(totalWeight(mst), 6, "MST weight: 2+3+1=6");
    }

    @Test
    public void testPrimSingleNode() {
        GraphImp g = new GraphImp(1);
        assertTrue(g.primMST().isEmpty(), "No MST edges for a single node");
    }

    @Test
    public void testPrimTwoNodes() {
        GraphImp g = new GraphImp(2);
        g.addEdge(1, 2, 9);
        List<Edge> mst = g.primMST();
        assertEquals(mst.size(), 1);
        assertEquals(totalWeight(mst), 9);
    }


    // Kruskal MST


    @Test
    public void testKruskalFourNodes() {
        // Same graph as Prim test; MST weight = 1+2+3 = 6
        GraphImp g = new GraphImp(4);
        g.addEdge(1, 2, 4);
        g.addEdge(1, 3, 2);
        g.addEdge(2, 3, 5);
        g.addEdge(2, 4, 1);
        g.addEdge(3, 4, 3);
        List<Edge> mst = g.kruskalMST();
        assertEquals(mst.size(), 3, "MST of 4-node graph has 3 edges");
        assertEquals(totalWeight(mst), 6, "Kruskal MST weight must be 6");
    }

    @Test
    public void testKruskalTriangle() {
        GraphImp g = new GraphImp(3);
        g.addEdge(1, 2, 1);
        g.addEdge(2, 3, 2);
        g.addEdge(1, 3, 10);
        List<Edge> mst = g.kruskalMST();
        assertEquals(mst.size(), 2);
        assertEquals(totalWeight(mst), 3);
    }

    @Test
    public void testKruskalSingleNode() {
        assertTrue(new GraphImp(1).kruskalMST().isEmpty());
    }

    @Test
    public void testKruskalPrimAgreement() {
        // Edges: (1-2,2),(1-3,3),(2-3,1),(2-4,4),(3-5,5),(4-5,6)
        // MST (Kruskal): (2-3,1)+(1-2,2)+(2-4,4)+(3-5,5) = 12
        int[][] edges = {{1,2,2},{1,3,3},{2,3,1},{2,4,4},{3,5,5},{4,5,6}};
        GraphImp gPrim    = new GraphImp(5);
        GraphImp gKruskal = new GraphImp(5);
        for (int[] e : edges) {
            gPrim.addEdge(e[0], e[1], e[2]);
            gKruskal.addEdge(e[0], e[1], e[2]);
        }
        int wPrim    = totalWeight(gPrim.primMST());
        int wKruskal = totalWeight(gKruskal.kruskalMST());
        assertEquals(wPrim, wKruskal, "Prim and Kruskal must yield the same MST weight");
    }


    // Dijkstra


    @Test
    public void testDijkstraSourceIsZero() {
        GraphImp g = new GraphImp(3);
        g.addEdge(1, 2, 5);
        g.addEdge(2, 3, 3);
        assertEquals(g.dijkstra(1)[1], 0);
    }

    @Test
    public void testDijkstraMultiHop() {
        GraphImp g = new GraphImp(4);
        g.addDirectedEdge(1, 2, 1);
        g.addDirectedEdge(2, 3, 2);
        g.addDirectedEdge(1, 3, 10);
        g.addDirectedEdge(3, 4, 1);
        int[] dist = g.dijkstra(1);
        assertEquals(dist[2], 1,  "1->2 = 1");
        assertEquals(dist[3], 3,  "1->3 via 2 = 3, not 10");
        assertEquals(dist[4], 4,  "1->4 via 2->3 = 4");
    }

    @Test
    public void testDijkstraUnreachable() {
        GraphImp g = new GraphImp(3);
        g.addDirectedEdge(1, 2, 5);
        // node 3 is isolated
        assertEquals(g.dijkstra(1)[3], Integer.MAX_VALUE);
    }

    @Test
    public void testDijkstraCheaperIndirectPath() {
        GraphImp g = new GraphImp(3);
        g.addDirectedEdge(1, 3, 100);
        g.addDirectedEdge(1, 2, 1);
        g.addDirectedEdge(2, 3, 1);
        assertEquals(g.dijkstra(1)[3], 2);
    }

    @Test
    public void testDijkstraSymmetricUndirected() {
        GraphImp g = new GraphImp(3);
        g.addEdge(1, 2, 4);
        g.addEdge(2, 3, 6);
        int[] fromOne   = g.dijkstra(1);
        int[] fromThree = g.dijkstra(3);
        assertEquals(fromOne[3], fromThree[1],
                "Undirected: dist(1,3) == dist(3,1)");
    }


    // DAG Shortest Path


    @Test
    public void testDagSourceIsZero() {
        GraphImp g = new GraphImp(3);
        g.addDirectedEdge(1, 2, 3);
        g.addDirectedEdge(2, 3, 2);
        assertEquals(g.dagShortestPath(1)[1], 0);
    }

    @Test
    public void testDagLinearChain() {
        GraphImp g = new GraphImp(4);
        g.addDirectedEdge(1, 2, 2);
        g.addDirectedEdge(2, 3, 3);
        g.addDirectedEdge(3, 4, 4);
        int[] dist = g.dagShortestPath(1);
        assertEquals(dist[2], 2);
        assertEquals(dist[3], 5);
        assertEquals(dist[4], 9);
    }

    @Test
    public void testDagMultiplePaths() {
        // 1->2->4 = 1+3 = 4
        // 1->3->4 = 4+1 = 5
        GraphImp g = new GraphImp(4);
        g.addDirectedEdge(1, 2, 1);
        g.addDirectedEdge(1, 3, 4);
        g.addDirectedEdge(2, 4, 3);
        g.addDirectedEdge(3, 4, 1);
        int[] dist = g.dagShortestPath(1);
        assertEquals(dist[4], 4, "Shortest path to 4 should be 4 via 1->2->4");
    }

    @Test
    public void testDagUnreachable() {
        GraphImp g = new GraphImp(3);
        g.addDirectedEdge(1, 2, 5);
        assertEquals(g.dagShortestPath(1)[3], Integer.MAX_VALUE);
    }

    @Test
    public void testDagThrowsOnCycle() {
        GraphImp g = new GraphImp(3);
        g.addDirectedEdge(1, 2, 1);
        g.addDirectedEdge(2, 3, 1);
        g.addDirectedEdge(3, 1, 1); // back-edge creates a cycle
        try {
            g.dagShortestPath(1);
            fail("Expected IllegalStateException for cyclic graph");
        } catch (IllegalStateException e) {
            // test passes
        }
    }

    @Test
    public void testDagSingleNode() {
        GraphImp g = new GraphImp(1);
        assertEquals(g.dagShortestPath(1)[1], 0);
    }
    }
