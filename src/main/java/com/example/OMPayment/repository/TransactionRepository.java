package com.example.OMPayment.repository;

import com.example.OMPayment.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Repository
public interface TransactionRepository  extends JpaRepository<Transaction, Long> {

    List<Transaction> findAllByOrderByDateDesc();

    List<Transaction> findAllByOrderByDateAsc();

    Boolean existsByReference(String reference);

    List<Transaction> findByStatus(String Status);

    List<Transaction> findByStatusAndDay(String Status, String day);

    List<Transaction> findByStatusAndEntiteAndDay(String Status, Long entite, String day);

    List<Transaction> findByStatusAndAgentAndDay(String Status, String email, String day);

    List<Transaction> findByStatusAndDayGreaterThanEqualAndDayLessThanEqual(String status, String date1, String date2);

    List<Transaction> findByStatusAndEntiteAndDayGreaterThanEqualAndDayLessThanEqual(String status, Long entite, String date1, String date2);

    List<Transaction> findByStatusAndAgentAndDayGreaterThanEqualAndDayLessThanEqual(String status, String email, String date1, String date2);

    Transaction findByTransactionId(String transactionId);

    List<Transaction> findByPartnerId(String id);

    List<Transaction> findByAgentOrderByDateDesc(String emailAgent);

    List<Transaction> findByAgentOrderByDateAsc(String emailAgent);

    List<Transaction> findByAgentAndStatus(String email, String status);

    List<Transaction> findByAgentAndDay(String email, String day);

    List<Transaction> findByAgentAndDayOrderByDateAsc(String email, String day);

    List<Transaction> findByPartnerIdAndStatus(String partnerId, String status);

    List<Transaction> findByEntiteOrderByDateDesc(Long entite);
    List<Transaction> findByEntiteOrderByDateAsc(Long entite);

    List<Transaction> findByMethodeNot(String methode);

    List<Transaction> findByMethode(String methode);

    List<Transaction> findByMethodeAndStatus(String methode, String status);

    Transaction findByMethodeAndTransactionId(String methode, String transactionId);

    List<Transaction> findByReference(String reference);

    List<Transaction> findByReferenceAndStatus(String reference, String status);

    List<Transaction> findByMethodeAndReferenceAndStatus(String methode, String reference, String status);

    List<Transaction> findByEntiteAndStatus(Long entite, String status);
    Transaction findByDate(Date date);
    List<Transaction> findByDay(String date);
    List<Transaction> findByDayOrderByDateAsc(String date);
    List<Transaction> findByMethodeAndDay(String methode, String date);
    List<Transaction> findByEntiteAndDay(Long entite, String day);

    List<Transaction> findByEntiteAndDayOrderByDateAsc(Long entite, String day);

    List<Transaction> findByMethodeAndEntiteAndDay(String methode, Long entite, String day);

    List<Transaction> findByDayGreaterThanEqualAndDayLessThanEqual(String date1, String date2);

    List<Transaction> findByMethodeAndDayGreaterThanEqualAndDayLessThanEqual(String methode, String date1, String date2);

    List<Transaction> findByEntiteAndDayGreaterThanEqualAndDayLessThanEqual(Long entite, String date1, String date2);

    List<Transaction> findByMethodeAndEntiteAndDayGreaterThanEqualAndDayLessThanEqual(String methode, Long entite, String date1, String date2);
}
