package com.github.javarar.matrix.production;

import lombok.extern.log4j.Log4j2;
import lombok.val;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.Instant;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Log4j2
public class MatrixProductionTest {

    @DisplayName("Задание 2. Вычисление произведения квадратных матриц")
    @ParameterizedTest
    @MethodSource("matrixProducer")
    public void validateMatrixProduction(Matrix a, Matrix b) {
//        log.info("Матрица A: {}", a);
//        log.info("Матрица B: {}", b);

        val start = Instant.now().toEpochMilli();
        val result = MatrixProduction.product(a,b);
//        log.info("Результат произведения: {}", result);
        val productTime = Instant.now().toEpochMilli() - start;
        log.info("Время выполнения последовательного алгоритма: {} мс.", productTime);

        val parallelStart = Instant.now().toEpochMilli();
        val parallelResult = MatrixProduction.parallelProduct(a,b);
        val parallelProductTime = Instant.now().toEpochMilli() - parallelStart;
        log.info("Время выполнения параллельного алгоритма: {} мс.", parallelProductTime);

        assertEquals(result, parallelResult);
    }

    private static Stream<Arguments> matrixProducer() {
        return Stream.of(
                Arguments.of(new Matrix(3,3).fillRandomly(), new Matrix(3,3).fillRandomly()),
                Arguments.of(new Matrix(100,100).fillRandomly(), new Matrix(100,100).fillRandomly()),
                Arguments.of(new Matrix(1000,1000).fillRandomly(), new Matrix(1000,1000).fillRandomly())
        );
    }
}
