package VTTP.projectB.Service;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class MailService {

    @Value("${mailjet.api.key}")
    private String apiKey;

    @Value("${mailjet.api.secret}")
    private String apiSecret;

    private final String URL = "https://api.mailjet.com/v3.1/send";

    public void sendCreateConfirmation(String email, String name) {
        RestTemplate restTemplate = new RestTemplate();

        JsonObject json = Json.createObjectBuilder()
                .add("Messages", Json.createArrayBuilder()
                        .add(Json.createObjectBuilder()
                                .add("From", Json.createObjectBuilder()
                                        .add("Email", "limyuaun3@gmail.com")
                                        .add("Name", "Admin"))
                                .add("To", Json.createArrayBuilder()
                                        .add(Json.createObjectBuilder()
                                                .add("Email", email)
                                                .add("Name", name)))
                                .add("Subject", "Confirmation Email")
                                .add("TextPart", "Dear user, your account has been created successfully.")
                                .add("HTMLPart",
                                        "<h3>Dear user,</h3><p>Your account has been created successfully.</p>")))
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBasicAuth(apiKey, apiSecret);

        HttpEntity<String> requestEntity = new HttpEntity<>(json.toString(), headers);

        ResponseEntity<String> response = restTemplate.exchange(URL, HttpMethod.POST, requestEntity, String.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            System.out.println("Email sent successfully!");
        } else {
            System.out.println("Failed to send email: " + response.getBody());
        }
    }

    public void sendCode(String email, String name, String code) {
        RestTemplate restTemplate = new RestTemplate();

        JsonObject json = Json.createObjectBuilder()
                .add("Messages", Json.createArrayBuilder()
                        .add(Json.createObjectBuilder()
                                .add("From", Json.createObjectBuilder()
                                        .add("Email", "limyuaun3@gmail.com")
                                        .add("Name", "Admin"))
                                .add("To", Json.createArrayBuilder()
                                        .add(Json.createObjectBuilder()
                                                .add("Email", email)
                                                .add("Name", name)))
                                .add("Subject", "Forget Password Code")
                                .add("TextPart", "Dear user, Your verification code is: " + code)
                                .add("HTMLPart",
                                        "<h3>Dear user,</h3><p>Your verification code is: <b>" + code + "</b></p>")))
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBasicAuth(apiKey, apiSecret);

        HttpEntity<String> requestEntity = new HttpEntity<>(json.toString(), headers);

        ResponseEntity<String> response = restTemplate.exchange(URL, HttpMethod.POST, requestEntity, String.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            System.out.println("Email sent successfully!");
        } else {
            System.out.println("Failed to send email: " + response.getBody());
        }
    }

    public void sendSubscriptionReminder(String email, String name, int daysRemaining) {
        RestTemplate restTemplate = new RestTemplate();
    
        JsonObject json = Json.createObjectBuilder()
                .add("Messages", Json.createArrayBuilder()
                        .add(Json.createObjectBuilder()
                                .add("From", Json.createObjectBuilder()
                                        .add("Email", "limyuaun3@gmail.com")
                                        .add("Name", "Admin"))
                                .add("To", Json.createArrayBuilder()
                                        .add(Json.createObjectBuilder()
                                                .add("Email", email)
                                                .add("Name", name)))
                                .add("Subject", "Your Premium Subscription is Expiring Soon")
                                .add("TextPart", "Dear " + name + ", your premium subscription will expire in " + daysRemaining + " days. Please renew to continue enjoying premium benefits.")
                                .add("HTMLPart",
                                        "<h3>Dear " + name + ",</h3>" +
                                        "<p>Your premium subscription will expire in <b>" + daysRemaining + " days</b>.</p>" +
                                        "<p>Please renew your subscription to continue enjoying all premium features.</p>" +
                                        "<p>Log in to your account to renew now.</p>" +
                                        "<p>Thank you for your continued support!</p>")))
                .build();
    
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBasicAuth(apiKey, apiSecret);
    
        HttpEntity<String> requestEntity = new HttpEntity<>(json.toString(), headers);
    
        ResponseEntity<String> response = restTemplate.exchange(URL, HttpMethod.POST, requestEntity, String.class);
    
        if (response.getStatusCode().is2xxSuccessful()) {
            System.out.println("Subscription reminder email sent successfully!");
        } else {
            System.out.println("Failed to send subscription reminder email: " + response.getBody());
        }
    }
}