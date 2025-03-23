package VTTP.projectB.Models;

import java.time.LocalDate;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;

public class Achievement {

    private Integer userId;
    private String achievement;
    private String description;
    private LocalDate dateEarned;


    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getAchievement() {
        return achievement;
    }

    public void setAchievement(String achievement) {
        this.achievement = achievement;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getDateEarned() {
        return dateEarned;
    }

    public void setDateEarned(LocalDate dateEarned) {
        this.dateEarned = dateEarned;
    }

    public JsonObject toJson() {
        JsonObjectBuilder builder = Json.createObjectBuilder()
            .add("achievement", this.achievement)
            .add("description", this.description)
            .add("dateEarned", this.dateEarned.toString());
        
        return builder.build();
    }

    @Override
    public String toString() {
        return "Achievement [userId=" + userId + ", achievement=" + achievement + ", description="
                + description + ", dateEarned=" + dateEarned + "]";
    }
}