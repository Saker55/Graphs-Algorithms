import java.io.FileWriter;
import java.io.IOException;
import java.io.File;
import java.io.PrintWriter;

public class BenchmarkRunner {

    public static void main(String[] args) throws IOException {
        new File("results").mkdirs();

        BenchmarkInput input = new BenchmarkInput();
        int source = input.generateSource();

        {
            System.out.println("Benchmarking SPARSE...");
            Graph g = input.GenerateSparse();
            runMSTBenchmark("results/mstSPARSE.csv", g);
            runSSSPSingleBenchmark("results/ssspSPARSE.csv", "Sparse", g, source);
        }

        {
            System.out.println("Benchmarking DENSE...");
            Graph g = input.GenerateDense();
            runMSTBenchmark("results/mstDENSE.csv", g);
            runSSSPSingleBenchmark("results/ssspDENSE.csv", "Dense", g, source);
        }

        {
            System.out.println("Benchmarking COMPLETE...");
            Graph g = input.GenerateComplete();
            runMSTBenchmark("results/mstCOMPLETE.csv", g);
            runSSSPSingleBenchmark("results/ssspCOMPLETE.csv", "Complete", g, source);
        }

        {
            System.out.println("Benchmarking DAG...");
            Graph g = input.GenerateDAG();
            runSSSPDAGBenchmark("results/SSSPDAG.csv", g, source);
        }

        System.out.println("Done — check the results folder.");
    }

    // ── MST: Prim vs Kruskal ────────────────────────────────────────────────

    private static void runMSTBenchmark(String path, Graph g) throws IOException {
        long[] primTimes    = measure(5, () -> g.primMST());
        long[] kruskalTimes = measure(5, () -> g.kruskalMST());

        writeCSV(path, new String[]{"run", "primTime", "kruskalTime"},
                new long[][]{primTimes, kruskalTimes},
                new String[]{"Metric", "Prim", "Kruskal"});
    }

    // ── SSSP: Dijkstra on a single graph type ────────────────────────────────

    private static void runSSSPSingleBenchmark(String path, String label,
                                               Graph g, int source) throws IOException {
        long[] times = measure(5, () -> g.dijkstra(source));

        writeCSV(path, new String[]{"run", label + "Time"},
                new long[][]{times},
                new String[]{"Metric", label});
    }

    // ── SSSP DAG: Dijkstra vs DAG + speedup ─────────────────────────────────

    private static void runSSSPDAGBenchmark(String path, Graph g, int source)
            throws IOException {
        long[] dijkTimes = measure(5, () -> g.dijkstra(source));
        long[] dagTimes  = measure(5, () -> g.dagShortestPath(source));

        double[] dijkStats = stats(dijkTimes);
        double[] dagStats  = stats(dagTimes);

        try (PrintWriter pw = new PrintWriter(new FileWriter(path))) {
            pw.println("run,dijkstraTime,DAGTime,speedUp");
            for (int i = 0; i < dijkTimes.length; i++) {
                double d = toMicros(dijkTimes[i]);
                double a = toMicros(dagTimes[i]);
                pw.printf("%d,%.2f,%.2f,%.2f%n", i + 1, d, a, a > 0 ? d / a : 0);
            }
            pw.println();
            double meanSpeed = dagStats[0] > 0 ? dijkStats[0] / dagStats[0] : 0;
            double medSpeed  = dagStats[1] > 0 ? dijkStats[1] / dagStats[1] : 0;
            double stdSpeed  = stdOfRatio(dijkTimes, dagTimes);
            pw.println("Metric,Dijkstra,DAG,SpeedUp");
            pw.printf("Mean,%.2f,%.2f,%.2f%n",
                    toMicros(dijkStats[0]), toMicros(dagStats[0]), meanSpeed);
            pw.printf("Median,%.2f,%.2f,%.2f%n",
                    toMicros(dijkStats[1]), toMicros(dagStats[1]), medSpeed);
            pw.printf("Standard Deviation,%.2f,%.2f,%.2f%n",
                    toMicros(dijkStats[2]), toMicros(dagStats[2]), stdSpeed);
        }
        System.out.println("Written: " + path);
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    @FunctionalInterface
    interface Task { void run(); }

    /** Warm up 3 times, then record `runs` measurements (nanoseconds). */
    private static long[] measure(int runs, Task task) {
        for (int i = 0; i < 3; i++) task.run();          // warm-up
        long[] times = new long[runs];
        for (int i = 0; i < runs; i++) {
            long start = System.nanoTime();
            task.run();
            times[i] = System.nanoTime() - start;
        }
        return times;
    }

    /** Write a standard run-table + stats block. */
    private static void writeCSV(String path, String[] headers,
                                 long[][] columns, String[] statHeaders) throws IOException {
        int runs = columns[0].length;
        double[][] s = new double[columns.length][];
        for (int c = 0; c < columns.length; c++) s[c] = stats(columns[c]);

        try (PrintWriter pw = new PrintWriter(new FileWriter(path))) {
            pw.println(String.join(",", headers));
            for (int i = 0; i < runs; i++) {
                StringBuilder sb = new StringBuilder().append(i + 1);
                for (long[] col : columns) sb.append(",").append(String.format("%.2f", toMicros(col[i])));
                pw.println(sb);
            }
            pw.println();
            pw.println(String.join(",", statHeaders));
            String[] labels = {"Mean", "Median", "Standard Deviation"};
            for (int m = 0; m < 3; m++) {
                StringBuilder sb = new StringBuilder().append(labels[m]);
                for (double[] st : s) sb.append(",").append(String.format("%.2f", toMicros(st[m])));
                pw.println(sb);
            }
        }
        System.out.println("Written: " + path);
    }

    /** Returns [mean, median, stddev] in nanoseconds. */
    private static double[] stats(long[] times) {
        long[] sorted = times.clone();
        java.util.Arrays.sort(sorted);
        double mean = 0;
        for (long t : sorted) mean += t;
        mean /= sorted.length;
        double median = sorted[sorted.length / 2];
        double var = 0;
        for (long t : sorted) var += (t - mean) * (t - mean);
        return new double[]{mean, median, Math.sqrt(var / sorted.length)};
    }

    private static double toMicros(double ns) { return ns / 1_000.0; }

    private static double stdOfRatio(long[] a, long[] b) {
        double sum = 0;
        double[] r = new double[a.length];
        for (int i = 0; i < a.length; i++) { r[i] = b[i] > 0 ? (double) a[i] / b[i] : 0; sum += r[i]; }
        double mean = sum / r.length, var = 0;
        for (double x : r) var += (x - mean) * (x - mean);
        return Math.sqrt(var / r.length);
    }
}