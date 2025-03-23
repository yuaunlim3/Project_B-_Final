package VTTP.projectB.Service;

import java.io.StringReader;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import VTTP.projectB.Models.Weather;
import VTTP.projectB.Repository.RedisRepository;
import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import jakarta.json.JsonReader;

@Service
public class WeatherService {
    private final String API_URL = "https://api.weatherbit.io/v2.0/forecast/daily";
    @Value("${weatherbit_api_key}")
    private String api_key;
    @Value("${weatherbit_country}")
    private String country;

    @Autowired private RedisRepository redisRepo;


    public JsonObject getForecast(){
        JsonObject result;
        if(redisRepo.check()){
            result = redisRepo.getData();
        }
        else{
            List<Weather> weathers = getForecastFromApi();
            JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
            for(Weather weather:weathers){
                JsonObject json = weather.toJson();
                arrayBuilder.add(json);
            }
            JsonArray array = arrayBuilder.build();

            result = Json.createObjectBuilder().add("weather",array).build();

            redisRepo.saveWeatherInfo(result);

        }
        return result;
    }

    private List<Weather> getForecastFromApi() {
        String params = UriComponentsBuilder.fromUriString(API_URL)
                .queryParam("city", country)
                .queryParam("key", api_key)
                .toUriString();
        RestTemplate template = new RestTemplate();

        List<Weather> weatherList = new LinkedList<>();

        try {
            RequestEntity req = RequestEntity.get(params).build();
            ResponseEntity<String> responseEntity = template.exchange(req, String.class);
            JsonReader jsonReader = Json.createReader(new StringReader(responseEntity.getBody()));
            JsonObject weatherData = jsonReader.readObject();

            JsonArray array = weatherData.getJsonArray("data");
            for (int i = 0; i < array.size(); i++) {
                JsonObject weatherInfo = array.getJsonObject(i);
                Weather weather = new Weather();
                weather.setDate(LocalDate.parse(weatherInfo.getString("valid_date")));
                weather.setMaxTemp(weatherInfo.getJsonNumber("max_temp").doubleValue());
                weather.setMinTemp(weatherInfo.getJsonNumber("min_temp").doubleValue());
                weather.setAvgTemp(weatherInfo.getJsonNumber("temp").doubleValue());

                JsonObject weatherDes = weatherInfo.getJsonObject("weather");
                weather.setImage(weatherDes.getString("icon"));
                weather.setDescription(weatherDes.getString("description"));

                weatherList.add(weather);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return weatherList;
    }
}
