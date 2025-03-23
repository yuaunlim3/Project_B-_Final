package VTTP.projectB.Models;

import java.time.LocalDate;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;

public class Weather {
    private LocalDate date;
    private double maxTemp;
    private double minTemp;
    private double avgTemp;
    private String description;
    private String image;
    public LocalDate getDate() {
        return date;
    }
    public void setDate(LocalDate date) {
        this.date = date;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public String getImage() {
        return image;
    }
    public void setImage(String image) {
        this.image = image;
    }
    public double getMaxTemp() {
        return maxTemp;
    }
    public void setMaxTemp(double maxTemp) {
        this.maxTemp = maxTemp;
    }
    public double getMinTemp() {
        return minTemp;
    }
    public void setMinTemp(double minTemp) {
        this.minTemp = minTemp;
    }
    public double getAvgTemp() {
        return avgTemp;
    }
    public void setAvgTemp(double avgTemp) {
        this.avgTemp = avgTemp;
    }
    public JsonObject toJson(){
        JsonObjectBuilder jsonObjectBuilder = Json.createObjectBuilder();
        jsonObjectBuilder.add("date",this.date.toString());
        jsonObjectBuilder.add("description",this.description);
        jsonObjectBuilder.add("minTemp",this.minTemp);
        jsonObjectBuilder.add("maxTemp",this.maxTemp);
        jsonObjectBuilder.add("avgTemp",this.avgTemp);
        jsonObjectBuilder.add("image",this.image);

        return jsonObjectBuilder.build();
    }
    @Override
    public String toString() {
        return "Weather [date=" + date + ", maxTemp=" + maxTemp + ", minTemp=" + minTemp + ", avgTemp=" + avgTemp
                + ", description=" + description + ", image=" + image + "]";
    }
    

    
    
}
