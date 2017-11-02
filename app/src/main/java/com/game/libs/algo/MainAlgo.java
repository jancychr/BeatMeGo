package com.game.libs.algo;

import com.game.libs.gameboard.IBoard;
import com.game.libs.gameboard.InRowPoints;

public abstract class MainAlgo {

    protected IBoard board;

    public MainAlgo(IBoard board){
        this.board = board;
    }

    public abstract InRowPoints getBestPlay(int param);
}
