package VTTP.projectB.Controller;

import java.io.StringReader;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.stripe.exception.StripeException;

import VTTP.projectB.Models.StripeResponse;
import VTTP.projectB.Service.PaymentService;
import jakarta.json.Json;
import jakarta.json.JsonObject;

@RestController
@RequestMapping("/app")
public class PaymentController {
    @Autowired private PaymentService paymentService;

    @GetMapping("/payment")
    public ResponseEntity<String> makePayment(@RequestParam String email){
        StripeResponse response = paymentService.makePayment(email);
        return ResponseEntity.ok(response.toJson().toString());
    }

    @PostMapping("/verify")
    public ResponseEntity<String> verifyPayment(@RequestBody String payload) throws StripeException{

        JsonObject json = Json.createReader(new StringReader(payload)).readObject();

        paymentService.VerifyPayment(json.getString("id"),json.getString("email"));

        return null;
    }


}
