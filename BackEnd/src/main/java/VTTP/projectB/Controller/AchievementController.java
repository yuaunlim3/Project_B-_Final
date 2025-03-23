package VTTP.projectB.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import VTTP.projectB.JWT.AuthenticationHelper;
import VTTP.projectB.Models.Achievement;
import VTTP.projectB.Service.AchievementService;
import jakarta.json.Json;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;

@RestController
@RequestMapping(path = "/app")
public class AchievementController {

    @Autowired
    private AchievementService achievementService;
    
    @Autowired
    private AuthenticationHelper authHelper;

    @GetMapping(path = "/achievements", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getUserAchievements(@RequestParam String email) {
        ResponseEntity<?> authCheck = authHelper.checkAuthorization(email);
        if (authCheck != null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied");
        }
        
        List<Achievement> achievements = achievementService.getUserAchievements(email);
        
        JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
        for (Achievement achievement : achievements) {
            arrayBuilder.add(achievement.toJson());
        }
        
        JsonObject response = Json.createObjectBuilder()
                .add("achievements", arrayBuilder.build())
                .build();
        
        return ResponseEntity.ok(response.toString());
    }
}