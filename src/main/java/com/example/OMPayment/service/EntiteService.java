package com.example.OMPayment.service;

import com.example.OMPayment.model.Entite;
import com.example.OMPayment.model.Transaction;

import java.util.List;

public interface EntiteService {

    List<Entite> getAllEntite();
    Entite getEntiteById(Long id);
    Entite getEntiteByName(String name);
    List<Entite> getEntiteByType(String type);

    Entite getEntiteByMsisdn(String msisdn);
}
