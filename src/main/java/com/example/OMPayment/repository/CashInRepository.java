package com.example.OMPayment.repository;


import com.example.OMPayment.model.CashIn;
import com.example.OMPayment.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface CashInRepository extends JpaRepository<CashIn, Long> {

    List<CashIn> findAllByOrderByDateDesc();

    List<CashIn> findByStatus(String Status);
    CashIn findByTransactionId(String transactionId);

    List<CashIn> findByPartnerId(String id);

    List<CashIn> findByAgent(String emailAgent);

    List<CashIn> findByAgentAndStatus(String email, String status);

    List<CashIn> findByPartnerIdAndStatus(String partnerId, String status);

    List<CashIn> findByEntite(Long entite);

    List<CashIn> findByMethodeNot(String methode);

    List<CashIn> findByMethodeOrderByDateDesc(String methode);

    List<CashIn> findByMethodeAndStatus(String methode, String status);

    List<CashIn> findByMethodeAndStatusAndCashInId(String methode, String status, String cashInId);

    CashIn findByMethodeAndTransactionId(String methode, String transactionId);

    List<CashIn> findByReference(String reference);

    Boolean existsByReference(String reference);

    List<CashIn> findByReferenceAndStatus(String reference, String status);

    List<CashIn> findByMethodeAndReferenceAndStatus(String methode, String reference, String status);

    List<CashIn> findByEntiteAndStatus(Long entite, String status);
    CashIn findByDate(Date date);
    List<CashIn> findByDay(String date);
    List<CashIn> findByMethodeAndDay(String methode, String date);
    List<CashIn> findByEntiteAndDay(Long entite, String day);

    List<CashIn> findByMethodeAndEntiteAndDay(String methode, Long entite, String day);

    List<CashIn> findByDayGreaterThanEqualAndDayLessThanEqual(String date1, String date2);

    List<CashIn> findByMethodeAndDayGreaterThanEqualAndDayLessThanEqual(String methode, String date1, String date2);

    List<CashIn> findByEntiteAndDayGreaterThanEqualAndDayLessThanEqual(Long entite, String date1, String date2);

    List<CashIn> findByMethodeAndEntiteAndDayGreaterThanEqualAndDayLessThanEqual(String methode, Long entite, String date1, String date2);


}
