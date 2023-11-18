package login.tomcat;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import com.mongodb.UserRepository;
@SpringBootApplication
@EnableMongoRepositories(basePackageClasses= {UserRepository.class})
public class SampleTomcatApplication extends SpringBootServletInitializer{

//@SpringBootApplication
//public class MainApp{
//   public static void main(String[] args) {
//       SpringApplication.run(MainApp.class, args);
//   }


}
