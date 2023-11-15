package login.tomcat.service;
//UserService.java
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.mongodb.User;
import com.mongodb.UserRepository;

@Service
public class UserService {

 private final UserRepository userRepository;
 private final PasswordEncoder passwordEncoder;

 public UserService(UserRepository userRepository) {
     this.userRepository = userRepository;
     this.passwordEncoder = new BCryptPasswordEncoder();
 }

 public User createUser(String username, String password, String[] roles) {
     // Hash the password before storing it
     String hashedPassword = passwordEncoder.encode(password);

     User user = new User();
     user.setUsername(username);
     user.setHash(hashedPassword);
     user.setRoles(roles);

     return userRepository.save(user);
 }

 public User findByUsername(String username) {
     return userRepository.findByUsername(username);
 }
}
