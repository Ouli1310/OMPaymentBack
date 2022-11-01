package com.example.OMPayment.repository;

import com.example.OMPayment.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Repository
public interface TransactionRepository  extends JpaRepository<Transaction, Long> {

    List<Transaction> findByStatus(String Status);
    Transaction findByTransactionId(String transactionId);

    List<Transaction> findByPartnerId(String id);

    List<Transaction> findByAgent(String emailAgent);

    List<Transaction> findByAgentAndStatus(String email, String status);

    List<Transaction> findByPartnerIdAndStatus(String partnerId, String status);

    List<Transaction> findByEntite(Long entite);

    List<Transaction> findByMethodeNot(String methode);

    List<Transaction> findByMethode(String methode);

    List<Transaction> findByEntiteAndStatus(Long entite, String status);
    Transaction findByDate(Date date);
    List<Transaction> findByDay(String date);

    List<Transaction> findByDayGreaterThanEqualAndDayLessThanEqual(String date1, String date2);

    List<Transaction> findByEntiteAndDayGreaterThanEqualAndDayLessThanEqual(Long entite, String date1, String date2);
}
