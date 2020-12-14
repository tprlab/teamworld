package com.leaguetor;


public class NeoMatrix
{
    int [] mData;
    int mWidth;
    int mHeight;
    int mTotalLen;

    public static interface Comparator
    {
        boolean isGood(int x, int y, NeoMatrix m);
    }

    public NeoMatrix()
    {
        mData = null;
        mWidth = 0;
        mHeight = 0;
    }

    protected boolean checkPoint(int x, int y)
    {
        return true;
    }

    public boolean fit(int x, int y)
    {
        return x >= 0 && y >= 0 && x < mWidth && y < mHeight;
    }

    

    public void init(int w, int h)
    {
        if (w < 1 || h < 1)
            return;
        mWidth = w;
        mHeight = h;
        mTotalLen = mWidth * mHeight;
        mData = new int [mTotalLen];
    }

    public int getPoint(int x, int y)
    {
//        if (!fit(x, y))
        return mData[x + y * mWidth];
    }

    public void setPoint(int x, int y, int val)
    {
        if (!fit(x, y))
            return;

        mData[x + y * mWidth] = val;
    }

    public int width() {return mWidth;}

    public int height() {return mHeight;}

    public int setBulk(int offset, int len, int c)
    {
        int written = 0;
        for (int i = offset; i < offset + len && i < mTotalLen; i++, written++)    
            mData[i] = c;

        return written;
    }

}