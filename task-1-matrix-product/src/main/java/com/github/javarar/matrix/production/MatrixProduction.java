package com.github.javarar.matrix.production;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import lombok.val;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

@Log4j2
public class MatrixProduction {

    private static ExecutorService executorService;

    public static Matrix product(Matrix a, Matrix b) {
        val result = new Matrix(a.getRows(), b.getColumns());
        val resultElements = result.getElements();
        for (int i = 0; i < result.getRows(); i++) {
            for (int j = 0; j < result.getColumns(); j++) {
                resultElements[i][j] = getProductElement(a.getRow(i), b.getColumn(j));
            }
        }
        return result;
    }

    public static Matrix parallelProduct(Matrix a, Matrix b) {
        val result = new Matrix(a.getRows(), b.getColumns());
        val resultElements = result.getElements();

        // CachedThreadPool справляется с задачей быстрее, чем FixedThreadPool
        // executorService = Executors.newFixedThreadPool(result.getRows());
        // executorService = Executors.newFixedThreadPool(10);

        executorService = Executors.newCachedThreadPool();

        // Разобьём общую задачу подзадачи по вычислению строки матрицы произведения
        record Pair(int rowNum, int[] row) {
        }
        IntStream.range(0, result.getRows())
                .mapToObj(i -> {
                    try {
                        return new Pair(i, executorService.submit(new GetProductRowTask(i, a.getRow(i), b)).get());
                    } catch (InterruptedException | ExecutionException e) {
                        throw new RuntimeException(e);
                    }
                })
                .forEach(pair -> resultElements[pair.rowNum()] = pair.row());
        return result;
    }

    private static int getProductElement(int[] row, int[] column) {
        return IntStream.range(0, row.length)
                .map(i -> row[i] * column[i])
                .sum();
    }

    @RequiredArgsConstructor
    private static class GetProductRowTask implements Callable<int[]> {
        private final int rowId;
        private final int[] firstMatrixRow;
        private final Matrix secondMatrix;

        @Override
        public int[] call() {
//            log.info("Вычисление строки {}", rowId);
            return IntStream.range(0, secondMatrix.getColumns())
                    .mapToObj(secondMatrix::getColumn)
                    .mapToInt(column -> getProductElement(firstMatrixRow, column))
                    .toArray();
        }
    }
}
