package com.game.libs.gameboard;

import com.game.libs.algo.*;
import java.util.Arrays;

public class AGameBoard implements IBoard{

    public static final int NUMX = 7;
    public static final int NUMY = 6;
    protected int[][] full = new int[7][6];
    protected int numSpaces;
    protected int playersGo = 1;
    protected int[] heights = new int[7];
    protected int depth = 1;
    protected InRowPoints[][] winLines;
    protected InRowPoints[][] threeLines;
    protected InRowPoints[][] twoLines;
    protected double rand;
    private int difficulty;
    protected MainAlgo alg;

    public AGameBoard(){
        WinLines.build();
        this.winLines = WinLines.winLines;
        this.threeLines = WinLines.threeLines;
        this.twoLines = WinLines.twoLines;
        reset();
    }

    public int getPlayersGo()
    {
        return this.playersGo;
    }

    public void setPlayersGo(int paramInt)
    {
        this.playersGo = paramInt;
    }

    protected InRowPoints playRandom()
    {
        return new InRowPoints(0, 0);
    }

    public void setDifficulty(int paramInt)
    {
        this.difficulty = paramInt;
    }

    public double getDifficulty()
    {
        return this.difficulty;
    }

    public int getDepth()
    {
        return this.depth;
    }

    public void setDepth(int paramInt)
    {
        this.depth = paramInt;
    }

    protected int countControlledPower()
    {
        int i = 0;
        for (int j = 0; j <= 6; j++) {
            for (int k = 0; k <= 5; k++) {
                if ((get(j, k) == 0) && ((controls(j, k)) == 1)) //change to 0 or 1 if bug
                {
                    int m = WinLines.getStrengthAt(j, k);
                    i += m;
                }
            }
        }
        return i;
    }

    private int controls(int paramInt1, int paramInt2)
    {
        int i = 0;
        int j = 0;
        int k = Math.max(paramInt1 - 1, 0);
        int m = Math.min(paramInt1 + 1, 6);
        int n = Math.max(paramInt2 - 1, 0);
        int i1 = Math.min(paramInt2 + 1, 5);
        int i2;
        for (i2 = k; i2 <= m; i2++) {
            for (int i3 = n; i3 <= i1; i3++)
            {
                int i4 = get(i2, i3);
                if (i4 == 2)
                {
                    i++;
                    j++;
                }
                else if (i4 == this.playersGo)
                {
                    i++;
                }
                else if (i4 != 0)
                {
                    j++;
                }
            }
        }
        i2 = (i >= 2) && (i > j) && (i + j >= 4) ? 1 : 0;
        return i2;
    }

    private InRowPoints makeFirstPlay()
    {
        double d1 = 0.25D;
        double d2 = Math.random();
        if (d2 < d1) {
            return new InRowPoints(2, 11);
        }
        if (d2 > 1.0D - d1) {
            return new InRowPoints(3, 11);
        }
        return new InRowPoints(4, 11);
    }

    public InRowPoints getBestPlay()
    {
        if (this.numSpaces == 42) {
            return makeFirstPlay();
        }
        int i = lookUp();
        if (i >= 0) {
            return new InRowPoints(i, 1);
        }
        int j = this.depth;
        int k = Math.random() < AlgoConstants.getRandCol(this.difficulty) ? 1 : 0;
        if (k != 0) {
            return new InRowPoints(getHighestStrengthCol(), 3);
        }
        int m = 0;
        if (Math.random() <= AlgoConstants.getRandDepth(this.difficulty))
        {
            this.depth = AlgoConstants.getDepth(this.difficulty);
            m = 1;
        }
        InRowPoints localInRowPoints = this.alg.getBestPlay(this.depth);
        if (m != 0)
        {
            this.depth = j;
            return new InRowPoints(localInRowPoints.y, 0);
        }
        return new InRowPoints(localInRowPoints.y, 2);
    }

    protected InRowPoints[] orderColsByStrength()
    {
        InRowPoints[] arrayOfInRowPoints = new InRowPoints[7];
        for (int i = 0; i <= 6; i++) {
            arrayOfInRowPoints[i] = new InRowPoints(i, getStrengthOnCol(i));
        }
        Arrays.sort(arrayOfInRowPoints, InRowPoints.YComparator);
        return arrayOfInRowPoints;
    }

    private int lookUp()
    {
        String str = encode();
        return LookUp.lookUp(str);
    }

    protected int countThreePower()
    {
        int i = 0;
        int j = 98;
        for (int k = 0; k <= j - 1; k++)
        {
            InRowPoints[] arrayOfInRowPoints = this.threeLines[k];
            if (getOwns(arrayOfInRowPoints)) {
                i += getPower(arrayOfInRowPoints);
            }
        }
        return i;
    }

    public int evaluateBoard()
    {
        return 0;
    }

    private int getPower(InRowPoints[] paramArrayOfInRowPoints)
    {
        int i = 0;
        for (int j = 0; j <= paramArrayOfInRowPoints.length - 1; j++) {
            i += WinLines.getStrengthAt(paramArrayOfInRowPoints[j]);
        }
        return i;
    }

    protected int countTwoPower()
    {
        int i = 0;
        int j = 131;
        for (int k = 0; k <= j - 1; k++)
        {
            InRowPoints[] arrayOfInRowPoints = this.twoLines[k];
            if (getOwns(arrayOfInRowPoints)) {
                i += getPower(arrayOfInRowPoints);
            }
        }
        return i;
    }

