package com.diverest.diverestmvp.repositories;

import com.diverest.diverestmvp.models.User;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<User, String> {

    public User findByEmail(String email);
}
