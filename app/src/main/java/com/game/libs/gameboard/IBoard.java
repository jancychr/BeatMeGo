package com.game.libs.gameboard;

public abstract interface IBoard {

    public abstract int getPlayersGo();

    public abstract void setPlayersGo(int paramInt);

    public abstract InRowPoints getBestPlay();

    public abstract void pushCol(int paramInt, boolean paramBoolean);

    public abstract void popCol(int paramInt);

    public abstract int getCanWinNow();

    public abstract void alternateTurn();

    public abstract InRowPoints[] checkWin();

    public abstract int getStepsDown(int paramInt);

    public abstract boolean getOwns(InRowPoints[] paramArrayOfInRowPoints);

    public abstract boolean colFull(int paramInt);

    public abstract int getNumSpaces();

    public abstract int get(InRowPoints paramInRowPoints);

    public abstract int get(int paramInt1, int paramInt2);

    public abstract void fill(InRowPoints paramInRowPoints, int paramInt);

    public abstract void reset();

    public abstract String encode();

    public abstract void output(String paramString);

    public abstract int evaluateBoard();

    public abstract void setDifficulty(int paramInt);

    public abstract void setDepth(int paramInt);

    public abstract int getDepth();

    public abstract int getHighestStrengthCol();

    public abstract int getCanStopWin();

}
