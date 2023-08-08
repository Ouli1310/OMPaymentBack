package com.example.OMPayment.service;

import com.example.OMPayment.model.Transaction;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.TreeMap;

public interface TransactionService {

    List<Transaction> listTransactions();
    List<Transaction> listTransactionParStatus(String status);

    List<Transaction> listTransactionParStatusAndDay(String status, String day);

    List<Transaction> listTransactionParStatusAndAgenceDay(String status, Long entire, String day);

    List<Transaction> listTransactionParStatusAndAgentAndDay(String status, String email, String day);

    List<Transaction> transactionsByStatusAndBetweenDates(String status, String date1, String date2);

    List<Transaction> transactionsByStatusAndEntiteAndBetweenDates(String status, Long entite, String date1, String date2);

    List<Transaction> transactionsByStatusAndAgentAndBetweenDates(String status, String email, String date1, String date2);
    String encryptedCode(Long id, String pinCode) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, JsonProcessingException;
    String getKey(Long id) throws JsonProcessingException;
    byte[] encrypt(String data, String publicKey) throws BadPaddingException, IllegalBlockSizeException, InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException;
    Transaction getTransactionByTransactionId(String id);
    Transaction getTransactionByMethodeAndTransactionId(String methode, String id);
    List<Transaction> listTransactionsByPartnerId(String partnerId);
    List<Transaction> lstTransactionByPartnerIdAndStatus(String partnerId, String status);
    List<Transaction> listTransactionsParAgence(Long entite);
    List<Transaction> listTransactionsParMethode(String methode);
    List<Transaction> listTransactionByMethodeAndStatus(String methode, String status);
     List<Transaction> listTransactionsCashIn(String methode);
    List<Transaction> listTransactionsParAgent(String emailAgent);
    List<Transaction> listTransactionParPNR(String pnr);
    List<Transaction> listTransactionParPNRAndStatus(String pnr, String status);

    List<Transaction> listTransactionParMethodeAndPNRAndStatus(String methode, String pnr, String status);
    List<Transaction> listTransactionParAgenceEtStatus(Long entite, String status);
    List<Transaction> listTransactionParAgentEtStatus(String email, String status);
    Transaction transactionByDate(Date date);
    List<Transaction> transactionByDay(String day);
    List<Transaction> transactionByMethodeAndDay(String methode, String day);
    List<Transaction> transactinByEntiteAndDay(Long entite, String day);
    List<Transaction> transactinByMethodeAndEntiteAndDay(String methode, Long entite, String day);
    List<Transaction> transactionBetweenDate1AnDate2(String date1, String date2);
    List<Transaction> transactionByMethodeAndBetweenDate1AnDate2(String methode, String date1, String date2);
    List<Transaction> transactionsByEntiteAndBetweenDates(Long entite, String date1, String date2);
    List<Transaction> transactionsByMethodeAndEntiteAndBetweenDates(String methode, Long entite, String date1, String date2);
    Transaction getTransactionById(Long id);
    Transaction changeStatus(Long id);

    List<Transaction> transactionsByAgentAndDay(String email, String day);
}
