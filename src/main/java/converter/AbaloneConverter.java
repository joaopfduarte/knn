package converter;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AbaloneConverter {
    private final String filePathTrain = "src/main/java/base/abalone/train.csv";
    private final String filePathTest = "src/main/java/base/abalone/test.csv";

    public List<Register> trainConverter() {
        List<Register> dataset = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePathTrain))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) continue;
                String[] tokens = splitLine(line);
                if (tokens.length < 3) continue;

                // In Abalone, the sex/class is the first column (e.g., M, F, I)
                String classToken = tokens[0];

                // Features are all numeric columns after the first column
                double[] features = new double[tokens.length - 1];
                for (int i = 1; i < tokens.length; i++) {
                    features[i - 1] = Double.parseDouble(tokens[i]);
                }

                // Single-output encoding (0=I, 1=M, 2=F)
                double[] target = new double[]{
                        "F".equalsIgnoreCase(classToken) ? 2.0 :
                                ("M".equalsIgnoreCase(classToken) ? 1.0 : 0.0)
                };

                dataset.add(new Register(features, target));
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }
        return dataset;
    }

    public List<Register> testConverter() {
        List<Register> dataset = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePathTest))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) continue;
                String[] tokens = splitLine(line);
                if (tokens.length < 3) continue;

                // In Abalone, the sex/class is the first column (e.g., M, F, I)
                String classToken = tokens[0];

                // Features are all numeric columns after the first column
                double[] features = new double[tokens.length - 1];
                for (int i = 1; i < tokens.length; i++) {
                    features[i - 1] = Double.parseDouble(tokens[i]);
                }

                // Single-output encoding (0=I, 1=M, 2=F)
                double[] target = new double[]{
                        "F".equalsIgnoreCase(classToken) ? 2.0 :
                                ("M".equalsIgnoreCase(classToken) ? 1.0 : 0.0)
                };

                dataset.add(new Register(features, target));
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }
        return dataset;
    }

    private String[] splitLine(String line) {
        String[] parts = line.trim().split(",");
        for (int i = 0; i < parts.length; i++) {
            parts[i] = parts[i].trim();
        }
        return parts;
    }
}
