package login.tomcat.web;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api/V1/")
public class ErrorController {

    @GetMapping("error")
    public String handleError() {
        // Custom error handling logic
        return "error"; // This should correspond to the name of your error view template
    }
}
