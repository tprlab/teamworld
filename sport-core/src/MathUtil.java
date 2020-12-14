package com.leaguetor;

import java.util.Random;

public class MathUtil {
    public static int[] getPerm(int n)
    {
        int[] ret = new int [n];
        for (int i = 0; i < n; i++)
            ret[i] = i;

        Random rnd = new Random();

        for (int i = 0; i < n - 1; i++)
        {
            int r = rnd.nextInt(n - i);
            if (r == i)
                continue;
            int a = ret[i];
            ret[i] = ret[r];
            ret[r] = a;
        }
        return ret; 
    }


    public static int getLowerBin(short n) {
        for (int b = 1 << 15; b > 0; b >>= 1) {
            if (n >= b)
                return b;
        }
        return 0;
    }

    public static boolean isBin(int n) {
        return (n & (n - 1)) == 0 ;
    }
}