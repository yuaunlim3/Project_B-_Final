package VTTP.projectB.JWT;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import VTTP.projectB.Models.Users;
import VTTP.projectB.Repository.mySQLRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private mySQLRepository sqlRepo;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Check if user exists
        boolean userExists = sqlRepo.checkUser(email);
        
        if (!userExists) {
            throw new UsernameNotFoundException("User not found with email: " + email);
        }
        
        // Get user password from repository
        Users user = sqlRepo.getUserJWT(email);

        
        // Return a UserDetails object
        return new User(email, user.getPassword(), new ArrayList<>());
    }
}
