package VTTP.projectB.Repository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import com.mongodb.client.result.UpdateResult;

import VTTP.projectB.MongoQuery;
import VTTP.projectB.Models.MealsInfo;
import VTTP.projectB.Models.NewUser;
import VTTP.projectB.Models.Workout;
import VTTP.projectB.Models.LeaderboardEntry;

@Repository
public class MongoRepository {
    @Autowired
    private MongoTemplate template;

    public void createUser(NewUser newUser) {
        Document toInsert = toSave(newUser);
        template.insert(toInsert, MongoQuery.C_USER);
    }

    private Document toSave(NewUser user) {
        LocalDate today = LocalDate.now();
        Document doc = new Document();
    
        doc.append("name", user.getName());
        doc.append("gender", user.getGender());
        doc.append("age", user.getAge());
        doc.append("height", user.getHeight());
        doc.append("weight", user.getWeight());
        doc.append("activity_level", user.getActLevel());
        doc.append("target", user.getAim());
        doc.append("target calories", user.getTargetCal());
        doc.append("net calories", 0);
        doc.append("meals count", 0);
        doc.append("workouts count", 0);
        doc.append("date", today.toString());
        
        doc.append("meals", new LinkedList<Document>());
        doc.append("workouts", new LinkedList<Document>());
        doc.append("planned_workouts", new LinkedList<Document>());
        
        doc.append("today_meals", new LinkedList<Document>());
        doc.append("today_workouts", new LinkedList<Document>());
        doc.append("missed_workout", 0);
        
        Document workoutData = new Document();
        workoutData.append("date", today.toString());
        workoutData.append("total_calories", 0.0);
        List<String> workoutTypes = List.of("Cardio", "Gym", "Strength", "Yoga", "Stretching", "Sports");
        for (String type : workoutTypes) {
            workoutData.append(type, 0.0);
        }
        
        Document mealData = new Document();
        mealData.append("date", today.toString());
        mealData.append("total_fats", 0.0);
        mealData.append("total_proteins", 0.0);
        mealData.append("total_sugar", 0.0);
        mealData.append("total_calories", 0.0);
        
        LinkedList<Document> workouts = new LinkedList<>();
        workouts.add(workoutData);
        doc.append("workouts", workouts);
        
        LinkedList<Document> meals = new LinkedList<>();
        meals.add(mealData);
        doc.append("meals", meals);
    
        return doc;
    }
    public NewUser getInfo(NewUser user) {
        Query query = Query.query(Criteria.where("name").regex(user.getName(), "i"));
        Document userInfo = template.find(query, Document.class, MongoQuery.C_USER).getFirst();

        user.setGender(userInfo.getString("gender"));
        user.setAge(userInfo.getInteger("age"));
        user.setActLevel(userInfo.getString("activity_level"));
        user.setAim(userInfo.getString("target"));
        user.setTargetCal(userInfo.getDouble("target calories"));
        user.setHeight(userInfo.getLong("height"));
        user.setWeight(userInfo.getLong("weight"));
        user.setMissed(userInfo.getInteger("missed_workout"));

        return user;

    }

    public String getTarget(String name) {
        Query query = Query.query(Criteria.where("name").regex(name, "i"));
        Document userInfo = template.find(query, Document.class, MongoQuery.C_USER).getFirst();

        return userInfo.getString("target_calories");

    }

    public String getAim(String name) {
        Query query = Query.query(Criteria.where("name").regex(name, "i"));
        Document userInfo = template.find(query, Document.class, MongoQuery.C_USER).getFirst();

        return userInfo.getString("target");

    }

