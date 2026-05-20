
import static java.util.Arrays.sort;

public class BenchmarkData {
    public static double[] makeAnalsis(long[] times) {
        double[] data = new double[3]; // {mean , median , standerd deviation}
        sort(times);
        data[0]= 0;
        for (int i = 0; i < times.length; i++) {
            data[0]+=times[i];
        }
        data[0] = data[0]/times.length;
        data[1] = times[(times.length/2)+1];
        data[2]= 0;
        for (int i = 0; i < times.length; i++) {
            data[2]=data[2]+((times[i] - data[0]) * (times[i] - data[0]));
        }
        data[2] = Math.sqrt(data[2]/ times.length);
        return data;
    }
}

