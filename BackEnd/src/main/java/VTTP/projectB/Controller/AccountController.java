package VTTP.projectB.Controller;

import java.io.StringReader;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import VTTP.projectB.JWT.AuthenticationHelper;

import VTTP.projectB.Models.NewUser;
import VTTP.projectB.Models.Users;
import VTTP.projectB.Models.Exceptions.ExistedAccountException;
import VTTP.projectB.Models.Exceptions.InvalidPasswordException;
import VTTP.projectB.Models.Exceptions.InvalidUserException;
import VTTP.projectB.Service.CreateService;
import VTTP.projectB.Service.ForgetService;
import VTTP.projectB.Service.LoginService;
import VTTP.projectB.Service.MailService;
import VTTP.projectB.Service.UserService;
import jakarta.json.Json;
import jakarta.json.JsonException;
import jakarta.json.JsonObject;

@Controller
@RequestMapping(path = "/app")
public class AccountController {

    @Autowired
    private LoginService loginSvc;

    @Autowired
    private CreateService createSvc;

    @Autowired
    private ForgetService forgetSvc;

    @Autowired
    private UserService userService;

    @Autowired
    private AuthenticationHelper authHelper;

    @Autowired private MailService mailService;

    @PostMapping(path = "/login", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> loginUser(@RequestBody String payload) {
        Users user = loginSvc.fromJson(payload);
        Boolean check = loginSvc.checker(user);
        boolean pwChecker = loginSvc.checkpassword(user);
        String name = "";
        String token = "";

        if (check) {
            name = loginSvc.getName(user.getEmail());
            if (!pwChecker) {
                throw new InvalidPasswordException("Wrong password");
            } else {
                loginSvc.login(user);
                token = loginSvc.generateToken(user.getEmail());
            }
        } else {
            throw new InvalidUserException("User is not created");
        }

        JsonObject response = Json.createObjectBuilder()
                .add("name", name)
                .add("token", token)
                .build();

        return ResponseEntity.ok(response.toString());
    }

    @PostMapping(path = "/create", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> createUser(@RequestBody String payload) {
        NewUser newUser = createSvc.fromJson(payload);

        boolean check = createSvc.saveUser(newUser);
        String name = "";
        String token = "";
        if (check) {
            throw new ExistedAccountException("Account already Exist");
        } else {
            name = newUser.getName();
            token = loginSvc.generateToken(newUser.getEmail());
            mailService.sendCreateConfirmation(newUser.getEmail(),newUser.getName());
        }

        JsonObject response = Json.createObjectBuilder()
                .add("name", name)
                .add("token", token)
                .build();

        return ResponseEntity.ok(response.toString());
    }

    @GetMapping(path = "/forgetPassword")
    public ResponseEntity<String> getCode(@RequestParam String email) {
        // get Code from mySQL
        String code = forgetSvc.getCode(email);
        String name = loginSvc.getName(email);
        mailService.sendCode(email,name,code);

        return ResponseEntity.ok(Json.createObjectBuilder().build().toString());
    }

    @PostMapping(path = "/verifyCode")
    public ResponseEntity<String> verifyCode(@RequestBody String payload) {
        JsonObject json = Json.createReader(new StringReader(payload)).readObject();
        forgetSvc.checkCode(json.getString("code"), json.getString("email"));
        return ResponseEntity.ok(Json.createObjectBuilder().build().toString());
    }

    @PostMapping(path = "/resetPassword")
    public ResponseEntity<String> resetPassword(@RequestBody String payload) {
        JsonObject json = Json.createReader(new StringReader(payload)).readObject();
        String password = json.getString("password");
        String email = json.getString("email");

        forgetSvc.resetPassword(password, email);
        return ResponseEntity.ok(Json.createObjectBuilder().build().toString());
    }

    @GetMapping(path = "/checkCode")
    public ResponseEntity<String> checkCode(@RequestParam String code) {
        return ResponseEntity.ok(Json.createObjectBuilder().build().toString());
    }

    @GetMapping(path = "/getInfo", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getInfo(@RequestParam String name) {

        ResponseEntity<?> authCheck = authHelper.checkAuthorization(name);
        if (authCheck != null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied");
        }

        NewUser userInfo = userService.getInfo(name);
        return ResponseEntity.ok(userInfo.toJson().toString());
    }

    @GetMapping(path = "/getInfoByEmail", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getInfoByEmail(@RequestParam String email) {

        String authenticatedEmail = authHelper.getAuthenticatedUserEmail();
        if (authenticatedEmail == null || !authenticatedEmail.equals(email)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied");
        }

        NewUser userInfo = userService.getInfoByEmail(email);
        JsonObject json = Json.createObjectBuilder().add("name", userInfo.getName()).build();
        return ResponseEntity.ok(json.toString());
    }

    @PostMapping(path = "/change", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> makeChanges(@RequestBody String payload) {
        JsonObject change = Json.createReader(new StringReader(payload)).readObject();
        String name = change.getString("name");

        ResponseEntity<?> authCheck = authHelper.checkAuthorization(name);
        if (authCheck != null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied");
        }

        userService.makeChanges(change.getString("item"), change.getString("change"), name);

        return ResponseEntity.ok().body(Json.createObjectBuilder()
                .add("status", "success")
                .add("message", "Update successful")
                .add("change",change.getString("change") )
                .build().toString());

    }
}