package VTTP.projectB.Models;

import java.time.LocalDate;
import org.bson.Document;

import jakarta.json.Json;
import jakarta.json.JsonObject;

public class Workout {
    private String details;
    private int duration;
    private String type;
    private String status;
    private LocalDate date;
    private double calories;
    public JsonObject toJson(){
        return Json.createObjectBuilder()
                .add("details",this.details)
                .add("type",this.type)
                .add("duration", this.duration)
                .add("status", this.status)
                .add("date", this.date.toString())
                .add("calories",this.calories)
                .build();
    }

    public static Workout fromDocPlanned(Document doc){
        Workout workout = new Workout();
        workout.setCalories(doc.getDouble("calories"));
        workout.setDate(LocalDate.parse(doc.getString("date")));
        workout.setType(doc.getString("type"));
        workout.setStatus("planned");
        workout.setDuration(doc.getInteger("duration"));
        workout.setDetails(doc.getString("details"));

        return workout;
    }

    public static Workout fromDoc(Document doc){
        Workout workout = new Workout();
        workout.setCalories(doc.getDouble("calories"));
        workout.setDate(LocalDate.parse(doc.getString("date")));
        workout.setType(doc.getString("type"));
        workout.setStatus("completed");
        workout.setDuration(doc.getInteger("duration"));
        workout.setDetails(doc.getString("details"));

        return workout;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public LocalDate getDate() {
        return date;
    }
    public void setDate(LocalDate date) {
        this.date = date;
    }
    
    public String getDetails() {
        return details;
    }
    public void setDetails(String details) {
        this.details = details;
    }
    
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public double getCalories() {
        return calories;
    }
    public void setCalories(double calories) {
        this.calories = calories;
    }

    @Override
    public String toString() {
        return "Workout [details=" + details + ", duration=" + duration + ", type=" + type + ", status=" + status
                + ", date=" + date + ", calories=" + calories + "]";
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    
}
