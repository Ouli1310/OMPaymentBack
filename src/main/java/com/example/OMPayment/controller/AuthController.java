package com.example.OMPayment.controller;

import com.example.OMPayment.model.User;
import com.example.OMPayment.payload.request.LoginRequest;
import com.example.OMPayment.payload.request.SignupRequest;
import com.example.OMPayment.payload.response.JwtResponse;
import com.example.OMPayment.payload.response.MessageResponse;
import com.example.OMPayment.repository.ProfilRepository;
import com.example.OMPayment.repository.UserRepository;
import com.example.OMPayment.security.jwt.JwtUtils;
import com.example.OMPayment.security.services.AuthenticationManagerSelf;
import com.example.OMPayment.security.services.SecurityContextSelf;
import com.example.OMPayment.security.services.UserDetailsImpl;
import com.example.OMPayment.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@AllArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController  {

    private final AuthenticationManager authenticationManager;

    private final UserRepository userRepository;

    private final UserService userService;

    private final ProfilRepository profilRepository;

    private final PasswordEncoder passwordEncoder;

    private final JwtUtils jwtUtils;

    private final AuthenticationManagerSelf authenticationManagerSelf;


    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Validated @RequestBody LoginRequest loginRequest) {
        System.out.println(loginRequest);
        System.out.println("aaaaaaaaaaaaaaaaaaaaaaaaaaa");
        System.out.println(loginRequest.getEmail());
        System.out.println(loginRequest.getPassword());
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword());
        System.out.println(usernamePasswordAuthenticationToken);
        System.out.println(usernamePasswordAuthenticationToken.isAuthenticated());
        System.out.println("bbbbbbbbbbbbbbbbbbb");
        Authentication authentication = authenticationManagerSelf.authenticate(usernamePasswordAuthenticationToken);

        System.out.println(authentication);
        System.out.println(authentication.isAuthenticated());
        System.out.println("cccccccccccccccccccccccccc");
        SecurityContextHolder.getContext().setAuthentication(authentication);
        System.out.println("ddddddddddddddddd");
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        String jwt = jwtUtils.generateJwtToken(name);
        System.out.println(jwt);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        new JwtResponse(jwt, userDetails.getId(), userDetails.getPassword());
        return ResponseEntity.ok("login successful");
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signupRequest) {
        System.out.println("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
        System.out.println(signupRequest.getEmail());
        System.out.println("bbbbbbbbbbbbbbbbbbbbbbbbbbbbbb");
        if (userRepository.existsByEmail(signupRequest.getEmail())) {
            System.out.println("Error: Email is already taken!");
            return ResponseEntity
                    .badRequest()
                    .body("Error: Email is already taken!");

        }
        //System.out.println("hhhhhhhhhhhhhhhhh");
        //userService.createUser(signupRequest);
        User newUser = new User();
        newUser.setFirstName(signupRequest.getFirstName());
        newUser.setLastName(signupRequest.getLastName());
        newUser.setEmail(signupRequest.getEmail());
        newUser.setMsisdn(signupRequest.getMsisdn());
        newUser.setPassword(passwordEncoder.encode(signupRequest.getPassword()));
        newUser.setProfil(signupRequest.getProfil());
        /**User newUser = new User(signupRequest.getFirstName(),
                                signupRequest.getLastName(),
                                signupRequest.getEmail(),
                signupRequest.getMsisdn(),
                passwordEncoder.encode(signupRequest.getPassword()),
                signupRequest.getProfil()

                ); */
        System.out.println(newUser);
        userRepository.save(newUser);
        System.out.println("User registered successfully!");
        return ResponseEntity.ok("User registered successfully!");
    }
}
