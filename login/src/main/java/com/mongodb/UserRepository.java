package com.mongodb;
import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Copyright 2021 Charles Swires All Rights Reserved
 * @author charl
 *
 */
public interface UserRepository extends MongoRepository<User, String>{

    public List<User> findAll();

    @SuppressWarnings("unchecked")
	public User save(User user);
	
    public Optional<User> findById(String id);
    public void deleteById(String id);

    public Optional<User> findByUsername(String username);

    public Optional<User> deleteByUsername(String username);
    
}
