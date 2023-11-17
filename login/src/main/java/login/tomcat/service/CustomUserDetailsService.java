package login.tomcat.service;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.mongodb.User;

@Service
public class CustomUserDetailsService implements UserDetailsService {

	@Autowired
	private UserService userService;
	
	public class MyUserDetails implements UserDetails{

		private String username;
		private String password;
		private List<GrantedAuthority> authorities;

		public MyUserDetails(String username, String password, List<GrantedAuthority> authorities) {
			this.username = username;
			this.password = password;
			this.authorities = authorities;
		}
		@Override
		public Collection<? extends GrantedAuthority> getAuthorities() {
			return this.authorities;
		}

		@Override
		public String getPassword() {
			return this.password;
		}

		@Override
		public String getUsername() {
			return this.username;
		}

		@Override
		public boolean isAccountNonExpired() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean isAccountNonLocked() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean isCredentialsNonExpired() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean isEnabled() {
			// TODO Auto-generated method stub
			return false;
		}
		
	}
    public UserDetails loadUserByUsername(String username, String password) throws UsernameNotFoundException {
        // Simulate loading user details from a data source (e.g., database)
        // In a real application, you would fetch user details based on the provided username
		if (username != null && !username.isEmpty()
				&& password != null && !password.isEmpty()) {
			Optional<User> user = userService.findByUsername(username);
			if (!user.isEmpty() && (userService.sha256(user.get().getSalt(), password)).equals(user.get().getHash())) {
	            List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
	            for (String role : user.get().getRoles()) {
	                    authorities.add(new SimpleGrantedAuthority("ROLE_"+role));
	            }

				UserDetails ud = new MyUserDetails(username, user.get().getHash(), authorities);
		        return ud;
			}
		}
        throw new UsernameNotFoundException("User not found with username: " + username);

	
    }

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		if (username != null && !username.isEmpty()) {
			Optional<User> user = userService.findByUsername(username);
			if (user.isEmpty()) {
		        throw new UsernameNotFoundException("User not found with username: " + username);
			}
            List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
            for (String role : user.get().getRoles()) {
                    authorities.add(new SimpleGrantedAuthority("ROLE_"+role));
            }

			UserDetails ud = new MyUserDetails(username, user.get().getHash(), authorities);
	        return ud;
		}
		return null;
	}
}
