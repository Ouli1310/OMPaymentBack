package com.example.OMPayment.controller;

import com.example.OMPayment.model.Profil;
import com.example.OMPayment.service.ProfilService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/profil")
@CrossOrigin(origins = "*", maxAge = 3600, allowedHeaders = "*")
@AllArgsConstructor
public class ProfilController {

    private final ProfilService profilService;

    @GetMapping
    public List<Profil> getAllProfils() {
        return profilService.getAllProfil();
    }

    @GetMapping("/{id}")
    public Profil getProfilById(@PathVariable("id") Long id) {
        return profilService.getProfilById(id);
    }

    @GetMapping("/name/{id}")
    public String getNameById(@PathVariable("id") Long id) {
        return profilService.getProfilNameById(id);
    }
}
