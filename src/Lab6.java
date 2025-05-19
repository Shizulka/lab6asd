import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

public class Lab6 extends JFrame {
    private static final int n = 11;
    private static final int n3 = 1, n4 = 9;
    private static final double k = 1.0 - 0.01 * n3 - 0.005 * n4 - 0.05;
    private static final int SEED = 4219;

    private static final int[][] adjacencyMatrix = new int[n][n];
    private static final int[][] weightMatrix = new int[n][n];

    private final List<Edge> mstEdges = new ArrayList<>();
    private final Set<Integer> visited = new HashSet<>();
    private final PriorityQueue<Edge> edgeQueue = new PriorityQueue<>(Comparator.comparingInt(e -> e.weight));

    private final GraphPanel graphPanel = new GraphPanel();

    public Lab6() {
        setTitle("ЛР6: MST (алгоритм Пріма)");
        setSize(900, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        generateMatrices();
        graphPanel.setGraph(adjacencyMatrix, weightMatrix);
        add(graphPanel, BorderLayout.CENTER);

        JButton stepButton = new JButton("Наступний крок");
        stepButton.addActionListener(e -> performStep(stepButton));
        add(stepButton, BorderLayout.SOUTH);
    }

    private void generateMatrices() {
        Random rand = new Random(SEED);
        double[][] B = new double[n][n];
        int[][] C = new int[n][n];
        int[][] D = new int[n][n];
        int[][] H = new int[n][n];

        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                B[i][j] = rand.nextDouble() * 2.0;

        for (int i = 0; i < n; i++)
            for (int j = i + 1; j < n; j++) {
                double v = B[i][j] * k;
                int adj = v < 1.0 ? 0 : 1;
                adjacencyMatrix[i][j] = adjacencyMatrix[j][i] = adj;
            }

        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                C[i][j] = adjacencyMatrix[i][j] == 1 ? (int) Math.ceil(B[i][j] * 100) : 0;

        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                D[i][j] = C[i][j] > 0 ? 1 : 0;

        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                H[i][j] = D[i][j] == D[j][i] ? 1 : 0;

        for (int i = 0; i < n; i++)
            for (int j = i + 1; j < n; j++) {
                if (H[i][j] == 1 && adjacencyMatrix[i][j] == 1) {
                    weightMatrix[i][j] = weightMatrix[j][i] = C[i][j];
                } else {
                    weightMatrix[i][j] = weightMatrix[j][i] = 0;
                    adjacencyMatrix[i][j] = adjacencyMatrix[j][i] = 0;
                }
            }
        showMatrix(adjacencyMatrix, "Матриця суміжності");
        showMatrix(weightMatrix, "Матриця ваг");
    }

    private void performStep(JButton button) {
        if (visited.isEmpty()) {
            visited.add(0);
            addEdges(0);
        }

        while (!edgeQueue.isEmpty()) {
            Edge edge = edgeQueue.poll();
            if (!visited.contains(edge.to)) {
                visited.add(edge.to);
                mstEdges.add(edge);
                addEdges(edge.to);
                graphPanel.setMSTEdges(mstEdges);
                graphPanel.repaint();

                if (mstEdges.size() == n - 1) {
                    int total = mstEdges.stream().mapToInt(e -> e.weight).sum();
                    JOptionPane.showMessageDialog(this, "Завершено! Сума MST: " + total);
                    button.setEnabled(false);
                }
                return;
            }
        }

        JOptionPane.showMessageDialog(this, "Граф не зв’язний або MST завершено.");
        button.setEnabled(false);
    }

    private void addEdges(int node) {
        for (int i = 0; i < n; i++) {
            if (!visited.contains(i) && adjacencyMatrix[node][i] == 1) {
                int w = weightMatrix[node][i];
                if (w > 0) edgeQueue.add(new Edge(node, i, w));
            }
        }
    }

    private static class Edge {
        int from, to, weight;
        public Edge(int from, int to, int weight) {
            this.from = from;
            this.to = to;
            this.weight = weight;
        }
    }
    private void showMatrix(int[][] matrix, String title) {
        StringBuilder sb = new StringBuilder();
        sb.append(title).append("\n");
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                sb.append(String.format("%4d", matrix[i][j]));
            }
            sb.append("\n");
        }
        JTextArea textArea = new JTextArea(sb.toString());
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        textArea.setEditable(false);
        JOptionPane.showMessageDialog(this, new JScrollPane(textArea), title, JOptionPane.INFORMATION_MESSAGE);
    }

    private class GraphPanel extends JPanel {
        private int[][] adj;
        private int[][] wgt;
        private List<Edge> mst;
        private final Point[] vertexPositions = new Point[n];

        public void setGraph(int[][] adjacencyMatrix, int[][] weightMatrix) {
            this.adj = adjacencyMatrix;
            this.wgt = weightMatrix;
            calculateVertexPositions();
        }

        public void setMSTEdges(List<Edge> mstEdges) {
            this.mst = new ArrayList<>(mstEdges);
        }

        private void calculateVertexPositions() {
            int centerX = 400;
            int centerY = 300;
            int width = 300;
            int height = 200;

            vertexPositions[0] = new Point(centerX - width / 2, centerY - height / 2);
            vertexPositions[1] = new Point(centerX - width / 6, centerY - height / 2);
            vertexPositions[2] = new Point(centerX + width / 6, centerY - height / 2);
            vertexPositions[3] = new Point(centerX + width / 2, centerY - height / 2);
            vertexPositions[4] = new Point(centerX + width / 2, centerY - height / 6);
            vertexPositions[5] = new Point(centerX + width / 2, centerY + height / 2);
            vertexPositions[6] = new Point(centerX + width / 6, centerY + height / 2);
            vertexPositions[7] = new Point(centerX - width / 6, centerY + height / 2);
            vertexPositions[8] = new Point(centerX - width / 2, centerY + height / 2);
            vertexPositions[9] = new Point(centerX - width / 2, centerY + height / 10);
            vertexPositions[10] = new Point(centerX, centerY);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (adj == null) return;

            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            for (int i = 0; i < n; i++)
                for (int j = i + 1; j < n; j++)
                    if (adj[i][j] == 1 && wgt[i][j] > 0) {
                        Point p1 = vertexPositions[i], p2 = vertexPositions[j];
                        g2.setColor(Color.LIGHT_GRAY);
                        g2.drawLine(p1.x, p1.y, p2.x, p2.y);
                        int mx = (p1.x + p2.x) / 2;
                        int my = (p1.y + p2.y) / 2;
                        g2.setColor(Color.BLUE);
                        g2.drawString(String.valueOf(wgt[i][j]), mx, my);
                    }

            if (mst != null) {
                g2.setColor(Color.RED);
                g2.setStroke(new BasicStroke(3));
                for (Edge e : mst) {
                    Point p1 = vertexPositions[e.from], p2 = vertexPositions[e.to];
                    g2.drawLine(p1.x, p1.y, p2.x, p2.y);
                }
                g2.setStroke(new BasicStroke(1));
            }
            
            for (int i = 0; i < n; i++) {
                Point p = vertexPositions[i];
                g2.setColor(Color.BLACK);
                g2.fillOval(p.x - 15, p.y - 15, 30, 30);
                g2.setColor(Color.WHITE);
                g2.drawString(String.valueOf(i), p.x - 5, p.y + 5);
            }
        }
    }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Lab6().setVisible(true));
    }
}
