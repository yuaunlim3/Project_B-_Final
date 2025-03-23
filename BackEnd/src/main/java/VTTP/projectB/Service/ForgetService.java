package VTTP.projectB.Service;

import java.security.SecureRandom;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import VTTP.projectB.Models.Exceptions.DuplicateItemException;
import VTTP.projectB.Models.Exceptions.InvalidCodeException;
import VTTP.projectB.Models.Exceptions.InvalidUserException;
import VTTP.projectB.Repository.mySQLRepository;

@Service
public class ForgetService {
    
    @Autowired private mySQLRepository sqlRepo;
    
    
    public String getCode(String email){
        if(!sqlRepo.checkUser(email)){
            throw new InvalidUserException("The email does not exist in database");
        }
        String code = sqlRepo.getCode(email);       
        return code;
    }

    public void checkCode(String code, String email){
        Boolean checker = sqlRepo.checkCode(code, email);
        if(!checker){
            throw new InvalidCodeException("The code given is wrong");
        }
    }

    public void resetPassword(String password, String email){
        if(sqlRepo.checkPassword(email,password)){
            throw new DuplicateItemException("Please give a new password");
        }
        sqlRepo.resetPassword(email, password);

    }


}
