package login.tomcat.service;

import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Security;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;
import java.util.Random;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

//SecurityConfig.java
@Configuration
@EnableWebSecurity

public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .cors().and().csrf().disable()
            .authorizeRequests()
            	.antMatchers("/public/**").permitAll()
            	.antMatchers("/register").permitAll()
            	//.anyRequest().authenticated()
            	.and()
            .formLogin()
                .loginProcessingUrl("/login/api/example/login")
                .permitAll()
                .and()
            .logout()
                .permitAll();
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
    	Random rand = new Random(0);
        byte salt1[] = new byte[20];
        byte salt2[] = new byte[20];
        byte salt3[] = new byte[20];
        rand.nextBytes(salt1);
        rand.nextBytes(salt2);
        rand.nextBytes(salt3);

        auth.inMemoryAuthentication()
            .withUser("developer").password(sha256(salt1, "hbdovhwofhbafhvbrhb")).roles("DEVELOPER")
            .and()
            .withUser("superuser").password(sha256(salt2, "janbpijnebrjnojnboj")).roles("SUPERUSER")
            .and()
            .withUser("user").password(sha256(salt3, "iengpiwqrnpieqrnin")).roles("USER");
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
