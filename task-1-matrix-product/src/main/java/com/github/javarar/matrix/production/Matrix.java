package com.github.javarar.matrix.production;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.val;

import java.util.Random;

@Getter
@ToString
@EqualsAndHashCode
public class Matrix {
    private final int rows;
    private final int columns;
    private final int[][] elements;

    public Matrix(int rows, int columns) {
        this.rows = rows;
        this.columns = columns;
        this.elements = new int[rows][columns];
    }

    public int[] getRow(int n) {
        return elements[n];
    }

    public int[] getColumn(int n) {
        val column = new int[rows];
        for (int i = 0; i < rows; i++) {
            column[i] = elements[i][n];
        }
        return column;
    }

    public Matrix fillRandomly() {
        val random = new Random();
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                elements[i][j] = random.nextInt(100);
            }
        }
        return this;
    }
}
