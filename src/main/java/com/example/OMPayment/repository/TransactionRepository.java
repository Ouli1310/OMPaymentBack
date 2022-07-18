package com.example.OMPayment.repository;

import com.example.OMPayment.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository  extends JpaRepository<Transaction, Long> {

    List<Transaction> findByStatus(String Status);
    Transaction findByTransactionId(String transactionId);

    List<Transaction> findByPartnerId(String id);

    List<Transaction> findByPartnerIdAndStatus(String partnerId, String status);

    List<Transaction> findByEntite(String entite);
}
