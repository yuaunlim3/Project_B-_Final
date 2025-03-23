package VTTP.projectB.Service;

import java.io.StringReader;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import VTTP.projectB.Models.Workout;
import VTTP.projectB.Repository.MongoRepository;
import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;

@Service
public class WorkoutService {
    private final String URL = "https://trackapi.nutritionix.com/v2/natural/exercise";
    @Value("${nutritionix_api_key}")
    private String api_key;
    @Value("${nutritionix_api_id}")
    private String api_id;

    @Autowired
    private MongoRepository mongoRepo;

    @Autowired private AchievementService achievementService;

    public Workout getWorkout(Workout workout, String name) {
        String workoutDetails = workout.getDetails();
        String duration = Double.toString(workout.getDuration());
        String requestBody = "{ \"query\": \"" + workoutDetails + " for " + duration + " minutes" + "\" }";

        RestTemplate template = new RestTemplate();
        try {
            RequestEntity<String> req = RequestEntity
                    .post(URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("X-app-key", api_key)
                    .header("x-app-id", api_id)
                    .body(requestBody);
            ResponseEntity<String> resp = template.exchange(req, String.class);
            String payload = resp.getBody();

            StringReader reader = new StringReader(payload);
            JsonReader jsonReader = Json.createReader(reader);
            JsonObject result = jsonReader.readObject();
            JsonObject workoutData = result.getJsonArray("exercises").getJsonObject(0);
            workout.setCalories(workoutData.getJsonNumber(("nf_calories")).doubleValue());

            if (workout.getStatus().equals("completed")) {
                mongoRepo.addWorkout(workout, name);
                achievementService.checkWorkoutAchievements(name);
            } else {
                mongoRepo.addPlanWorkout(workout, name);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return workout;
    }

    public Workout fromJson(JsonObject json) {
        Workout workout = new Workout();
        workout.setDetails(json.getString("details"));
        workout.setDuration(json.getInt("duration"));
        workout.setType(json.getString("type"));
        workout.setStatus(json.getString("status"));
        workout.setDate(LocalDate.parse(json.getString("date")));
        return workout;
    }

    public void completedWorkout(String name, int index) {
        mongoRepo.completedPlannedWorkout(name, index);
        achievementService.checkWorkoutAchievements(name);
    }

    public JsonObject getWorkouts(String name) {
        List<Workout> plannedWorkout = mongoRepo.getPlannedWorkouts(name);
        List<Workout> workouts = mongoRepo.getTodayWorkouts(name);
        List<Workout> combinedWorkouts = new ArrayList<>(plannedWorkout);
        combinedWorkouts.addAll(workouts);
        JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
        for (Workout wo : combinedWorkouts) {
            JsonObject json = wo.toJson();
            arrayBuilder.add(json);
        }

        return Json.createObjectBuilder().add("workouts", arrayBuilder.build()).build();
    }

    public JsonArray getWorkoutChartData(String name) {
        List<Document> documents = mongoRepo.getWorkoutChartData(name);
        JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
        for (Document document : documents) {
            JsonObject json = Json.createObjectBuilder()
                    .add("date", document.getString("date"))
                    .add("total_calories", document.get("total_calories").toString())
                    .build();
            arrayBuilder.add(json);
        }

        return arrayBuilder.build();
    }
}