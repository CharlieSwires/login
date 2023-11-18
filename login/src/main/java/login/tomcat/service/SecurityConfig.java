package login.tomcat.service;

import java.util.Optional;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import com.mongodb.User;

//SecurityConfig.java
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    private final UserService userService;


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
    private final CustomUserDetailsService customUserDetailsService;


    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(customUserDetailsService);
    }

    @Autowired
    public SecurityConfig(UserService userService, CustomUserDetailsService customUserDetailsService) {
        this.userService = userService;
        this.customUserDetailsService = customUserDetailsService;
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
        Optional<User> existingUser = userService.findByUsername(username);
        if (existingUser.isPresent()){
        	userService.deleteById(existingUser.get().getId());
        }

        User user = new User(username, new String[]{role}, UserService.sha256(salt, password), salt);
        userService.save(user);
    }


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
                	.loginPage("http://localhost:8882/login/hello/build/index.html")
                    .loginProcessingUrl("/login")
                    .permitAll()
                    .and()
                .logout()
            	.logoutUrl("http://localhost:8882/login/api/V1/logout")
                .logoutSuccessUrl("http://localhost:8882/login/hello/build/index.html") // Redirect to this URL after successful logout
                .invalidateHttpSession(true) // Invalidate the HttpSession
                .deleteCookies("JSESSIONID") // Delete cookies (e.g., JSESSIONID)
                .permitAll()
                .and()
                .csrf().disable();
        }
     

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {

        auth.inMemoryAuthentication()
            .withUser(DEV_USERNAME).password(UserService.sha256(salt1, DEV_PASSWORD)).roles(DEV_ROLES[0])
            .and()
            .withUser(SUPER_USERNAME).password(UserService.sha256(salt2, SUPER_PASSWORD)).roles(SUPER_ROLES[0])
            .and()
            .withUser(USER_USERNAME).password(UserService.sha256(salt3, USER_PASSWORD)).roles(USER_ROLES[0]);
    }


}
