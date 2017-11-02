package com.game.libs.algo;

import com.game.libs.gameboard.IBoard;
import com.game.libs.gameboard.InRowPoints;

public class MinMax extends MainAlgo {

    public MinMax(IBoard board) {
        super(board);
    }

    @Override
    public InRowPoints getBestPlay(int paramInt) {
        return minimax(paramInt);
    }

    public InRowPoints minimax(int paramInt)
    {
        int i = this.board.getNumSpaces();
        int k = -1;
        int m = Integer.MIN_VALUE;
        InRowPoints[] arrayOfInRowPoints1 = this.board.checkWin();
        this.board.alternateTurn();
        InRowPoints[] arrayOfInRowPoints2 = this.board.checkWin();
        this.board.alternateTurn();
        if (arrayOfInRowPoints1 != null) {
            return new InRowPoints(Integer.MAX_VALUE, k);
        }
        if (arrayOfInRowPoints2 != null) {
            return new InRowPoints(Integer.MIN_VALUE, k);
        }
        int n;
        if (paramInt == 0)
        {
            n = this.board.evaluateBoard();
            return new InRowPoints(n, this.board.getHighestStrengthCol());
        }
        int j = this.board.getCanWinNow();
        if (j >= 0) {
            return new InRowPoints(Integer.MAX_VALUE, j);
        }
        j = this.board.getCanStopWin();
        if (j >= 0) {
            return new InRowPoints(Integer.MAX_VALUE, j);
        }
        if (i != this.board.getNumSpaces()) {
            return new InRowPoints(Integer.MAX_VALUE, 0);
        }
        for (j = 0; j <= 6; j++) {
            if (!this.board.colFull(j))
            {
                this.board.pushCol(j, false);
                InRowPoints localInRowPoints = minimax(paramInt - 1);
                n = -localInRowPoints.x;
                this.board.popCol(j);
                if (n > m)
                {
                    m = n;
                    k = j;
                }
            }
        }
        return new InRowPoints(m, k);
    }
}
