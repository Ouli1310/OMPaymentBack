package com.example.OMPayment.repository;

import com.example.OMPayment.model.Entite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface EntiteRepository extends JpaRepository<Entite, Long> {

    Entite findByName(String name);
    List<Entite> findByType(String type);

    Entite findByMsisdn(String msisdn);
}
