package com.leaguetor;

import java.util.List;
import java.util.ArrayList;

public class RoundRobin
{
    public static List<int[]>  schedule(int n)
    {
        NeoMatrix m = fillMatrix(n);
        return buildTours(m, n);
    }

    static NeoMatrix fillMatrix(int N)
    {
        int n = N % 2 == 0 ? N : N + 1;
        
        NeoMatrix m = new NeoMatrix();
        m.init(n, n);
        for (int i = 0; i < n; i++)
            m.setPoint(i, i, 0);

        for (int i = 0; i < n / 2; i++)
        {
            for (int j = i + 1; j < n - i; j++)
                set(m, j, i, j + i);
        }

        for (int j = n - 2; j > n / 2; j--)
        {
            for (int i = n - j; i < j; i++)
                set(m, j, i, i - n + j + 1);
        }

        for (int i = 1; i < n - 1 ; i++)
        {
                set(m, n - 1, i, (m.getPoint(i, i - 1) + 1) % (n - 1));
        }

        return m;
    }

    static void set(NeoMatrix m, int i, int j, int v)
    {
        m.setPoint(i, j, v);
        m.setPoint(j, i, v);
    }

    static int findOpp(NeoMatrix m, int p, int t)
    {
        for (int i = p + 1; i < m.width(); i++)
        {
            int v = m.getPoint(p, i);
            if (v == t)
                return i;
        }
        return -1;
    }

    static List<int[]> buildTours(NeoMatrix m, int N)
    {
        if (N < 2)
            return null;
        int n = N % 2 == 0 ? N : N + 1;
        int T = (n - 1) * n / 2;
        ArrayList<int[]> ret = new ArrayList<int[]>();

        for (int t = 1; t < n; t++)
        {
            int [] g = new int [N];
            int idx = 0;

            for (int p = 0; p < N; p++)
            {
                int opp = findOpp(m, p, t);
                if (opp == -1)
                    continue;
                if (opp > N - 1)
                    continue;
                g[idx] = p + 1;
                g[idx + 1] = opp + 1;
                idx += 2;
            }

            ret.add(g);
        }
        return ret;
    }
}