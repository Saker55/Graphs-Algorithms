import java.util.Random;

public class BenchmarkInput {
    private final Random generator = new Random(5);
    private final int GraphSize = 5000;
    private final int WeightBound = 1000;

    public Graph GenerateSparse(){
        Graph g = new GraphImp(GraphSize);
        for (int i = 2; i <= GraphSize; i++) {
            int u = generator.nextInt(i-1)+1;
            int w = generator.nextInt(WeightBound)+1;
            g.addEdge(u,i,w);
        }

        for (int i = 0; i < (4 * GraphSize)+1; i++) {
            int u = generator.nextInt(GraphSize)+1;
            int v = generator.nextInt(GraphSize)+1;
            int w = generator.nextInt(WeightBound)+1;
            g.addEdge(u,v,w);
        }

        return g;
    }

    public Graph GenerateDense(){

        Graph g = new GraphImp(GraphSize);
        for (int i = 2; i <= GraphSize; i++) {
            int u = generator.nextInt(i-1)+1;
            int w = generator.nextInt(WeightBound)+1;
            g.addEdge(u,i,w);
        }

        int n = (((GraphSize -1) * GraphSize)/8) - GraphSize + 1;

        for (int i = 0; i < n; i++) {
            int u = generator.nextInt(GraphSize)+1;
            int v = generator.nextInt(GraphSize)+1;
            int w = generator.nextInt(WeightBound)+1;
            g.addEdge(u,v,w);
        }

        return g;
    }

    public Graph GenerateComplete(){
        Graph g = new GraphImp(GraphSize);
        for (int i = 1; i <= GraphSize; i++) {
            for (int j = i+1; j <= GraphSize; j++) {
                int w = generator.nextInt(WeightBound)+1;
                g.addEdge(i,j,w);
            }
        }
        return g;
    }


    public Graph GenerateDAG(){
        int counter = 0;
        Graph g = new GraphImp(GraphSize);
        for (int i = 2; i <= GraphSize; i++) {
            int w = generator.nextInt(WeightBound)+1;
            g.addDirectedEdge(i-1,i,w);
            counter++;
        }

        while (counter < (5 * GraphSize)) {
            int len = 2;
            int u = generator.nextInt(GraphSize) + 1;
            for (int i = u; i <= GraphSize -len; i++) {
                int w = generator.nextInt(WeightBound) + 1;
                g.addDirectedEdge(u, u+len, w);
                counter++;
            }
        }


        return g;
    }

    public int generateSource(){
        return generator.nextInt(GraphSize) + 1;
    }
}
