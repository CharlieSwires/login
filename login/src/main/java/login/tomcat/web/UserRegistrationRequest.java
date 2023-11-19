package login.tomcat.web;

import java.util.Arrays;

public class UserRegistrationRequest {

	private String username;
	private String password;
	private String roles[];
	public UserRegistrationRequest() {
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String[] getRoles() {
		return roles;
	}
	public void setRoles(String[] roles) {
		this.roles = roles;
	}
	@Override
	public String toString() {
		return "UserRegistrationRequest [username=" + username + ", password=" + password + ", roles="
				+ Arrays.toString(roles) + "]";
	}
	
	
}
