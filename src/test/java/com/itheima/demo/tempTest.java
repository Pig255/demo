package com.itheima.demo;

import org.ejml.simple.SimpleMatrix;
import org.junit.Test;

import java.util.HashMap;

public class tempTest {
    @Test
    public void tet(){
        double[][] arrayDouble2 = new double[][]{{1,2,3},{2,4,47}};
        double[][] arrayDouble1 = new double[][]{{2,3},{2,4},{4,8}};
        SimpleMatrix matrixDouble2 = new SimpleMatrix(arrayDouble2);
        SimpleMatrix matrixDouble1 = new SimpleMatrix(arrayDouble1);
        SimpleMatrix matrixC = matrixDouble2.mult(matrixDouble1);
        SimpleMatrix D_invert = matrixC.invert();
        System.out.println(D_invert);

    }
}
