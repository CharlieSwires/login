/*
 * Copyright 2012-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package login.tomcat.web;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.mongodb.User;

import login.tomcat.service.CustomUserDetailsService;
import login.tomcat.service.HelloWorldService;
import login.tomcat.service.UserService;

@RestController
@RequestMapping("/api/V1")
public class UserController {
    Logger log = LoggerFactory.getLogger(UserController.class);

	@Autowired
	private HelloWorldService helloWorldService;
	private final UserService userService;

	public UserController(UserService userService) {
		this.userService = userService;
	}
	@Autowired
	private CustomUserDetailsService customService;
		
	@RequestMapping(path = "/register", method = RequestMethod.POST, consumes = "application/json",
			produces = "application/json")
	@PreAuthorize("hasRole('SUPERUSER')")
	public ResponseEntity<UserResponse> registerUser(@RequestBody UserRegistrationRequest request) {
		log.debug(request.toString());
		String username = request.getUsername();
		String password = request.getPassword();
		List<String> rl = new ArrayList<>();
		for (String role : request.getRoles()) {
			rl.add(role);
		}
		String[] roles = rl.toArray(new String[0]);
		if (username == null || username.isBlank() || username .isEmpty() ||
				password == null || password.isBlank() || password.isEmpty() ||
				roles == null || roles.length == 0) {
			log.error(request.toString());
			throw new IllegalArgumentException(request.toString());
		}
		log.info("register");
		Optional<User> existingUser = userService.findByUsername(username);
		if (!existingUser.isEmpty()) {
			userService.deleteByUsername(username);
		}
		User newUser = userService.createUser(username, password, roles);
		log.info(newUser.toString());
		return ResponseEntity.ok(new UserResponse(newUser.getRoles()));
	}
	@RequestMapping("/helloWorld")
	public String helloWorld() {
		log.info("/helloWorld");
		return this.helloWorldService.getHelloMessage();
	}

	@GetMapping("/public")
	public String publicEndpoint() {
		log.info("/public");
		return "Public endpoint accessible to all";
	}
	
	@RequestMapping(path = "/loginAttempt", method = RequestMethod.POST, consumes = "application/json",
			produces = "application/json")
	public ResponseEntity<UserResponse> loginEndpoint(@RequestBody UserRegistrationRequest request, HttpServletRequest httpRequest) {
	    log.info("/loginAttempt");
	    log.debug(request.toString());

	    if (request != null && !request.getUsername().isEmpty() && !request.getPassword().isEmpty()) {
	        Optional<User> user = userService.findByUsername(request.getUsername());

	        if (user.isPresent() && !user.get().getHash().isEmpty() &&
	            UserService.sha256(user.get().getSalt(), request.getPassword()).equals(user.get().getHash())) {
	            UserDetails ud = customService.loadUserByUsername(user.get().getUsername());

	            // Manually create a session
	            HttpSession session = httpRequest.getSession(true);

	            // Set authentication details
	            SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(ud, null, ud.getAuthorities()));

	            return ResponseEntity.ok(new UserResponse(user.get().getRoles()));
	        }
	    }

	    // Return unauthorized status
	    return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
	}
	@GetMapping("/logout")
	public ResponseEntity<String> logoutEndpoint(HttpServletRequest request, HttpServletResponse response) {
		log.info("/logout");
        // Perform any additional logic before logout (if needed)

        // Invalidate the HttpSession (this is done by Spring Security's logout, but you can do it manually if needed)
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }

        // Perform any additional logic after logout (if needed)

        // Return a response, e.g., success message
        return ResponseEntity.ok("Logout successful");
    }
	@GetMapping("/developer")
	@PreAuthorize("hasRole('DEVELOPER')")
	public String developerEndpoint() {
		log.info("/developer");

		return "Developer endpoint accessible to users with the 'DEVELOPER' role";
	}

	@GetMapping("/superuser")
	@Secured("ROLE_SUPERUSER")
	public String superuserEndpoint() {
		log.info("/superuser");

		return "Superuser endpoint accessible to users with the 'SUPERUSER' role";
	}

	@GetMapping("/user")
	@PreAuthorize("hasRole('USER')")
	public String userEndpoint() {
		log.info("/user");

		return "User endpoint accessible to users with the 'USER' role";
	}
}
