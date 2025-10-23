import converter.Register;

import java.util.List;
import java.util.logging.Logger;

public class KnnImp {
    private static final Logger log = Logger.getLogger(KnnImp.class.getName());

    public long manhattanDistance(double[] a, double[] b) {
        log.info("Manhattan distance");
        long sum = 0;
        for (int i = 0; i < a.length; i++) {
            sum += Math.abs(a[i] - b[i]);
        }
        return sum;
    }

    public long euclideanDistance(double[] a, double[] b) {
        log.info("Euclidean distance");
        long sum = 0;
        for (int i = 0; i < a.length; i++) {
            sum += Math.pow(a[i] - b[i], 2);
        }
        return (long) Math.sqrt(sum);
    }

    public long minkowskiDistance(double[] a, double[] b, int p) {
        log.info("Minkowski distance");
        long sum = 0;
        for (int i = 0; i < a.length; i++) {
            sum += Math.pow(Math.abs(a[i] - b[i]), p);
        }
        return sum;
    }

    public long mahalanobisDistance(double[] a, double[] b, double[] mean) {
        log.info("Mahalanobis distance");
        long sum = 0;
        for (int i = 0; i < a.length; i++) {
            sum += Math.pow(a[i] - b[i], 2) * (mean[i] - a[i]);
        }
        return (long) Math.sqrt(sum);
    }

    public static Object[] calculateMetricsForK(int k, String metricName,
                                                 List<Register> trainData,
                                                 List<Register> testData,
                                                 KnnImp knn) {
        // AQUI VOCÊ IMPLEMENTARÁ A LÓGICA REAL DO KNN
        // Por enquanto, retorna valores simulados para demonstração

        long startTime = System.currentTimeMillis();

        // TODO: Implementar a classificação KNN real aqui
        // 1. Para cada registro de teste
        // 2. Calcular distância para todos os registros de treino
        // 3. Encontrar os K vizinhos mais próximos
        // 4. Classificar por votação majoritária
        // 5. Calcular métricas (acurácia, precisão, recall, F1)

        // Valores simulados para demonstração
        double accuracy = 85.0 + (Math.random() * 10); // Simular acurácia entre 85-95%
        double precision = 0.80 + (Math.random() * 0.15);
        double recall = 0.80 + (Math.random() * 0.15);
        double f1Score = 2 * (precision * recall) / (precision + recall);
        int errors = (int) ((100 - accuracy) * testData.size() / 100);

        long endTime = System.currentTimeMillis();
        long executionTime = endTime - startTime;

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
}
