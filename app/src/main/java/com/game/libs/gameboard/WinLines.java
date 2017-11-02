package com.game.libs.gameboard;

public class WinLines {

    public static final int NUM_WIN = 69;
    public static final int NUM_3 = 98;
    public static final int NUM_2 = 131;
    public static InRowPoints[][] twoLines = new InRowPoints[''][2];
    public static InRowPoints[][] threeLines = new InRowPoints[98][3];
    public static InRowPoints[][] winLines = new InRowPoints[69][4];
    public static int[][] strength = new int[7][6];
    public static boolean built = false;

    public static void build(){
        if (!built)
        {
            buildLines(4, winLines);
            buildLines(3, threeLines);
            buildLines(2, twoLines);
            buildStrength();
        }
        built = true;
    }

    public static int getStrengthAt(InRowPoints paramInRowPoints)
    {
        return strength[paramInRowPoints.x][paramInRowPoints.y];
    }

    public static int getStrengthAt(int paramInt1, int paramInt2)
    {
        return strength[paramInt1][paramInt2];
    }

    public static void buildStrength()
    {
        int[] arrayOfInt1 = { 3, 4, 5, 5, 4, 3 };
        int[] arrayOfInt2 = { 4, 6, 8, 8, 6, 4 };
        int[] arrayOfInt3 = { 5, 8, 11, 11, 8, 5 };
        int[] arrayOfInt4 = { 7, 10, 13, 13, 10, 7 };
        strength[0] = arrayOfInt1;
        strength[1] = arrayOfInt2;
        strength[2] = arrayOfInt3;
        strength[3] = arrayOfInt4;
        strength[4] = arrayOfInt3;
        strength[5] = arrayOfInt2;
        strength[6] = arrayOfInt1;
    }

    private static void buildLines(int paramInt, InRowPoints[][] paramArrayOfInRowPoints)
    {
        int m = 0;
        int j;
        InRowPoints[] arrayOfInRowPoints;
        int k;
        int i;
        for (i = 0; i <= 7 - paramInt; i++) {
            for (j = 0; j <= 5; j++)
            {
                arrayOfInRowPoints = new InRowPoints[paramInt];
                for (k = 0; k <= paramInt - 1; k++) {
                    arrayOfInRowPoints[k] = new InRowPoints(i + k, j);
                }
                paramArrayOfInRowPoints[m] = arrayOfInRowPoints;
                m++;
            }
        }
        for (i = 0; i <= 6; i++) {
            for (j = 0; j <= 6 - paramInt; j++)
            {
                arrayOfInRowPoints = new InRowPoints[paramInt];
                for (k = 0; k <= paramInt - 1; k++) {
                    arrayOfInRowPoints[k] = new InRowPoints(i, j + k);
                }
                paramArrayOfInRowPoints[m] = arrayOfInRowPoints;
                m++;
            }
        }
        for (i = 0; i <= 7 - paramInt; i++) {
            for (j = 0; j <= 6 - paramInt; j++)
            {
                arrayOfInRowPoints = new InRowPoints[paramInt];
                for (k = 0; k <= paramInt - 1; k++) {
                    arrayOfInRowPoints[k] = new InRowPoints(i + k, j + k);
                }
                paramArrayOfInRowPoints[m] = arrayOfInRowPoints;
                m++;
            }
        }
        for (i = 0; i <= 7 - paramInt; i++) {
            for (j = paramInt - 1; j <= 5; j++)
            {
                arrayOfInRowPoints = new InRowPoints[paramInt];
                for (k = 0; k <= paramInt - 1; k++) {
                    arrayOfInRowPoints[k] = new InRowPoints(i + k, j - k);
                }
                paramArrayOfInRowPoints[m] = arrayOfInRowPoints;
                m++;
            }
        }
    }
}
