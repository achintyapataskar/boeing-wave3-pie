package com.stackroute.pie.controller;

import com.stackroute.pie.domain.*;
import com.stackroute.pie.repository.UserRepository;
import com.stackroute.pie.exceptions.UserNotFoundException;
import com.stackroute.pie.message.request.SignUpForm;
import com.stackroute.pie.message.response.ResponseMessage;
import com.stackroute.pie.services.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class UserController {


    @Autowired
    private KafkaTemplate<String, Insured> kafkaTemplate;
//    @Autowired
//    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

//    @Autowired
//    RoleRepository roleRepository;

    @Autowired
    PasswordEncoder encoder;

//    @Autowired
//    JwtProvider jwtProvider;
    @Autowired
    private UserDetailsServiceImpl userService;



    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignUpForm signUpRequest) {
        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            return new ResponseEntity<>(new ResponseMessage("Fail -> Username is already taken!"),
                    HttpStatus.BAD_REQUEST);
        }

        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return new ResponseEntity<>(new ResponseMessage("Fail -> Email is already in use!"),
                    HttpStatus.BAD_REQUEST);
        }

        // Creating insured's account
        Insured insured = new Insured(signUpRequest.getFullName(), signUpRequest.getUsername(), signUpRequest.getEmail(),
                encoder.encode(signUpRequest.getPassword()),signUpRequest.getGender(),signUpRequest.getCreatedDate(),signUpRequest.getSecurityAnswer());

//        Set<String> strRoles = signUpRequest.getRole();

        Set<Policy> policySet=signUpRequest.getPolicees();

         List<Request> requestList =signUpRequest.getRequests();

        Set<Role> roles = new HashSet<>();

        Role userrRole = new Role();


        Set<Request> requests =new HashSet<>();

//        Request request = new Request();



        userrRole.setName(RoleName.ROLE_USER);

//        requests.add(request);

        roles.add(userrRole);

        insured.setRoles(roles);

//        insured.setPolicies();

        userRepository.save(insured);
        kafkaTemplate.send("userregg_json", insured);
        return new ResponseEntity<>(new ResponseMessage("Insured registered successfully!"), HttpStatus.OK);
    }

//    @RequestMapping(value = "/requests/{insuredId}", method = RequestMethod.GET)
//    public ResponseEntity Insured (@PathVariable("insuredId") int insuredId) {
//
//        ResponseEntity responseEntity;
//
//        try {
//
//            Insured user1 = userService.getRequests(insuredId);
//            responseEntity =  new ResponseEntity<Insured>(user1, HttpStatus.OK);
//
//        }
//        catch (UserNotFoundException ex) {
//            responseEntity = new ResponseEntity<String>(ex.getMessage(),HttpStatus.CONFLICT);
//            ex.printStackTrace();
//        }
//        return responseEntity;
//
//    }
//    @RequestMapping(value = "/request", method = RequestMethod.POST)
//    public ResponseEntity Insured (@RequestBody Insured user) {
//        ResponseEntity responseEntity;
//        Insured user1 = userService.postRequest(user);
//        System.out.println("In controller" + user1);
//        responseEntity = new ResponseEntity<Insured>(user1,HttpStatus.OK);
//        return responseEntity;
//    }
    @RequestMapping(value = "/profile/{username}", method = RequestMethod.GET)
    public ResponseEntity User (@PathVariable("username") String username){
        ResponseEntity responseEntity;

        try {

            Insured insured1 = userService.getProfile(username);
            responseEntity =  new ResponseEntity<Insured>(insured1, HttpStatus.OK);

        }
        catch (UserNotFoundException ex) {
            responseEntity = new ResponseEntity<String>(ex.getMessage(),HttpStatus.CONFLICT);
            ex.printStackTrace();
        }
        return responseEntity;

    }
    @RequestMapping(value = "/profile/{username}", method = RequestMethod.PUT)
    public ResponseEntity User (@PathVariable("username") String username, @RequestBody Insured insured){
        ResponseEntity responseEntity;

        try {

            Insured insured1 = userService.updateProfile(username, insured);
            responseEntity =  new ResponseEntity<Insured>(insured1, HttpStatus.OK);

        }
        catch (UserNotFoundException ex) {
            responseEntity = new ResponseEntity<String>(ex.getMessage(),HttpStatus.CONFLICT);
            ex.printStackTrace();
        }
        return responseEntity;

    }

}



