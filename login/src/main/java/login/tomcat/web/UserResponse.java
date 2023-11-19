package login.tomcat.web;

import java.util.Arrays;

public class UserResponse {
	private String [] roles;

	public UserResponse() {
	}

	public String[] getRoles() {
		return roles;
	}

	public void setRoles(String[] roles) {
		this.roles = roles;
	}

	@Override
	public String toString() {
		return "UserResponse [roles=" + Arrays.toString(roles) + "]";
	}

	public UserResponse(String[] roles) {
		super();
		this.roles = roles;
	}
	

}
