package login.tomcat.service;

import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Security;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;
import java.util.Optional;
import java.util.Random;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import com.mongodb.User;
import com.mongodb.UserRepository;

//SecurityConfig.java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final UserRepository userRepository;


    private static byte salt1[] = new byte[20];
    private static byte salt2[] = new byte[20];
    private static byte salt3[] = new byte[20];
    private static final String DEV_USERNAME = "developer";
    private static final String [] DEV_ROLES = {"DEVELOPER"};
    private static String DEV_PASSWORD;
    private static final String USER_USERNAME = "user";
    private static final String [] USER_ROLES = {"USER"};
    private static String USER_PASSWORD;
    private static final String SUPER_USERNAME = "superuser";
    private static final String [] SUPER_ROLES = {"SUPERUSER"};
    private static String SUPER_PASSWORD;

    @Autowired
    public SecurityConfig(UserRepository userRepository) {
        this.userRepository = userRepository;
        Random rand = new Random(0);
        rand.nextBytes(salt1);
        rand.nextBytes(salt2);
        rand.nextBytes(salt3);
        DEV_PASSWORD = System.getenv("DEV_PASSWORD");
        USER_PASSWORD = System.getenv("USER_PASSWORD");
        SUPER_PASSWORD = System.getenv("SUPER_PASSWORD");

        initializeUsers();
    }

    private void initializeUsers() {
        initializeUser(DEV_USERNAME, DEV_PASSWORD, salt1, DEV_ROLES[0]);
        initializeUser(USER_USERNAME, USER_PASSWORD, salt3, USER_ROLES[0]);
        initializeUser(SUPER_USERNAME, SUPER_PASSWORD, salt2, SUPER_ROLES[0]);
    }

    private void initializeUser(String username, String password, byte[] salt, String role) {
        Optional<User> existingUser = userRepository.findByUsername(username);
        existingUser.ifPresent(userRepository::delete);

        User user = new User(username, new String[]{role}, sha256(salt, password), salt);
        userRepository.save(user);
    }


    @Configuration
    public static class ApiSecurityConfigurerAdapter extends WebSecurityConfigurerAdapter {


        @Override
        public void configure(HttpSecurity http) throws Exception {
            http
            .authorizeRequests()
                //.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                    .antMatchers("/public/**").permitAll()
                    .antMatchers("/register").permitAll()
                    //.anyRequest().authenticated()
                    .and()
                .formLogin()
                    .loginProcessingUrl("/login/api/example/login")
                    .permitAll()
                    .and()
                .logout()
            	.logoutUrl("/logout")
                .logoutSuccessUrl("http://localhost:8882/login/hello/build/index.html") // Redirect to this URL after successful logout
                .invalidateHttpSession(true) // Invalidate the HttpSession
                .deleteCookies("JSESSIONID") // Delete cookies (e.g., JSESSIONID)
                .permitAll()
                .and()
                .csrf().disable();
        }
    }   

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {

        auth.inMemoryAuthentication()
            .withUser(DEV_USERNAME).password(sha256(salt1, DEV_PASSWORD)).roles(DEV_ROLES[0])
            .and()
            .withUser(SUPER_USERNAME).password(sha256(salt2, SUPER_PASSWORD)).roles(SUPER_ROLES[0])
            .and()
            .withUser(USER_USERNAME).password(sha256(salt3, USER_PASSWORD)).roles(USER_ROLES[0]);
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
