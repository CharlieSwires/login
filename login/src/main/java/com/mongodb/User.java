package com.mongodb;

import java.util.Arrays;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Copyright 2021 Charles Swires All Rights Reserved
 * @author charl
 *
 */
@Document(collection="Entries")
public class User {
	public User() {
	}

	//in the clear
	@Id
	private String id;
	private String username;
	private String[] roles;
	private String hash;
	private byte[] salt;
	
	public void setSalt(byte[] salt) {
		this.salt = salt;		
	}
	public byte[] getSalt() {
		return salt;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String[] getRoles() {
		return roles;
	}
	public void setRoles(String[] roles) {
		this.roles = roles;
	}
	public String getHash() {
		return hash;
	}
	public void setHash(String hash) {
		this.hash = hash;
	}
	@Override
	public String toString() {
		return "User [id=" + id + ", username=" + username + ", roles=" + Arrays.toString(roles) + ", hash=" + hash
				+ ", salt=" + Arrays.toString(salt) + "]";
	}

}
