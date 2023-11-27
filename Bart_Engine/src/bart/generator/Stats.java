package bart.generator;

import java.util.Arrays;
import java.util.List;

public class Stats {

    private List<String> algorithms;
    private List<Integer> sizes;
    private Long[][] times;

    public Stats(String[] algorithms, Integer[] sizes) {
        this.algorithms = Arrays.asList(algorithms);
        this.sizes = Arrays.asList(sizes);
        this.times = new Long[algorithms.length][sizes.length];
    }

    public void addExperiment(String algorithm, int size, long time) {
        int row = algorithms.indexOf(algorithm);
        int col = sizes.indexOf(size);
        if (row == -1 || col == -1) {
            throw new IllegalArgumentException("Experiment unknown " + algorithm + ", " + size);
        }
        times[row][col] = time;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Sizes\t");
        for (Integer size : sizes) {
            sb.append(size).append("\t");
        }
        sb.append("\n");
        for (int i = 0; i < algorithms.size(); i++) {
            sb.append(algorithms.get(i)).append("\t");
            for (int j = 0; j < sizes.size(); j++) {
                Long time = times[i][j];
                sb.append((time == null ? "" : time)).append("\t");
            }
            sb.append("\n");
        }
        return sb.toString();
    }

}
