package VTTP.projectB.Models;

import java.time.LocalDate;
import java.util.Date;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;

public class NewUser {
    /*
     * {"name":"sfdsf","email":"fdsdfsd","password":"fsdfdfsd","height":21,"weight":
     * 123,"age":123,
     * "gender":"male","activity_level":"light","target":"lose","subscription":
     * "free","paymentMethod":"","cardNumber":"","expiryDate":"","cvv":""}
     */
    private String name;
    private String email;
    private String password;
    private Long height;
    private Long weight;
    private int age;
    private String aim;
    private String gender;
    private String actLevel;
    private String subscription;
    private LocalDate created_Date;
    private LocalDate last_login;
    private double targetCal;
    private int login_streak;
    private Date paid_date;
    private int missed;
    

    public NewUser() {
        this.subscription = "free";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Long getHeight() {
        return height;
    }

    public void setHeight(Long height) {
        this.height = height;
    }

    public Long getWeight() {
        return weight;
    }

    public void setWeight(Long weight) {
        this.weight = weight;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getAim() {
        return aim;
    }

    public void setAim(String aim) {
        this.aim = aim;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getActLevel() {
        return actLevel;
    }

    public void setActLevel(String actLevel) {
        this.actLevel = actLevel;
    }

    public String getSubscription() {
        return subscription;
    }

    public void setSubscription(String subscription) {
        this.subscription = subscription;
    }

    public LocalDate getCreated_Date() {
        return created_Date;
    }

    public void setCreated_Date(LocalDate created_Date) {
        this.created_Date = created_Date;
    }
    public LocalDate getLast_login() {
        return last_login;
    }

    public void setLast_login(LocalDate last_login) {
        this.last_login = last_login;
    }

    public void setTargetCal(double targetCal) {
        this.targetCal = targetCal;
    }

    public int getLogin_streak() {
        return login_streak;
    }

    public void setLogin_streak(int login_streak) {
        this.login_streak = login_streak;
    }

    public double getTargetCal() {
        return targetCal;
    }

    public void setTargetCal() {
        double bmr;// Basal Metablic Rate
        if (this.gender.equalsIgnoreCase("male")) {
            bmr = 10 * this.weight + 6.25 * this.height - 5 * this.age + 5;
        } else {
            bmr = 10 * this.weight + 6.25 * this.height - 5 * this.age - 161;
        }
        double activityMultiplier;
        switch (this.actLevel.toLowerCase()) {
            case "sedentary":
                activityMultiplier = 1.2;
                break;
            case "light":
                activityMultiplier = 1.375;
                break;
            case "moderate":
                activityMultiplier = 1.55;
                break;
            case "active":
                activityMultiplier = 1.725;
                break;
            case "very_active":
                activityMultiplier = 1.9;
                break;
            default:
                activityMultiplier = 1.55;
                break;
        }

        switch (this.aim.toLowerCase()) {
            case "maintain":
                this.targetCal = bmr * activityMultiplier;
            case "lose":
                this.targetCal = bmr * activityMultiplier - 500;
            case "gain":
                this.targetCal = bmr * activityMultiplier + 500;
            default:
                this.targetCal = bmr * activityMultiplier;
        }
    }

    public JsonObject toJson() {
        JsonObjectBuilder jsonObjectBuilder = Json.createObjectBuilder();

        jsonObjectBuilder.add("name", this.name);
        jsonObjectBuilder.add("email", this.email);
        jsonObjectBuilder.add("password", this.password);
        jsonObjectBuilder.add("height", this.height);
        jsonObjectBuilder.add("weight", this.weight);
        jsonObjectBuilder.add("created_at", this.created_Date.toString());
        jsonObjectBuilder.add("age", this.getAge());
        jsonObjectBuilder.add("target", this.aim);
        jsonObjectBuilder.add("target_cal", this.targetCal);
        jsonObjectBuilder.add("gender", this.gender);
        jsonObjectBuilder.add("activity_level", this.actLevel);
        jsonObjectBuilder.add("subscription", this.subscription);
        jsonObjectBuilder.add("last_login", this.last_login.toString());
        if(this.subscription.equals("premium")){
            jsonObjectBuilder.add("paid_date",this.paid_date.toString());
        }
        jsonObjectBuilder.add("login_streak", this.login_streak);
        jsonObjectBuilder.add("missed", this.missed);

        return jsonObjectBuilder.build();
    }

    
    public Date getPaid_date() {
        return paid_date;
    }

    public void setPaid_date(Date paid_date) {
        this.paid_date = paid_date;
    }

    @Override
    public String toString() {
        return "NewUser [name=" + name + ", email=" + email + ", password=" + password + ", height=" + height
                + ", weight=" + weight + ", age=" + age + ", aim=" + aim + ", gender=" + gender + ", actLevel="
                + actLevel + ", subscription=" + subscription + ", created_Date=" + created_Date + ", last_login="
                + last_login + ", targetCal=" + targetCal + ", login_streak=" + login_streak + ", paid_date="
                + paid_date + "]";
    }

    public int getMissed() {
        return missed;
    }

    public void setMissed(int missed) {
        this.missed = missed;
    }

    


}
