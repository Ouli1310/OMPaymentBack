package com.example.OMPayment.serviceImpl;

import com.example.OMPayment.model.Profil;
import com.example.OMPayment.repository.ProfilRepository;
import com.example.OMPayment.service.ProfilService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;


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
        return profilRepository.findNameById(id);
    }
}
