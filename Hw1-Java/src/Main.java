public class Main {
    static int[] MatrixA = new int[]{50, 80};
    static int[] MatrixB = new int[]{80, 50};

    public static void main(String[] args) throws InterruptedException {
        // 初始化
        double[][] matrixA = initMatrixA();
        double[][] matrixB = initMatrixB();
        double[][] matrixC = new double[matrixA.length][matrixB[0].length];

        // 執行運算
        for (int i = 1; i <= 3; i++) {
            System.out.println("第" + i + "次" + "【A】耗時：");
            runByForLoops(matrixA, matrixB, matrixC);
            System.out.println("第" + i + "次" + "【B-1】耗時：");
            runByMultiThread(matrixA, matrixB, matrixC, 50);
            System.out.println("第" + i + "次" + "【B-2】耗時：");
            runByMultiThread(matrixA, matrixB, matrixC, 10);
            System.out.println("第" + i + "次" + "當使用全部核心：");
            runByMultiThread(matrixA, matrixB, matrixC, Runtime.getRuntime().availableProcessors());
            System.out.println("-------------------------");
        }
    }

    private static double[][] initMatrixA() { // 初始化 Matrix-A
        double[][] matrixA = new double[MatrixA[0]][MatrixA[1]];
        for (int i = 0; i < matrixA.length; i++)
            for (int j = 0; j < matrixA[0].length; j++)
                matrixA[i][j] = 6.6 * (i + 1) - 3.3 * (j + 1);
        return matrixA;
    }

    private static double[][] initMatrixB() { // 初始化 Matrix-B
        double[][] matrixB = new double[MatrixB[0]][MatrixB[1]];
        for (int i = 0; i < matrixB.length; i++)
            for (int j = 0; j < matrixB[0].length; j++)
                matrixB[i][j] = 100 + 2.2 * (i + 1) - 5.5 * (j + 1);
        return matrixB;
    }

    private static void showMatrix(double[][] matrix) { // 顯示 Matrix
        for (double[] doubles : matrix) {
            for (double aDouble : doubles)
                System.out.print(aDouble + "  ");
            System.out.println();
        }
    }

    private synchronized static void runByForLoops(double[][] matrixA, double[][] matrixB, double[][] matrixC) { // 一般迴圈解法
        long t1 = System.currentTimeMillis();
        for (int i = 0; i < matrixA.length; i++) // 0 ~ 49
            for (int j = 0; j < matrixB[0].length; j++) { // 0 ~ 49
                matrixC[i][j] = 0; // 初始化MatrixC
                for (int k = 0; k < matrixB.length; k++) // 0 ~ 79
                    matrixC[i][j] += matrixA[i][k] * matrixB[k][j];
            }
        long t2 = System.currentTimeMillis();
        System.out.println((t2 - t1) + "ms");
    }

    private synchronized static void runByMultiThread(double[][] matrixA, double[][] matrixB, double[][] matrixC, int thread) throws InterruptedException { // 使用執行緒
        long t1 = System.currentTimeMillis();
        Thread[] threads = new Thread[thread]; // 定義多執行緒
        int rows = matrixC.length / thread; // 分配給每個執行緒的row

        for (int i = 0; i < thread; i++) {
            int row = rows;

            if (i == thread - 1) // 多餘的row留給最後一個執行緒
                row += matrixC.length % thread;

            multiThread multiThread = new multiThread(matrixA, matrixB, matrixC, (rows * i), ((rows * i) + row));

            threads[i] = new Thread(multiThread);
            threads[i].start(); // 執行運算
        }

        for (int i = 0; i < thread; i++)
            threads[i].join(); // 整合所有執行緒

        long t2 = System.currentTimeMillis();
        System.out.println((t2 - t1) + "ms");
    }
}

class multiThread implements Runnable {
    double[][] matrixA, matrixB, matrixC;
    int startRow, endRow; // 起始行 和 結束行。

    public multiThread(double[][] matrixA, double[][] matrixB, double[][] matrixC, int startRow, int endRow) {
        this.matrixA = matrixA;
        this.matrixB = matrixB;
        this.matrixC = matrixC;
        this.startRow = startRow;
        this.endRow = endRow;
    }

    public void run() {
        for (int i = startRow; i < endRow && i < matrixA.length; i++)
            for (int j = 0; j < matrixB[0].length; j++) {
                matrixC[i][j] = 0; // 初始化MatrixC
                for (int k = 0; k < matrixB.length; k++)
                    matrixC[i][j] += matrixA[i][k] * matrixB[k][j];
            }
    }
}