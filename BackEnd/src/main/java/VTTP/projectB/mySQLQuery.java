package VTTP.projectB;

public class mySQLQuery {
        public static final String CHECK_USER = "SELECT count(*) AS checker FROM users WHERE email=?;";
        public static final String CHECK_USERByName = "SELECT count(*) AS checker FROM users WHERE name=?;";
        public static final String CHECK_PASSWORD = "SELECT password  FROM user_data WHERE user_id=?;";
        public static final String GET_EMAIL = "SELECT email  FROM users WHERE name=?;";
        public static final String GET_LAST_LOGIN = "SELECT DATE_FORMAT(last_login, '%Y-%m-%d') AS last_login FROM user_data WHERE user_id=?;";
        public static final String UPDATE_LOGIN = "UPDATE user_data SET last_login=? WHERE user_id=?";
        public static final String GET_STREAK = "SELECT login_streak FROM user_data WHERE user_id=?";
        public static final String UPDATE_STREAK = "UPDATE user_data SET login_streak=? WHERE user_id=?";
        public static final String GET_NAME = "SELECT name FROM users WHERE email=?";
        public static final String SAVE_USER = "INSERT INTO users(name,email)VALUES(?,?);";
        public static final String GET_ID = "SELECT id FROM users WHERE email=?;";
        public static final String SAVE_DETAILS = "INSERT INTO user_data(user_id,password,created_at,last_login,login_streak)VALUES(?,?,?,?,?);";

        public static final String SAVE_OTP = "INSERT INTO userotp(user_id,first,second,third)VALUES(?,?,?,?)";
        public static final String SAVE_TOKEN = "UPDATE user_data  SET token = ? WHERE user_id = ?";

        public static final String GET_USER = "SELECT * FROM users where name=?";
        public static final String GET_OTPS = "SELECT * from userotp where user_id = ?";
        public static final String GET_DETAILS = "SELECT password, DATE_FORMAT(created_at, '%Y-%m-%d') AS created_at, DATE_FORMAT(last_login, '%Y-%m-%d') AS last_login , login_streak FROM user_data WHERE id =?";
        public static final String GET_CODE = """
                        SELECT
                                CASE
                                        WHEN RAND() < 0.33 THEN first
                                        WHEN RAND() < 0.66 THEN second
                                        ELSE third
                                END AS otp
                                FROM userOTP WHERE user_id = ? LIMIT 1;
                        """;

        public static final String UPDATE_PAID = "UPDATE users SET paid=? WHERE email=?";
        public static final String UPDATE_SUB = "UPDATE users SET premium=? WHERE email=?";
        public static final String UPDATE_PASSWORD = "UPDATE user_data SET password=? WHERE user_id=? ";

        public static final String JWT_USER = "SELECT * FROM user_data WHERE user_id=?";

        public static final String ADD_ACHIEVEMENT = "INSERT INTO achievements (user_id, achievement, description, date_earned) VALUES (?, ?, ?, ?)";

        public static final String CHECK_ACHIEVEMENT = "SELECT COUNT(*) AS count FROM achievements WHERE user_id = ? AND achievement = ?";

        public static final String GET_USER_ACHIEVEMENTS = "SELECT id, achievement, description, DATE_FORMAT(date_earned, '%Y-%m-%d') AS date_earned FROM achievements WHERE user_id = ? ORDER BY date_earned DESC ";

        public static final String GET_LOGINSTEAKS = """
                                                SELECT
                                                        u.name,
                                                        ud.login_streak as score
                                                FROM
                                                        users u
                                                JOIN
                                                        user_data ud ON u.id = ud.user_id
                                                ORDER BY
                                                        ud.login_streak DESC
                                                LIMIT 10
                                        """;
        public static final String SAVE_PAYMENT_ID = """
                        UPDATE user_data SET payment_id = ? WHERE user_id = ?
                        """;
        public static final String GET_PAY_DATE ="""
                        SELECT paid FROM users WHERE email = ?
                        """;
}
