package login.tomcat.service;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.mongodb.User;

import login.tomcat.web.MyUserDetails;

@Service
public class CustomUserDetailsService implements UserDetailsService {

	Logger log = LoggerFactory.getLogger(CustomUserDetailsService.class);

	@Autowired
	private UserService userService;



	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		log.info("loadUserByUsername: "+username);

		if (username != null && !username.isEmpty()) {
			Optional<User> user = userService.findByUsername(username);
			if (user.isEmpty() || !user.isPresent()) {
				throw new UsernameNotFoundException("User not found with username: " + username);
			}
			List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
			if (user.get().getRoles() != null) {
				for (String role : user.get().getRoles()) {
					log.info("role: "+role);

					authorities.add(new SimpleGrantedAuthority(role));
				}
				log.debug(username + ": " + user.get().toString() );
			}
			UserDetails ud = new MyUserDetails(username, user.get().getHash(), authorities);
			return ud;
		}
		else {
			log.error("No username given");

			throw new UsernameNotFoundException("No username given");
		}
	}
}
