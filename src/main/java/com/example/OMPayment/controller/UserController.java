package com.example.OMPayment.controller;


import com.example.OMPayment.model.IdType;
import com.example.OMPayment.model.Profil;
import com.example.OMPayment.model.User;
import com.example.OMPayment.payload.request.SignupRequest;
import com.example.OMPayment.service.IdTypeService;
import com.example.OMPayment.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("api/user/")
@CrossOrigin(origins = "*", maxAge = 3600, allowedHeaders = "*")
@AllArgsConstructor
public class UserController {

    private final UserService userService;
    private final IdTypeService idTypeService;

    @GetMapping
    public ResponseEntity<?> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("{id}")
    public ResponseEntity<?> getUserById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @GetMapping("firstName/{firstName}")
    public ResponseEntity<?> getUserByFirstName(@PathVariable("firstName") String firstName) {
        return ResponseEntity.ok(userService.getUserByFirstName(firstName));
    }

    @GetMapping("email/{email}")
    public ResponseEntity<?> getUserByEmail(@PathVariable("email") String email) {
        return ResponseEntity.ok(userService.getUserByEmail(email));
    }

    @GetMapping("profil/{profilId}")
    public ResponseEntity<?> getUserByProfilId(@PathVariable("profilId") Long id) {
        return ResponseEntity.ok(userService.getUserByProfil(id));
    }

    @GetMapping("pinCode/{id}")
    public ResponseEntity<?> getUserPinCode(@PathVariable("id") Long id) {
        User user = userService.getUserById(id);
        return ResponseEntity.ok(userService.getUserPinCode(user.getMsisdn()));
    }

    @GetMapping("{id}/{idType}")
    public ResponseEntity<?> getIdByIdType(@PathVariable("id") Long id, @PathVariable("idType") Long idType) {
        return ResponseEntity.ok(idTypeService.getIdByType(id,  idType));
    }

    @GetMapping("entite/{entite}")
    public ResponseEntity<?> getUserByEntite(@PathVariable("entite") Long entite) {
        return ResponseEntity.ok(userService.getUserByEntite(entite));
    }

    @PutMapping("updateUser/{id}")
    public ResponseEntity<?> updateUser(@PathVariable("id") Long id, @Valid @RequestBody SignupRequest signupRequest) {
        return ResponseEntity.ok(userService.updateUser(id, signupRequest));
    }

    @PostMapping("blockUser/{email}")
    public void blockUser(@PathVariable("email") String email) {
         userService.blockUser(email);
    }

    @PostMapping("unblockUser/{email}")
    public void unblockUser(@PathVariable("email") String email) {
        userService.unblockUser(email);
    }

    @DeleteMapping("deleteUser/{id}")
    public void deleteUser(@PathVariable("id") Long id) {
         userService.deleteUser(id);
    }
}
