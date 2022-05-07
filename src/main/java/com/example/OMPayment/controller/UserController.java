package com.example.OMPayment.controller;


import com.example.OMPayment.model.User;
import com.example.OMPayment.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/user/")
@CrossOrigin(origins = "*", maxAge = 3600, allowedHeaders = "*")
@AllArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("{id}")
    public User getUserById(@PathVariable("id") Long id) {
        return userService.getUserById(id);
    }

    @GetMapping("profil/{profilId}")
    public List<User> getUserByProfilId(@PathVariable("profilId") Long id) {
        return userService.getUserByProfil(id);
    }

}
