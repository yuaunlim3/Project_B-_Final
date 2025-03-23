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
import VTTP.projectB.Models.Meals;
import VTTP.projectB.Models.MealsInfo;
import VTTP.projectB.Service.AdviceService;
import VTTP.projectB.Service.MealService;
import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;

@Controller
@RequestMapping(path = "/app")
public class MealController {

    @Autowired
    private MealService mealService;

    @Autowired 
    private AdviceService adviceService;

    @Autowired
    private AuthenticationHelper authHelper;

    @PostMapping(path = "/getMealInfo", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getMealInfo(@RequestBody String payload) {
        JsonReader jsonReader = Json.createReader(new StringReader(payload));
        JsonObject json = jsonReader.readObject();
        
     
        if (json.containsKey("name")) {
            String name = json.getString("name");
            ResponseEntity<?> authCheck = authHelper.checkAuthorization(name);
            if (authCheck != null) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied");
            }
        }
        
        Meals meal = mealService.fromJson(json);
        MealsInfo mealsInfo = mealService.getMealInfo(meal);
        return ResponseEntity.ok(mealsInfo.toJson().toString());
    }

    @PostMapping(path = "/saveMeal", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> saveMealInfo(@RequestBody String payload) {
        JsonReader jsonReader = Json.createReader(new StringReader(payload));
        JsonObject json = jsonReader.readObject();
        String name = json.getString("name");
        

        ResponseEntity<?> authCheck = authHelper.checkAuthorization(name);
        if (authCheck != null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied");
        }
        
        MealsInfo mealInfo = mealService.fromJsonMealsInfo(json);
        mealService.addMeals(mealInfo, name);        
        return ResponseEntity.ok(Json.createObjectBuilder().build().toString());
    }
    
    @GetMapping(path = "/getTotalMeals")
    public ResponseEntity<String> getMealData(@RequestParam String name) {

        ResponseEntity<?> authCheck = authHelper.checkAuthorization(name);
        if (authCheck != null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied");
        }
        
        JsonArray data = mealService.getChartData(name);
        return ResponseEntity.ok(data.toString());
    }

    @GetMapping(path = "/getTodayMeals")
    public ResponseEntity<String> getTodayMeals(@RequestParam String name) {
  
        ResponseEntity<?> authCheck = authHelper.checkAuthorization(name);
        if (authCheck != null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied");
        }
        
        JsonArray data = mealService.getTodayMeals(name);
        return ResponseEntity.ok(data.toString());
    }

    @GetMapping(path = "/advice")
    public ResponseEntity<String> getAdvice(@RequestParam String name) {
        String advice = adviceService.getAdvice(name);
        JsonObject json = Json.createObjectBuilder().add("advice", advice).build();
        return ResponseEntity.ok(json.toString());
    }
}