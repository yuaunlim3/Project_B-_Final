package VTTP.projectB.Service;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatCompletionResult;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.service.OpenAiService;

import VTTP.projectB.Repository.MongoRepository;

@Service
public class AdviceService{
    @Autowired
    private MongoRepository mongoRepo;
    @Value("${openai.api.key}")
    private String apiKey;

    public String getAdvice(String name) {
        List<Document> todayMeals = mongoRepo.getTodayMeals(name);
        String target = mongoRepo.getTarget(name);
        String aim = mongoRepo.getAim(name);
        if (todayMeals.size() == 0) {
            return "You have no meals added for today";
        }
        StringBuilder prompt = new StringBuilder();
        prompt.append("Please analyze the meals taken \n\n");
        for (Document meal : todayMeals) {
            prompt.append("Food Item: ").append(meal.getString("food")).append("\n");
            prompt.append("Fat: ").append(meal.getDouble("fat")).append("g \n");
            prompt.append("Sugar: ").append(meal.getDouble("sugar")).append("g \n");
            prompt.append("Protein: ").append(meal.getDouble("protein")).append("g \n");
            prompt.append("Calories: ").append(meal.getDouble("calories")).append("kcal \n");
        }
        
        prompt.append("\nThe target calories for the day is: ").append(target).append(" kcal.\n");
        prompt.append("The aim is to ").append(aim).append(" weight.\n\n");
        prompt.append("Based on the meals provided, can you suggest how to improve the nutritional intake for future meals? Please provide simple and concise advice in no more than two sentences.");
        OpenAiService openAiService = new OpenAiService(apiKey);
        List<ChatMessage> messages = new ArrayList<>();
        messages.add(new ChatMessage("user", prompt.toString()));

        ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder()
                .model("gpt-3.5-turbo")
                .messages(messages)
                .temperature(0.7)
                .maxTokens(500)
                .build();

        ChatCompletionResult response = openAiService.createChatCompletion(chatCompletionRequest);

        return response.getChoices().get(0).getMessage().getContent();
    }
}
