package com.example.OMPayment.controller;

import com.example.OMPayment.model.Profil;
import com.example.OMPayment.service.ProfilService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/profil")
@CrossOrigin(origins = "*", maxAge = 3600, allowedHeaders = "*")
@AllArgsConstructor
public class ProfilController {

    private final ProfilService profilService;

    @GetMapping
    public ResponseEntity<?> getAllProfils() {
        return ResponseEntity.ok(profilService.getAllProfil());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getProfilById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(profilService.getProfilById(id));
    }

    @GetMapping("/name/{id}")
    public ResponseEntity<?> getNameById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(profilService.getProfilNameById(id));
    }

    @GetMapping("/code/{id}")
    public ResponseEntity<?> getCodeById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(profilService.getProfilCodeById(id));
    }

}
