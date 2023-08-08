package com.example.OMPayment.controller;


import com.example.OMPayment.repository.StatusRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/status")
@CrossOrigin(origins = "*", maxAge = 3600, allowedHeaders = "*")
@AllArgsConstructor
public class StatusController {

    private final StatusRepository statusRepository;

    @GetMapping
    public ResponseEntity<?> getAllStatus() {
        return ResponseEntity.ok(statusRepository.findAll());
    }
}
