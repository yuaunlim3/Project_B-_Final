package VTTP.projectB.JWT;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import VTTP.projectB.Repository.mySQLRepository;

@Component
public class AuthenticationHelper {
    
    @Autowired
    private mySQLRepository sqlRepo;


    public String getAuthenticatedUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            return authentication.getName();
        }
        return null;
    }

    public boolean isAuthorizedForUser(String requestedUsername) {
        String authenticatedEmail = getAuthenticatedUserEmail();
        if (authenticatedEmail == null) {
            return false;
        }
        
        String nameFromEmail = sqlRepo.getName(authenticatedEmail);
        
        return nameFromEmail.equals(requestedUsername);
    }

    public ResponseEntity<String> checkAuthorization(String requestedUsername) {
        if (!isAuthorizedForUser(requestedUsername)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body("You are not authorized to access this resource");
        }
        return null;
    }
}