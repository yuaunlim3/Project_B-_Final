package VTTP.projectB.Service;

import java.io.StringReader;
import java.time.LocalDate;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import VTTP.projectB.Models.NewUser;
import VTTP.projectB.Repository.MongoRepository;
import VTTP.projectB.Repository.mySQLRepository;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;

@Service
public class CreateService {

    private final Logger logger = Logger.getLogger(CreateService.class.getName());
    @Autowired
    private mySQLRepository sqlRepo;
    @Autowired
    private MongoRepository mongoRepo;

    public NewUser fromJson(String payload) {
        JsonReader jsonReader = Json.createReader(new StringReader(payload));
        JsonObject json = jsonReader.readObject();
        NewUser newUser = new NewUser();
        newUser.setActLevel(json.getString("activity_level"));
        newUser.setAge(json.getInt("age"));
        newUser.setEmail(json.getString("email"));
        newUser.setGender(json.getString("gender"));
        newUser.setHeight(json.getJsonNumber("height").longValue());
        newUser.setName(json.getString("name"));
        newUser.setPassword(json.getString("password"));
        newUser.setWeight(json.getJsonNumber("weight").longValue());
        newUser.setAim(json.getString("target"));
        if (json.getString("subscription") == "premium") {
            newUser.setSubscription(json.getString("subscription"));
        }

        newUser.setTargetCal();

        return newUser;
    }

    @Transactional
    public boolean saveUser(NewUser newUser) {
        if (sqlRepo.checkUser(newUser.getEmail())) {
            return true;
        }

        logger.info(newUser.toString());
        sqlRepo.createUser(newUser);
        mongoRepo.createUser(newUser);

        return false;
    }

    public String getName(NewUser newUser) {
        return sqlRepo.getName(newUser.getEmail());
    }

}
