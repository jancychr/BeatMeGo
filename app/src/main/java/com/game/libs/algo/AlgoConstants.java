package com.game.libs.algo;

public class AlgoConstants {

    public static final int[] DEPTHS = { 1, 2, 4 };
    public static final double[] COLUMN_RANDOMS = { 0.4D, 0.1D, 0.0D };
    public static final double[] DEPTH_RANDOMS = { 1.0D, 0.5D, 0.25D };

    public static final int getDefaultDepth()
    {
        return getDepth(2);
    }

    public static final int getDepth(int paramInt)
    {
        return DEPTHS[paramInt];
    }

    public static final double getRandCol(int paramInt)
    {
        return COLUMN_RANDOMS[paramInt];
    }

    public static final double getRandDepth(int paramInt)
    {
        return DEPTH_RANDOMS[paramInt];
    }

}
