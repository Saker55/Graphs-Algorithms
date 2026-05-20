
import java.util.*;


public class GraphImp implements Graph{
    private List<Edge>[] adj ;
    private List<Edge> EdgeList;

    public GraphImp(int n) {
        this.adj = new List[n+1];
        for (int i = 0; i < adj.length; i++) {
            if (adj[i] == null) adj[i] = new ArrayList<>();
        }
        this.EdgeList = new ArrayList<>();
    }

    @Override
    public void addEdge(int u, int v, int weight) {
        this.adj[u].add(new Edge(u,v,weight,true));
        this.adj[v].add(new Edge(v,u,weight, true));
        this.EdgeList.add(new Edge(u,v,weight,true));
    }

    @Override
    public void addDirectedEdge(int u, int v, int weight) {
        this.adj[u].add(new Edge(u,v,weight, false));
        this.EdgeList.add(new Edge(u,v,weight,false));
    }

    @Override
    public List<Edge> primMST() {
        List<Edge> mst = new ArrayList<>();
        boolean[] visited = new boolean[adj.length + 1];
        PriorityQueue<Edge> pq = new PriorityQueue<>(Comparator.comparingInt(Edge::getWeight));
        visited[1] = true;
        pq.addAll(adj[1]);
        while (!pq.isEmpty() && mst.size() < adj.length - 2) {
            Edge current = pq.remove();
            int v = current.getV();
            if (visited[v]) {
                continue;
            }
            visited[v] = true;
            mst.add(current);
            for (Edge next : adj[v]) {
                if (!visited[next.getV()]) {
                    pq.add(next);
                }
            }
        }
        return mst;
    }



    @Override
    public List<Edge> kruskalMST() {
        DSU DSU = new DSU(adj.length);
        PriorityQueue<Edge> pq = new PriorityQueue<>(Comparator.comparingInt(Edge::getWeight));
        pq.addAll(EdgeList);
        List<Edge> sol = new ArrayList<>();
        while(!pq.isEmpty() && sol.size() < adj.length - 2){
            Edge current = pq.remove();
            if (DSU.find(current.getU()) != DSU.find(current.getV())){
                sol.add(current);
                DSU.union(current.getU(), current.getV());
            }
        }

        return sol;
    }

    @Override
    public int[] dijkstra(int source) {
        int[] sol = new int[adj.length+1];
        Arrays.fill(sol, Integer.MAX_VALUE);
        sol[source] = 0;
        PriorityQueue<pair> pq = new PriorityQueue<>(Comparator.comparingInt(pair::getWeight));
        pq.add(new pair(0, source));
        while (!pq.isEmpty()) {
            pair curr = pq.remove();
            int u = curr.getV();

            if (curr.getWeight() > sol[u]) {
                continue;
            }

            for (Edge edge : adj[u]) {
                int v = edge.getV();
                int w = edge.getWeight();
                if (sol[u] + w < sol[v]) {
                    sol[v] = sol[u] + w;
                    pq.add(new pair(sol[v], v));
                }
            }
        }

        return sol;
    }

    @Override
    public int[] dagShortestPath(int source) {
        int[] visited = new int[adj.length+1];
        Stack<Integer> stack = new Stack<>();
        int[] sol = new int[adj.length+1];
        Arrays.fill(sol, Integer.MAX_VALUE);
        dfs(source,stack,visited);


        sol[source] = 0;

        while(!stack.isEmpty()){
            int current = stack.pop();

            if (sol[current] == Integer.MAX_VALUE){
                continue;
            }

            for (Edge edge : adj[current]) {
                int v = edge.getV();
                int w = edge.getWeight();
                if (sol[current] + w < sol[v]) {
                    sol[v] = sol[current] + w;
                }
            }
        }
        return sol;
    }

    private void dfs (int current, Stack<Integer> stack, int[] visited){
        visited[current] = 1;

        for (int i = 0; i < adj[current].size(); i++)
        {
            if (visited[adj[current].get(i).getV()] == 0)
            {
                dfs(adj[current].get(i).getV(),stack ,visited);
            } else if (visited[adj[current].get(i).getV()] == 1) {
                throw new IllegalStateException("Graph contains a cycle");
            }
        }
        stack.push(current);
        visited[current] = 2;
    }
}