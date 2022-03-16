package com.diverest.diverestmvp.controllers;

import com.diverest.diverestmvp.jwt.JwtUtils;
import com.diverest.diverestmvp.models.ERole;
import com.diverest.diverestmvp.models.Role;
import com.diverest.diverestmvp.models.User;
import com.diverest.diverestmvp.payload.request.LoginRequest;
import com.diverest.diverestmvp.payload.request.SignupRequest;
import com.diverest.diverestmvp.payload.response.JwtResponse;
import com.diverest.diverestmvp.payload.response.MessageResponse;
import com.diverest.diverestmvp.services.AuthenticationService;
import com.diverest.diverestmvp.services.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/")
public class AuthenticationController {

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private LoginRequest loginRequest;

    @Autowired
    private SignupRequest signupRequest;


    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/")
    public void home() {

    }

    @PostMapping("/subscribe")
    public ResponseEntity<?> subscribeSimpleUser(@RequestBody SignupRequest signupRequest) {

        if (authenticationService.emailExists(signupRequest.getEmail())) {

            return ResponseEntity
                    .badRequest()
                    .body("Email is already in use");
        }

        User user = new User(signupRequest.getFirstName(), signupRequest.getLastName(),
                signupRequest.getEmail(), passwordEncoder.encode(signupRequest.getPassword()));

        Set<String> strRoles = signupRequest.getRoles();
        Set<Role> roles = new HashSet<>();
        System.out.println(signupRequest.toString());
        if (strRoles == null) {
            Role userRole = authenticationService.findByRole(ERole.ROLE_USER);
            if (userRole == null) authenticationService.createRole(ERole.ROLE_USER);
            roles.add(userRole);
        }else {
            strRoles.forEach( role -> {
                if (role.equals("admin")){
                    Role adminRole = authenticationService.findByRole(ERole.ROLE_ADMIN);
                    if (adminRole == null) throw new RuntimeException("Error:Role is not found");
                    roles.add(adminRole);
                }else if (role.equals("user")){
                    Role userRole = authenticationService.findByRole(ERole.ROLE_USER);
                    if (userRole == null) throw new RuntimeException("Error:Role is not found");
                    roles.add(userRole);
                }else if (role.equals("business")) {
                    Role businessRole = authenticationService.findByRole(ERole.ROLE_BUSINESS_USER);
                    if (businessRole == null) throw new RuntimeException("Error:Role is not found");
                    roles.add(businessRole);
                } else  throw new RuntimeException("Error:Role is not passed");
            });
        }

        user.setRoles(roles);
        authenticationService.subscribeUser(user);
        System.out.println("SUSCRIBE ENDS");

        return ResponseEntity.ok("User subscribed succesfully");
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginRequest loginRequest) {

        System.out.println(loginRequest.toString());
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
        );
        
        if (authentication == null) System.out.println("NULLLLLLLLL");

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl)  authentication.getPrincipal();
        System.out.println("Auth controller : "+ userDetails.toString());
        /*List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());*/
        //Small tweak for current needs
        List<String> roles = new ArrayList<>();
        roles.add(ERole.ROLE_USER.toString());

        System.out.println("JWT: "+ jwt);
       // return  ResponseEntity.ok(new JwtResponse(jwt,userDetails.getEmail(), roles));
        return  ResponseEntity.ok(new JwtResponse(jwt,userDetails.getEmail(), roles));
    }

}
