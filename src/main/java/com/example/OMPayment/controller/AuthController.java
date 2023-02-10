package com.example.OMPayment.controller;

import com.example.OMPayment.model.User;
import com.example.OMPayment.payload.request.ChangePasswordRequest;
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
import com.example.OMPayment.security.services.UserDetailsServiceImpl;
import com.example.OMPayment.service.ProfilService;
import com.example.OMPayment.service.UserService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;

import javax.validation.Valid;
import java.util.UUID;

@AllArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
@Slf4j
public class AuthController  {

    @Autowired
    private AuthenticationManager authenticationManager;

    private final UserRepository userRepository;

    private final UserService userService;

    private final ProfilRepository profilRepository;

    private final ProfilService profilService;

    private final PasswordEncoder passwordEncoder;

    private final JwtUtils jwtUtils;

    private final AuthenticationManagerSelf authenticationManagerSelf;

    private final UserDetailsServiceImpl userDetailsService;

    private JavaMailSender mailSender;


    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Validated @RequestBody LoginRequest loginRequest) throws Exception {
        System.out.println(loginRequest);
        System.out.println("aaaaaaaaaaaaaaaaaaaaaaaaaaa");
        System.out.println(loginRequest.getEmail());
        System.out.println(loginRequest.getPassword());
        final UserDetailsImpl userDetails = (UserDetailsImpl) userDetailsService.loadUserByUsername(loginRequest.getEmail());
        System.out.println(userDetails);
        User user = userService.getUserByEmail(loginRequest.getEmail());
        System.out.println(user);
        //UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword());
        //System.out.println(usernamePasswordAuthenticationToken);
        //System.out.println(usernamePasswordAuthenticationToken.isAuthenticated());
        System.out.println("bbbbbbbbbbbbbbbbbbb");
        //Authentication authentication = authenticationManager.authenticate(usernamePasswordAuthenticationToken);

        System.out.println(userService.passwordExist(loginRequest.getEmail()));
        /*if(userService.passwordExist(loginRequest.getEmail()) == false) {
            user.setPassword(passwordEncoder.encode(loginRequest.getPassword()));
            userRepository.save(user);
            System.out.println(user.getPassword());
            System.out.println(userDetailsService.loadUserByUsername(loginRequest.getEmail()));
            return ResponseEntity.ok("Password saved");
        }*/
        if(userDetailsService.doPasswordsMatch(loginRequest.getPassword(), user.getPassword())) {
            try {
                if(user.getStatus() == true) {
                    Authentication authentication = authenticationManagerSelf.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));
                    System.out.println("cccccccccccccccccccccccccc");
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    System.out.println(authentication.isAuthenticated());
                    System.out.println("ddddddddddddddddd");
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    String name = SecurityContextHolder.getContext().getAuthentication().getName();
                    String jwt = jwtUtils.generateJwtToken(name);
                    System.out.println(jwt);

                    //UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
                    System.out.println("login successful");
                    return ResponseEntity.ok(new JwtResponse(jwt, user.getId(), user.getEmail(), user.getProfil()));
                } else {
                    return ResponseEntity.ok("Votre compte est bloqué. Veuillez notifier à l'admin.");

                }
                }catch (NullPointerException e) {
                log.debug("error message"+e.getMessage());
                //throw new Exception("Incorrect user or password", e);
            }

        }
        return ResponseEntity.ok("yes");

    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signupRequest) {
        System.out.println("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
        System.out.println(signupRequest);
        System.out.println(signupRequest.getEmail());
        System.out.println(signupRequest.getPassword());
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
        newUser.setProfil(signupRequest.getProfil());

        newUser.setMsisdn(signupRequest.getMsisdn());
        newUser.setCode(signupRequest.getCode());
        //newUser.setPassword(passwordEncoder.encode(signupRequest.getPassword()));

        newUser.setEntite(signupRequest.getEntite());
        newUser.setStatus(true);

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
        String link = "http://localhost:4200/resetPassword";
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(newUser.getEmail());
        message.setSubject("Création de compte sur la plateforme de paiement OM");
        message.setText("Votre compte a été créé avec succes;/n Allez sur le lien pour créer votre mot de passe: "+link);
        this.mailSender.send(message);
        return ResponseEntity.ok(newUser);

    }

    @PostMapping("/resetPassword")
    public ResponseEntity<?> restPassword(@RequestBody String email) {
        User user = userService.getUserByEmail(email);
        System.out.println(user);
        if (user == null) {
            throw new UsernameNotFoundException(email);
        }
        String token = jwtUtils.generateJwtToken(email);
        user.setRequestPasswordToken(token);
        userRepository.save(user);
        System.out.println("zueeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee"+token);
        System.out.println(user);
        return ResponseEntity.ok(token);
    }

    @PostMapping("/changePassword")
    public ResponseEntity<?> changedPassword(@RequestBody ChangePasswordRequest changePasswordRequest) {
        System.out.println(changePasswordRequest.getEmail());
        System.out.println(changePasswordRequest.getPassword());
        User user = userService.getUserByEmail(changePasswordRequest.getEmail());
        if (user.getRequestPasswordToken() != null) {
            user.setPassword( passwordEncoder.encode(changePasswordRequest.getPassword()));
            user.setRequestPasswordToken(null);
            userRepository.save(user);
            return ResponseEntity.ok(user);
        }
       return null;
    }




}
