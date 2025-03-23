package VTTP.projectB.Service;

import java.io.StringReader;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import VTTP.projectB.JWT.JwtUtil;
import VTTP.projectB.Models.NewUser;
import VTTP.projectB.Models.Users;
import VTTP.projectB.Repository.MongoRepository;
import VTTP.projectB.Repository.mySQLRepository;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;

@Service
public class LoginService {
    @Autowired
    private mySQLRepository sqlRepo;
    @Autowired
    private MongoRepository mongoRepo;

    @Autowired private MailService mailService;


    @Autowired private AchievementService achievementService;

    @Autowired
    private JwtUtil jwtUtil;

    public Users fromJson(String payload) {
        JsonReader jsonReader = Json.createReader(new StringReader(payload));
        JsonObject json = jsonReader.readObject();
        Users user = new Users();
        user.setEmail(json.getString("email"));
        user.setPassword(json.getString("password"));
        return user;
    }

    public Boolean checker(Users user) {
        return sqlRepo.checkUser(user.getEmail());
    }

    public String getName(String email) {
        return sqlRepo.getName(email);
    }

    public boolean checkpassword(Users user) {
        return sqlRepo.checkPassword(user);
    }

    public void login(Users user) {
        sqlRepo.loginUser(user);
        String name = sqlRepo.getName(user.getEmail());
        mongoRepo.login(name);
        int loginStreak = sqlRepo.getStreak(sqlRepo.getID(user.getEmail()));
        achievementService.checkLoginStreakAchievements(user.getEmail(), loginStreak);
        //check payment
        NewUser userInfo = sqlRepo.getInfo(name);
        if(userInfo.getSubscription().equalsIgnoreCase("premium")){
            Date paidDate = sqlRepo.getPayDate(user.getEmail());
            Date currentDate = new Date(); 
            
            long diffInMillies = currentDate.getTime() - paidDate.getTime();
            long diffInDays = diffInMillies / (1000 * 60 * 60 * 24);

            int daysRemaining = 30 - (int)diffInDays;
    
            if (daysRemaining == 5) {
                mailService.sendSubscriptionReminder(user.getEmail(), name, daysRemaining);
            }
            
            if (diffInDays > 30) {
                sqlRepo.failedPayment(user.getEmail());
            } 
        }

    }

    public String generateToken(String email) {
        return jwtUtil.generateToken(email);
    }

}
