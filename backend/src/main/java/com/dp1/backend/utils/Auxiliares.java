package com.dp1.backend.utils;

import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.special.Gamma;

import com.dp1.backend.models.Paquete;

public class Auxiliares {
    public static double[][] levy(int n, int m, double beta) {
        double num = Gamma.gamma(1 + beta) * Math.sin(Math.PI * beta / 2);
        double den = Gamma.gamma((1 + beta) / 2) * beta * Math.pow(2, (beta - 1) / 2);
        double sigma_u = Math.pow(num / den, 1 / beta);

        NormalDistribution distU = new NormalDistribution(0, sigma_u);
        NormalDistribution distV = new NormalDistribution(0, 1);

        double[][] z = new double[n][m];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                double u = distU.sample();
                double v = distV.sample();
                z[i][j] = u / Math.pow(Math.abs(v), 1 / beta);
            }
        }
        return z;
    }

    public static double fitness (Paquete paquete, int[] solucion){
        return 0;
    }
}