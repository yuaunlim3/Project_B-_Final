package VTTP.projectB.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class AngularController {
    
    @RequestMapping(value = {
        "/",
        "/healthtracker/**", 
        "/user/**"
    })
    public String forward() {
        return "forward:/index.html";
    }
}
