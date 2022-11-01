package com.example.OMPayment.repository;

import com.example.OMPayment.model.Entite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface EntiteRepository extends JpaRepository<Entite, Long> {

    Entite findByName(String name);
}