    public void login(String name) {
        Query query = new Query(Criteria.where("name").is(name));
        Document userDoc = template.findOne(query, Document.class, MongoQuery.C_USER);
    
        if (userDoc != null) {
            LocalDate today = LocalDate.now();
            String lastLoginDateString = userDoc.getString("date");
            LocalDate lastLoginDate = LocalDate.parse(lastLoginDateString);
    
            if (!today.equals(lastLoginDate)) {
                int missedWorkoutCount = userDoc.getInteger("missed_workout", 0);
                List<Document> plannedWorkouts = (List<Document>) userDoc.get("planned_workouts");
                List<Document> updatedWorkouts = new ArrayList<>();
                int newMissedCount = 0;
                
                if (plannedWorkouts != null) {
                    for (Document workout : plannedWorkouts) {
                        String workoutDateStr = workout.getString("date");
                        try {
                            LocalDate workoutDate = LocalDate.parse(workoutDateStr);
                            
                            if (workoutDate.isBefore(today)) {
                                newMissedCount++;
                            } else {
                                updatedWorkouts.add(workout);
                            }
                        } catch (Exception e) {
                            updatedWorkouts.add(workout);
                        }
                    }
                }

                Update dateUpdate = new Update().set("date", today.toString());
                dateUpdate.set("net calories", 0.0);
    
                dateUpdate.set("planned_workouts", updatedWorkouts);
                dateUpdate.set("missed_workout", missedWorkoutCount + newMissedCount);
    
                Document workoutData = new Document();
                workoutData.append("date", today.toString());
                workoutData.append("total_calories", 0.0);
                List<String> workoutTypes = List.of("Cardio", "Gym", "Strength", "Yoga", "Stretching", "Sports");
                for (String type : workoutTypes) {
                    workoutData.append(type, 0.0);
                }
    
                Document mealData = new Document();
                mealData.append("date", today.toString());
                mealData.append("total_fats", 0.0);
                mealData.append("total_proteins", 0.0);
                mealData.append("total_sugar", 0.0);
                mealData.append("total_calories", 0.0);
    
                dateUpdate.push("workouts", workoutData);
                dateUpdate.push("meals", mealData);
                dateUpdate.set("today_meals", new ArrayList<>());
                dateUpdate.set("today_workouts", new ArrayList<>());
    
                template.updateFirst(query, dateUpdate, MongoQuery.C_USER);
            }
        }
    }

    public void addWorkout(Workout workout, String name) {
        Query query = new Query(Criteria.where("name").is(name)
                .and("workouts.date").is(workout.getDate().toString()));
        Update update = new Update();
        update.inc("workouts.$." + workout.getType(), workout.getDuration());
        update.inc("workouts.$.total_calories", workout.getCalories());

        template.updateFirst(query, update, MongoQuery.C_USER);

        Document data = new Document();
        data.append("date", workout.getDate().toString());
        data.append("details", workout.getDetails());
        data.append("calories", workout.getCalories());
        data.append("type", workout.getType());
        data.append("duration", workout.getDuration());
        Update newUpdate = new Update().push("today_workouts", data).inc("workouts count", 1).inc("net calories",
                -workout.getCalories());
        template.updateFirst(new Query(Criteria.where("name").is(name)), newUpdate, MongoQuery.C_USER);
    }

    public void addPlanWorkout(Workout workout, String name) {

        Document data = new Document();
        data.append("date", workout.getDate().toString());
        data.append("details", workout.getDetails());
        data.append("calories", workout.getCalories());
        data.append("type", workout.getType());
        data.append("duration", workout.getDuration());

        Update newUpdate = new Update().push("planned_workouts", data);
        template.updateFirst(new Query(Criteria.where("name").is(name)), newUpdate, MongoQuery.C_USER);

    }

    public void addMeals(MealsInfo mealsInfo, String name) {

        Query query = new Query(Criteria.where("name").is(name)
                .and("meals.date").is(LocalDate.now().toString()));
        Document meal = new Document();
        meal.append("food", mealsInfo.getFood())
                .append("fat", mealsInfo.getFat())
                .append("protein", mealsInfo.getProtein())
                .append("sugar", mealsInfo.getSugars())
                .append("calories", mealsInfo.getCalories())
                .append("type", mealsInfo.getType());

        Update update = new Update()
                .inc("meals.$.total_fats", mealsInfo.getFat())
                .inc("meals.$.total_proteins", mealsInfo.getProtein())
                .inc("meals.$.total_sugar", mealsInfo.getSugars())
                .inc("meals.$.total_calories", mealsInfo.getCalories())
                .inc("net calories", mealsInfo.getCalories())
                .inc("meals count", 1)
                .inc("net calories", mealsInfo.getCalories())
                .push("today_meals", meal);
        UpdateResult checker = template.updateFirst(query, update, MongoQuery.C_USER);

    }

