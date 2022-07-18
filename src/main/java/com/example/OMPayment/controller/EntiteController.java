package com.example.OMPayment.controller;

import com.example.OMPayment.model.Entite;
import com.example.OMPayment.service.EntiteService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

}
