package com.example.OMPayment.repository;


import com.example.OMPayment.model.CashInActor;
import com.example.OMPayment.model.PaymentActor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CashInActorRepository extends JpaRepository<CashInActor, Long> {

    PaymentActor findByMsisdn(String msisdn);
}
