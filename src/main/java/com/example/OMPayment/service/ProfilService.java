package com.example.OMPayment.service;

import com.example.OMPayment.model.Profil;
import org.springframework.stereotype.Service;

import java.util.List;

public interface ProfilService {

    List<Profil> getAllProfil();
    Profil getProfilById(Long id);

    String getProfilNameById(Long id);
    String getProfilCodeById(Long id);
}
