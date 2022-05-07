package com.example.OMPayment.repository;

import com.example.OMPayment.model.Profil;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProfilRepository extends JpaRepository<Profil, Long> {

    String findNameById(Long id);

}
