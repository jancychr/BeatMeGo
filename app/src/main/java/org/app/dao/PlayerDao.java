package org.app.dao;

public class PlayerDao {

    private String playerID;
    private String playerName;

    public PlayerDao(String playerID, String playerName){
        this.playerID = playerID;
        this.playerName = playerName;
    }

    public String getPlayerID() {
        return playerID;
    }

    public void setPlayerID(String playerID) {
        this.playerID = playerID;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }
}
