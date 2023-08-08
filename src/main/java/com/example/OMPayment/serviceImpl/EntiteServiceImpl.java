package com.example.OMPayment.serviceImpl;

import com.example.OMPayment.model.Entite;
import com.example.OMPayment.repository.EntiteRepository;
import com.example.OMPayment.service.EntiteService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class EntiteServiceImpl implements EntiteService {

    private final EntiteRepository entiteRepository;
    @Override
    public List<Entite> getAllEntite() {
        return entiteRepository.findAll();
    }

    @Override
    public Entite getEntiteById(Long id) {
        return entiteRepository.findById(id).get();
    }

    @Override
    public Entite getEntiteByName(String name) {
        return entiteRepository.findByName(name);
    }

    @Override
    public List<Entite> getEntiteByType(String type) {
        return entiteRepository.findByType(type);
    }

    @Override
    public Entite getEntiteByMsisdn(String msisdn) {
        return entiteRepository.findByMsisdn(msisdn);
    }
}
