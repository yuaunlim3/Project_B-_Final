package VTTP.projectB.Service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import VTTP.projectB.Repository.mySQLRepository;
import VTTP.projectB.Repository.MongoRepository;
import VTTP.projectB.Models.LeaderboardEntry;

@Service
public class LeaderboardService {
    @Autowired
    private mySQLRepository sqlRepo;
    @Autowired
    private MongoRepository mongoRepository;

    public List<LeaderboardEntry> getWorkouts(){
        return mongoRepository.getWorkoutsLeaderboard();
    }

    public List<LeaderboardEntry> getMeals(){
        return mongoRepository.getMealsLeaderboard();
    }

    public List<LeaderboardEntry> getSteaks(){
        return sqlRepo.getLoginStreakLeaderboard();
    }
}
