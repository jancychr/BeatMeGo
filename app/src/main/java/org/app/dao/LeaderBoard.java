package org.app.dao;

public class LeaderBoard {

    private int userrank;
    private String username;
    private Long userscore;

    public LeaderBoard(int userrank, String username, Long userscore){
        this.userrank = userrank;
        this.username = username;
        this.userscore = userscore;
    }

    public int getUserrank() {
        return this.userrank;
    }

    public void setUserrank(int userrank) {
        this.userrank = userrank;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Long getUserscore() {
        return this.userscore;
    }

    public void setUserscore(Long userscore) {
        this.userscore = userscore;
    }
}
