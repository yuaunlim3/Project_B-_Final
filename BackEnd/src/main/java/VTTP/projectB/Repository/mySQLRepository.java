package VTTP.projectB.Repository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import VTTP.projectB.mySQLQuery;
import VTTP.projectB.Models.Achievement;
import VTTP.projectB.Models.LeaderboardEntry;
import VTTP.projectB.Models.NewUser;
import VTTP.projectB.Models.Users;

@Repository
public class mySQLRepository {
    @Autowired
    private JdbcTemplate template;

    public Boolean checkUser(String email) {
        SqlRowSet rs = template.queryForRowSet(mySQLQuery.CHECK_USER, email);
        int checker = 0;
        while (rs.next()) {
            checker = rs.getInt("checker");
        }

        return checker == 1;
    }

    public String getEmail(String name){
        SqlRowSet rs = template.queryForRowSet(mySQLQuery.GET_EMAIL,name);
        String email= "";
        while (rs.next()) {
            email = rs.getString("email");
        }

        return email;
    }

    public Users getUserJWT(String email) {
        int id = getID(email);
        SqlRowSet rs = template.queryForRowSet(mySQLQuery.JWT_USER, id);
        Users user = new Users();
        while (rs.next()) {
            user.setEmail(email);
            user.setPassword(rs.getString("password"));
        }

        return user;

    }

    public String getCode(String email) {
        SqlRowSet rs = template.queryForRowSet(mySQLQuery.GET_CODE, email);
        String code = "";
        while (rs.next()) {
            code = rs.getString("otp");
        }

        return code;
    }

    public void createUser(NewUser newUser) {

        template.update(mySQLQuery.SAVE_USER, newUser.getName(), newUser.getEmail());
        int id = getID(newUser.getEmail());

        template.update(mySQLQuery.SAVE_DETAILS, id, newUser.getPassword(), LocalDate.now(), LocalDate.now(), 1);

        String otp1 = UUID.randomUUID().toString().replace("-", "").substring(0, 5);
        String otp2 = UUID.randomUUID().toString().replace("-", "").substring(0, 5);
        String otp3 = UUID.randomUUID().toString().replace("-", "").substring(0, 5);

        template.update(mySQLQuery.SAVE_OTP, id, otp1, otp2, otp3);

    }

    public void loginUser(Users user) {
        int id = getID(user.getEmail());
        LocalDate now = LocalDate.now();
        LocalDate lastLogin = getLastLogin(id);
        template.update(mySQLQuery.UPDATE_LOGIN, now, id);
        if (lastLogin.equals(now.minusDays(1))) {
            int streak = getStreak(id);
            streak++;
            template.update(mySQLQuery.UPDATE_STREAK, streak, id);
        } else if (lastLogin.equals(now)) {
            return;
        } else {
            template.update(mySQLQuery.UPDATE_STREAK, 1, id);
        }
    }

    private LocalDate getLastLogin(int id) {
        SqlRowSet rs = template.queryForRowSet(mySQLQuery.GET_LAST_LOGIN, id);
        String date = "";
        while (rs.next()) {
            date = rs.getString("last_login");
        }
        return LocalDate.parse(date);
    }

    public int getStreak(int id) {
        SqlRowSet rs = template.queryForRowSet(mySQLQuery.GET_STREAK, id);
        int steak = 0;
        while (rs.next()) {
            steak = rs.getInt("login_streak");
        }
        return steak;
    }

    public boolean checkPassword(Users user) {
        int id = getID(user.getEmail());
        SqlRowSet rs = template.queryForRowSet(mySQLQuery.CHECK_PASSWORD, id);
        String password = "";
        while (rs.next()) {
            password = rs.getString("password");
        }

        return user.getPassword().equals(password);
    }

    public boolean checkPassword(String email, String newPassword) {
        int id = getID(email);
        SqlRowSet rs = template.queryForRowSet(mySQLQuery.CHECK_PASSWORD, id);
        String password = "";
        while (rs.next()) {
            password = rs.getString("password");
        }

        return newPassword.equals(password);
    }

    public void resetPassword(String email, String newPassword) {
        int id = getID(email);
        template.update(mySQLQuery.UPDATE_PASSWORD, newPassword, id);
    }

    public String getName(String email) {
        SqlRowSet rs = template.queryForRowSet(mySQLQuery.GET_NAME, email);
        String name = "";
        while (rs.next()) {
            name = rs.getString("name");
        }

        return name;
    }

