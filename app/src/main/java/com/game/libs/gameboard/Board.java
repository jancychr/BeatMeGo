package com.game.libs.gameboard;

import com.game.libs.algo.MinMax;

public class Board extends AGameBoard{

    public Board(){
        this.alg = new MinMax(this);
    }

    public int evaluateBoard(){
        int i = 1;
        int j = 4;
        int k = 3;
        int m = countTwoPower();
        int n = countThreePower();
        int i1 = countControlledPower();
        alternateTurn();
        int i2 = countTwoPower();
        int i3 = countThreePower();
        int i4 = countControlledPower();
        alternateTurn();
        int i5 = j * (n - i3) + k * (i1 - i4) + i * (m - i2);
        return i5;
    }
}