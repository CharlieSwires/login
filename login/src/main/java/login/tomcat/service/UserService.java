package login.tomcat.service;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.Security;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;
import java.util.Optional;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
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
     System.out.println("createUser");
     // Hash the password before storing it

     User user = new User();
     user.setUsername(username);
     user.setRoles(roles);
     SecureRandom random = new SecureRandom();
     byte salt[] = new byte[20];
     random.nextBytes(salt);
     user.setSalt(salt);
     String hashedPassword = sha256(salt, password);
     user.setHash(hashedPassword);
     System.out.println(user.toString());
     return userRepository.save(user);
 }

 public Optional<User> findByUsername(String username) {
     return userRepository.findByUsername(username);
 }


/**
 * SecureRandom random = new SecureRandom();
 * byte salt[] = new salt[20];
 * random.nextBytes(salt);
 * @param salt
 * @param input
 * @return
 */
public static String sha256(byte[] salt, String input) {
    Security.addProvider(new BouncyCastleProvider());

    Base64.Encoder b64 = Base64.getEncoder();
    SecretKeyFactory factoryBC = null;
    try {
        factoryBC = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256", "BC");
    } catch (NoSuchAlgorithmException e) {
        throw new RuntimeException(e);
    } catch (NoSuchProviderException e) {
        throw new RuntimeException(e);
    }
    KeySpec keyspecBC = new PBEKeySpec(input.toCharArray(), salt, 65000, 256);
    SecretKey keyBC = null;
    try {
        keyBC = factoryBC.generateSecret(keyspecBC);
    } catch (InvalidKeySpecException e) {
        throw new RuntimeException(e);
    }

    return b64.encodeToString(keyBC.getEncoded());

}
}