    public NewUser getInfo(String name) {
        NewUser userInfo = new NewUser();
        userInfo = getUser(userInfo, name);
        int id = getID(userInfo.getEmail());
        userInfo = getDetails(userInfo, id);
        return userInfo;

    }

    public NewUser getInfoByEmail(String email) {
        NewUser userInfo = new NewUser();
        String name = getName(email);
        userInfo = getUser(userInfo, name);
        int id = getID(userInfo.getEmail());
        userInfo = getDetails(userInfo, id);
        return userInfo;

    }

    public int getID(String email) {
        SqlRowSet rs = template.queryForRowSet(mySQLQuery.GET_ID, email);
        int id = 0;
        while (rs.next()) {
            id = rs.getInt("id");
        }

        return id;
    }

    public Date getPayDate(String email){
        SqlRowSet rs = template.queryForRowSet(mySQLQuery.GET_PAY_DATE, email);
        Date date = null;
        while (rs.next()) {
            date = rs.getDate("paid");
        }

        return date;

    }

    private NewUser getUser(NewUser userInfo, String name) {
        SqlRowSet user = template.queryForRowSet(mySQLQuery.GET_USER, name);
        while (user.next()) {
            userInfo.setName(user.getString("name"));
            userInfo.setEmail(user.getString("email"));
            if (user.getBoolean("premium")) {
                userInfo.setSubscription("premium");
                userInfo.setPaid_date(user.getDate("paid"));
            } else {
                userInfo.setSubscription("free");
            }
        }
        return userInfo;
    }

    private NewUser getDetails(NewUser userInfo, int id) {
        SqlRowSet user = template.queryForRowSet(mySQLQuery.GET_DETAILS, id);
        while (user.next()) {
            userInfo.setPassword(user.getString("password"));
            userInfo.setCreated_Date(LocalDate.parse(user.getString("created_at")));
            userInfo.setLast_login(LocalDate.parse(user.getString("last_login")));
            userInfo.setLogin_streak(user.getInt("login_streak"));
        }
        return userInfo;
    }

    public void failedPayment(String email) {
        int userId = getID(email);
        template.update(mySQLQuery.SAVE_PAYMENT_ID,null,userId);
        template.update(mySQLQuery.UPDATE_SUB, false, email);

    }

    public void successfulPayment(String email,String id) {
        int userId = getID(email);
        template.update(mySQLQuery.SAVE_PAYMENT_ID,id,userId);
        template.update(mySQLQuery.UPDATE_PAID, new Date(), email);
        template.update(mySQLQuery.UPDATE_SUB, true, email);
    }

    

    public Boolean checkCode(String code, String email) {
        List<String> otps = new LinkedList<>();
        int id = getID(email);
        SqlRowSet rw = template.queryForRowSet(mySQLQuery.GET_OTPS, id);
        while (rw.next()) {
            String first = rw.getString("first");
            String second = rw.getString("second");
            String third = rw.getString("third");
            otps.add(first);
            otps.add(second);
            otps.add(third);
        }
        return otps.contains(code);
    }

    private boolean hasAchievement(int userId, String achievement) {
        SqlRowSet rs = template.queryForRowSet(mySQLQuery.CHECK_ACHIEVEMENT, userId, achievement);
        if (rs.next()) {
            int count = rs.getInt("count");
            return count > 0;
        }
        return false;
    }

    public void addAchievement(String email, String achievement, String description) {
        int userId = getID(email);
        
        if (hasAchievement(userId, achievement)) {
            return;
        }
        
        try {
            template.update(mySQLQuery.ADD_ACHIEVEMENT, userId, achievement, description, LocalDate.now());

        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    public List<Achievement> getUserAchievements(String name) {
        String email = getEmail(name);
        int userId = getID(email);
        List<Achievement> achievements = new ArrayList<>();

        SqlRowSet rs = template.queryForRowSet(mySQLQuery.GET_USER_ACHIEVEMENTS, userId);
        while (rs.next()) {
            Achievement achievement = new Achievement();
            achievement.setUserId(userId);
            achievement.setAchievement(rs.getString("achievement"));
            achievement.setDescription(rs.getString("description"));
            achievement.setDateEarned(LocalDate.parse(rs.getString("date_earned")));
            achievements.add(achievement);
        }

        return achievements;
    }

    public List<LeaderboardEntry> getLoginStreakLeaderboard() {

        return template.query(mySQLQuery.GET_LOGINSTEAKS, (rs, rowNum) -> {
            LeaderboardEntry entry = new LeaderboardEntry();
            entry.setName(rs.getString("name"));
            entry.setScore(rs.getInt("score"));
            return entry;
        });
    }

}