    public void completedPlannedWorkout(String name, int toRemove) {
        Query query = new Query(Criteria.where("name").is(name));

        Document userDoc = template.find(query, Document.class, MongoQuery.C_USER).getFirst();
        if (userDoc != null) {
            List<Document> plannedWorkouts = userDoc.getList("planned_workouts", Document.class);

            if (plannedWorkouts != null && plannedWorkouts.size() > toRemove) {
                Document workoutDoc = plannedWorkouts.get(toRemove);
                plannedWorkouts.remove(toRemove);
                Update update = new Update().set("planned_workouts", plannedWorkouts);
                UpdateResult result = template.updateFirst(query, update, "users");

                if (result.getMatchedCount() > 0) {
                    addWorkout(Workout.fromDoc(workoutDoc), name);
                }
            }
        }
    }

    public List<Workout> getPlannedWorkouts(String name) {
        Query query = new Query(Criteria.where("name").is(name));
        Document userDoc = template.find(query, Document.class, MongoQuery.C_USER).getFirst();
        List<Document> documents = userDoc.getList("planned_workouts", Document.class);

        return documents.stream().map(Workout::fromDocPlanned).collect(Collectors.toList());
    }

    public List<Workout> getTodayWorkouts(String name) {
        Query query = new Query(Criteria.where("name").is(name));
        Document userDoc = template.find(query, Document.class, MongoQuery.C_USER).getFirst();

        List<Document> documents = userDoc.getList("today_workouts", Document.class);

        if (documents == null || documents.isEmpty()) {
            return new LinkedList<>();
        }

        return documents.stream().map(Workout::fromDoc).collect(Collectors.toList());
    }

    public List<Document> getWorkoutChartData(String name) {
        Query query = new Query(Criteria.where("name").is(name));
        Document userDoc = template.find(query, Document.class, MongoQuery.C_USER).getFirst();

        List<Document> workouts = userDoc.getList("workouts", Document.class);

        return workouts;

    }

    public List<Document> getMealsChartData(String name) {
        Query query = new Query(Criteria.where("name").is(name));
        Document userDoc = template.find(query, Document.class, MongoQuery.C_USER).getFirst();

        List<Document> workouts = userDoc.getList("meals", Document.class);

        return workouts;

    }

    public List<Document> getTodayMeals(String name) {
        Query query = new Query(Criteria.where("name").is(name));
        Document userDoc = template.find(query, Document.class, MongoQuery.C_USER).getFirst();
        List<Document> todayMeals = userDoc.getList("today_meals", Document.class);

        return todayMeals;

    }

    public int getMealsCount(String name) {
        Query query = new Query(Criteria.where("name").is(name));
        Document userDoc = template.findOne(query, Document.class, MongoQuery.C_USER);

        if (userDoc != null) {
            return userDoc.getInteger("meals count");
        }
        return 0;
    }

    public int getWorkoutCount(String name) {
        Query query = new Query(Criteria.where("name").is(name));
        Document userDoc = template.findOne(query, Document.class, MongoQuery.C_USER);

        if (userDoc != null) {
            return userDoc.getInteger("workouts count");
        }
        return 0;
    }

    public void updateField(String name, String fieldName, Object value) {
        Query query = Query.query(Criteria.where("name").is(name));
        Update update = new Update().set(fieldName, value);
        template.updateFirst(query, update, MongoQuery.C_USER);
    }

    public List<LeaderboardEntry> getMealsLeaderboard() {
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.project()
                        .and("name").as("name")
                        .and("meals count").as("score"),
                Aggregation.sort(Sort.Direction.DESC, "score"),
                Aggregation.limit(10));

        AggregationResults<LeaderboardEntry> results = template.aggregate(
                aggregation, "users", LeaderboardEntry.class);

        return results.getMappedResults();
    }

    public List<LeaderboardEntry> getWorkoutsLeaderboard() {
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.project()
                        .and("name").as("name")
                        .and("workouts count").as("score"),
                Aggregation.sort(Sort.Direction.DESC, "score"),
                Aggregation.limit(10));

        AggregationResults<LeaderboardEntry> results = template.aggregate(
                aggregation, "users", LeaderboardEntry.class);

        return results.getMappedResults();
    }


}
