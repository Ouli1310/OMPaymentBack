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
import java.util.List;

public interface TransactionService {

    List<Transaction> listTransactions();
    List<Transaction> listTransactionParStatus(String status);
    String encryptedCode(Long id, String pinCode) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, JsonProcessingException;
    String getKey(Long id) throws JsonProcessingException;
    byte[] encrypt(String data, String publicKey) throws BadPaddingException, IllegalBlockSizeException, InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException;
    Transaction getTransactionByTransactionId(String id);
    List<Transaction> listTransactionsByPartnerId(String partnerId);
    List<Transaction> lstTransactionByPartnerIdAndStatus(String partnerId, String status);
    List<Transaction> listTransactionsParAgence(String entite);
}
