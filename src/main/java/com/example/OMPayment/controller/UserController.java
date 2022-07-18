package com.example.OMPayment.controller;


import com.example.OMPayment.model.IdType;
import com.example.OMPayment.model.Profil;
import com.example.OMPayment.model.User;
import com.example.OMPayment.service.IdTypeService;
import com.example.OMPayment.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/user/")
@CrossOrigin(origins = "*", maxAge = 3600, allowedHeaders = "*")
@AllArgsConstructor
public class UserController {

    private final UserService userService;
    private final IdTypeService idTypeService;

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

    @GetMapping("pinCode/{id}")
    public Long getUserPinCode(@PathVariable("id") Long id) {
        User user = userService.getUserById(id);
        return userService.getUserPinCode(user.getMsisdn());
    }

    @GetMapping("{id}/{idType}")
    public String getIdByIdType(@PathVariable("id") Long id, @PathVariable("idType") Long idType) {
        return idTypeService.getIdByType(id, idType);
    }
}
