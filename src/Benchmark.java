public class Benchmark {

        public long[] MeasurePrim(Graph g) {
            long[] times = new long[5];

            for (int i = 0; i < 8; i++) {
                long start = System.nanoTime();
                g.primMST();
                if (i >= 3) times[i - 3] = System.nanoTime() - start;
            }
            return times;
        }

        public long[] MeasureKruskal(Graph g) {
            long[] times = new long[5];

            for (int i = 0; i < 8; i++) {
                long start = System.nanoTime();
                g.kruskalMST();
                if (i >= 3) times[i - 3] = System.nanoTime() - start;
            }
            return times;
        }

        public long[] MeasureDijkstra(Graph g,int source) {
            long[] times = new long[5];
            for (int i = 0; i < 8; i++) {
                long start = System.nanoTime();
                g.dijkstra(source);
                if (i >= 3) times[i - 3] = System.nanoTime() - start;
            }
            return times;
        }

    public long[] MeasureDAG(Graph g,int source) {
        long[] times = new long[5];
        for (int i = 0; i < 8; i++) {
            long start = System.nanoTime();
            g.dagShortestPath(source);
            if (i >= 3) times[i - 3] = System.nanoTime() - start;
        }
        return times;
    }


    }

