package VTTP.projectB.Service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import VTTP.projectB.Models.Achievement;
import VTTP.projectB.Repository.MongoRepository;
import VTTP.projectB.Repository.mySQLRepository;

@Service
public class AchievementService {
    @Autowired
    private mySQLRepository sqlRepo;

    @Autowired private MongoRepository mongoRepo;

    private final Map<String, String> ACHIEVEMENT_TYPES = Map.of(
            "FIRST_LOGIN", "Logged in for the first time",
            "LOGIN_STREAK_7", "Logged in for 7 consecutive days",
            "LOGIN_STREAK_30", "Logged in for 30 consecutive days",
            "FIRST_MEAL", "Added your first meal",
            "MEAL_MASTER", "Added 50 meals",
            "FIRST_WORKOUT", "Completed your first workout",
            "WORKOUT_WARRIOR", "Completed 10 workouts",
            "WORKOUT_MASTER", "Completed 50 workouts",
            "CALORIE_COUNTER", "Tracked calories for 7 consecutive days",
            "PREMIUM_MEMBER", "Became a premium member");

    public List<Achievement> getUserAchievements(String email) {
        return sqlRepo.getUserAchievements(email);
    }

    public void checkLoginStreakAchievements(String email, int loginStreak) {
        if (loginStreak == 1) {
            unlockAchievement(email, "FIRST_LOGIN");
        }
        if (loginStreak >= 7) {
            unlockAchievement(email, "LOGIN_STREAK_7");
        }
        if (loginStreak >= 30) {
            unlockAchievement(email, "LOGIN_STREAK_30");
        }
    }

    public void checkMealAchievements(String name) {
        String email = sqlRepo.getEmail(name);
        int mealCount = mongoRepo.getMealsCount(name);
        if (mealCount == 1) {
            unlockAchievement(email, "FIRST_MEAL");
        }
        if (mealCount >= 50) {
            unlockAchievement(email, "MEAL_MASTER");
        }
    }

    public void checkWorkoutAchievements(String name) {
        String email = sqlRepo.getEmail(name);
        int workoutCount = mongoRepo.getWorkoutCount(name);
        if (workoutCount == 1) {
            unlockAchievement(email, "FIRST_WORKOUT");
        }
        if (workoutCount >= 10) {
            unlockAchievement(email, "WORKOUT_WARRIOR");
        }
        if (workoutCount >= 50) {
            unlockAchievement(email, "WORKOUT_MASTER");
        }
    }

    public void unlockPremiumAchievement(String email) {
        
        unlockAchievement(email, "PREMIUM_MEMBER");
    }

    private void unlockAchievement(String email,String achievementType) {
        String description = ACHIEVEMENT_TYPES.get(achievementType);
        sqlRepo.addAchievement(email, achievementType, description);
    }
}
