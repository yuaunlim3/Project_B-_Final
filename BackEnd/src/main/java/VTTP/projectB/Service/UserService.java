package VTTP.projectB.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import VTTP.projectB.Models.NewUser;
import VTTP.projectB.Models.Exceptions.DuplicateItemException;
import VTTP.projectB.Repository.MongoRepository;
import VTTP.projectB.Repository.mySQLRepository;

@Service
public class UserService {
    @Autowired
    private mySQLRepository sqlRepo;
    @Autowired
    private MongoRepository mongoRepo;

    public NewUser getInfo(String name) {
        NewUser user = sqlRepo.getInfo(name);
        user = mongoRepo.getInfo(user);
        return user;
    }

    public NewUser getInfoByEmail(String email) {
        NewUser user = sqlRepo.getInfoByEmail(email);
        user = mongoRepo.getInfo(user);
        return user;
    }

    // Add this method to your Controller or Service class that handles user updates
    public void makeChanges(String field, String change, String name) {
        switch (field.toLowerCase()) {
            case "password":
                updatePasswordInMySQL(change, name);
                break;
            case "height":
            case "weight":
            case "target":
            case "activity_level":
                updateFieldInMongoDB(field.toLowerCase(), change, name);
                break;
            default:
                throw new IllegalArgumentException("Invalid field: " + field);
        }
    }

    private void updatePasswordInMySQL(String newPassword, String name) {
        String email = sqlRepo.getEmail(name);

        if (sqlRepo.checkPassword(email, newPassword)) {
            throw new DuplicateItemException("New password is the same as the current password");
        }

        sqlRepo.resetPassword(email, newPassword);
    }

    private void updateFieldInMongoDB(String fieldName, String newValue, String name) {
        NewUser currentUser = new NewUser();
        currentUser.setName(name);
        currentUser = mongoRepo.getInfo(currentUser);

        Object currentValue = getCurrentValue(currentUser, fieldName);
        Object typedValue = convertToAppropriateType(fieldName, newValue);

        if (String.valueOf(currentValue).equals(String.valueOf(typedValue))) {
            throw new DuplicateItemException("The new value is the same as the current value");
        }

        mongoRepo.updateField(name, fieldName, typedValue);

        if (fieldName.equals("height") || fieldName.equals("weight") || fieldName.equals("activity_level")) {
            recalculateTargetCalories(name);
        }
    }

    private Object getCurrentValue(NewUser user, String fieldName) {
        switch (fieldName) {
            case "height":
                return user.getHeight();
            case "weight":
                return user.getWeight();
            case "activity_level":
                return user.getActLevel();
            case "target":
                return user.getAim();
            default:
                return null;
        }
    }

    private Object convertToAppropriateType(String fieldName, String value) {
        switch (fieldName) {
            case "height":
            case "weight":
                return Long.parseLong(value);
            case "activity_level":
                validateActivityLevel(value);
                return value;
            case "target":
                validateTarget(value);
                return value;
            default:
                return value;
        }
    }

    private void validateActivityLevel(String level) {
        String[] validLevels = { "sedentary", "light", "moderate", "active", "very_active" };
        boolean isValid = java.util.Arrays.asList(validLevels).contains(level);

        if (!isValid) {
            throw new IllegalArgumentException("Invalid activity level: " + level);
        }
    }

    private void validateTarget(String target) {
        String[] validTargets = { "maintain", "lose", "gain" };
        boolean isValid = java.util.Arrays.asList(validTargets).contains(target);

        if (!isValid) {
            throw new IllegalArgumentException("Invalid target: " + target);
        }
    }

    private void recalculateTargetCalories(String name) {
        NewUser user = new NewUser();
        user.setName(name);
        user = mongoRepo.getInfo(user);

        String gender = user.getGender();
        int age = user.getAge();
        long height = user.getHeight();
        long weight = user.getWeight();
        String activityLevel = user.getActLevel();
        String target = user.getAim();

        double bmr;
        if (gender.equalsIgnoreCase("male")) {
            bmr =  10 * weight + 6.25 * height - 5 * age + 5;
        } else {
            bmr = 10 * weight + 6.25 * height - 5 * age - 161;
        }

        double activityMultiplier;
        switch (activityLevel) {
            case "sedentary":
                activityMultiplier = 1.2;
                break;
            case "light":
                activityMultiplier = 1.375;
                break;
            case "moderate":
                activityMultiplier = 1.55;
                break;
            case "active":
                activityMultiplier = 1.725;
                break;
            case "very_active":
                activityMultiplier = 1.9;
                break;
            default:
                activityMultiplier = 1.2;
        }

        double tdee = bmr * activityMultiplier;

        double targetCalories;
        switch (target) {
            case "lose":
                targetCalories = tdee - 500; 
                break;
            case "gain":
                targetCalories = tdee + 500; 
                break;
            default: 
                targetCalories = tdee;
        }


        mongoRepo.updateField(name, "target calories", targetCalories);
    }

}
