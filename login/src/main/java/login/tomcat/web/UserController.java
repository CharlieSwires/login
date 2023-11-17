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

import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.mongodb.User;

import login.tomcat.service.HelloWorldService;
import login.tomcat.service.UserService;

@RestController
@RequestMapping("/api/example")
public class UserController {

	@Autowired
	private HelloWorldService helloWorldService;
	private final UserService userService;

	public UserController(UserService userService) {
		this.userService = userService;
	}

	@PostMapping("/register")
	public ResponseEntity<User> registerUser(@RequestBody UserRegistrationRequest request) {
		System.out.println(request.toString());
		String username = request.getUsername();
		String password = request.getPassword();
		String[] roles = request.getRoles();
		System.out.println("register");
		Optional<User> existingUser = userService.findByUsername(username);
		if (!existingUser.isEmpty()) {
			userService.deleteByUsername(username);
		}
		User newUser = userService.createUser(username, password, roles);
		return ResponseEntity.ok(newUser);
	}
	@RequestMapping("/")
	@ResponseBody
	public String helloWorld() {
		return this.helloWorldService.getHelloMessage();
	}

	@GetMapping("/public")
	public String publicEndpoint() {
		return "Public endpoint accessible to all";
	}
	
	@SuppressWarnings("static-access")
	@PostMapping("/login")
	public String[] loginEndpoint(@RequestBody UserRegistrationRequest request) {
		System.out.println("/login "+request.toString());
		if (request != null && request.getUsername() != null && !request.getUsername().isEmpty()
				&& request.getPassword() != null && !request.getPassword().isEmpty()) {
			Optional<User> user = userService.findByUsername(request.getUsername());
			System.out.println(user.get().toString());
			System.out.println("hash = "+(userService.sha256(user.get().getSalt(), request.getPassword())));
			System.out.println("request.getPassword() = "+request.getPassword());
			if (!user.isEmpty() && (userService.sha256(user.get().getSalt(), request.getPassword())).equals(user.get().getHash())) {
				return user.get().getRoles();
			}

		} else {
			String[] result = {"Login failed", (String)null};
			return result;
		}
		return null;
	}
//	@GetMapping("/logout")
//    public ResponseEntity<String> logoutEndpoint(HttpServletRequest request, HttpServletResponse response) {
//        // Perform any additional logic before logout (if needed)
//
//        // Invalidate the HttpSession (this is done by Spring Security's logout, but you can do it manually if needed)
//        HttpSession session = request.getSession(false);
//        if (session != null) {
//            session.invalidate();
//        }
//
//        // Perform any additional logic after logout (if needed)
//
//        // Return a response, e.g., success message
//        return ResponseEntity.ok("Logout successful");
//    }
	@GetMapping("/developer")
	@PreAuthorize("hasRole('DEVELOPER')")
	public String developerEndpoint() {
		return "Developer endpoint accessible to users with the 'DEVELOPER' role";
	}

	@PostMapping("/superuser")
	@Secured("ROLE_SUPERUSER")
	public String superuserEndpoint() {
		return "Superuser endpoint accessible to users with the 'SUPERUSER' role";
	}

	@DeleteMapping("/user")
	@PreAuthorize("hasRole('USER')")
	public String userEndpoint() {
		return "User endpoint accessible to users with the 'USER' role";
	}
}
