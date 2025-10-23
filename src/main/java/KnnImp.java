import converter.Register;

import java.util.*;
import java.util.logging.Logger;

public class KnnImp {
    private static final Logger log = Logger.getLogger(KnnImp.class.getName());

    public double manhattanDistance(double[] a, double[] b) {
        double sum = 0.0;
        for (int i = 0; i < a.length; i++) {
            sum += Math.abs(a[i] - b[i]);
        }
        return sum;
    }

    public double euclideanDistance(double[] a, double[] b) {
        double sum = 0.0;
        for (int i = 0; i < a.length; i++) {
            double d = a[i] - b[i];
            sum += d * d;
        }
        return Math.sqrt(sum);
    }

    public double minkowskiDistance(double[] a, double[] b, int p) {
        double sum = 0.0;
        for (int i = 0; i < a.length; i++) {
            sum += Math.pow(Math.abs(a[i] - b[i]), p);
        }
        return Math.pow(sum, 1.0 / p);
    }

    // Mahalanobis aproximada com matriz de covariância diagonal (variância por feature)
    public double mahalanobisDistanceApprox(double[] a, double[] b, double[] variances) {
        double sum = 0.0;
        for (int i = 0; i < a.length; i++) {
            double var = variances[i] > 1e-12 ? variances[i] : 1e-12; // evitar divisão por zero
            double d = a[i] - b[i];
            sum += (d * d) / var;
        }
        return Math.sqrt(sum);
    }

    private static class Neighbor {
        final double distance;
        final int label;
        Neighbor(double distance, int label) { this.distance = distance; this.label = label; }
    }

    public static Object[] calculateMetricsForK(int k, String metricName,
                                                 List<Register> trainData,
                                                 List<Register> testData,
                                                 KnnImp knn) {
        long startTime = System.currentTimeMillis();

        // Preparar variâncias para Mahalanobis aproximada (com base no treino)
        double[] variances = null;
        if (metricName.equalsIgnoreCase("Mahalanobis") && !trainData.isEmpty()) {
            int dims = trainData.get(0).getFeatures().length;
            double[] means = new double[dims];
            for (Register r : trainData) {
                double[] f = r.getFeatures();
                for (int i = 0; i < dims; i++) means[i] += f[i];
            }
            for (int i = 0; i < dims; i++) means[i] /= trainData.size();
            variances = new double[dims];
            for (Register r : trainData) {
                double[] f = r.getFeatures();
                for (int i = 0; i < dims; i++) {
                    double d = f[i] - means[i];
                    variances[i] += d * d;
                }
            }
            for (int i = 0; i < dims; i++) variances[i] = Math.max(variances[i] / Math.max(1, trainData.size() - 1), 1e-12);
        }

        int numClasses = inferNumClasses(trainData, testData);
        int[][] confusion = new int[numClasses][numClasses]; // [true][pred]
        int errors = 0;

        for (Register test : testData) {
            double[] tf = test.getFeatures();
            int trueLabel = (int) test.getTarget()[0];

            // Top-K via heap
            PriorityQueue<Neighbor> pq = new PriorityQueue<>(Comparator.comparingDouble(n -> -n.distance)); // max-heap
            for (Register train : trainData) {
                double d;
                double[] trf = train.getFeatures();
                switch (metricName.toLowerCase()) {
                    case "manhattan" -> d = knn.manhattanDistance(tf, trf);
                    case "euclidiana" -> d = knn.euclideanDistance(tf, trf);
                    case "minkowski" -> d = knn.minkowskiDistance(tf, trf, 3);
                    case "mahalanobis" -> d = knn.mahalanobisDistanceApprox(tf, trf, variances);
                    default -> d = knn.euclideanDistance(tf, trf);
                }
                int label = (int) train.getTarget()[0];
                if (pq.size() < k) {
                    pq.offer(new Neighbor(d, label));
                } else if (d < pq.peek().distance) {
                    pq.poll();
                    pq.offer(new Neighbor(d, label));
                }
            }

            // votação
            int[] votes = new int[numClasses];
            for (Neighbor n : pq) votes[n.label]++;
            int pred = 0; int max = votes[0];
            for (int c = 1; c < numClasses; c++) {
                if (votes[c] > max) { max = votes[c]; pred = c; }
            }

            confusion[trueLabel][pred]++;
            if (pred != trueLabel) errors++;
        }

        int total = testData.size();
        int correct = 0;
        for (int c = 0; c < numClasses; c++) correct += confusion[c][c];
        double accuracy = total > 0 ? (correct * 100.0) / total : 0.0;

        // Precision/Recall macro-avg
        double precisionSum = 0.0, recallSum = 0.0;
        for (int c = 0; c < numClasses; c++) {
            int tp = confusion[c][c];
            int fp = 0, fn = 0;
            for (int r = 0; r < numClasses; r++) if (r != c) fp += confusion[r][c];
            for (int r = 0; r < numClasses; r++) if (r != c) fn += confusion[c][r];
            double prec = (tp + fp) > 0 ? (double) tp / (tp + fp) : 0.0;
            double rec = (tp + fn) > 0 ? (double) tp / (tp + fn) : 0.0;
            precisionSum += prec;
            recallSum += rec;
        }
        double precision = numClasses > 0 ? precisionSum / numClasses : 0.0;
        double recall = numClasses > 0 ? recallSum / numClasses : 0.0;
        double f1Score = (precision + recall) > 0 ? 2.0 * (precision * recall) / (precision + recall) : 0.0;

        long executionTime = System.currentTimeMillis() - startTime;

        return new Object[]{
                k,
                String.format("%.2f", accuracy),
                String.format("%.4f", precision),
                String.format("%.4f", recall),
                String.format("%.4f", f1Score),
                executionTime,
                errors
        };
    }

    private static int inferNumClasses(List<Register> trainData, List<Register> testData) {
        int maxLabel = -1;
        for (Register r : trainData) maxLabel = Math.max(maxLabel, (int) r.getTarget()[0]);
        for (Register r : testData) maxLabel = Math.max(maxLabel, (int) r.getTarget()[0]);
        return maxLabel + 1;
    }
}