    public int getHighestStrengthCol()
    {
        InRowPoints[] arrayOfInRowPoints = orderColsByStrength();
        return arrayOfInRowPoints[(arrayOfInRowPoints.length - 1)].x;
    }

    public void outputE(int paramInt1, int paramInt2, int paramInt3) {}

    public void pushCol(int paramInt, boolean paramBoolean)
    {
        int i = getStepsDown(paramInt);
        if (!paramBoolean) {
            fill(paramInt, i, this.playersGo);
        } else {
            fill(paramInt, i, 2);
        }
        alternateTurn();
        this.heights[paramInt] += 1;
    }

    public void popCol(int paramInt)
    {
        int i = getStepsDown(paramInt);
        fill(paramInt, i + 1, 0);
        alternateTurn();
        this.heights[paramInt] -= 1;
    }

    public int getCanWinNow()
    {
        for (int i = 0; i <= 6; i++)
        {
            boolean bool = playWinsNow(i);
            if (bool) {
                return i;
            }
        }
        return -1;
    }

    public boolean getOwns(InRowPoints[] paramArrayOfInRowPoints)
    {
        int i = paramArrayOfInRowPoints.length;
        for (int j = 0; j <= i - 1; j++)
        {
            int k = get(paramArrayOfInRowPoints[j]);
            if ((k != 2) && (k != this.playersGo)) {
                return false;
            }
        }
        return true;
    }

    protected boolean playWinsNow(int paramInt)
    {
        if (!colFull(paramInt))
        {
            pushCol(paramInt, false);
            alternateTurn();
            InRowPoints[] arrayOfInRowPoints = checkWin();
            popCol(paramInt);
            alternateTurn();
            if (arrayOfInRowPoints != null) {
                return true;
            }
        }
        return false;
    }

    public int getCanStopWin()
    {
        alternateTurn();
        for (int i = 0; i <= 6; i++) {
            if (playWinsNow(i))
            {
                alternateTurn();
                return i;
            }
        }
        alternateTurn();
        return -1;
    }

    public static int getAlternateTurn(int paramInt)
    {
        if (paramInt == 1) {
            return -1;
        }
        if (paramInt == -1) {
            return 1;
        }
        return 0;
    }

    public void alternateTurn()
    {
        this.playersGo = getAlternateTurn(this.playersGo);
    }

    public InRowPoints[] checkWin()
    {
        int i = 69;
        for (int j = 0; j <= i - 1; j++)
        {
            InRowPoints[] arrayOfInRowPoints = this.winLines[j];
            boolean bool = getOwns(arrayOfInRowPoints);
            if (bool) {
                return arrayOfInRowPoints;
            }
        }
        return null;
    }

    public int getStepsDown(int paramInt)
    {
        return 5 - this.heights[paramInt];
    }

    public boolean colFull(int paramInt)
    {
        return this.heights[paramInt] == 6;
    }

    public int getNumSpaces()
    {
        return this.numSpaces;
    }

    public int get(InRowPoints paramInRowPoints)
    {
        return get(paramInRowPoints.x, paramInRowPoints.y);
    }

    private int getStrengthOnCol(int paramInt)
    {
        int i = getStepsDown(paramInt);
        int j;
        if (i == -1) {
            j = -1;
        } else {
            j = WinLines.getStrengthAt(paramInt, i);
        }
        return j;
    }

    public int get(int paramInt1, int paramInt2)
    {
        return this.full[paramInt1][paramInt2];
    }

    public void fill(int paramInt1, int paramInt2, int paramInt3)
    {
        this.full[paramInt1][paramInt2] = paramInt3;
        if (paramInt3 == 0) {
            this.numSpaces += 1;
        } else {
            this.numSpaces -= 1;
        }
    }

    public void fill(InRowPoints paramInRowPoints, int paramInt)
    {
        fill(paramInRowPoints.x, paramInRowPoints.y, paramInt);
    }

    public void reset()
    {
        this.heights = new int[7];
        for (int i = 0; i <= 6; i++)
        {
            for (int j = 0; j <= 5; j++) {
                this.full[i][j] = 0;
            }
            this.heights[i] = 0;
        }
        setPlayersGo(1);
        this.numSpaces = 42;
    }

    public String encode()
    {
        String str = "";
        int i = 0;
        for (int k = 0; k <= 5; k++) {
            for (int m = 0; m <= 6; m++)
            {
                int j = get(m, k);
                if (j == 0)
                {
                    i++;
                }
                else
                {
                    if (i >= 1)
                    {
                        str = str + "" + i;
                        i = 0;
                    }
                    if (j == 1) {
                        str = str + "R";
                    } else if (j == -1) {
                        str = str + "Y";
                    }
                }
            }
        }
        return str;
    }

    public void output(String paramString)
    {
        for (int i = 0; i <= 5; i++)
        {
            String str = paramString;
            for (int j = 0; j <= 6; j++)
            {
                int k = get(j, i);
                if (k == 0) {
                    str = str + "O ";
                } else if (k == -1) {
                    str = str + "Y ";
                } else if (k == 1) {
                    str = str + "R ";
                }
            }
            System.out.println(str);
        }
    }
}
