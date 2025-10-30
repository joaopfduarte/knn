import converter.IrisConverter;
import converter.Register;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.logging.Logger;

public class KnnMain {

    private static final Logger log = Logger.getLogger(KnnMain.class.getName());

    private static Object[][] averageMatrix = new Object[29][4];

    public static void main(String[] args) {
        final int numK = 30;
        boolean isIris = true;

        if (isIris) {
            IrisConverter converter = new IrisConverter();
            List<Register> train = converter.trainConverter();
            List<Register> test = converter.testConverter();

            SwingUtilities.invokeLater(() -> createAndShowGUI(train, test, numK));
        }
    }
    
    private static void createAndShowGUI(List<Register> trainData, List<Register> testData, int maxK) {
        JFrame frame = new JFrame("Resultados KNN - Dataset Iris");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(900, 700);
        frame.setLocationRelativeTo(null);
        
        // Criar painel com abas para cada métrica
        JTabbedPane tabbedPane = new JTabbedPane();
        
        // Adicionar uma aba para cada métrica de distância
        tabbedPane.addTab("Manhattan", createResultPanel("Manhattan", trainData, testData, maxK, 0));
        tabbedPane.addTab("Euclidiana", createResultPanel("Euclidiana", trainData, testData, maxK, 1));
        tabbedPane.addTab("Minkowski (p=3)", createResultPanel("Minkowski", trainData, testData, maxK, 2));
        tabbedPane.addTab("Mahalanobis", createResultPanel("Mahalanobis", trainData, testData, maxK, 3));

        log.info("Criação da tabela de médias");
        tabbedPane.addTab("Médias", createAveragePanel());
        
        frame.add(tabbedPane);
        frame.setVisible(true);
    }

    private static JPanel createAveragePanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));

        String[] columnNames = {
                "Manhattan",
                "Euclidiana",
                "Minkowski",
                "Mahalanobis"
        };

        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Tornar células não editáveis
            }
        };

        for (int i = 0; i < averageMatrix.length; i++) {
            Object[] row = new Object[5];
            row[0] = i + 1;              // Valor de K (começa em 2)
            row[1] = averageMatrix[i][0]; // Manhattan
            row[2] = averageMatrix[i][1]; // Euclidiana
            row[3] = averageMatrix[i][2]; // Minkowski
            row[4] = averageMatrix[i][3]; // Mahalanobis
            tableModel.addRow(row);
        }

        JTable table = new JTable(tableModel);
        table.setRowHeight(25);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        table.setFont(new Font("Arial", Font.PLAIN, 11));

        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }
    
    private static JPanel createResultPanel(String metricName, List<Register> trainData, 
                                           List<Register> testData, int maxK, int metricIndex) {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Título
        JLabel titleLabel = new JLabel("Métricas de Desempenho - Distância " + metricName, JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        panel.add(titleLabel, BorderLayout.NORTH);
        
        // Criar colunas da tabela
        String[] columnNames = {
            "K", 
            "Acurácia (%)", 
            "Precisão", 
            "Recall", 
            "Average-F1-Score",
            "Tempo (ms)",
            "Erros"
        };
        
        // Criar modelo da tabela
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Tornar células não editáveis
            }
        };
        
        KnnImp knn = new KnnImp();
        int i = 0;
        for (int k = 2; k <= maxK; k++) {
            Object[] row = KnnImp.calculateMetricsForK(k, metricName, trainData, testData, knn);
            averageMatrix[i][metricIndex] = row[4];
            tableModel.addRow(row);
            i++;
        }
        
        JTable table = new JTable(tableModel);
        table.setRowHeight(25);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        table.setFont(new Font("Arial", Font.PLAIN, 11));
        
        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        JPanel infoPanel = new JPanel(new GridLayout(3, 1, 5, 5));
        infoPanel.setBorder(BorderFactory.createTitledBorder("Informações do Dataset"));
        infoPanel.add(new JLabel("Total de registros de treino: " + trainData.size()));
        infoPanel.add(new JLabel("Total de registros de teste: " + testData.size()));
        infoPanel.add(new JLabel("Métrica de distância: " + metricName));
        
        panel.add(infoPanel, BorderLayout.SOUTH);
        
        return panel;
    }
}
