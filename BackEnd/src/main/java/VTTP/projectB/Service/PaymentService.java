package VTTP.projectB.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;

import VTTP.projectB.Models.Premium;
import VTTP.projectB.Models.StripeResponse;
import VTTP.projectB.Models.Exceptions.FailedPaymentException;
import VTTP.projectB.Repository.mySQLRepository;

@Service
public class PaymentService {
    @Value("${stripe.public.key}")
    String public_key;

    @Value("${stripe.secret.key}")
    String secret_key;

    @Value("${app.base-url}")
    private String baseUrl;

    @Autowired
    private mySQLRepository sqlRepo;

    @Autowired
    private AchievementService achievementService;

    public StripeResponse makePayment(String email) {
        Premium premium = new Premium();
        Stripe.apiKey = secret_key;

        if (premium == null || premium.getName() == null || premium.getCurrency() == null || premium.getAmount() == null
                || premium.getQuantity() == null) {
            throw new IllegalArgumentException("Premium object data is missing or invalid.");
        }

        SessionCreateParams.LineItem.PriceData.ProductData productData = SessionCreateParams.LineItem.PriceData.ProductData
                .builder()
                .setName(premium.getName())
                .build();

        SessionCreateParams.LineItem.PriceData price = SessionCreateParams.LineItem.PriceData.builder()
                .setCurrency(premium.getCurrency())
                .setUnitAmount(premium.getAmount())
                .setProductData(productData)
                .build();

        SessionCreateParams.LineItem lineItem = SessionCreateParams.LineItem.builder()
                .setQuantity(premium.getQuantity())
                .setPriceData(price)
                .build();

        SessionCreateParams params = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl(
                        baseUrl + "/healthtracker/payment/success?id={CHECKOUT_SESSION_ID}&email=" + email)
                .setCancelUrl(baseUrl + "/healthtracker/home")
                .addLineItem(lineItem)
                .build();

        Session session = null;
        try {
            session = Session.create(params);
            StripeResponse response = new StripeResponse();
            response.setMessage("Payment being made");
            response.setStatus("Paying");
            response.setSessionId(session.getId());
            response.setSessionUrl(session.getUrl());
            return response;
        } catch (StripeException ex) {
            StripeResponse response = new StripeResponse();
            response.setMessage("Payment failed: " + ex.getMessage());
            response.setStatus("Failure");
            return response;
        }
    }

    public void VerifyPayment(String id, String email) throws StripeException {
        Stripe.apiKey = secret_key;

        Session session = Session.retrieve(id);
        if (!"complete".equalsIgnoreCase(session.getStatus()) && !"paid".equalsIgnoreCase(session.getPaymentStatus())) {
            sqlRepo.failedPayment(email);
            throw new FailedPaymentException("The Payment failed");
        } else {
            sqlRepo.successfulPayment(email,id);
            achievementService.unlockPremiumAchievement(email);

        }

    }

}
