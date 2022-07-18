package com.example.OMPayment.repository;

import com.example.OMPayment.model.IdType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IdTypeRepository extends JpaRepository<IdType, Long> {
}
