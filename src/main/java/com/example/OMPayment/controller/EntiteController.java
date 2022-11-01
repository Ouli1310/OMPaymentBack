package com.example.OMPayment.controller;

import com.example.OMPayment.model.Entite;
import com.example.OMPayment.service.EntiteService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/entite")
@CrossOrigin(origins = "*", maxAge = 3600, allowedHeaders = "*")
@AllArgsConstructor
public class EntiteController {

    private final EntiteService entiteService;

    @GetMapping
    public List<Entite> getAllEntites() {
        return entiteService.getAllEntite();
    }

    @GetMapping("/{id}")
    public Entite getEntiteById(@PathVariable("id") Long id) {
        return entiteService.getEntiteById(id);
    }

    @GetMapping("/name/{name}")
    public Entite getEntiteByName(@PathVariable("name") String name) {
        return entiteService.getEntiteByName(name);
    }

}
