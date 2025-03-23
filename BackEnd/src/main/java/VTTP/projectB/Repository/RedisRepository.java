package VTTP.projectB.Repository;

import java.io.StringReader;
import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;

@Repository
public class RedisRepository {
    @Autowired
    @Qualifier("redis-0")
    private RedisTemplate template;

    public void saveWeatherInfo(JsonObject weatherData){
        template.opsForHash().put("WeatherData","last_update",LocalDate.now().toString());
        template.opsForHash().put("WeatherData","data", weatherData.toString());
    }

    public boolean check(){
        String dateString = (String) template.opsForHash().get("WeatherData", "last_update");
        if (dateString == null) {
            return false;
        }
        LocalDate lastUpdate = LocalDate.parse(dateString);
        return lastUpdate.equals(LocalDate.now());
    }

    public JsonObject getData(){
        String data = (String) template.opsForHash().get("WeatherData", "data");
        JsonReader reader = Json.createReader(new StringReader(data));

        return reader.readObject();
    }
}
