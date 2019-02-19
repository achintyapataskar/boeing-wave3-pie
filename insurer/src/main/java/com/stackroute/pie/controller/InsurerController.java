package com.stackroute.pie.controller;


import com.stackroute.pie.message.request.LogInForm;
import com.stackroute.pie.message.request.SignUpForm;
import com.stackroute.pie.message.response.JwtResponse;
import com.stackroute.pie.message.response.ResponseMessage;
import com.stackroute.pie.model.Insurer;
import com.stackroute.pie.model.Role;
import com.stackroute.pie.model.RoleName;
import com.stackroute.pie.repository.InsurerRepository;
import com.stackroute.pie.repository.RoleRepository;
import com.stackroute.pie.security.jwt.JwtProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashSet;
import java.util.Set;

import static com.stackroute.pie.model.RoleName.ROLE_INSURER;

//import com.stackroute.pie.model.Insurer;
//import com.stackroute.pie.repository.InsurerRepository;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/auth")
public class InsurerController {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    InsurerRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtProvider jwtProvider;

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LogInForm loginRequest) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getInsurerLicense() , loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = jwtProvider.generateJwtToken(authentication);
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        return ResponseEntity.ok(new JwtResponse(jwt, userDetails.getUsername(), userDetails.getAuthorities()));
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignUpForm signUpRequest) {
        if (userRepository.existsByInsurerName(signUpRequest.getInsurerName())) {
            return new ResponseEntity<>(new ResponseMessage("Fail -> Username is already taken!"),
                    HttpStatus.BAD_REQUEST);
        }

        if (userRepository.existsByInsurerEmail(signUpRequest.getInsurerEmail())) {
            return new ResponseEntity<>(new ResponseMessage("Fail -> Email is already in use!"),
                    HttpStatus.BAD_REQUEST);
        }

        /* Creating Insurer's account */
        Insurer user = new Insurer(signUpRequest.getInsurerName(),signUpRequest.getInsurerLicense(),signUpRequest.getInsurerEmail(),signUpRequest.getPassword(),signUpRequest.getCreatedDate(),signUpRequest.getUpdateDate(),signUpRequest.getInsurerSecurityQuestion(),signUpRequest.getInsurerSecurityAnswer());

        Set<String> strRoles = signUpRequest.getRole();
        Set<Role> roles = new HashSet<>();

        strRoles.forEach(role -> {
            switch (role) {
                case "admin":
                    Role adminRole = roleRepository.findByName(RoleName.ROLE_ADMIN)
                            .orElseThrow(() -> new RuntimeException("Fail! -> Cause: User Role not find."));
                    roles.add(adminRole);

                    break;
                case "pm":
                    Role pmRole = roleRepository.findByName(ROLE_INSURER)
                            .orElseThrow(() -> new RuntimeException("Fail! -> Cause: User Role not find."));
                    roles.add(pmRole);

                    break;
                default:
                    Role userRole = roleRepository.findByName(RoleName.ROLE_USER)
                            .orElseThrow(() -> new RuntimeException("Fail! -> Cause: User Role not find."));
                    roles.add(userRole);
            }
        });

        user.setRoles(roles);
        userRepository.save(user);

        return new ResponseEntity<>(new ResponseMessage("User registered successfully!"), HttpStatus.OK);
    }
}