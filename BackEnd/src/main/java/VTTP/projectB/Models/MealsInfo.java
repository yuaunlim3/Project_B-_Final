package VTTP.projectB.Models;

import java.util.List;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;

public class MealsInfo {
    private String type;
    private String food;
    private Double calories;
    private Double fat;
    private Double sugars;
    private Double protein;
    private boolean found;



    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getFood() { return food; }
    public void setFood(String food) { this.food = food; }
    public Double getCalories() { return calories; }
    public void setCalories(Double calories) { this.calories = calories; }
    public Double getFat() { return fat; }
    public void setFat(Double fat) { this.fat = fat; }
    public Double getSugars() { return sugars; }
    public void setSugars(Double sugars) { this.sugars = sugars; }
    public Double getProtein() { return protein; }
    public void setProtein(Double protein) { this.protein = protein; }
    public boolean isFound() { return found; }
    public void setFound(boolean found) { this.found = found; }

    
    
    public JsonObject toJson() {
        JsonObjectBuilder jsonObjectBuilder = Json.createObjectBuilder()
                .add("food", this.food)
                .add("found",String.valueOf(this.found));

        if (this.calories != null) jsonObjectBuilder.add("calories", this.calories);
        if (this.fat != null) jsonObjectBuilder.add("fat", this.fat);
        if (this.sugars != null) jsonObjectBuilder.add("sugars", this.sugars);
        if (this.protein != null) jsonObjectBuilder.add("protein", this.protein);


        return jsonObjectBuilder.build();
    }
    @Override
    public String toString() {
        return "MealsInfo [type=" + type + ", food=" + food + ", calories=" + calories + ", fat=" + fat + ", sugars="
                + sugars + ", protein=" + protein + ", found=" + found + "]";
    }





}
