package com.example.OMPayment.serviceImpl;

import com.example.OMPayment.model.Profil;
import com.example.OMPayment.repository.ProfilRepository;
import com.example.OMPayment.service.ProfilService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
@AllArgsConstructor
public class ProfilServiceImpl implements ProfilService {

    private final ProfilRepository profilRepository;

    @Override
    public List<Profil> getAllProfil() {
        return profilRepository.findAll();
    }

    @Override
    public Profil getProfilById(Long id) {
        return profilRepository.findById(id).get();
    }

    @Override
    public String getProfilNameById(Long id) {
       Profil profil = profilRepository.findById(id).orElse(null);

        return profil.getName();
    }

    @Override
    public String getProfilCodeById(Long id) {
        Profil profil = profilRepository.findById(id).orElse(null);

        return profil.getCode();
    }

}
