package com.example.OMPayment.controller;

import com.example.OMPayment.model.Entite;
import com.example.OMPayment.service.EntiteService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/entite")
@CrossOrigin(origins = "*", maxAge = 3600, allowedHeaders = "*")
@AllArgsConstructor
public class EntiteController {

    private final EntiteService entiteService;

    @GetMapping
    public ResponseEntity<?> getAllEntites() {
        return ResponseEntity.ok(entiteService.getAllEntite());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getEntiteById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(entiteService.getEntiteById(id));
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<?> getEntiteByName(@PathVariable("name") String name) {
        return ResponseEntity.ok(entiteService.getEntiteByName(name));
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<?> getEntiteByType(@PathVariable("type") String type) {
        return ResponseEntity.ok(entiteService.getEntiteByType(type));
    }

    @GetMapping("/telephone/{msisdn}")
    public ResponseEntity<?> getEntiteByTel(@PathVariable("msisdn") String msisdn) {
        return ResponseEntity.ok(entiteService.getEntiteByMsisdn(msisdn));
    }

}
