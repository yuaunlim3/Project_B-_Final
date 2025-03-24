package VTTP.projectB.Service;

import java.io.StringReader;
import java.util.LinkedList;
import java.util.List;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import VTTP.projectB.Models.Meals;
import VTTP.projectB.Models.MealsInfo;
import VTTP.projectB.Repository.MongoRepository;
import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;

@Service
public class MealService {

    @Autowired
    private MongoRepository mongoRepository;
    private static final String API_URL = "https://trackapi.nutritionix.com/v2/natural/nutrients";

    @Autowired private AchievementService achievementService;

    @Value("${nutritionix_api_key}")
    private String api_key;
    @Value("${nutritionix_api_id}")
    private String api_id;

    public Meals fromJson(JsonObject json) {
        Meals meal = new Meals();
        meal.setType(json.getString("meal_type"));
        meal.setFood(json.getString("food"));
        meal.setAmount(json.getInt("amount"));
        meal.setServing(json.getString("serving_size"));

        return meal;
    }

    public MealsInfo fromJsonMealsInfo(JsonObject json) {
        MealsInfo mealsInfo = new MealsInfo();
        mealsInfo.setFood(json.getString("food"));
        mealsInfo.setSugars(json.getJsonNumber("sugars").doubleValue());
        mealsInfo.setCalories(json.getJsonNumber("calories").doubleValue());
        mealsInfo.setFat((json.getJsonNumber("fat").doubleValue()));
        mealsInfo.setProtein(json.getJsonNumber("protein").doubleValue());
        mealsInfo.setType(json.getString("type"));
        return mealsInfo;
    }

    public MealsInfo getMealInfo(Meals meal) {
        String query = meal.getAmount() + " " + meal.getServing() + " of " + meal.getFood();
        String requestBody = "{ \"query\": \"" + query + "\" }";
        RestTemplate restTemplate = new RestTemplate();

        MealsInfo mealInfo = new MealsInfo();
        mealInfo.setFood(meal.getFood());

        try {
            RequestEntity<String> req = RequestEntity
                    .post(API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("X-app-key", api_key)
                    .header("x-app-id", api_id)
                    .body(requestBody);

            ResponseEntity<String> resp = restTemplate.exchange(req, String.class);
            String payload = resp.getBody();

            StringReader reader = new StringReader(payload);
            JsonReader jsonReader = Json.createReader(reader);
            JsonObject foodInfo = jsonReader.readObject();

            if (foodInfo.containsKey("foods")) {
                JsonArray items = foodInfo.getJsonArray("foods");
                if (items != null && !items.isEmpty()) {
                    double total_calories = 0.0;
                    double total_fat = 0.0;
                    double total_sugar = 0.0;
                    double total_protein = 0.0;
                    for (int i = 0; i < items.size(); i++) {
                        JsonObject foodItem = items.getJsonObject(i);
                        total_calories += foodItem.getJsonNumber("nf_calories").doubleValue();
                        total_fat += foodItem.getJsonNumber("nf_total_fat").doubleValue();
                        total_sugar += foodItem.getJsonNumber("nf_sugars").doubleValue();
                        total_protein += foodItem.getJsonNumber("nf_protein").doubleValue();

                    }

                    mealInfo.setSugars(total_sugar);
                    mealInfo.setCalories(total_calories);
                    mealInfo.setFat(total_fat);
                    mealInfo.setProtein(total_protein);
                    mealInfo.setFound(true);

                }
            } else {
                mealInfo.setFound(false);
            }
            return mealInfo;

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return mealInfo;
    }

    public void addMeals(MealsInfo mealsInfo, String name) {
        mongoRepository.addMeals(mealsInfo, name);
        achievementService.checkMealAchievements(name);
    }

    public JsonArray getChartData(String name) {
        List<Document> documents = mongoRepository.getMealsChartData(name);
        JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
        for (Document document : documents) {
            JsonObject json = Json.createObjectBuilder()
                    .add("date", document.getString("date"))
                    .add("total_calories", document.getDouble("total_calories").toString())
                    .add("total_fats", document.getDouble("total_fats").toString())
                    .add("total_sugar", document.getDouble("total_sugar").toString())
                    .add("total_proteins", document.getDouble("total_proteins").toString())
                    .build();
            arrayBuilder.add(json);

        }

        return arrayBuilder.build();
    }


    public JsonArray getTodayMeals(String name){
        List<Document> documents = mongoRepository.getTodayMeals(name);
        JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
        for (Document document : documents) {
            //{{food=Apple, fat=0.41, protein=0.63, sugar=25.14, calories=125.84}}
            JsonObject json = Json.createObjectBuilder()
                    .add("food", document.getString("food"))
                    .add("calories", document.getDouble("calories"))
                    .add("fat",document.getDouble("fat"))
                    .add("sugars", document.getDouble("sugar"))
                    .add("protein",document.getDouble("protein"))
                    .add("type",document.getString("type"))
                    .build();
            arrayBuilder.add(json);

        }

        return arrayBuilder.build();
    }
}
