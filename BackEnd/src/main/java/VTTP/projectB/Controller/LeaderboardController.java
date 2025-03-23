package VTTP.projectB.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import VTTP.projectB.Service.LeaderboardService;
import VTTP.projectB.Models.LeaderboardEntry;

@Controller
@RequestMapping("/app/leaderboard")
public class LeaderboardController {

    @Autowired private LeaderboardService leaderboardSvc;

    @GetMapping("/meals")
    public ResponseEntity<List<LeaderboardEntry>> getMealsLeaderboard() {
        List<LeaderboardEntry> entries = leaderboardSvc.getMeals();
        return ResponseEntity.ok(addRanks(entries));
    }

    @GetMapping("/workouts")
    public ResponseEntity<List<LeaderboardEntry>> getWorkoutsLeaderboard() {
        List<LeaderboardEntry> entries = leaderboardSvc.getWorkouts();
        return ResponseEntity.ok(addRanks(entries));
    }

    @GetMapping("/login-streak")
    public ResponseEntity<List<LeaderboardEntry>> getLoginStreakLeaderboard() {
        List<LeaderboardEntry> entries = leaderboardSvc.getSteaks();
        return ResponseEntity.ok(addRanks(entries));
    }

    private List<LeaderboardEntry> addRanks(List<LeaderboardEntry> entries) {
        for (int i = 0; i < entries.size(); i++) {
            entries.get(i).setRank(i + 1);
        }
        return entries;
    }
}