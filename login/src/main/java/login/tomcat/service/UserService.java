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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.mongodb.User;
import com.mongodb.UserRepository;

@Service
public class UserService {
    Logger log = LoggerFactory.getLogger(UserService.class);

	private final UserRepository userRepository;

	public UserService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	public User createUser(String username, String password, String[] roles) {
		log.info("createUser " + username);
		User user = new User();
		user.setUsername(username);
		user.setRoles(roles);
		SecureRandom random = new SecureRandom();
		byte salt[] = new byte[20];
		random.nextBytes(salt);
		user.setSalt(salt);
		String hashedPassword = sha256(salt, password);
		user.setHash(hashedPassword);
		log.debug(user.toString());
		return userRepository.save(user);
	}

	public Optional<User> findByUsername(String username) {
		return userRepository.findByUsername(username);
	}

	public Optional<User> deleteByUsername(String username) {
		return userRepository.deleteByUsername(username);

	}
    public User save(User entity) {
    	return userRepository.save(entity);
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

	public void deleteById(String id) {
    	userRepository.deleteById(id);
    	return;
	}


}
