package VTTP.projectB.Models;

public class LeaderboardEntry {
    private String name;  
    private int score;    
    private int rank;    


    public LeaderboardEntry() {}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    @Override
    public String toString() {
        return "LeaderboardEntry [name=" + name + ", score=" + score + ", rank=" + rank + "]";
    }

}