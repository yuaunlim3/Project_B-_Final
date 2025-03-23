package VTTP.projectB.Controller;

import java.io.StringReader;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import VTTP.projectB.JWT.AuthenticationHelper;
import VTTP.projectB.Models.Workout;
import VTTP.projectB.Service.WeatherService;
import VTTP.projectB.Service.WorkoutService;
import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;

@Controller
@RequestMapping(path = "/app")
public class WorkoutController {

    @Autowired
    private WorkoutService workoutService;

    @Autowired
    private WeatherService weatherService;

    @Autowired
    private AuthenticationHelper authHelper;

    @PostMapping(path = "/workout", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getWorkout(@RequestBody String payload) {
        JsonReader jsonReader = Json.createReader(new StringReader(payload));
        JsonObject json = jsonReader.readObject();
        String name = json.getString("name");
        

        ResponseEntity<?> authCheck = authHelper.checkAuthorization(name);
        if (authCheck != null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied");
        }
        
        Workout workout = workoutService.fromJson(json);
        Workout result = workoutService.getWorkout(workout, name);
        return ResponseEntity.ok(result.toJson().toString());
    }

    @PostMapping(path = "/completedWorkout", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> completedPlannedWorkout(@RequestBody String payload) {
        JsonReader jsonReader = Json.createReader(new StringReader(payload));
        JsonObject json = jsonReader.readObject();
        

        ResponseEntity<?> authCheck = authHelper.checkAuthorization(json.getString("name"));
        if (authCheck != null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied");
        }
        
        workoutService.completedWorkout(json.getString("name"), json.getInt("index"));
        return ResponseEntity.ok(Json.createObjectBuilder().build().toString());
    }

    @GetMapping(path = "/getAll")
    public ResponseEntity<String> getPlannedWorkout(@RequestParam String name) {

        ResponseEntity<?> authCheck = authHelper.checkAuthorization(name);
        if (authCheck != null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied");
        }
        
        JsonObject json = workoutService.getWorkouts(name);
        return ResponseEntity.ok(json.toString());
    }

    @GetMapping(path = "/getTotalWorkouts")
    public ResponseEntity<String> getWorkoutData(@RequestParam String name) {

        ResponseEntity<?> authCheck = authHelper.checkAuthorization(name);
        if (authCheck != null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied");
        }
        
        JsonArray data = workoutService.getWorkoutChartData(name);
        return ResponseEntity.ok(data.toString());
    }


    @GetMapping(path = "/WeatherInfo", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getWeatherInfo() {
        JsonObject result = weatherService.getForecast();
        return ResponseEntity.ok(result.getJsonArray("weather").toString());
    }
}