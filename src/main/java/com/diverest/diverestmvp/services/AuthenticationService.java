package com.diverest.diverestmvp.services;

import com.diverest.diverestmvp.models.ERole;
import com.diverest.diverestmvp.models.Role;
import com.diverest.diverestmvp.models.User;
import com.diverest.diverestmvp.repositories.RoleRepository;
import com.diverest.diverestmvp.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    public void subscribeUser(User user) {

         userRepository.save(user);
    }

    public User loginUser(User user) {

      return userRepository.findByEmail(user.getEmail());
    }

    public boolean emailExists(String email) {

       if (userRepository.findByEmail(email) == null)  return false;
       return true;
    }

    public Role findByRole(ERole roleUser) {

        return roleRepository.findByName(roleUser);
    }

    public void createRole(ERole roleUSer) {
        roleRepository.save(new Role(roleUSer));
    }
}
