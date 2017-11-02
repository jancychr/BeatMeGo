package com.game.libs.gameboard;

import java.util.Comparator;

public class InRowPoints implements Comparable<InRowPoints> {
    public int x, y;

    public static Comparator<InRowPoints> XComparator = new Comparator<InRowPoints>()
    {

        @Override
        public int compare(InRowPoints paramAnonymousInRowPoints1, InRowPoints paramAnonymousInRowPoints2)
        {
            InRowPoints localInRowPoints1 = paramAnonymousInRowPoints1;
            InRowPoints localInRowPoints2 = paramAnonymousInRowPoints2;
            return localInRowPoints1.x - localInRowPoints2.x;
        }
    };

    public static Comparator<InRowPoints> YComparator = new Comparator<InRowPoints>()
    {
        @Override
        public int compare(InRowPoints paramAnonymousInRowPoints1, InRowPoints paramAnonymousInRowPoints2)
        {
            InRowPoints localInRowPoints1 = paramAnonymousInRowPoints1;
            InRowPoints localInRowPoints2 = paramAnonymousInRowPoints2;
            return localInRowPoints1.y - localInRowPoints2.y;
        }
    };

    public InRowPoints(int paramInt1, int paramInt2)
    {
        this.x = paramInt1;
        this.y = paramInt2;
    }

    public String toString()
    {
        return "" + this.x + "," + this.y;
    }

    public int compareTo(InRowPoints paramInRowPoints)
    {
        InRowPoints localInRowPoints = paramInRowPoints;
        return this.x - localInRowPoints.x;
    }

    public static InRowPoints[] concat(InRowPoints[] paramArrayOfInRowPoints1, InRowPoints[] paramArrayOfInRowPoints2)
    {
        int i = paramArrayOfInRowPoints1.length;
        int j = paramArrayOfInRowPoints2.length;
        InRowPoints[] arrayOfInRowPoints = new InRowPoints[i + j];
        for (int k = 0; k <= i - 1; k++) {
            arrayOfInRowPoints[k] = paramArrayOfInRowPoints1[k];
        }
        for (int k = 0; k <= j - 1; k++) {
            arrayOfInRowPoints[(k + i)] = paramArrayOfInRowPoints2[k];
        }
        return arrayOfInRowPoints;
    }
}