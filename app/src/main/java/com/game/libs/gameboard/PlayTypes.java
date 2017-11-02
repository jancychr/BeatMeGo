package com.game.libs.gameboard;

public class PlayTypes {

    public static final int USED_RANDOM_DEPTH = 0;
    public static final int USED_LOOK = 1;
    public static final int USED_MINIMAX = 2;
    public static final int USED_RAND_COL = 3;
    public static final int USED_FORCE = 4;

    public static String getString(int paramInt)
    {
        if (paramInt == 0) {
            return "USED_RANDOM_DEPTH";
        }
        if (paramInt == 1) {
            return "USED_LOOK";
        }
        if (paramInt == 2) {
            return "USED_MINMAX";
        }
        if (paramInt == 3) {
            return "USED_RAND_COL";
        }
        if (paramInt == 4) {
            return "USED_FORCE";
        }
        return "";
    }

}
