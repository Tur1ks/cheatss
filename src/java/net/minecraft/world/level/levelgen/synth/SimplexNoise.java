package net.minecraft.world.level.levelgen.synth;

import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;

public class SimplexNoise {
    protected static final int[][] GRADIENT = new int[][]{
        {1, 1, 0},
        {-1, 1, 0},
        {1, -1, 0},
        {-1, -1, 0},
        {1, 0, 1},
        {-1, 0, 1},
        {1, 0, -1},
        {-1, 0, -1},
        {0, 1, 1},
        {0, -1, 1},
        {0, 1, -1},
        {0, -1, -1},
        {1, 1, 0},
        {0, -1, 1},
        {-1, 1, 0},
        {0, -1, -1}
    };
    private static final double SQRT_3 = Math.sqrt(3.0);
    private static final double F2 = 0.5 * (SQRT_3 - 1.0);
    private static final double G2 = (3.0 - SQRT_3) / 6.0;
    private final int[] p = new int[512];
    public final double xo;
    public final double yo;
    public final double zo;

    public SimplexNoise(RandomSource pRandom) {
        this.xo = pRandom.nextDouble() * 256.0;
        this.yo = pRandom.nextDouble() * 256.0;
        this.zo = pRandom.nextDouble() * 256.0;
        int i = 0;

        while (i < 256) {
            this.p[i] = i++;
        }

        for (int l = 0; l < 256; l++) {
            int j = pRandom.nextInt(256 - l);
            int k = this.p[l];
            this.p[l] = this.p[j + l];
            this.p[j + l] = k;
        }
    }

    private int p(int pIndex) {
        return this.p[pIndex & 0xFF];
    }

    protected static double dot(int[] pGradient, double pX, double pY, double pZ) {
        return (double)pGradient[0] * pX + (double)pGradient[1] * pY + (double)pGradient[2] * pZ;
    }

    private double getCornerNoise3D(int pGradientIndex, double pX, double pY, double pZ, double pOffset) {
        double d1 = pOffset - pX * pX - pY * pY - pZ * pZ;
        double d0;
        if (d1 < 0.0) {
            d0 = 0.0;
        } else {
            d1 *= d1;
            d0 = d1 * d1 * dot(GRADIENT[pGradientIndex], pX, pY, pZ);
        }

        return d0;
    }

    public double getValue(double pX, double pY) {
        double d0 = (pX + pY) * F2;
        int i = Mth.floor(pX + d0);
        int j = Mth.floor(pY + d0);
        double d1 = (double)(i + j) * G2;
        double d2 = (double)i - d1;
        double d3 = (double)j - d1;
        double d4 = pX - d2;
        double d5 = pY - d3;
        int k;
        int l;
        if (d4 > d5) {
            k = 1;
            l = 0;
        } else {
            k = 0;
            l = 1;
        }

        double d6 = d4 - (double)k + G2;
        double d7 = d5 - (double)l + G2;
        double d8 = d4 - 1.0 + 2.0 * G2;
        double d9 = d5 - 1.0 + 2.0 * G2;
        int i1 = i & 0xFF;
        int j1 = j & 0xFF;
        int k1 = this.p(i1 + this.p(j1)) % 12;
        int l1 = this.p(i1 + k + this.p(j1 + l)) % 12;
        int i2 = this.p(i1 + 1 + this.p(j1 + 1)) % 12;
        double d10 = this.getCornerNoise3D(k1, d4, d5, 0.0, 0.5);
        double d11 = this.getCornerNoise3D(l1, d6, d7, 0.0, 0.5);
        double d12 = this.getCornerNoise3D(i2, d8, d9, 0.0, 0.5);
        return 70.0 * (d10 + d11 + d12);
    }

    public double getValue(double pX, double pY, double pZ) {
        double d0 = 0.3333333333333333;
        double d1 = (pX + pY + pZ) * 0.3333333333333333;
        int i = Mth.floor(pX + d1);
        int j = Mth.floor(pY + d1);
        int k = Mth.floor(pZ + d1);
        double d2 = 0.16666666666666666;
        double d3 = (double)(i + j + k) * 0.16666666666666666;
        double d4 = (double)i - d3;
        double d5 = (double)j - d3;
        double d6 = (double)k - d3;
        double d7 = pX - d4;
        double d8 = pY - d5;
        double d9 = pZ - d6;
        int l;
        int i1;
        int j1;
        int k1;
        int l1;
        int i2;
        if (d7 >= d8) {
            if (d8 >= d9) {
                l = 1;
                i1 = 0;
                j1 = 0;
                k1 = 1;
                l1 = 1;
                i2 = 0;
            } else if (d7 >= d9) {
                l = 1;
                i1 = 0;
                j1 = 0;
                k1 = 1;
                l1 = 0;
                i2 = 1;
            } else {
                l = 0;
                i1 = 0;
                j1 = 1;
                k1 = 1;
                l1 = 0;
                i2 = 1;
            }
        } else if (d8 < d9) {
            l = 0;
            i1 = 0;
            j1 = 1;
            k1 = 0;
            l1 = 1;
            i2 = 1;
        } else if (d7 < d9) {
            l = 0;
            i1 = 1;
            j1 = 0;
            k1 = 0;
            l1 = 1;
            i2 = 1;
        } else {
            l = 0;
            i1 = 1;
            j1 = 0;
            k1 = 1;
            l1 = 1;
            i2 = 0;
        }

        double d10 = d7 - (double)l + 0.16666666666666666;
        double d11 = d8 - (double)i1 + 0.16666666666666666;
        double d12 = d9 - (double)j1 + 0.16666666666666666;
        double d13 = d7 - (double)k1 + 0.3333333333333333;
        double d14 = d8 - (double)l1 + 0.3333333333333333;
        double d15 = d9 - (double)i2 + 0.3333333333333333;
        double d16 = d7 - 1.0 + 0.5;
        double d17 = d8 - 1.0 + 0.5;
        double d18 = d9 - 1.0 + 0.5;
        int j2 = i & 0xFF;
        int k2 = j & 0xFF;
        int l2 = k & 0xFF;
        int i3 = this.p(j2 + this.p(k2 + this.p(l2))) % 12;
        int j3 = this.p(j2 + l + this.p(k2 + i1 + this.p(l2 + j1))) % 12;
        int k3 = this.p(j2 + k1 + this.p(k2 + l1 + this.p(l2 + i2))) % 12;
        int l3 = this.p(j2 + 1 + this.p(k2 + 1 + this.p(l2 + 1))) % 12;
        double d19 = this.getCornerNoise3D(i3, d7, d8, d9, 0.6);
        double d20 = this.getCornerNoise3D(j3, d10, d11, d12, 0.6);
        double d21 = this.getCornerNoise3D(k3, d13, d14, d15, 0.6);
        double d22 = this.getCornerNoise3D(l3, d16, d17, d18, 0.6);
        return 32.0 * (d19 + d20 + d21 + d22);
    }
}