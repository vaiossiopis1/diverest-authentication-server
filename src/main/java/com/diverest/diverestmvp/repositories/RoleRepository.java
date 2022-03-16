package com.diverest.diverestmvp.repositories;

import com.diverest.diverestmvp.models.ERole;
import com.diverest.diverestmvp.models.Role;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface RoleRepository extends MongoRepository<Role, String> {

    public Role findByName(ERole role);
}
