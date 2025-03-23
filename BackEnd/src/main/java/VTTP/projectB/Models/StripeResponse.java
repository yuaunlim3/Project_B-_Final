package VTTP.projectB.Models;

import jakarta.json.Json;
import jakarta.json.JsonObject;

import jakarta.json.JsonObjectBuilder;
public class StripeResponse {
    private String status;
    private String message;
    private String sessionId;
    private String sessionUrl;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getSessionUrl() {
        return sessionUrl;
    }

    public void setSessionUrl(String sessionUrl) {
        this.sessionUrl = sessionUrl;
    }

    public JsonObject toJson() {
        JsonObjectBuilder builder = Json.createObjectBuilder();
        if (message != null)
            builder.add("message", message);
        if (status != null)
            builder.add("status", status);
        if (sessionId != null)
            builder.add("id", sessionId);
        if (sessionUrl != null)
            builder.add("url", sessionUrl);

        return builder.build();
    }
}
