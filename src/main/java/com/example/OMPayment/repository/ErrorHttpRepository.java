package com.example.OMPayment.repository;

import com.example.OMPayment.model.ErrorHttp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ErrorHttpRepository extends JpaRepository<ErrorHttp, Long> {
}
